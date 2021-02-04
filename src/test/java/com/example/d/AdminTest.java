package com.example.d;

import com.example.d.dao.DirDao;
import com.example.d.dao.FileDao;
import com.example.d.dao.UserDao;
import com.example.d.dao.basedao.HbaseDao;
import com.example.d.entity.*;
import com.example.d.service.AdminService;
import com.example.d.service.FileService;
import com.example.d.service.UserService;
import com.example.d.util.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
public class AdminTest {
    @Autowired
    private UserDao userDao;
    @Autowired
    private User user;
    @Autowired
    private FileDao fileDao;
    @Autowired
    private File file;
    @Autowired
    private DirDao dirDao;
    @Autowired
    private Dir dir;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private HbaseDao hbaseDao;

    @Test
    public void addAdmin(){
        Admin admin = new Admin();
        admin.setId("administrator");
        admin.setPhone("123");
        admin.setPwd("123");
        admin.setLevel("admin");
        adminService.addAdmin(admin);
    }
    // 查询所有用户
    @Test
    public void findAllUserTest(){
        List<User> users = adminService.findAllUser();
        for(User user: users){
            System.out.println(user.getUserInfo());
        }
    }
    // 删除用户
    @Test
    public void delUserTest(){
        String userId = "123";
        adminService.delUserAllInfo(userId);
    }
    // 更改用户总空间大小
    @Test
    public void changeUserTotalSpace(){
        String userId = "17031110112";
        userDao.addUserTotalSpace(userId, Constants.LARGE_TOTAL_SPACE);
        userService.calSpace(userId);
    }
    // 修改用户信息
    @Test
    public void changeUserInfo(){
        String userId = "17031110112";
        // 添加信息
        user.setId(userId);
        user.setPwd("123456");
        user.setName("杨泽豪");
        user.setSex("男");
        user.setInstitute("安阳工学院");
        user.setGrade("2017");
        user.setMajor("网络工程");
        user.setDate("1998-11-05");
        user.setPhone("18737275671");
        user.setAddress("河南安阳");
        user.setMail("1127042356@qq.com");
        // 向数据库中添加用户信息
        userService.addUser(user);
    }

    // 查询所有文件
    @Test
    public void findAllFileTest(){
        List<File> files = adminService.findAllFile();
        for(File file: files){
            System.out.println(file.toString());
        }
    }
    // 禁用文件
    @Test
    public void banFile(){
        // 禁用文件id、是否备份、备份更改的新名字
        String banFileMd5 = "jingjingwa";
        String fileId = "2";
        boolean flag = false;
        String newName = "error";

        String md5 = fileDao.getFileMD5ById(fileId).getMd5();
        System.out.println("md5: " + md5);
        adminService.banFile(banFileMd5, md5, flag, newName);
    }

    // 查询所有分享
    @Test
    public void findAllShareTest(){
        List<Share> shares = adminService.findAllShare();
        for(Share share: shares){
            System.out.println(share.toString());
        }
    }
    // 更改用户总空间大小
    @Test
    public void banShare(){
        String share_md5="1cc2cee1c8fcb0e2152ca25860de9643";
        adminService.banShare(share_md5);
    }
}
