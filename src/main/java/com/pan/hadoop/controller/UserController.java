package com.pan.hadoop.controller;


import com.pan.hadoop.dao.DirDao;
import com.pan.hadoop.dao.MailDao;
import com.pan.hadoop.dao.UserDao;
import com.pan.hadoop.entity.*;
import com.pan.hadoop.service.FileService;
import com.pan.hadoop.service.UserService;
import com.google.code.kaptcha.Producer;
import com.pan.hadoop.util.Constants;
import com.pan.hadoop.util.FilesUtil;
import com.pan.hadoop.util.MD5Util;
import com.pan.hadoop.util.MailClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;


@Controller
public class UserController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private MailDao mailDao;
    @Autowired
    private DirDao dirDao;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private Producer kaptchproducer;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
        /**
         * 主页面（登录页面）
         * @param model
         * @param ticket 是否已经登录过
         * @return
         */
    @RequestMapping(value = {"/index", "/"})
    public String index(Model model
            , @CookieValue(value = "ticket", defaultValue = "null") String ticket) {
        // 是否有登录凭证
        if(!ticket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(ticket);
            // 判断登录凭证是否有效
            if(loginTicket != null && loginTicket.getStatus().equals("true") && Long.valueOf(loginTicket.getExpired()) > System.currentTimeMillis()){
                String userId = loginTicket.getUserId();

                User userDirs = userDao.getUserDirs(userId);
                List<Dir> dirs = fileService.getDirOrFileList(userDirs, userId, "name", "up");
                model.addAttribute("type", "name");
                model.addAttribute("upOrDown", "up");
                model.addAttribute("fileType", "all");
                model.addAttribute("parentId", userId);
                model.addAttribute("path", "/");
                model.addAttribute("dirs", dirs);
                return "user/index";
            }
        }
        model.addAttribute("content", "content");
        return "login";
    }

    /**
     * 注册账号
     * @param model
     * @param user          用户信息对象
     * @param confirmPwd    确认密码
     * @param request       获取地址（用于发送邮件的链接地址）
     * @return
     */
    @RequestMapping("/userRegister")
    public String register(Model model
            ,User user
            , String confirmPwd
            , HttpServletRequest request){
        model.addAttribute("user", user);
        // 检查用户ID是否已存在
        if(userService.checkId(user.getId())){
            model.addAttribute("registerResult", "账号已存在");
            model.addAttribute("content", "content s--signup");
            return "login";
        }
        if(userService.checkMail(user.getMail())){
            model.addAttribute("registerResult", "该邮箱已被占用");
            model.addAttribute("content", "content s--signup");
            return "login";
        }
        // 检查两次输入的密码是否一致
        if(!user.getPwd().equals(confirmPwd)){
            model.addAttribute("registerResult", "两次密码不相同");
            model.addAttribute("content", "content s--signup");
            return "login";
        }
        // 生成激活邮件信息
        Mail mail = new Mail();
        mail.setMail_md5(MD5Util.encodePwd(user.getMail() + System.currentTimeMillis()));
        mail.setMail(user.getMail());
        mail.setUser_id(user.getId());
        mail.setAction("register");
        mail.setExpired(String.valueOf(System.currentTimeMillis() + 1800 * 1000));
        mail.setContent("0");
        mailDao.createMail(mail);
        // 注册用户信息
        user.setStatus("false");
        userService.addUser(user);
        // 服务器IP地址加端口号，例：127.0.0.1:8080
        String url = request.getServerName() + ":" + request.getServerPort();
        // 发送邮件
        Context context = new Context();
        context.setVariable("url","http://" + url + "/action/" + mail.getMail_md5());
        String content = templateEngine.process("mail/mailRegister", context);
        mailClient.sendMail(user.getMail(), "hadoop网盘注册邮件", content);

        model.addAttribute("code", "register");
        return "wait";
    }

    /**
     * 邮件内容注册链接
     * @param model
     * @param mail_md5      邮件MD5
     * @return
     */
    @RequestMapping("/action/{mail_md5}")
    public String register(Model model
            , @PathVariable(value = "mail_md5") String mail_md5) {
        Mail mail = mailDao.getMailInfoById(mail_md5);
        if(mail == null){
            return "error";
        }
        else if(mail.getContent().equals("1")){
            if(mail.getAction().equals("register")){
                User user = userDao.getUserInfoById(mail.getUser_id());
                if(user.getStatus().equals("true")){
                    model.addAttribute("code", "registerSuccess");
                    return "wait";
                }
            }
            model.addAttribute("code", "used");
            return "wait";
        }
        else if(Long.valueOf(mail.getExpired()) < System.currentTimeMillis()){
            model.addAttribute("code", "expired");
            return "wait";
        }
        mail.setContent("1");
        mailDao.createMail(mail);
        if(mail.getAction().equals("register")){
            User user = userDao.getUserInfoById(mail.getUser_id());
            user.setStatus("true");
            userDao.userInfo(user);
            userService.initDir(user);
            // 设置用户空间大小，500MB
            userDao.addUserTotalSpace(user.getId(), Constants.SMALL_TOTAL_SPACE);
            // 初始化用户已用空间、剩余空间大小
            userService.calSpace(user.getId());
            // 添加信息到邮箱用户表（一个邮箱只能绑定一个账号）
            Mail_User mail_user = new Mail_User();
            mail_user.setMail(user.getMail());
            mail_user.setUserId(user.getId());
            mailDao.createMailUser(mail_user);
            model.addAttribute("code", "registerSuccess");
            return "wait";
        }
        else if(mail.getAction().equals("forget")){
            model.addAttribute("code", mail.getMail_md5());
            model.addAttribute("userId", mail.getUser_id());
            return "user/forgotEditPwd";
        }
        else if(mail.getAction().equals("unbound")){
            User user = userDao.getUserInfoById(mail.getUser_id());
            mailDao.delMailUserInfoById(user.getMail());
            user.setMail("null");
            userDao.userInfo(user);
            model.addAttribute("code", "unboundSuccess");
            return "wait";
        }
        else if(mail.getAction().equals("bound")){
            // 添加信息到邮箱用户表（一个邮箱只能绑定一个账号）
            Mail_User mail_user = new Mail_User();
            mail_user.setMail(mail.getMail());
            mail_user.setUserId(mail.getUser_id());
            User user = userDao.getUserInfoById(mail.getUser_id());
            user.setMail(mail.getMail());
            mailDao.createMailUser(mail_user);
            userDao.userInfo(user);
            model.addAttribute("code", "boundSuccess");
            return "wait";
        }
        return "error";
    }
    @RequestMapping("/Pwd")
    public String changePwd(Model model){
        model.addAttribute("code", "");
        model.addAttribute("userId", "17031110100");
        return "user/forgotEditPwd";
    }


    /**
     * 用户自己修改密码
     * @param model
     * @param id            用户ID
     * @param old           旧密码
     * @param pwd           新密码
     * @param confirmPwd    确认新密码
     * @return
     */
    @RequestMapping("/userChangePwd")
    public String changePwd(Model model
            , String id
            ,@RequestParam(value="old", defaultValue="") String old
            , String pwd
            , String confirmPwd) {
        if(old.equals("")) {
            model.addAttribute("changePwdResult", "旧密码没有填写");
            return "user/editPwd";
        }
        model.addAttribute("userId", id);
        if(!pwd.equals(confirmPwd)){
            model.addAttribute("changePwdResult", "两次密码不相同");
            return "user/editPwd";
        }
        User user = userDao.getUserInfoById(id);
        if(!MD5Util.isPwdRight(old,user.getPwd())){
            model.addAttribute("changePwdResult", "旧密码不正确");
            return "user/editPwd";
        }
        if(old.equals(pwd)){
            model.addAttribute("changePwdResult", "新旧密码重复");
            return "user/editPwd";
        }
        user.setPwd(pwd);
        userService.addUser(user);
        return "close";
    }

    /**
     * 用忘记密码邮件修改密码
     * @param model
     * @param id            用户ID
     * @param pwd           新密码
     * @param confirmPwd    确认新密码
     * @param code          邮件认证码
     * @return
     */
    @RequestMapping("/userForgotEditPwd")
    public String forgotEditPwd(Model model
            , String id
            , String pwd
            , String confirmPwd
            , @RequestParam(value="code", defaultValue="") String code) {
        if(!pwd.equals(confirmPwd)){
            model.addAttribute("userId", id);
            model.addAttribute("changePwdResult", "两次密码不相同");
            return "user/forgotEditPwd";
        }
        // 邮件认证（防止直接通过链接进行修改任意账户的密码）
        if(code.equals("")){
            model.addAttribute("code", "forgotEditError");
            return "wait";
        }
        Mail mail = mailDao.getMailInfoById(code);
        // 邮件认证码不存在 || 邮件认证码无效（超时） || 邮件认证码与用户不对应
        if(mail == null || (Long.valueOf(mail.getExpired()) < System.currentTimeMillis()) || !mail.getUser_id().equals(id)){
            model.addAttribute("code", "forgotEditError");
            return "wait";
        }
        User user = userDao.getUserInfoById(mail.getUser_id());
        if(MD5Util.isPwdRight(pwd,user.getPwd())){
            model.addAttribute("changePwdResult", "密码与当前密码重复");
            return "user/forgotEditPwd";
        }
        user.setPwd(pwd);
        userService.addUser(user);
        model.addAttribute("code", "forgotEditSuccess");
        return "wait";
    }

    /**
     * 用户修改密码跳转（是否已登录，有登录凭证）
     * @param model
     * @param ticket
     * @return
     */
    @RequestMapping("/user/changePwd")
    public String userChangePwd(Model model, @CookieValue(value = "ticket", defaultValue = "null") String ticket) {
        // 是否有登录凭证
        if(!ticket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(ticket);
            // 判断登录凭证是否有效
            if (loginTicket != null && loginTicket.getStatus().equals("true") && Long.valueOf(loginTicket.getExpired()) > System.currentTimeMillis()) {
                String userId = loginTicket.getUserId();
                model.addAttribute("userId", userId);
                model.addAttribute("code", "change");
                return "user/editPwd";
            }
        }
        return "error";
    }


    /**
     * 生成验证码
     * @param response      响应验证码图片
     * @param session       设置验证码文本内容
     */
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response
            , HttpSession session){
        // 生成验证码
        String text = kaptchproducer.createText();
        BufferedImage image = kaptchproducer.createImage(text);
        // 将验证码存入session
        session.setAttribute("kaptcha", text);
        // 将图片输出到浏览器
        response.setContentType("image/png");
        try{
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        }catch(IOException e){
            System.out.println(("验证码失败" + e.getMessage()));
        }
    }

    /**
     * 用户登录
     * @param model
     * @param user          用户登录信息对象
     * @param captcha       输入的验证码
     * @param rememberMe    是否记住用户（不记住：半天；记住：100天）
     * @param session       获取验证码信文本
     * @param response
     * @return
     */
    @RequestMapping("/userLogin")
    public String login(Model model
            , User user
            , String captcha
            , boolean rememberMe
            , HttpSession session
            , HttpServletResponse response) {
        model.addAttribute("user", user);
        // 获取验证码（生成的验证码）
        String kaptcha = (String) session.getAttribute("kaptcha");
        // 生成的验证码与输入的验证码不同或一个为空
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(captcha) || !kaptcha.equalsIgnoreCase(captcha)) {
            model.addAttribute("loginResult", "验证码错误!");
            model.addAttribute("content", "content");
            return "login";
        }
        // 检测账号、密码
        // 是否勾选记住密码
        int expiredSeconds = rememberMe ? Constants.REMEMBER_EXPIRED_SECONDS : Constants.DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(user.getId(), user.getPwd(), expiredSeconds);
        // 是否登录成功
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath("/");
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);

            User userDirs = userDao.getUserDirs(user.getId());
            List<Dir> dirs = fileService.getDirOrFileList(userDirs, user.getId(), "name", "up");
            Dir dir = dirDao.getDirInfoById(user.getId());
            model.addAttribute("type", "name");
            model.addAttribute("upOrDown", "up");
            model.addAttribute("fileType", "all");
            user = userDao.getHostHolderUserById(user.getId());
            Long percent = Long.valueOf(user.getUse_space()) * 100 / Long.valueOf(user.getTotalSpace());
            user.setTotalSpace(String.valueOf(percent));
            user.setUse_space(FilesUtil.FormatFileSize(Long.valueOf(user.getUse_space()) + Long.valueOf(user.getSurplus_space())));
            if(percent < 60)
                user.setSurplus_space("green");
            else if(percent >=60 && percent < 80)
                user.setSurplus_space("orange");
            else if(percent >= 80)
                user.setSurplus_space("red");
            model.addAttribute("loginUser", user);
            model.addAttribute("path", dir.getPath());
            model.addAttribute("dirs", dirs);
            model.addAttribute("parentId", user.getId());
            return "user/index";
        }
        else{
            model.addAttribute("loginResult", map.get("middleResult"));
            model.addAttribute("content", "content");
            return "login";
        }
    }

    /**
     * 退出登录
     * @param ticket        用户认证凭证
     * @return
     */
    @RequestMapping(path = "/userLogout", method = RequestMethod.GET)
    public String logout(Model model, @CookieValue("ticket") String ticket){
        userService.logout(ticket);
        model.addAttribute("content", "content");
        return "login";
    }

    /**
     * 跳转到用户修改信息页面（赋值数据库内容）
     * @param model
     * @param ticket    用户登录凭证
     * @return
     */
    @RequestMapping("/user/editInfo")
    public String userEditInfo(Model model, @CookieValue("ticket") String ticket){
        LoginTicket loginTicket = userService.fineLoginTicket(ticket);
        User user = userDao.getUserInfoById(loginTicket.getUserId());
        if(user.getInstitute() != null){
            for(int i=0; i < Constants.INSTITUTE.length; i++){
                if(user.getInstitute().equals(Constants.INSTITUTE[i]))
                    user.setInstitute(String.valueOf(i));
            }
        }
        model.addAttribute("user", user);
        return "user/editInfo";
    }

    /**
     * 用户信息修改
     * @param model
     * @param user          用户信息对象
     * @param request       修改邮箱时发送解绑邮件
     * @return
     */
    @RequestMapping("/userEdit")
    public String edit(Model model
            , User user
            ,HttpServletRequest request){
        // 数据库里的用户信息
        User dbUser = userDao.getUserInfoById(user.getId());
        // 判断邮箱信息是否修改
        if(!user.getMail().equals(dbUser.getMail())){
            if(userService.checkMail(user.getMail())){
                model.addAttribute("user", user);
                model.addAttribute("mail", "该邮箱已被占用");
                return "user/editInfo";
            }
            else{
                Mail mail = new Mail();
                mail.setMail_md5(MD5Util.encodePwd(dbUser.getMail() + System.currentTimeMillis()));
                mail.setUser_id(dbUser.getId());
                mail.setExpired(String.valueOf(System.currentTimeMillis() + 1800 * 1000));
                mail.setContent("0");
                // 服务器IP地址加端口号，例：127.0.0.1:8080
                String url = request.getServerName() + ":" + request.getServerPort();
                // 发送邮件信息设置
                Context context = new Context();
                context.setVariable("id", dbUser.getId());
                context.setVariable("url", "http://" + url + "/action/" + mail.getMail_md5());
                if(dbUser.getMail() == null || dbUser.getMail().equals("null")){
                    // 生成邮箱绑定邮件信息
                    mail.setAction("bound");
                    mail.setMail(user.getMail());
                    // 添加信息到数据库
                    mailDao.createMail(mail);
                    // 设置邮件信息并发送
                    String content = templateEngine.process("mail/mailBound", context);
                    mailClient.sendMail(user.getMail(), "hadoop网盘绑定邮件", content);
                    model.addAttribute("code", "boundMail");
                    return "waitClose";
                }
                else {
                    // 生成解绑邮件信息
                    mail.setAction("unbound");
                    mail.setMail(dbUser.getMail());
                    // 添加信息到数据库
                    mailDao.createMail(mail);
                    // 设置邮件信息并发送
                    String content = templateEngine.process("mail/mailUnbound", context);
                    mailClient.sendMail(dbUser.getMail(), "hadoop网盘解绑邮件", content);
                    model.addAttribute("code", "unboundMail");
                    return "waitClose";
                }
            }
        }
        if(user.getDate() == null)
            user.setDate("");
        if(user.getPhone() == null)
            user.setPhone("");
        if(user.getAddress() == null)
            user.setAddress("");
        user.setInstitute(Constants.INSTITUTE[Integer.valueOf(user.getInstitute())]);
        user.setPwd(dbUser.getPwd());
        userDao.userInfo(user);
        if(dbUser.getTotalSpace().equals(Constants.DEFAULT_TOTAL_SPACE)){
            // 原空间为2GB，删除信息，空间减小为500MB
            if(!userService.checkUserInfo(user)){
                userDao.addUserTotalSpace(user.getId(), Constants.SMALL_TOTAL_SPACE);
                // 初始化用户已用空间、剩余空间大小
                userService.calSpace(user.getId());
            }
        } else if(dbUser.getTotalSpace().equals(Constants.SMALL_TOTAL_SPACE)){
            // 原空间为500MB，补全信息，空间增加为2GB
            if(userService.checkUserInfo(user)) {
                userDao.addUserTotalSpace(user.getId(), Constants.DEFAULT_TOTAL_SPACE);
                // 初始化用户已用空间、剩余空间大小
                userService.calSpace(user.getId());
            }
        }
        return "close";
    }

    /**
     * 跳转到忘记密码页面
     * @return
     */
    @RequestMapping("/user/forgot")
    public String toForgot() {
        return "user/forgot";
    }

    /**
     * 忘记密码
     * @param model
     * @param user      用户对象信息
     * @param request   忘记密码邮件
     * @return
     */
    @RequestMapping("/userForgot")
    public String forgot(Model model, User user, HttpServletRequest request) {
        if (!userService.checkId(user.getId())) {
            model.addAttribute("forgotResult", "账号不存在。");
            return "user/forgot";
        }
        User dbUser = userDao.getUserInfoById(user.getId());
        if(!dbUser.getName().equals(user.getName())){
            model.addAttribute("forgotResult", "姓名不正确。");
            return "user/forgot";
        }
        else if(!dbUser.getMail().equals(user.getMail())){
            model.addAttribute("forgotResult", "邮箱不正确。");
            return "user/forgot";
        }
        // 生成激活邮件信息
        Mail mail = new Mail();
        mail.setMail_md5(MD5Util.encodePwd(user.getMail() + System.currentTimeMillis()));
        user.setMail(user.getMail());
        mail.setUser_id(user.getId());
        mail.setAction("forget");
        mail.setExpired(String.valueOf(System.currentTimeMillis() + 3600 * 1000));
        mail.setContent("0");
        mailDao.createMail(mail);
        // 服务器IP地址加端口号，例：127.0.0.1:8080
        String url = request.getServerName() + ":" + request.getServerPort();
        // 发送邮件
        Context context = new Context();
        context.setVariable("id",mail.getUser_id());
        context.setVariable("url","http://" + url + "/action/" + mail.getMail_md5());
        String content = templateEngine.process("mail/mailForget", context);

        mailClient.sendMail(user.getMail(), "hadoop网盘修改密码邮件", content);
        model.addAttribute("code", "forgotMail");
        return "waitClose";
    }
}
