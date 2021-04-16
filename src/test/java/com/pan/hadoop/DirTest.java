package com.pan.hadoop;

import com.pan.hadoop.dao.DirDao;
import com.pan.hadoop.dao.FileDao;
import com.pan.hadoop.dao.UserDao;
import com.pan.hadoop.entity.Dir;
import com.pan.hadoop.entity.File;
import com.pan.hadoop.entity.User;
import com.pan.hadoop.service.impl.FileServiceImpl;
import com.pan.hadoop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
public class DirTest {
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

    /**
     * 新建文件夹
     */
    @Test
    public void makeDirTest(){
        String userId = "17031110112";
        String dirName = "ttt";
        String isDir = "true";
        String parentId = "17031110112";

        Dir dir = new Dir();
        dir.setDir_name(dirName);
        dir.setIsDir(isDir);
        System.out.println(fileService.makeFolder(userId, dir, parentId));
    }

    /**
     * 删除文件夹，递归
     */
    @Test
    public void deleteDirTest(){
        String userId = "17031110112";
        String dirIds = "2 3 4";

        user = userDao.getUserDirs(userId);
        for(String dirId: dirIds.split(" ")) {
            System.out.println("dirId: " + dirId);
            Dir dir = dirDao.getDirInfoById(dirId);
            fileService.deleteFolder(user, dir);
        }
        userService.calSpace(userId);
    }

    /**
     * 测试获取文件夹目录
     */
    @Test
    public void getDirListTest(){
        String userId = "17031110112";
        String dirId = "17031110112";
        String type = "name";
        String upOrDown = "up";

        user = userDao.getUserDirs(userId);
        user.setId(userId);
        List<Dir> dirs = fileService.getDirOrFileList(user, dirId, type,upOrDown);
        for (Dir dir : dirs){
            if (dir.getIsDir().equals("true"))
                System.out.println("文件夹名：" + dir.getDir_name() + "  创建日期：" + dir.getDate());
            else
                System.out.println("文件名：" + dir.getDir_name() + "  创建日期：" + dir.getDate() + "  文件大小：" + dir.getFile_size() + "b");
        }
    }

    /**
     * 重命名文件夹
     */
    @Test
    public void renameDirTest(){
        String userId = "17031110112";
        String dirId = "2";
        String newName = "123";
        fileService.rename(userId, dirId, newName);
    }

    @Test
    public void moveFolderTest(){
        String userId = "17031110112";
        String parentId = "19";
        String dirId = "28";
        System.out.println(fileService.moveFolder(userId, parentId, dirId));
    }

    @Test
    public void copyFolderTest(){
        String userId = "17031110112";
        String parentId = "23";
        String dirId = "28";
        String type = "copy";
        System.out.println(fileService.copyOrSaveFolder(userId, parentId, dirId, type));
    }

    @Test
    public void createShareTest(){
        String dirIds = "10 15 20";
        String choice = "day";
        String userId = "17031110112";

        System.out.println(fileService.shareDirs(userId, dirIds, choice));
    }
}
