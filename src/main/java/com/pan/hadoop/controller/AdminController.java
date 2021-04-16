package com.pan.hadoop.controller;

import com.pan.hadoop.dao.*;
import com.pan.hadoop.entity.*;
import com.pan.hadoop.service.AdminService;
import com.pan.hadoop.service.LoginTicketService;
import com.pan.hadoop.service.UserService;
import com.pan.hadoop.util.Constants;
import com.pan.hadoop.util.DateUtil;
import com.pan.hadoop.util.FilesUtil;
import com.pan.hadoop.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Controller
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private FileDao fileDao;
    @Autowired
    private AdminDao adminDao;
    @Autowired
    private MailDao mailDao;
    @Autowired
    private AnalysisDao analysisDao;
    @Autowired
    private LoginTicketService loginTicketService;

    /**
     * 跳转到管理员登录页面
     * @return
     */
    @RequestMapping(value = "/adminLoginIndex")
    public String adminIndex(){
        return "admin/adminLogin";
    }

    /**
     * 管理员登录
     * @param model
     * @param adminid           输入的管理员ID
     * @param adminphone        输入的管理员电话
     * @param adminpassword     输入的管理员密码
     * @param response          响应（Cookie）
     * @return
     */
    @RequestMapping("/adminLogin")
    public String adminLogin(Model model, String adminid, String adminphone, String adminpassword, HttpServletResponse response) {
        if(adminid != null && adminphone != null && adminpassword != null) {
            Admin admin = adminDao.getAdminInfoById(adminid);
            if (admin != null && adminid.equals(admin.getId()) && adminphone.equals(admin.getPhone()) && MD5Util.isPwdRight(adminpassword, admin.getPwd())) {
                // 生成登录凭证
                LoginTicket loginTicket = new LoginTicket();
                loginTicket.setUserId(admin.getId());
                loginTicket.setStatus("admin");
                loginTicket.setTicket(MD5Util.encodePwd(admin.getId() + System.currentTimeMillis()));
                loginTicketService.insertLoginTicket(loginTicket);
                // 添加cookie(会话级，关闭浏览器结束)
                Cookie cookie = new Cookie("adminTicket", loginTicket.getTicket());
                cookie.setPath("/");
                cookie.setMaxAge(-1);
                response.addCookie(cookie);
                Analysis analysis = analysisDao.getAnalysisInfoByRowKey("createDate");
                model.addAttribute("admin", admin);
                model.addAttribute("startDate", analysis.getCount());
                model.addAttribute("endDate", DateUtil.longToString("yyyy-MM-dd", System.currentTimeMillis()));
                return "admin/analysis/analysisDay";
            }
        }
        model.addAttribute("adminLoginResult", "登录失败");
        return "admin/adminLogin";
    }

    /**
     * 管理员添加用户
     * @param model
     * @param user              用户对象信息
     * @param adminTicket       管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminAddUser")
    public String adminAddUser(Model model, User user
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {
        if(!adminTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(adminTicket);
            if (loginTicket != null && loginTicket.getStatus().equals("admin")) {
                String adminId = loginTicket.getUserId();
                Admin admin = adminDao.getAdminInfoById(adminId);
                model.addAttribute("admin", admin);
                // 检查用户ID是否已存在
                if(userService.checkId(user.getId())){
                    model.addAttribute("addUser", user);
                    model.addAttribute("message","添加用户失败，用户学号已被占用");
                    model.addAttribute("code", "user");
                    return "admin/addInfo";
                }
                if(!user.getMail().equals("") || user.getMail()==null){
                    if(userService.checkMail(user.getMail())){
                        model.addAttribute("addUser", user);
                        model.addAttribute("message","添加用户失败，邮箱已被占用");
                        model.addAttribute("code", "user");
                        return "admin/addInfo";
                    }
                }
                user.setInstitute(Constants.INSTITUTE[Integer.valueOf(user.getInstitute())]);
                userService.addUser(user);
                userService.initDir(user);
                if(userService.checkUserInfo(user)) {
                    userDao.addUserTotalSpace(user.getId(), Constants.DEFAULT_TOTAL_SPACE);
                }else{
                    userDao.addUserTotalSpace(user.getId(), Constants.SMALL_TOTAL_SPACE);
                }
                userService.calSpace(user.getId());
            }
        }
        return "close";
    }

    /**
     * 批量添加用户
     * @param users
     * @return
     */
    @RequestMapping(path = "/addUsers", method = RequestMethod.POST)
    @ResponseBody
    public String adminAddUsers(@RequestParam("users") String users) {
        // 添加结果
        String output = "<br>";
        for(String line: users.split("~")) {
            if(line == null || line.equals(""))
                continue;
            User user = new User();
            // 各元素
            String[] fields = line.split("_");
            // 学号不为空
            if (!fields[0].equals(" ")) {
                if (userService.checkId(fields[0])) {
                    output += fields[0] + " 已被注册，添加用户失败！！<br>";
                    continue;
                }
                user.setId(fields[0]);
            } else {
                output += "添加用户失败，学号为空！！<br>";
                continue;
            }
            // 名字不为空
            if (!fields[1].equals(" ")) {
                user.setName(fields[1]);
            } else {
                output += "添加用户" + fields[0] + "失败，名字不能为空！！<br>";
                continue;
            }
            // 性别不为空
            if (!fields[2].equals(" ")) {
                user.setSex(fields[2]);
            }
            // 专业不为空
            if (!fields[3].equals(" ")) {
                user.setInstitute(fields[3]);
            }
            // 年级不为空
            if (!fields[4].equals(" ")) {
                user.setGrade(fields[4]);
            }
            // 专业不为空
            if (!fields[5].equals(" ")) {
                user.setMajor(fields[5]);
            }
            // 生日不为空
            if (!fields[6].equals(" ")) {
                user.setDate(fields[6]);
            }
            // 电话不为空
            if (!fields[7].equals(" ")) {
                user.setPhone(fields[7]);
            }
            // 邮箱不为空
            if (!fields[8].equals(" ")) {
                if (userService.checkMail(fields[8])) {
                    output += fields[8] + " 邮箱已被注册，添加用户" + fields[0] + "失败，请更换新的邮箱！！<br>";
                    continue;
                }
                user.setMail(fields[8]);
            }
            // 住址不为空
            if (!fields[9].equals(" ")) {
                user.setAddress(fields[9]);
            }
            // 状态
            if (fields[10].equals("激活")) {
                fields[10] = "true";
            } else {
                fields[10] = "false";
            }
            user.setStatus(fields[10]);
            // 设置用户初始密码
            user.setPwd("888888");
            // 添加用户信息
            userService.addUser(user);
            // 初始化用户文件夹
            userService.initDir(user);
            // 邮箱与用户绑定（用户ID与邮箱不为空时）
            if(!fields[0].equals(" ") && !fields[8].equals(" ")){
                Mail_User mail_user = new Mail_User();
                mail_user.setMail(fields[8]);
                mail_user.setUserId(fields[0]);
                mailDao.createMailUser(mail_user);
            }
            // 设置用户的空间大小
            if(fields[11].equals("5GB")){
                userDao.addUserTotalSpace(user.getId(), Constants.MIDDLE_TOTAL_SPACE);
            }else if(fields[11].equals("20GB")){
                userDao.addUserTotalSpace(user.getId(), Constants.LARGE_TOTAL_SPACE);
            }else{
                if(userService.checkUserInfo(user)) {
                    userDao.addUserTotalSpace(user.getId(), Constants.DEFAULT_TOTAL_SPACE);
                }else{
                    userDao.addUserTotalSpace(user.getId(), Constants.SMALL_TOTAL_SPACE);
                }
            }
            userService.calSpace(user.getId());
        }
        return output;
    }

    /**
     * 管理员添加管理员
     * @param model
     * @param newAdmin          要添加的管理员对象信息
     * @param adminTicket       管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminAddAdmin")
    public String adminAddAdmin(Model model,Admin newAdmin
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {
        if(!adminTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(adminTicket);
            if (loginTicket != null && loginTicket.getStatus().equals("admin")) {
                String adminId = loginTicket.getUserId();
                Admin admin = adminDao.getAdminInfoById(adminId);
                model.addAttribute("admin", admin);
                if(adminService.checkId(newAdmin.getId())){
                    model.addAttribute("addAdmin", newAdmin);
                    model.addAttribute("message","添加管理员失败，账号已被占用");
                    model.addAttribute("code", "admin");
                    return "admin/addInfo";
                }
                adminService.addAdmin(newAdmin);
            }
        }
        return "close";
    }

    /**
     * 显示所有用户列表或文件列表
     * @param model
     * @param type
     * @param adminTicket       管理员Cookie凭证
     * @return
     */
    @RequestMapping("/allList")
    public String allList(Model model
            , @RequestParam(value = "type", defaultValue = "null") String type
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {
        if(!adminTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(adminTicket);
            if (loginTicket != null && loginTicket.getStatus().equals("admin")) {
                String adminId = loginTicket.getUserId();
                Admin admin = adminDao.getAdminInfoById(adminId);
                model.addAttribute("admin", admin);
                model.addAttribute("type", type);

                if (type.equals("user")) {
                    List<User> users = adminService.findAllUser();
                    model.addAttribute("users", users);
                    return "admin/list/userList";
                } else if (type.equals("admin")) {
                    List<Admin> admins = adminService.findAllAdmin(admin.getLevel());
                    model.addAttribute("admins", admins);
                    return "admin/admin";
                } else if (type.equals("file")) {
                    return "admin/list/fileList";
                } else if (type.equals("banFile")) {
                    return "admin/list/banFileList";
                } else if (type.equals("share")) {
                    return "admin/list/shareList";
                }
            }
        }
        model.addAttribute("adminLoginResult", "违规操作！！");
        return "admin/adminLogin";
    }

    /**
     * 返回用户列表数据
     * @return List<User>
     */
    @RequestMapping(path = "/allListUsers", method = RequestMethod.POST)
    @ResponseBody
    public List<User> allListUsers() {
        List<User> users = adminService.findAllUser();
        return users;
    }

    /**
     * 返回文件列表数据
     * @return List<File>
     */
    @RequestMapping(path = "/allListFiles", method = RequestMethod.POST)
    @ResponseBody
    public List<File> allListFiles() {
        List<File> files = adminService.findAllFile();
        return files;
    }

    /**
     * 返回被禁用文件列表数据
     * @return List<File>
     */
    @RequestMapping(path = "/allListBanFiles", method = RequestMethod.POST)
    @ResponseBody
    public List<File> allListBanFiles() {
        List<File> files = adminService.findAllBanFile();
        return files;
    }

    /**
     * 返回分享链接列表
     * @return List<Share>
     */
    @RequestMapping(path = "/allListShares", method = RequestMethod.POST)
    @ResponseBody
    public List<Share> allListShares() {
        List<Share> shares = adminService.findAllShare();
        return shares;
    }

    /**
     * 用户信息修改
     * @param model
     * @param user          用户新信息
     * @param space         空间大小
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminEditUser")
    public String adminEditUser(Model model, User user, String space
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket){
        if(!adminTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(adminTicket);
            if (loginTicket != null && loginTicket.getStatus().equals("admin")) {
                String adminId = loginTicket.getUserId();
                Admin admin = adminDao.getAdminInfoById(adminId);
                // 数据库里的用户信息
                User dbUser = userDao.getUserInfoById(user.getId());
                if (user.getDate() == null)
                    user.setDate("");
                if (user.getPhone() == null)
                    user.setPhone("");
                if (user.getAddress() == null)
                    user.setAddress("");
                user.setInstitute(Constants.INSTITUTE[Integer.valueOf(user.getInstitute())]);
                if (user.getPwd() == "" || user.getPwd() == null){
                    user.setPwd(dbUser.getPwd());
                    userDao.userInfo(user);
                }
                else
                    userService.addUser(user);
                if(space.equals("default")){
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
                    } else{
                        if(userService.checkUserInfo(user)) {
                            userDao.addUserTotalSpace(user.getId(), Constants.DEFAULT_TOTAL_SPACE);
                            // 初始化用户已用空间、剩余空间大小
                            userService.calSpace(user.getId());
                        }else{
                            userDao.addUserTotalSpace(user.getId(), Constants.SMALL_TOTAL_SPACE);
                            // 初始化用户已用空间、剩余空间大小
                            userService.calSpace(user.getId());
                        }
                    }
                }else if(space.equals("middle")){
                    if(!dbUser.getTotalSpace().equals(Constants.MIDDLE_TOTAL_SPACE)) {
                        userDao.addUserTotalSpace(user.getId(), Constants.MIDDLE_TOTAL_SPACE);
                        // 初始化用户已用空间、剩余空间大小
                        userService.calSpace(user.getId());
                    }
                }else if(space.equals("large")){
                    if(!dbUser.getTotalSpace().equals(Constants.LARGE_TOTAL_SPACE)) {
                        userDao.addUserTotalSpace(user.getId(), Constants.LARGE_TOTAL_SPACE);
                        // 初始化用户已用空间、剩余空间大小
                        userService.calSpace(user.getId());
                    }
                }
                List<User> users = adminService.findAllUser();
                model.addAttribute("users", users);
                model.addAttribute("admin", admin);
                return "close";
            }
        }
        model.addAttribute("adminLoginResult", "违规操作！！");
        return "admin/adminLogin";
    }

    /**
     * 管理员信息修改
     * @param model
     * @param editAdmin     新管理员信息
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminEditAdmin")
    public String adminEditAdmin(Model model, Admin editAdmin, @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket){
        if(!adminTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(adminTicket);
            if (loginTicket != null && loginTicket.getStatus().equals("admin")) {
                String adminId = loginTicket.getUserId();
                Admin admin = adminDao.getAdminInfoById(adminId);
                if(editAdmin.getPwd().equals("") || editAdmin.getPwd() == null){
                    Admin dbAdmin = adminDao.getAdminInfoById(editAdmin.getId());
                    editAdmin.setPwd(dbAdmin.getPwd());
                }
                adminService.addAdmin(editAdmin);
                return "close";
            }
        }
        model.addAttribute("adminLoginResult", "违规操作！！");
        return "admin/adminLogin";
    }

    /**
     * 管理员下载文件
     * @param request   获取请求的路径
     * @param md5     要下载的文件md5
     * @return String url
     */
    @RequestMapping(path = "/AdminDownloadFile", method = RequestMethod.POST)
    @ResponseBody
    public String AdminDownloadFile(HttpServletRequest request
            , @RequestParam(value = "md5", defaultValue = "null") String md5){
        String downloadPath = request.getSession().getServletContext().getRealPath("/downloadFile/");
        java.io.File localDir = new java.io.File(downloadPath);
        if (!localDir.exists()) {
            localDir.mkdir();
        }
        File file = fileDao.getFileInfoByRowkey(md5);
        String name = file.getFile_name();
        try{
            InputStream in = new FileInputStream(downloadPath + name);
            if(file.getSize() != in.available()){
                fileDao.downloadFile(name , md5, downloadPath);
            }
        }catch (IOException e){
            fileDao.downloadFile(name , md5, downloadPath);
        }
        String url = "downloadFile/" + name;
        return url;
    }

    /**
     * 显示分析数据
     * @param model
     * @param type          要分析的类型
     * @param top           个数
     * @param startDate     起始日期 yyyy-MM-dd
     * @param endDate       结束日期 yyyy-MM-dd
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/analysis")
    public String analysis(Model model
            , @RequestParam(value = "type", defaultValue = "null") String type
            , @RequestParam(value = "top", defaultValue = "3") String top
            , @RequestParam(value = "startDate", defaultValue = "null") String startDate
            , @RequestParam(value = "endDate", defaultValue = "null") String endDate
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {
        if(!adminTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(adminTicket);
            if (loginTicket != null && loginTicket.getStatus().equals("admin")) {
                String adminId = loginTicket.getUserId();
                Admin admin = adminDao.getAdminInfoById(adminId);
                model.addAttribute("admin", admin);
                List<String> list = null;
                if(endDate.equals("null"))
                    endDate = DateUtil.longToString("yyyy-MM-dd", System.currentTimeMillis());
                if(startDate.equals("null")){
                    Analysis analysis = analysisDao.getAnalysisInfoByRowKey("createDate");
                    startDate = analysis.getCount();
                }
                model.addAttribute("startDate", startDate);
                model.addAttribute("endDate", endDate);
                model.addAttribute("top", top);
                if(type.equals("top")){
                    List<String> lines = new ArrayList<>();
                    List<String> fileLines = new ArrayList<>();
                    List<String> userLines = new ArrayList<>();

                    list = adminService.analysisTopLineByDate("file_upload_", Integer.valueOf(top), startDate, endDate);
                    fileLines.addAll(list);
                    list = adminService.analysisTopLineByDate("file_download_", Integer.valueOf(top), startDate, endDate);
                    fileLines.addAll(list);

                    list = adminService.analysisTopLineByDate("user_upload_", Integer.valueOf(top), startDate, endDate);
                    userLines.addAll(list);
                    list = adminService.analysisTopLineByDate("user_download_", Integer.valueOf(top), startDate, endDate);
                    userLines.addAll(list);
                    list = adminService.analysisTopLineByDate("user_share_",Integer.valueOf(top), startDate, endDate);
                    userLines.addAll(list);

                    if(fileLines.size() <= userLines.size()){
                        for(int i=0; i< userLines.size(); i++){
                            if(i >= fileLines.size())
                                lines.add(" : : : :" + userLines.get(i));
                            else
                                lines.add(fileLines.get(i) + ":" + userLines.get(i));
                        }
                    }
                    else{
                        for(int i=0; i< fileLines.size(); i++){
                            if(i >= userLines.size())
                                lines.add(fileLines.get(i) + ": : : : ");
                            else
                                lines.add(fileLines.get(i) + ":" + userLines.get(i));
                        }
                    }
                    model.addAttribute("lines", lines);
                    return "admin/analysis/analysisTop";
                }else if(type.equals("share")){
                    list = adminService.analysisTopLineByDate("share_open_",Integer.valueOf(top), startDate, endDate);
                    model.addAttribute("shareList", list);
                    return "admin/analysis/analysisShare";
                }else if(type.equals("day")){
                    return "admin/analysis/analysisDay";
                }
                return "admin/analysis/analysisDay";
            }
        }
        model.addAttribute("adminLoginResult", "违规操作！！");
        return "admin/adminLogin";
    }

    /**
     * 按天分析数据（每日上传量、下载量、分享量）
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(path = "/analysisByDay", method = RequestMethod.POST)
    @ResponseBody
    public List<List<List<String>>> analysisByDay(@RequestParam(value = "startDate", defaultValue = "null") String startDate
            , @RequestParam(value = "endDate", defaultValue = "null") String endDate) {
        // [[['date','count'],[]],[]]
        if(endDate.equals("null"))
            endDate = DateUtil.longToString("yyyy-MM-dd", System.currentTimeMillis());
        if(startDate.equals("null")){
            Analysis analysis = analysisDao.getAnalysisInfoByRowKey("createDate");
            startDate = analysis.getCount();
        }
        List<List<List<String>>> list = new ArrayList<>();
        list.add(adminService.analysisDay("user_upload_", startDate, endDate));
        list.add(adminService.analysisDay("user_download_", startDate, endDate));
        list.add(adminService.analysisDay("user_share_", startDate, endDate));

        return list;
    }

    /**
     * 用户文件排行榜（用户 上传、下载、分享数，文件 被上传、被下载数）
     * @param startDate     起始日期
     * @param endDate       结束日期
     * @param top           个数
     * @return
     */
    @RequestMapping(path = "/analysisTop", method = RequestMethod.POST)
    @ResponseBody
    public List<List<List<String>>> analysisTop(@RequestParam(value = "startDate", defaultValue = "null") String startDate
            , @RequestParam(value = "endDate", defaultValue = "null") String endDate
            , @RequestParam(value = "top", defaultValue = "3") String top) {
        // [[['count', 'top', 'id'],[]],[]]
        if(endDate.equals("null"))
            endDate = DateUtil.longToString("yyyy-MM-dd", System.currentTimeMillis());
        if(startDate.equals("null")){
            Analysis analysis = analysisDao.getAnalysisInfoByRowKey("createDate");
            startDate = analysis.getCount();
        }
        List<List<List<String>>> list = new ArrayList<>();
        list.add(adminService.analysisTopDataByDate("user_upload_", Integer.valueOf(top), startDate, endDate));
        list.add(adminService.analysisTopDataByDate("user_download_", Integer.valueOf(top), startDate, endDate));
        list.add(adminService.analysisTopDataByDate("user_share_", Integer.valueOf(top), startDate, endDate));
        list.add(adminService.analysisTopDataByDate("file_upload_", Integer.valueOf(top), startDate, endDate));
        list.add(adminService.analysisTopDataByDate("file_download_", Integer.valueOf(top), startDate, endDate));
        return list;
    }

    /**
     * 分享被访问数
     * @param startDate     起始日期
     * @param endDate       结束日期
     * @param top           个数
     * @return
     */
    @RequestMapping(path = "/analysisShareTop", method = RequestMethod.POST)
    @ResponseBody
    public List<List<String>> analysisShareTop(@RequestParam(value = "startDate", defaultValue = "null") String startDate
            , @RequestParam(value = "endDate", defaultValue = "null") String endDate
            , @RequestParam(value = "top", defaultValue = "3") String top) {
        // [[['count', 'id', 'md5'],[]],[]]
        if(endDate.equals("null"))
            endDate = DateUtil.longToString("yyyy-MM-dd", System.currentTimeMillis());
        if(startDate.equals("null")){
            Analysis analysis = analysisDao.getAnalysisInfoByRowKey("createDate");
            startDate = analysis.getCount();
        }
        List<List<String>> list = adminService.analysisTopDataByDate("share_open_", Integer.valueOf(top), startDate, endDate);
        return list;
    }

    /**
     * 禁用文件
     * @param type      类型（文件、分享链接）
     * @param flag      文件是否保存
     * @param name      新文件名
     * @param id        文件ID或链接MD5
     * @return
     */
    @RequestMapping("/ban")
    @ResponseBody
    public String ban(@RequestParam(value = "type", defaultValue = "null") String type
            , @RequestParam(value = "flag", defaultValue = "null") String flag
            , @RequestParam(value = "name", defaultValue = "null") String name
            , @RequestParam(value = "id", defaultValue = "null") String id ){
        if(type.equals("file")){
            String fileType = fileDao.getFileInfoByRowkey(id).getFile_type();
            if(Constants.IMG_SUFFIX.contains(fileType)){
                fileType = "img";
            } else if(Constants.VIDEO_SUFFIX.contains(fileType)){
                fileType = "video";
            } else{
                fileType = "document";
            }
            adminService.banFile(fileType , id, Boolean.valueOf(flag), name);
        } else if(type.equals("share")){
            adminService.banShare(id);
        }
        return "1";
    }

    /**
     * 显示详细信息
     * @param model
     * @param type          类型（用户或管理员）
     * @param id            ID
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/showInfo")
    public String showInfo(Model model
            , @RequestParam(value = "type", defaultValue = "null") String type
            , @RequestParam(value = "id", defaultValue = "null") String id
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {
        if(!adminTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(adminTicket);
            if (loginTicket != null && loginTicket.getStatus().equals("admin")) {
                String adminId = loginTicket.getUserId();
                Admin admin = adminDao.getAdminInfoById(adminId);
                model.addAttribute("admin", admin);
            if(type.equals("user")){
                    User user = userDao.getUserInfoById(id);
                    user.setTotalSpace(FilesUtil.FormatFileSize(Long.valueOf(user.getTotalSpace())));
                    model.addAttribute("userInfo", user);
                    return "admin/info/userInfo";
                }else if(type.equals("file")){
                    File file = fileDao.getFileInfoByRowkey(id);
                    if(file.getCurrentChunk() == null || file.getTotalChunks() == null)
                        file.setTotalChunks("上传完成");
                    else{
                        if(!file.getCurrentChunk().equals(file.getTotalChunks()))
                            file.setTotalChunks("未上传完成");
                        else
                            file.setTotalChunks("上传完成");
                    }
                    file.setCurrentChunk(FilesUtil.FormatFileSize(file.getSize()));
                    file.setFile_type(DateUtil.longToString("yyyy-MM-dd hh:mm:ss", file.getDate()));
                    model.addAttribute("fileInfo", file);
                    return "admin/info/fileInfo";
                }else if(type.equals("fileId")){
                    String fileMd5 = fileDao.getFileMD5ById(id).getMd5();
                    File file = fileDao.getFileInfoByRowkey(fileMd5);
                    if(file.getCurrentChunk() == null || file.getTotalChunks() == null)
                        file.setTotalChunks("上传完成");
                    else{
                        if(!file.getCurrentChunk().equals(file.getTotalChunks()))
                            file.setTotalChunks("未上传完成");
                        else
                            file.setTotalChunks("上传完成");
                    }
                    file.setCurrentChunk(FilesUtil.FormatFileSize(file.getSize()));
                    file.setFile_type(DateUtil.longToString("yyyy-MM-dd hh:mm:ss", file.getDate()));
                    model.addAttribute("fileInfo", file);
                    return "admin/info/fileInfo";
                }
            }
        }
        model.addAttribute("adminLoginResult", "违规操作！！");
        return "admin/adminLogin";
    }

    /**
     * 管理员操作（添加或修改信息）
     * @param model
     * @param type          类型（用户或管理员）
     * @param id            ID
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminChange")
    public String adminChange(Model model
            , @RequestParam(value = "type", defaultValue = "null") String type
            , @RequestParam(value = "id", defaultValue = "null") String id
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {
        if(!adminTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(adminTicket);
            if (loginTicket != null && loginTicket.getStatus().equals("admin")) {
                String adminId = loginTicket.getUserId();
                Admin admin = adminDao.getAdminInfoById(adminId);
                model.addAttribute("admin", admin);
                if(type.equals("user")){
                    User user = userDao.getUserInfoById(id);
                    if(user.getInstitute() != null){
                        for(int i=0; i < Constants.INSTITUTE.length; i++){
                            if(user.getInstitute().equals(Constants.INSTITUTE[i]))
                                user.setInstitute(String.valueOf(i));
                        }
                    }
                    if(user.getTotalSpace().equals(Constants.DEFAULT_TOTAL_SPACE) || user.getTotalSpace().equals(Constants.SMALL_TOTAL_SPACE))
                        user.setTotalSpace("default");
                    else if(user.getTotalSpace().equals(Constants.MIDDLE_TOTAL_SPACE))
                        user.setTotalSpace("middle");
                    else if(user.getTotalSpace().equals(Constants.LARGE_TOTAL_SPACE))
                        user.setTotalSpace("large");
                    model.addAttribute("editUser", user);
                    return "admin/edit";
                }else if(type.equals("admin")){
                    if(id.equals("null")){
                        model.addAttribute("editAdmin", admin);
                        return "admin/edit";
                    }
                    Admin admin1 = adminDao.getAdminInfoById(id);
                    if(admin1.getLevel().equals(admin.getLevel()) || admin1.getLevel().equals("admin")){
                        model.addAttribute("code", "修改失败，权限不足。请联系更高级的管理员修改。");
                        return "admin/admin";
                    }
                    model.addAttribute("editAdmin", admin1);
                    return "admin/edit";
                }
                else if(type.equals("addUser")){
                    model.addAttribute("code", "user");
                    return "admin/addInfo";
                }
                else if(type.equals("addAdmin")){
                    model.addAttribute("code", "admin");
                    return "admin/addInfo";
                }
            }
        }
        model.addAttribute("adminLoginResult", "违规操作！！");
        return "admin/adminLogin";
    }

    /**
     * 管理员删除(用户或管理员)
     * @param model
     * @param type          类型（用户或管理员）
     * @param id            ID
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminDel")
    public String adminDel(Model model
            , @RequestParam(value = "type", defaultValue = "null") String type
            , @RequestParam(value = "id", defaultValue = "null") String id
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {
        if(!adminTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(adminTicket);
            if (loginTicket != null && loginTicket.getStatus().equals("admin")) {
                String adminId = loginTicket.getUserId();
                Admin admin = adminDao.getAdminInfoById(adminId);
                model.addAttribute("admin", admin);
                if(type.equals("user")){
                    adminService.delUserAllInfo(id);
                    model.addAttribute("code", "删除用户" + id + "成功！");
                }else if(type.equals("admin")){
                    Admin admin1 = adminDao.getAdminInfoById(id);
                    if(admin1.getLevel().equals(admin.getLevel()) || admin1.getLevel().equals("admin")){
                        model.addAttribute("code", "删除失败，权限不足。请联系更高级的管理员修改。");
                    }else {
                        adminDao.delAdminInfoById(id);
                        model.addAttribute("code", "删除管理员" + id + "成功！");
                    }
                }
                return "admin/admin";
            }
        }
        model.addAttribute("adminLoginResult", "违规操作！！");
        return "admin/adminLogin";
    }

    /**
     * 管理员批量删除用户
     * @param model
     * @param type          类型（用户或管理员）
     * @param ids            IDs
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminDelUsers")
    public String adminDelUsers(Model model
            , @RequestParam(value = "type", defaultValue = "null") String type
            , @RequestParam(value = "ids", defaultValue = "null") String ids
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {
        if(!adminTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(adminTicket);
            if (loginTicket != null && loginTicket.getStatus().equals("admin")) {
                String adminId = loginTicket.getUserId();
                Admin admin = adminDao.getAdminInfoById(adminId);
                model.addAttribute("admin", admin);
                if(type.equals("user")){
                    String output = "";
                    for(String id: ids.split(" ")){
                        adminService.delUserAllInfo(id);
                        output += "删除用户" + id + "成功！*";
                    }
                    model.addAttribute("code", output);
                }
                return "admin/admin";
            }
        }
        model.addAttribute("adminLoginResult", "违规操作！！");
        return "admin/adminLogin";
    }
}
