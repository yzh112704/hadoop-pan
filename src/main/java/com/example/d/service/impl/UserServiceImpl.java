package com.example.d.service.impl;

import com.example.d.dao.DirDao;
import com.example.d.dao.FileDao;
import com.example.d.dao.MailDao;
import com.example.d.dao.UserDao;
import com.example.d.entity.Dir;
import com.example.d.entity.LoginTicket;
import com.example.d.entity.User;
import com.example.d.service.LoginTicketService;
import com.example.d.service.UserService;
import com.example.d.util.DateUtil;
import com.example.d.util.MD5Util;
import com.example.d.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private MailDao mailDao;
    @Autowired
    private FileDao fileDao;
    @Autowired
    private DirDao dirDao;
    @Autowired
    private LoginTicketService loginTicketService;

    /**
     * 检测id是否已经注册
     * @param id
     * @return
     */
    @Override
    public boolean checkId(String id) {
        User user = userDao.getUserInfoById(id);
        if(user != null){
            if(user.getStatus().equals("true"))
                return true;
            else
                return false;
        }
        else
            return false;
    }

    @Override
    public boolean checkMail(String mail) {
        if(mailDao.getMailUserInfoById(mail) != null)
            return true;
        return false;
    }

    @Override
    public boolean checkUserInfo(User user) {
        String info = user.getUserInfo();
        int count = 0;
        for(String field: info.split("'")){
            if(field.startsWith(",") || field.startsWith("UserInfo{") || field.startsWith("}"))
                continue;
            if(!field.equals("") && !field.equals("null"))
                count++;
        }
        if(count == 11)
            return true;
        return false;
    }

    /**
     * 添加用户或注册用户
     */
    @Override
    public void addUser(User user) {
        user.setPwd(MD5Util.encodePwd(user.getPwd()));
        userDao.userInfo(user);
    }

    /**
     * 用户注册后文件夹初始化
     * @param user
     */
    @Override
    public void initDir(User user) {
        Dir dir = new Dir();
        dir.setDir_id(user.getId());
        dir.setDir_name(user.getId());
        dir.setPath("");
        dir.setParent_id("null");
        dir.setDate(DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
        dir.setIsDir("true");
        user.setDir_id(user.getId());
        // 添加文件夹信息
        userDao.addUserDir(user);
        dirDao.addDirInfo(dir);
    }

    /**
     * 计算用户空间
     */
    @Override
    public void calSpace(String userId) {
        User user = userDao.getUserFilesAndSpace(userId);
        user.setId(userId);

        String[] fileIds = user.getFile_ids();
        Integer totalSize = 0;
        for(String fileId : fileIds){
            if (fileId == null || fileId.equals(""))
                continue;
            Integer count = Integer.valueOf(userDao.getUserFileCount(user.getId(), fileId));
            totalSize += count * Integer.valueOf(fileDao.getFileMD5ById(fileId).getSize());
        }
        user.setUse_space(String.valueOf(totalSize));
        UserUtil.countSurplusSpace(user);
        // 添加用户空间相关数据
        userDao.addUserSpace(user);
    }

    /**
     * 登录判断用户名与密码
     */
    @Override
    public Map<String, Object> login(String id, String pwd, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if(StringUtils.isBlank(id)){
            map.put("middleResult", "账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(pwd)){
            map.put("middleResult", "密码不能为空");
            return map;
        }
        // 验证账号
        User user = userDao.getUserInfoById(id);
        if(user == null){
            map.put("middleResult", "账号不存在");
            return map;
        }
        if(user.getStatus() == null || user.getStatus().equals("false")){
            map.put("middleResult", "账号未激活!");
            return map;
        }
        // 验证密码
        if(!MD5Util.isPwdRight(pwd, user.getPwd())){
            map.put("middleResult", "密码不正确");
            return map;
        }
        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(MD5Util.encodePwd(user.getId() + System.currentTimeMillis()));
        loginTicket.setStatus("true");
        loginTicket.setExpired(String.valueOf(System.currentTimeMillis() + Long.valueOf(expiredSeconds) * 1000));
        loginTicketService.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录
     * @param ticket
     */
    @Override
    public void logout(String ticket) {
        loginTicketService.updateStatus(ticket, "false");
    }

    /**
     * 查找凭证
     * @param ticket
     * @return
     */
    @Override
    public LoginTicket fineLoginTicket(String ticket) {
        return loginTicketService.selectByTicket(ticket);
    }
}