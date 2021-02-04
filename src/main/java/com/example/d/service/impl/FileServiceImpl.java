package com.example.d.service.impl;

import com.example.d.dao.*;
import com.example.d.entity.*;
import com.example.d.service.FileService;
import com.example.d.util.*;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service("fileService")
public class FileServiceImpl implements FileService {
    @Autowired
    private FileDao fileDao;
    @Autowired
    private DirDao dirDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ShareDao shareDao;
    @Autowired
    private AnalysisDao analysisDao;
    @Autowired
    private Producer producer;

    /**
     * 根据文件夹父id获得文件夹列表
     * @param user
     * @param parentId      父文件夹id
     * @param type          排序类型（名字name、时间date、大小size）
     * @param upOrDown      排序方式（升序up、降序down）
     * @return
     */
    @Override
    public List<Dir> getDirOrFileList(User user
            , String parentId
            , String type
            , String upOrDown) {
        // 存储文件夹对象
        List<Dir> dirs = new ArrayList<>();
        // 存储文件对象
        List<Dir> files = new ArrayList<>();
        // 判断该文件夹是否属于该用户
        List<String> ids = Arrays.asList(user.getDir_ids());
        if(!ids.contains(parentId))
            return null;
        Dir dir = dirDao.getDirInfoById(parentId);
        String subIds = dir.getSub_ids();
        if(subIds==null || subIds.equals("") || subIds.equals("null"))
            return null;
        // 遍历子文件夹
        for(String subId: subIds.split(" ")) {
            if(subId.equals("") || subId.equals("null"))
                continue;
            Dir subDir = dirDao.getDirInfoById(subId);
            if(subDir.getIsDir().equals("null") || subDir.getIsDir() == null)
                continue;
            if(subDir.getIsDir().equals("true")){
                subDir.setFile_type("dir");
                dirs.add(subDir);
            }
            else if(subDir.getIsDir().equals("false")){
                addDirFileType(subDir);
                files.add(subDir);
            }
        }
        if(type.equals("name")){
            if(upOrDown.equals("up")){
                Collections.sort(dirs, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        String name1 = d1.getDir_name();
                        String name2 = d2.getDir_name();
                        if(name1.charAt(0) >= 19968 && name1.charAt(0) <= 40869){
                            name1 = PinYinUtil.getPinyin(name1).toLowerCase();
                        }else{
                            name1 = name1.toUpperCase();
                        }
                        if(name2.charAt(0) >= 19968 && name2.charAt(0) <= 40869){
                            name2 = PinYinUtil.getPinyin(name2).toLowerCase();
                        }else{
                            name2 = name2.toUpperCase();
                        }
                        return name2.compareTo(name1);
                    }
                });
                Collections.sort(files, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        String name1 = d1.getDir_name();
                        String name2 = d2.getDir_name();
                        if(name1.charAt(0) >= 19968 && name1.charAt(0) <= 40869){
                            name1 = PinYinUtil.getPinyin(name1).toLowerCase();
                        }else{
                            name1 = name1.toUpperCase();
                        }
                        if(name2.charAt(0) >= 19968 && name2.charAt(0) <= 40869){
                            name2 = PinYinUtil.getPinyin(name2).toLowerCase();
                        }else{
                            name2 = name2.toUpperCase();
                        }
                        return name2.compareTo(name1);
                    }
                });
            }
            else{
                Collections.sort(dirs, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        String name1 = d1.getDir_name();
                        String name2 = d2.getDir_name();
                        if(name1.charAt(0) >= 19968 && name1.charAt(0) <= 40869){
                            name1 = PinYinUtil.getPinyin(name1).toLowerCase();
                        }else{
                            name1 = name1.toUpperCase();
                        }
                        if(name2.charAt(0) >= 19968 && name2.charAt(0) <= 40869){
                            name2 = PinYinUtil.getPinyin(name2).toLowerCase();
                        }else{
                            name2 = name2.toUpperCase();
                        }
                        return name1.compareTo(name2);
                    }
                });
                Collections.sort(files, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        String name1 = d1.getDir_name();
                        String name2 = d2.getDir_name();
                        if(name1.charAt(0) >= 19968 && name1.charAt(0) <= 40869){
                            name1 = PinYinUtil.getPinyin(name1).toLowerCase();
                        }else{
                            name1 = name1.toUpperCase();
                        }
                        if(name2.charAt(0) >= 19968 && name2.charAt(0) <= 40869){
                            name2 = PinYinUtil.getPinyin(name2).toLowerCase();
                        }else{
                            name2 = name2.toUpperCase();
                        }
                        return name1.compareTo(name2);
                    }
                });
            }
        }
        else if(type.equals("size")){
            if(upOrDown.equals("up")){
                Collections.sort(dirs, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        String name1 = d1.getDir_name();
                        String name2 = d2.getDir_name();
                        if(name1.charAt(0) >= 19968 && name1.charAt(0) <= 40869){
                            name1 = PinYinUtil.getPinyin(name1).toLowerCase();
                        }else{
                            name1 = name1.toUpperCase();
                        }
                        if(name2.charAt(0) >= 19968 && name2.charAt(0) <= 40869){
                            name2 = PinYinUtil.getPinyin(name2).toLowerCase();
                        }else{
                            name2 = name2.toUpperCase();
                        }
                        return name1.compareTo(name2);
                    }
                });
                Collections.sort(files, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        return FilesUtil.compareSize(d2.getFile_size(), d1.getFile_size());
                    }
                });
            }
            else{
                Collections.sort(dirs, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        String name1 = d1.getDir_name();
                        String name2 = d2.getDir_name();
                        if(name1.charAt(0) >= 19968 && name1.charAt(0) <= 40869){
                            name1 = PinYinUtil.getPinyin(name1).toLowerCase();
                        }else{
                            name1 = name1.toUpperCase();
                        }
                        if(name2.charAt(0) >= 19968 && name2.charAt(0) <= 40869){
                            name2 = PinYinUtil.getPinyin(name2).toLowerCase();
                        }else{
                            name2 = name2.toUpperCase();
                        }
                        return name1.compareTo(name2);
                    }
                });
                Collections.sort(files, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        return FilesUtil.compareSize(d1.getFile_size(), d2.getFile_size());
                    }
                });
            }
        }
        else if(type.equals("date")){
            if(upOrDown.equals("up")){
                Collections.sort(dirs, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        return d2.getDate().compareTo(d1.getDate());
                    }
                });
                Collections.sort(files, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        return d2.getDate().compareTo(d1.getDate());
                    }
                });
            }
            else{
                Collections.sort(dirs, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        return d1.getDate().compareTo(d2.getDate());
                    }
                });
                Collections.sort(files, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        return d1.getDate().compareTo(d2.getDate());
                    }
                });
            }
        }
        dirs.addAll(files);
        return dirs;
    }

    /**
     * 获取用户所有相应文件列表
     * @param user
     * @param fileType      文件类型（文档document、图片png、音频video）
     * @param type          排序类型（名字name、时间date、大小size）
     * @param upOrDown      排序方式（升序up、降序down）
     * @return
     */
    @Override
    public List<Dir> getUserFileList(User user
            , String fileType
            , String type
            , String upOrDown) {
        String[] ids = user.getDir_ids();

        List<Dir> dirs = new ArrayList<>();
        // 遍历用户所有拥有的文件夹ID
        for (String d : ids) {
            Dir dir = dirDao.getDirInfoById(d);
            // 判断是否为当前文件夹的子文件夹
            if (dir.getIsDir() == "")
                continue;
            if (dir.getIsDir().equals("false")) {
                String fileSuffix = FilesUtil.getFileSuffix(dir.getDir_name());
                if (fileType.equals("document")){
                    if (Constants.DOCUMENT_SUFFIX.contains(fileSuffix)){
                        dir.setFile_type(fileSuffix);
                        dirs.add(dir);
                    }
                }
                else if (fileType.equals("img")){
                    if (Constants.IMG_SUFFIX.contains(fileSuffix)){
                        dir.setFile_type("img");
                        dirs.add(dir);
                    }
                }
                else if (fileType.equals("video")){
                    if (Constants.VIDEO_SUFFIX.contains(fileSuffix)) {
                        dir.setFile_type("video");
                        dirs.add(dir);
                    }
                }
                else{
                    if(Constants.DOCUMENT_SUFFIX.contains(fileSuffix))
                        continue;
                    else if(Constants.IMG_SUFFIX.contains(fileSuffix))
                        continue;
                    else if(Constants.VIDEO_SUFFIX.contains(fileSuffix))
                        continue;
                    addDirFileType(dir);
                    dirs.add(dir);
                }
            }
        }
        if(type.equals("name")){
            if(upOrDown.equals("up")){
                Collections.sort(dirs, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        String name1 = d1.getDir_name();
                        String name2 = d2.getDir_name();
                        if(name1.charAt(0) >= 19968 && name1.charAt(0) <= 40869){
                            name1 = PinYinUtil.getPinyin(name1).toLowerCase();
                        }else{
                            name1 = name1.toUpperCase();
                        }
                        if(name2.charAt(0) >= 19968 && name2.charAt(0) <= 40869){
                            name2 = PinYinUtil.getPinyin(name2).toLowerCase();
                        }else{
                            name2 = name2.toUpperCase();
                        }
                        return name2.compareTo(name1);
                    }
                });
            }
            else{
                Collections.sort(dirs, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        String name1 = d1.getDir_name();
                        String name2 = d2.getDir_name();
                        if(name1.charAt(0) >= 19968 && name1.charAt(0) <= 40869){
                            name1 = PinYinUtil.getPinyin(name1).toLowerCase();
                        }else{
                            name1 = name1.toUpperCase();
                        }
                        if(name2.charAt(0) >= 19968 && name2.charAt(0) <= 40869){
                            name2 = PinYinUtil.getPinyin(name2).toLowerCase();
                        }else{
                            name2 = name2.toUpperCase();
                        }
                        return name1.compareTo(name2);
                    }
                });
            }
        }
        else if(type.equals("size")){
            if(upOrDown.equals("up")){
                Collections.sort(dirs, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        return FilesUtil.compareSize(d2.getFile_size(), d1.getFile_size());
                    }
                });
            }
            else{
                Collections.sort(dirs, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        return FilesUtil.compareSize(d1.getFile_size(), d2.getFile_size());
                    }
                });
            }
        }
        else if(type.equals("date")){
            if(upOrDown.equals("up")){
                Collections.sort(dirs, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        return d2.getDate().compareTo(d1.getDate());
                    }
                });
            }
            else{
                Collections.sort(dirs, new Comparator<Dir>() {
                    public int compare(Dir d1, Dir d2) {
                        return d1.getDate().compareTo(d2.getDate());
                    }
                });
            }
        }
        return dirs;
    }

    /**
     * 获取用户分享链接列表
     * @param user
     * @param upOrDown      排序方式（升序up、降序down）
     * @return
     */
    @Override
    public List<Share> getUserShareList(User user, String upOrDown) {
        String[] share_md5s = user.getShare_md5s();
        if(share_md5s.equals("") || share_md5s == null)
            return null;
        List<Share> shares = new ArrayList<>();
        List<Share> foreverShares = new ArrayList<>();
        List<Share> expiredShares = new ArrayList<>();
        List<Share> unexpiredShares = new ArrayList<>();
        // 遍历用户所有拥有的文件夹ID
        for (String share_md5 : share_md5s) {
            if(share_md5.equals("") || share_md5 == null)
                continue;
            Share share = shareDao.getShareInfoById(share_md5);
            if(share.getStatus().equals("true")){
                if(share.getExpired().equals("forever")){
                    share.setExpired("永久");
                    foreverShares.add(share);
                }
                else if(System.currentTimeMillis() > Long.valueOf(share.getExpired())){
                    share.setExpired("已过期");
                    expiredShares.add(share);
                }
                else{
                    share.setExpired(DateUtil.longToString("yyyy-MM-dd HH:mm:ss", Long.valueOf(share.getExpired())));
                    unexpiredShares.add(share);
                }
            }
            else{
                share.setExpired("已过期");
                expiredShares.add(share);
            }
        }
        if(!upOrDown.equals("up")){
            Collections.sort(unexpiredShares, new Comparator<Share>() {
                public int compare(Share s1, Share s2) {
                    return s2.getExpired().compareTo(s1.getExpired());
                }
            });
            shares.addAll(unexpiredShares);
            shares.addAll(foreverShares);
        }
        else{
            Collections.sort(unexpiredShares, new Comparator<Share>() {
                public int compare(Share s1, Share s2) {
                    return s1.getExpired().compareTo(s2.getExpired());
                }
            });
            shares.addAll(foreverShares);
            shares.addAll(unexpiredShares);
        }
        shares.addAll(expiredShares);
        return shares;
    }

    /**
     * 转变文件类型（图标显示）
     * @param dir
     */
    @Override
    public void addDirFileType(Dir dir) {
        if(dir.getIsDir().equals("true")){
            dir.setFile_type("dir");
        }else {
            String fileType = FilesUtil.getFileSuffix(dir.getDir_name());
            if (Constants.DOCUMENT_SUFFIX.contains(fileType) || Constants.OTHER_SUFFIX.contains(fileType))
                dir.setFile_type(fileType);
            else if (Constants.IMG_SUFFIX.contains(fileType))
                dir.setFile_type("img");
            else if (Constants.VIDEO_SUFFIX.contains(fileType))
                dir.setFile_type("video");
            else if (Constants.CODE_SUFFIX.contains(fileType))
                dir.setFile_type("code");
            else
                dir.setFile_type("misc");
        }
    }

    /**
     * 判断文件夹或文件是否已经存在
     * @param parentId      父文件夹ID
     * @param path          路径
     * @return
     */
    @Override
    public boolean isFolderRepeat(String parentId, String path){
        List<String> list = new ArrayList<String>();
        Dir dir = dirDao.getDirInfoById(parentId);
        String subIds = dir.getSub_ids();
        if(subIds==null || subIds.equals("") || subIds.equals("null"))
            return false;
        for(String subId: subIds.split(" ")){
            Dir subDir = dirDao.getDirInfoById(subId);
            if(subDir != null) {
                if (subDir.getPath() != null){
                    list.add(subDir.getPath());
                }
                // 判断路径是否重复
                if (list.contains(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 上传文件
     * @param inputStream
     * @param md5
     * @return
     */
    @Override
    public String uploadFile(InputStream inputStream, String md5){
        try{
            File file = fileDao.getFileInfoByRowkey(md5);
            file.setSize(inputStream.available());
            file.setDate(System.currentTimeMillis());
            // File表添加文件信息
            fileDao.addFileInfo(file);
            // File_MD5表添加id对应的md5值
            fileDao.addFileMD5(file);
            return fileDao.upload(inputStream, md5);
        }catch (IOException e){
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * 上传文件信息
     * @param userId
     * @param parentId
     * @param md5
     */
    @Override
    public String uploadFileInfo(String userId, String parentId, String md5){
        Dir dir = new Dir();
        // 根据MD5值获取文件信息
        File file = fileDao.getFileInfoByRowkey(md5);
        User user = userDao.getUserFilesAndSpace(userId);
        if(Long.valueOf(user.getSurplus_space()) < file.getSize())
            return "space";
        // 用户添加文件id
        user.setFile_id(file.getFile_id());
        user.setFile_count(userDao.getUserFileCount(userId, file.getFile_id()));

        // 设置文件夹信息设置
        dir.setDir_id(dirDao.getDirIdByTableCount());
        // 用户添加文件夹信息
        user.setDir_id(dir.getDir_id());
        // 配置文件夹信息（isDir为false，是文件）
        dir.setDir_name(file.getFile_name());
        dir.setParent_id(parentId);
        dir.setIsDir("false");
        // 获取父文件夹信息
        Dir parentDir = dirDao.getDirInfoById(parentId);
        // 在用户根目录下
        if(parentId.equals(userId))
            dir.setPath(">" + dir.getDir_name());
        else
            dir.setPath(parentDir.getPath() + ">" + dir.getDir_name());
        // 查看是否已经存在相同的文件
        if(isFolderRepeat(parentId, dir.getPath()))
            return "repeat";
        // 父文件夹设置子id
        if(parentDir.getSub_ids() == null || parentDir.getSub_ids().equals(""))
            parentDir.setSub_ids(dir.getDir_id());
        else
            parentDir.setSub_ids(parentDir.getSub_ids() + " " + dir.getDir_id());
        // 配置文件夹剩余信息
        dir.setDate(DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
        dir.setFile_id(file.getFile_id());
        dir.setFile_size(FilesUtil.FormatFileSize(file.getSize()));

        // 生成并添加分析信息
        // 用户上传次数
        Analysis analysis = new Analysis();
        String date = DateUtil.longToString("yyyy-MM-dd", System.currentTimeMillis());
        analysis.setRowKey("user_upload_" + userId + "_" + date);
        analysis.setCount(analysisDao.getAnalysisCount(analysis.getRowKey()));
        analysisDao.addAnalysis(analysis);
        // 文件上传次数
        analysis.setRowKey("file_upload_" + file.getFile_id() + "_" + date);
        analysis.setCount(analysisDao.getAnalysisCount(analysis.getRowKey()));
        analysisDao.addAnalysis(analysis);
        // 上传信息
        dirDao.addDirInfo(dir);
        dirDao.addDirInfo(parentDir);
        userDao.addUserDir(user);
        userDao.addUserFile(user);
        return "success";
    }

    /**
     * 新建文件夹
     * @param userId        用户ID
     * @param dir           文件夹ID
     * @param parentId      父文件夹ID
     * @return
     */
    public boolean makeFolder(String userId, Dir dir, String parentId) {
        // 配置文件夹信息
        // 文件夹id为当前表行数+1，传入名字、父id、根据父文件夹路径生成当前目录路径
        dir.setDir_id(dirDao.getDirIdByTableCount());
        dir.setParent_id(parentId);
        // 获取父文件夹信息
        Dir parentDir = dirDao.getDirInfoById(parentId);
        // 判断是否在根目录下创建文件夹
        if(parentId.equals(userId))
            dir.setPath(">" + dir.getDir_name());
        else
            dir.setPath(parentDir.getPath() + ">" + dir.getDir_name());
        dir.setDate(DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
        // 判断该文件夹是否已经存在,已经存在直接结束
        if(isFolderRepeat(parentId, dir.getPath()))
            return false;
        // 父文件夹的子ID添加该文件夹的ID
        if(parentDir.getSub_ids() == null)
            parentDir.setSub_ids(dir.getDir_id());
        else
            parentDir.setSub_ids(parentDir.getSub_ids() + " " + dir.getDir_id());
        // 创建文件夹,上传信息到用户表、文件夹表
        User user = new User();
        user.setId(userId);
        user.setDir_id(dir.getDir_id());
        // 添加文件夹信息
        dirDao.addDirInfo(dir);
        // 更新父文件夹信息
        dirDao.addDirInfo(parentDir);
        // 更新用户的文件夹信息
        userDao.addUserDir(user);
        return true;
    }

    /**
     * 移动文件夹
     * @param userId
     * @param parentId      移动到的目的文件夹id内
     * @param dirId         需要移动文件夹id
     * @return true 成功      false 文件夹已存在（重复）
     */
    @Override
    public String moveFolder(String userId, String parentId, String dirId) {
        // 移动到的目的文件夹信息
        Dir parentDir = dirDao.getDirInfoById(parentId);
        // 需要移动的文件夹信息
        Dir dir = dirDao.getDirInfoById(dirId);
        // 需要移动的文件夹的父文件夹信息
        Dir dirParentDir = dirDao.getDirInfoById(dir.getParent_id());
        if(!parentId.equals(userId) && isFolderInDir(userId, parentDir, dir))
            return "moveError";
        // 目的文件夹添加子id信息
        parentDir.setSub_ids(parentDir.getSub_ids() + " " + dirId);
        // 需要移动的文件夹添加父id信息，设置路径
        dir.setParent_id(parentId);
        // 更新路径
        if(parentDir.getPath() == null || parentDir.getPath().equals("null")){
            dir.setPath(">" + dir.getDir_name());
        }
        else{
            dir.setPath(parentDir.getPath() + ">" + dir.getDir_name());
        }
        // 目的文件夹内是否已存在该文件夹
        if(isFolderRepeat(parentId, dir.getPath()))
            return "moveRepeat";
        // 需要移动的文件夹的原父文件夹删除该文件夹的子id
        String newSubIds = "";
        for(String id: dirParentDir.getSub_ids().split(" ")){
            if(!id.equals(dir.getDir_id())){
                newSubIds += id + " ";
            }
        }
        // 更新父文件夹的子id信息
        dirParentDir.setSub_ids(newSubIds.equals("")?newSubIds:newSubIds.substring(0, newSubIds.length() - 1));
        // 更新信息
        dirDao.addDirInfo(parentDir);
        dirDao.addDirInfo(dir);
        dirDao.addDirInfo(dirParentDir);
        return "moveSuccess";
    }

    /**
     *  判断目的文件夹是否在要移动的文件夹内
     * @param userId         用户ID
     * @param parentDir      移动到的目的文件夹
     * @param dir            需要移动文件夹
     * @return
     */
    public boolean isFolderInDir(String userId, Dir parentDir, Dir dir){
        if(parentDir.getDir_id().equals("null"))
            return false;
        if(parentDir.getDir_id().equals(dir.getDir_id())){
            return true;
        }
        String parentId = parentDir.getParent_id();
        if(parentId.equals(userId))
            return false;
        parentDir = dirDao.getDirInfoById(parentId);
        if(isFolderInDir(userId, parentDir, dir)){
            return true;
        }
        return false;
    }

    /**
     * 复制文件夹
     * @param userId
     * @param parentId      复制到的目的文件夹id内
     * @param dirId         需要复制文件夹id
     * @param type          复制还是保存
     * @return success 成功      repeat 文件夹已存在（重复）    space 空间不足
     */
    @Override
    public String copyOrSaveFolder(String userId, String parentId, String dirId, String type) {
        // 复制到的目的文件夹信息
        Dir parentDir = dirDao.getDirInfoById(parentId);
        // 需要复制的文件夹信息
        Dir dir = dirDao.getDirInfoById(dirId);
        if(!parentId.equals(userId) && isFolderInDir(userId, parentDir, dir))
            return type + "Error";

        // 更新路径
        if(parentDir.getPath() == null || parentDir.getPath().equals("null")){
            dir.setPath(">" + dir.getDir_name());
        }
        else{
            dir.setPath(parentDir.getPath() + ">" + dir.getDir_name());
        }
        // 判断是否可以成功复制（是否目的文件夹已存在、空间是否充足）
        if(isFolderRepeat(parentId, dir.getPath()))
            return type + "Repeat";
        if(countDirSize(dirId, 0l) > Long.valueOf(userDao.getUserSpace(userId).getSurplus_space()))
            return type + "Space";
        copyFolderInfo(userId, parentId, dirId);
        if(type.equals("copy"))
            return "copySuccess";
        return "saveSuccess";
    }
    // 计算文件夹大小
    public Long countDirSize(String dirId, Long size){
        Dir dir = dirDao.getDirInfoById(dirId);
        String subIds = dir.getSub_ids();
        if(subIds==null || subIds.equals("") || subIds.equals("null"))
            return 0l;
        // 遍历子文件夹
        for(String subId: subIds.split(" ")) {
            if(subId.equals(""))
                continue;
            Dir subDir = dirDao.getDirInfoById(subId);
            if(subDir.getIsDir().equals("null") || subDir.getIsDir() == null)
                continue;
            if(subDir.getIsDir().equals("true")){
                size += countDirSize(subId, 0l);
            }
            else if(subDir.getIsDir().equals("false")){
                File_MD5 file_md5 = fileDao.getFileMD5ById(subDir.getFile_id());
                size += Long.valueOf(file_md5.getSize());
            }
        }
        return size;
    }
    // 复制子文件夹信息
    public void copyFolderInfo(String userId, String parentId, String dirId){
        // 复制到的目的文件夹信息
        Dir parentDir = dirDao.getDirInfoById(parentId);
        // 需要复制的文件夹信息
        Dir dir = dirDao.getDirInfoById(dirId);
        // 复制文件夹所生成的新文件夹信息
        Dir newDir = new Dir();

        newDir.setDir_id(dirDao.getDirIdByTableCount());
        newDir.setDir_name(dir.getDir_name());
        newDir.setParent_id(parentId);
        newDir.setIsDir(dir.getIsDir());
        newDir.setDate(DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
        if(parentDir.getPath() == null || parentDir.getPath().equals("null")){
            newDir.setPath(">" + dir.getDir_name());
        }
        else{
            newDir.setPath(parentDir.getPath() + ">" + dir.getDir_name());
        }
        if(newDir.getIsDir().equals("false")){
            newDir.setFile_id(dir.getFile_id());
            newDir.setFile_size(dir.getFile_size());
        }
        // 目的文件夹添加新文件夹的子id信息
        if(parentDir.getSub_ids() == null || parentDir.getSub_ids().equals("") || parentDir.getSub_ids().equals("null"))
            parentDir.setSub_ids(newDir.getDir_id());
        else
            parentDir.setSub_ids(parentDir.getSub_ids() + " " + newDir.getDir_id());
        // 用户新增文件夹信息和文件数量
        User user = new User();
        user.setId(userId);
        user.setDir_id(newDir.getDir_id());
        if(newDir.getIsDir().equals("false")){
            user.setFile_id(newDir.getFile_id());
            user.setFile_count(userDao.getUserFileCount(userId, newDir.getFile_id()));
            userDao.addUserFile(user);
        }
        // 更新信息
        dirDao.addDirInfo(parentDir);
        dirDao.addDirInfo(newDir);
        userDao.addUserDir(user);
        // 需要复制的文件夹的子文件夹id信息
        String subIds = dir.getSub_ids();
        if(subIds==null || subIds.equals("") || subIds.equals("null"))
            return;
        // 遍历子文件夹
        for(String subId: subIds.split(" ")) {
            if(subId.equals(""))
                continue;
            Dir subDir = dirDao.getDirInfoById(subId);
            if(subDir.getIsDir().equals("null") || subDir.getIsDir() == null)
                continue;
            copyFolderInfo(userId, newDir.getDir_id(), subId);
        }
    }

    /**
     * 删除文件夹或文件
     * @param user
     * @param dir
     */
    public void deleteFolder(User user, Dir dir) {
        if(dir.getIsDir().equals("true")) {
            // 是文件夹的话，递归删除用户该文件夹下的子文件夹
            String subIds = dir.getSub_ids();
            // 子id存在且不为空时，遍历子id
            if (subIds != null && !subIds.equals("")) {
                for (String subId : subIds.split(" ")) {
                    // 子文件夹信息
                    Dir subDir = dirDao.getDirInfoById(subId);
                    deleteFolder(user, subDir);
                }
            }
        }
        else{
            // 为文件时，用户对应的文件数量-1
            user.setFile_id(dir.getFile_id());
            user.setFile_count(userDao.getUserFileCount(user.getId(), dir.getFile_id()));
            userDao.delUserFile(user);
        }
        // 得到父文件夹信息，删除父文件夹的该文件夹id的子文件夹id
        Dir parentDir = dirDao.getDirInfoById(dir.getParent_id());
        String newSubIds = "";
        for(String id: parentDir.getSub_ids().split(" ")){
            if(!id.equals(dir.getDir_id())){
                newSubIds += id + " ";
            }
        }
        // 更新父文件夹的子id信息
        parentDir.setSub_ids(newSubIds.equals("")?newSubIds:newSubIds.substring(0, newSubIds.length() - 1));
        dirDao.addDirInfo(parentDir);
        // 删除该文件夹信息
        user.setDir_id(dir.getDir_id());
        userDao.delUserDir(user);
        // 删除文件夹信息
        dirDao.delDirInfo(dir.getDir_id());
    }

    /**
     * 文件或文件夹重命名
     * @param userId
     * @param dirId
     * @param newName
     */
    @Override
    public String rename(String userId, String dirId, String newName) {
        Dir dir = dirDao.getDirInfoById(dirId);
        dir.setDir_name(newName);
        String path = dir.getPath();
        String newPath = "";
        String[] fields = path.split(">");
        if(fields.length != 1)
            for(int i = 0; i< fields.length -1; i++)
                newPath += ">" + fields[i];
        newPath += newName;
        dir.setPath(newPath);
        if(isFolderRepeat(dir.getParent_id(), dir.getPath()))
            return "repeat";
        dirDao.addDirInfo(dir);
        // 是文件夹时，更改子文件夹的路径
        if(dir.getIsDir().equals("true"))
            changePath(dirId);
        return "success";
    }

    // 更改子文件夹路径
    public void changePath(String dirId) {
        Dir dir = dirDao.getDirInfoById(dirId);
        String subIds = dir.getSub_ids();
        if(subIds == null)
            return;
        for(String subId: subIds.split(" ")){
            Dir subDir = dirDao.getDirInfoById(subId);
            subDir.setPath(dir.getPath() + ">" + subDir.getDir_name());
            dirDao.addDirInfo(subDir);
            if(subDir.getIsDir().equals("true"))
                changePath(subId);
        }
    }

    /**
     * 下载文件
     * @param dir
     * @param local
     * @return
     */
    @Override
    public boolean downloadFile (Dir dir, String local) {
        String fileMd5 = fileDao.getFileMD5ById(dir.getFile_id()).getMd5();
        File file = fileDao.getFileInfoByRowkey(fileMd5);
        try{
            InputStream in = new FileInputStream(local + dir.getDir_name());
            if(file.getSize() != in.available()){
                fileDao.downloadFile(file.getFile_name() , fileMd5, local);
            }
        }catch (IOException e){
            fileDao.downloadFile(file.getFile_name() , fileMd5, local);
        }
        return true;
    }


    /**
     * 下载文件夹（里面可包含多个文件）（未使用）
     * @param user
     * @param dir
     * @param rootPC
     * @param path
     * @return
     */
    @Override
    public void downloadDir(User user, Dir dir, String rootPC, String path) {
        // 获取用户该文件夹下的子文件夹
        String[] ids = userDao.getUserDirs(user.getId()).getDir_ids();
        for (String d : ids) {
            // 文件夹id为有效id
            if(d == null || dirDao.getDirInfoById(d).getIsDir().equals(""))
                continue;
            // 获取父id为当前文件夹id的所有用户的子目录
            if (dirDao.getDirInfoById(d).getParent_id().equals(dir.getDir_id())) {
                // 获取子文件夹dir1的信息
                Dir dir1 = dirDao.getDirInfoById(d);
                if(dir1.getIsDir().equals("false")){
                    if(downloadFile(dir1, path)) {
                        System.out.println(path + dir1.getDir_name() + "下载成功");
                    }
                    else{
                        System.out.println(path + dir1.getDir_name() + "下载失败");
                    }
                }
                else{
                    path = rootPC + dir1.getPath();
                    java.io.File localDir = new java.io.File(path);
                    if(!localDir.exists()){
                        localDir.mkdir();
                    }
                    downloadDir(user, dir1, rootPC, path);
                }
            }
        }
    }

    /**
     * 分享文件
     * @param userId
     * @param dirIds
     * @param choice
     * @return
     */
    @Override
    public String shareDirs(String userId, String dirIds, String choice) {
        Share share = new Share();
        String text = producer.createText();
        String expired = "";
        if(choice.equals("day"))
            expired = String.valueOf(System.currentTimeMillis() + Constants.DAY * 1000);
        else if(choice.equals("week"))
            expired = String.valueOf(System.currentTimeMillis() + Constants.WEEK * 1000);
        else if(choice.equals("month"))
            expired = String.valueOf(System.currentTimeMillis() + Constants.MONTH * 1000);
        else if(choice.equals("forever"))
            expired = Constants.FOREVER;
        share.setShare_md5(MD5Util.encodePwd(dirIds + text));
        share.setUser_id(userId);
        share.setDir_ids(dirIds);
        share.setKey(text);
        share.setExpired(expired);
        share.setStatus("true");
        share.toString();
        User user = new User();
        user.setId(userId);
        user.setShare_md5(share.getShare_md5());
        // 生成并添加分析信息
        // 用户分享次数
        Analysis analysis = new Analysis();
        String date = DateUtil.longToString("yyyy-MM-dd", System.currentTimeMillis());
        analysis.setRowKey("user_share_" + userId + "_" + date);
        analysis.setCount(analysisDao.getAnalysisCount(analysis.getRowKey()));
        analysisDao.addAnalysis(analysis);
        userDao.addUserShare(user);
        shareDao.createShare(share);
        return share.getShare_md5() + " " + share.getKey();
    }
}
