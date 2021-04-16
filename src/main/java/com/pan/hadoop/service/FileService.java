package com.pan.hadoop.service;

import com.pan.hadoop.entity.Dir;
import com.pan.hadoop.entity.Share;
import com.pan.hadoop.entity.User;

import java.io.InputStream;
import java.util.List;

public interface FileService {

    // 获得文件夹列表，查看文件或目录列表
    public List<Dir> getDirOrFileList(User user, String parentId, String type, String upOrDown);

    // 获得文件列表，查看文件或目录列表
    public List<Dir> getUserFileList(User user, String fileType, String type, String upOrDown);

    // 获得文件列表，查看文件或目录列表
    public List<Share> getUserShareList(User user, String upOrDown);

    // 添加dir文件类型
    public void addDirFileType(Dir dir);

    // 判断文件夹是否已存在
    public boolean isFolderRepeat(String parentId, String path);

    // 上传文件
    public String uploadFile(InputStream inputStream, String md5);

    // 用户生成上传文件信息
    public String uploadFileInfo(String userId, String parentId, String md5);

   // 创建文件夹
    public boolean makeFolder(String userId, Dir dir, String parentid);

    // 移动文件夹
    public String moveFolder(String userId, String parentId, String dirId);

    // 复制或保存文件夹
    public String copyOrSaveFolder(String userId, String parentId, String dirId, String type);

    // 删除文件夹
    public void deleteFolder(User user, Dir dir);

    // 文件夹或重命名
    public String rename(String userId, String dirId, String newName);

    // 下载文件
    public boolean downloadFile(Dir dir, String local);

    //下载文件夹（里面可包含多个文件）
    public void downloadDir(User user, Dir dir, String rootPC, String path);

    // 分享文件
    public String shareDirs(String userId, String dirIds, String choice);

}
