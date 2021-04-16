package com.pan.hadoop;

import com.pan.hadoop.dao.DirDao;
import com.pan.hadoop.dao.FileDao;
import com.pan.hadoop.dao.UserDao;
import com.pan.hadoop.entity.Dir;
import com.pan.hadoop.entity.File;
import com.pan.hadoop.entity.User;
import com.pan.hadoop.service.impl.FileServiceImpl;
import com.pan.hadoop.service.impl.UserServiceImpl;
import com.pan.hadoop.util.Constants;
import com.pan.hadoop.util.FilesUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserTest {
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
    private UserServiceImpl userService;
    @Autowired
    private FileServiceImpl fileService;

    // 添加用户信息
    @Test
    public void addUserTest(){
        String userId = "17031110106";
        // 添加信息
        user.setId(userId);
        user.setPwd("123456");
        user.setName("杨泽豪");
        user.setSex("男");
        user.setInstitute("安阳工学院");
        user.setGrade("2017");
        user.setMajor("网络工程");
        user.setDate("1998-11-05");
//        user.setPhone("18737275671");
//        user.setAddress("河南安阳");
        user.setStatus("true");
        user.setMail("1127042356@qq.com");
        // 向数据库中添加用户信息
        userService.addUser(user);
        // 初始化用户根目录文件夹信息
        userService.initDir(user);

        if(userService.checkUserInfo(user))
            user.setTotalSpace(Constants.DEFAULT_TOTAL_SPACE);
        else
            user.setTotalSpace(Constants.SMALL_TOTAL_SPACE);
        userDao.addUserTotalSpace(user.getId(), user.getTotalSpace());
        userService.calSpace(userId);
        // 查看添加的用户信息
        user = userDao.getUserInfoById(userId);
        System.out.println(user.getUserInfo());
        // 查看用户文件夹信息
        user = userDao.getUserDirs(userId);
        // 查看该文件夹详细信息
        dir = dirDao.getDirInfoById(userId);
        System.out.println(dir.getDirInfo());
    }

    @Test
    public void checkUserInfo(){
        String userId = "17031110116";
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
//        user.setAddress("河南安阳");
        user.setMail("1127042356@qq.com");
        // 2147483648  2GB
        if(userService.checkUserInfo(user))
            System.out.println(Constants.DEFAULT_TOTAL_SPACE);
        else
            System.out.println(Constants.SMALL_TOTAL_SPACE);
        // 524288000  500MB
    }

    // 检查Id是否已经被注册
    @Test
    public void checkIdTest(){
        System.out.println(userService.checkId("17031110106"));
        System.out.println(userService.checkId("17031110112"));
    }

    // 计算用户剩余空间
    @Test
    public void getSpaceTest(){
        String userId = "17031110112";

        user = userDao.getUserFilesAndSpace(userId);
        user.setId(userId);
        userService.calSpace(userId);
        System.out.println("已用空间:" + user.getUse_space() + "b");
        System.out.println(FilesUtil.FormatFileSize(Long.valueOf(user.getUse_space())));
        System.out.println("剩余空间：" + user.getSurplus_space() + "b");
        System.out.println(FilesUtil.FormatFileSize(Long.valueOf(user.getSurplus_space())));

        // 查询
        user = userDao.getUserSpace(userId);
        System.out.println("查询结果，已用空间:" + user.getUse_space() + "b");
        System.out.println(FilesUtil.FormatFileSize(Long.valueOf(user.getUse_space())));
        System.out.println("查询结果，剩余空间：" + user.getSurplus_space() + "b");
        System.out.println(FilesUtil.FormatFileSize(Long.valueOf(user.getSurplus_space())));
    }
}
