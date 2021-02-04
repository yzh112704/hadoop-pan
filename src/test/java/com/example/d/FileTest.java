package com.example.d;


import com.example.d.dao.DirDao;
import com.example.d.dao.FileDao;
import com.example.d.dao.UserDao;
import com.example.d.entity.Dir;
import com.example.d.entity.File;
import com.example.d.entity.User;
import com.example.d.service.impl.FileServiceImpl;
import com.example.d.service.impl.UserServiceImpl;
import com.example.d.util.FilesUtil;
import com.example.d.util.MD5Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public class FileTest {
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

    @Test
    public void test(){
        FilesUtil.compareSize("35.59KB", "38.41KB");
    }

    @Test
    public void openStaticFileTest() throws IOException{
        String pcPath = "static/ban/document";
        String data = "";
        ClassPathResource cpr = new ClassPathResource(pcPath);
        try {
            byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            data = new String(bdata, StandardCharsets.UTF_8);
            System.out.println(data);
        } catch (IOException e) {
            System.out.println("IOException!!");
        }

    }
    /**
     * 测试上传文件
     * @throws IOException
     */
    @Test
    public void uploadFileTest() throws IOException {
        // 传入信息：上传路径，用户ID，文件夹ID（父id）
        String pcPath = "F:\\hadoop网盘\\demo1\\src\\main\\resources\\static\\ban\\document";
//        String parentId = "17031110112";

        // 安装文件名求出文件名
        String[] fields = pcPath.split("\\\\");
        String fileName = fields[fields.length - 1];
        // 获取文件Md5值
//        String md5 = "ban1";
//        String md5 = MD5Util.getFileMD5(new FileInputStream(pcPath));
        // 上传
//        fileService.uploadFile(new FileInputStream(pcPath), fileName, fileName);
    }
    @Test
    public void uploadFileInfoTest() throws IOException {
        String userId = "17031110112";
        String parentId = userId;

        String pcPath = "F:\\Akka2.txt";
        String md5 = MD5Util.getFileMD5(new FileInputStream(pcPath));

        String code = fileService.uploadFileInfo(userId, parentId, md5);
        userService.calSpace(userId);
        if(code.equals("success"))
            System.out.println("创建成功");
        else if(code.equals("repeat"))
            System.out.println("文件已存在");
        else if(code.equals("space"))
            System.out.println("空间不足");
    }

    /**
     * 下载文件 未使用（只能下载到本地目录）
     */
    @Test
    public void downloadFileTest() {
        String downloadPath = "F://home/";
        String dirId = "17031110112";
        String userId = "17031110112";

        user = userDao.getUserDirs(userId);
        dir = dirDao.getDirInfoById(dirId);
        if (dir.getIsDir().equals("false"))
            fileService.downloadFile(dir, downloadPath);
        else{
            fileService.downloadDir(user, dir, downloadPath, downloadPath);
        }
    }

    /**
     * 删除文件
     */
    @Test
    public void delFileTest() {
        String dirId = "17031110112";
        String userId = "17031110112";

        user = userDao.getUserDirs(userId);
        dir = dirDao.getDirInfoById(dirId);
        user.setId(userId);
        fileService.deleteFolder(user, dir);
    }
}
