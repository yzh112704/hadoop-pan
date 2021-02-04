package com.example.d.service;

import com.example.d.entity.LoginTicket;
import com.example.d.entity.User;

import java.util.Map;

public interface UserService {
    // 检查ID是否已经被占用
    public boolean checkId(String id);
    // 检查邮箱是否已被占用
    public boolean checkMail(String mail);
    // 添加用户信息到数据库（密码加密）
    public void addUser(User user);
    // 检查用户信息是否完整
    public boolean checkUserInfo(User user);
    // 登录操作
    public Map<String, Object> login(String userId, String password, int expiredSeconds);
    // 初始化用户根目录文件夹及信息
    public void initDir(User user);
    // 计算用户空间信息
    public void calSpace(String userId);
    // 退出登录
    public void logout(String ticket);
    // 查找登录凭证
    public LoginTicket fineLoginTicket(String ticket);
}
