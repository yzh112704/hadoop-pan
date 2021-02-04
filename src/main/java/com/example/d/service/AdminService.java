package com.example.d.service;

import com.example.d.entity.*;

import java.util.List;

public interface AdminService {
    // 检查ID是否已经被占用
    public boolean checkId(String id);
    // 添加管理员
    public void addAdmin(Admin admin);
    // 分析数据
    public List<Analysis> analysisData(String choice);
    public List<Analysis> analysisDataByDate(String choice, String startDate, String endDate);
    public List<List<String>> analysisDay(String choice, String startDate, String endDate);
    public List<List<String>> analysisTopDataByDate(String choice, int top, String startDate, String endDate);
    public List<String> analysisTopLineByDate(String choice, int top, String startDate, String endDate);
    // 查找所有用户
    public List<User> findAllUser();
    // 查找管理员信息（比自己低一级别的）
    public List<Admin> findAllAdmin(String level);
    // 查找所有文件
    public List<File> findAllFile();
    // 查找所有被禁用的文件
    public List<File> findAllBanFile();
    // 查找所有分享
    public List<Share> findAllShare();
    // 删除用户及其关联的信息
    public void delUserAllInfo(String userId);
    // 和谐资源,资源是否备份,新名字
    public void banFile(String banFileId, String destFileId, boolean flag, String newName);
    // 和谐分享链接
    public void banShare(String shareMd5);
}
