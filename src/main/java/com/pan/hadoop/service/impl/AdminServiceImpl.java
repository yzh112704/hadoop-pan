package com.pan.hadoop.service.impl;


import com.pan.hadoop.dao.*;
import com.pan.hadoop.dao.basedao.HbaseDao;
import com.pan.hadoop.dao.basedao.HdfsDao;
import com.pan.hadoop.entity.*;
import com.pan.hadoop.service.AdminService;
import com.pan.hadoop.util.Constants;
import com.pan.hadoop.util.DateUtil;
import com.pan.hadoop.util.FilesUtil;
import com.pan.hadoop.util.MD5Util;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("adminService")
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private FileDao fileDao;
    @Autowired
    private DirDao dirDao;
    @Autowired
    private ShareDao shareDao;
    @Autowired
    private AnalysisDao analysisDao;
    @Autowired
    private AdminDao adminDao;
    @Autowired
    private HdfsDao hdfsDao;
    @Autowired
    private HbaseDao hbaseDao;

    /**
     * 检测id是否已经注册
     * @param id
     * @return
     */
    @Override
    public boolean checkId(String id) {
        Admin admin = adminDao.getAdminInfoById(id);
        if(admin != null)
            return true;
        else
            return false;
    }

    /**
     * 添加管理员，密码md5加密
     * @param admin
     */
    @Override
    public void addAdmin(Admin admin) {
        admin.setPwd(MD5Util.encodePwd(admin.getPwd()));
        adminDao.addAdmin(admin);
    }

    /**
     * 根据选择获取分析信息
     * @param choice            选择（用户 上传、下载、分析；文件 被上传、被下载；分享链接 被打开）
     * @return
     */
    @Override
    public List<Analysis> analysisData(String choice) {
        String[] filterHeadList = {"file_download_", "file_upload_", "share_open_", "user_download_", "user_share_", "user_upload_", "user_upload_~"};
        int i = 0;
        for (; i < filterHeadList.length - 1; i++)
            if (choice.equals(filterHeadList[i]))
                break;
        List<Analysis> list = new ArrayList<>();
        Filter filter = new PrefixFilter(Bytes.toBytes(choice));
        list = analysisDao.getMyAnalysisList(filter, filterHeadList[i], filterHeadList[i + 1]);
        return list;
    }

    /**
     * 根据日期获取分析信息
     * @param choice            选择（用户 上传、下载、分析；文件 被上传、被下载；分享链接 被打开）
     * @param startDate         起始日期
     * @param endDate           结束日期
     * @return
     */
    @Override
    public List<Analysis> analysisDataByDate(String choice, String startDate, String endDate) {
        String[] filterHeadList = {"file_download_", "file_upload_", "share_open_", "user_download_", "user_share_", "user_upload_", "user_upload_~"};
        int i = 0;
        for (; i < filterHeadList.length - 1; i++)
            if (choice.equals(filterHeadList[i]))
                break;
        Long startTime = DateUtil.StringToLong(startDate);
        Long stopTime = DateUtil.StringToLong(endDate);
        List<Analysis> list = new ArrayList<>();
        Filter filter = new PrefixFilter(Bytes.toBytes(choice));
        list = analysisDao.getMyAnalysisListByDate(filter, filterHeadList[i], filterHeadList[i + 1], startTime, stopTime);
        return list;
    }

    /**
     * 获取每天分享信息
     * @param choice            选择（用户 上传、下载、分析；文件 被上传、被下载；分享链接 被打开）
     * @param startDate         起始日期
     * @param endDate           结束日期
     * @return
     */
    @Override
    public List<List<String>> analysisDay(String choice, String startDate, String endDate) {
        List<List<String>> type = new ArrayList<>();
        Map<String, String> data = new HashMap<>();
        List<Analysis> analyses = analysisDataByDate(choice, startDate, endDate);
        Long start = DateUtil.StringToLong(startDate);
        Long end = DateUtil.StringToLong(endDate);
        for(;start <= end;start += 3600 * 24 * 1000){
            data.put(DateUtil.longToString("yyyy-MM-dd", start), "0");
        }
        for(Analysis analysis : analyses){
            String key = analysis.getRowKey().split("_")[3];
            String value = analysis.getCount();
            if(data.get(key) != null){
                data.put(key,String.valueOf(Integer.valueOf(data.get(key)) + Integer.valueOf(value)));
            }else{
                data.put(key,value);
            }
        }
        List<Map.Entry<String,String>> sortList = new ArrayList<Map.Entry<String,String>>(data.entrySet());
        Collections.sort(sortList,new Comparator<Map.Entry<String,String>>() {
            //升序排序
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        for(Map.Entry<String,String> mapping:sortList){
            List<String> dateAndCount = new ArrayList<>();
            dateAndCount.add(mapping.getKey());
            dateAndCount.add(mapping.getValue());
            type.add(dateAndCount);
        }
        return type;
    }

    /**
     * 获取排名
     * @param choice            选择（用户 上传、下载、分析；文件 被上传、被下载；分享链接 被打开）
     * @param top               排名
     * @param startDate         起始日期
     * @param endDate           结束日期
     * @return
     */
    @Override
    public List<List<String>> analysisTopDataByDate(String choice, int top, String startDate, String endDate){
        List<Analysis> list = new ArrayList<>();
        list = analysisDataByDate(choice, startDate, endDate);
        Map<String, String> map = new HashMap<String, String>();
        String Id = "";
        Long count = 0l;
        for(Analysis analysis: list){
            if(Id.equals(analysis.getRowKey().split("_")[2])){
                count += Long.valueOf(analysis.getCount());
            }else{
                if(!Id.equals(""))
                    map.put(Id, String.valueOf(count));
                Id = analysis.getRowKey().split("_")[2];
                count = Long.valueOf(analysis.getCount());
            }
        }
        map.put(Id, String.valueOf(count));

        List<Map.Entry<String,String>> sortList = new ArrayList<Map.Entry<String,String>>(map.entrySet());
        Collections.sort(sortList,new Comparator<Map.Entry<String,String>>() {
            //升序排序
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return Long.valueOf(o2.getValue()).compareTo(Long.valueOf(o1.getValue()));
            }

        });
        List<List<String>> topList = new ArrayList<>();
        count = 0l;
        for(Map.Entry<String,String> mapping:sortList){
            List<String> data = new ArrayList<>();
            count ++;
            data.add(mapping.getValue());
            if(choice.equals("share_open_")){
                Share share = shareDao.getShareInfoById(mapping.getKey());
                data.add(share.getUser_id());
            }else
                data.add("Top" + count);
            data.add(mapping.getKey());
            topList.add(data);
            if(top == count)
                break;
        }
        count++;
        for(; count<=top; count++){
            List<String> data = new ArrayList<>();
            data.add("");
            data.add("Top" + count);
            data.add("");
            topList.add(data);
        }
        return topList;
    }

    /**
     * 按照日期获取排名列表
     * @param choice            选择（用户 上传、下载、分析；文件 被上传、被下载；分享链接 被打开）
     * @param top               排名
     * @param startDate         起始日期
     * @param endDate           结束日期
     * @return
     */
    @Override
    public List<String> analysisTopLineByDate(String choice, int top, String startDate, String endDate){
        List<Analysis> list = new ArrayList<>();
        list = analysisDataByDate(choice, startDate, endDate);
        Map<String, String> map = new HashMap<String, String>();
        String Id = "";
        Long count = 0l;
        for(Analysis analysis: list){
            if(Id.equals(analysis.getRowKey().split("_")[2])){
                count += Long.valueOf(analysis.getCount());
            }else{
                if(!Id.equals(""))
                    map.put(Id, String.valueOf(count));
                Id = analysis.getRowKey().split("_")[2];
                count = Long.valueOf(analysis.getCount());
            }
        }
        map.put(Id, String.valueOf(count));

        List<Map.Entry<String,String>> sortList = new ArrayList<Map.Entry<String,String>>(map.entrySet());
        Collections.sort(sortList,new Comparator<Map.Entry<String,String>>() {
            //升序排序
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return Long.valueOf(o2.getValue()).compareTo(Long.valueOf(o1.getValue()));
            }

        });
        List<String> line = new ArrayList<>();
        count = 0l;
        for(Map.Entry<String,String> mapping:sortList){
            count ++;
            if(!mapping.getKey().equals("") && !mapping.getValue().equals("") && mapping.getValue()!=null && mapping.getKey()!=null){
                if(choice.equals("share_open_")){
                    Share share = shareDao.getShareInfoById(mapping.getKey());
                    line.add(mapping.getKey() + ":" + share.getUser_id() + ":" + mapping.getValue());
                }else
                    line.add(mapping.getKey() + ":Top" + count + ":" + mapping.getValue() + ":" + choice);
            }
            if(top == count)
                break;
        }
        return line;
    }

    /**
     * 找到所有用户
     * @return
     */
    @Override
    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        List<User> unUsers = new ArrayList<>();
        List<String> list = hbaseDao.getRowKey(Constants.TABLE_USER);
        for(String userId: list){
            User user = userDao.getUserInfoById(userId);
            if(user.getStatus().equals("true"))
                users.add(user);
            else
                unUsers.add(user);
        }
        users.addAll(unUsers);
        return users;
    }

    /**
     * 根据用户id删除用户所有信息
     * @param userId
     */
    @Override
    public void delUserAllInfo(String userId){
        // 清空用户的所有文件夹信息
        User user = userDao.getUserDirs(userId);
        String[] dirIds = user.getDir_ids();
        for (String dirId : dirIds) {
            if(dirId.equals(""))
                continue;
            dirDao.delDirInfo(dirId);
        }
        user = userDao.getUserShares(userId);
        for(String share_md5: user.getShare_md5s()){
            if(share_md5.equals(""))
                continue;
            shareDao.delShareInfoById(share_md5);
        }
        // 删除用户
        userDao.delUserInfoById(userId);
    }

    /**
     * 找到所有管理员（级别比自己低的）
     * @param level
     * @return
     */
    @Override
    public List<Admin> findAllAdmin(String level) {
        List<Admin> admins = new ArrayList<>();
        List<String> list = hbaseDao.getRowKey(Constants.TABLE_ADMIN);
        for(String adminId: list){
            Admin admin1 = adminDao.getAdminInfoById(adminId);
            if(level.equals("operate")){
                if(admin1.getLevel().equals("common"))
                    admins.add(admin1);
            }
            else if(level.equals("admin")){
                if(!admin1.getLevel().equals("admin"))
                    admins.add(admin1);
            }
        }
        return admins;
    }

    /**
     * 找到所有文件（禁用、替换原文件除外）
     * @return
     */
    @Override
    public List<File> findAllFile() {
        List<File> files = new ArrayList<>();
        List<String> list = hbaseDao.getRowKey(Constants.TABLE_FILE);
        for(String fileMd5: list){
            if(fileMd5.equals("document") || fileMd5.equals("img") || fileMd5.equals("video"))
                continue;
            File file = fileDao.getFileInfoByRowkey(fileMd5);
            if(!file.getFile_id().equals("ban") && !file.getFile_id().equals("init")) {
                if (Constants.DOCUMENT_SUFFIX.contains(file.getFile_type()) || Constants.OTHER_SUFFIX.contains(file.getFile_type()))
                    file.setFile_type(file.getFile_type());
                else if (Constants.IMG_SUFFIX.contains(file.getFile_type()))
                    file.setFile_type("img");
                else if (Constants.VIDEO_SUFFIX.contains(file.getFile_type()))
                    file.setFile_type("video");
                else if (Constants.CODE_SUFFIX.contains(file.getFile_type()))
                    file.setFile_type("code");
                else
                    file.setFile_type("misc");
                file.setCurrentChunk(FilesUtil.FormatFileSize(file.getSize()));
                file.setTotalChunks(DateUtil.longToString("yyyy-MM-dd hh:mm:ss", file.getDate()));
                files.add(file);
            }
        }
        return files;
    }

    /**
     * 找到所有被禁用文件
     * @return
     */
    @Override
    public List<File> findAllBanFile() {
        List<File> files = new ArrayList<>();
        List<String> list = hbaseDao.getRowKey(Constants.TABLE_FILE);
        for(String fileMd5: list){
            if(fileMd5.equals("document") || fileMd5.equals("img") || fileMd5.equals("video"))
                continue;
            File file = fileDao.getFileInfoByRowkey(fileMd5);
            if(file.getFile_id().equals("ban")){
                if (Constants.DOCUMENT_SUFFIX.contains(file.getFile_type()) || Constants.OTHER_SUFFIX.contains(file.getFile_type()))
                    file.setFile_type(file.getFile_type());
                else if (Constants.IMG_SUFFIX.contains(file.getFile_type()))
                    file.setFile_type("img");
                else if (Constants.VIDEO_SUFFIX.contains(file.getFile_type()))
                    file.setFile_type("video");
                else if (Constants.CODE_SUFFIX.contains(file.getFile_type()))
                    file.setFile_type("code");
                else
                    file.setFile_type("misc");
                file.setCurrentChunk(FilesUtil.FormatFileSize(file.getSize()));
                file.setTotalChunks(DateUtil.longToString("yyyy-MM-dd hh:mm:ss", file.getDate()));
                files.add(file);
            }
        }
        return files;
    }

    /**
     * 和谐资源
     * @param banFileMd5     要替换成的文件md5
     * @param destFileMd5    被替换的文件md5
     * @param flag           是否备份要被替换的文件
     * @param newName        备份的新名字
     */
    @Override
    public void banFile(String banFileMd5, String destFileMd5, boolean flag, String newName) {
        // 得到文件之前所有的信息
        File banFileInfo = fileDao.getFileInfoByRowkey(destFileMd5);
        // 是否备份
        if(flag) {
            // 需要备份，更改为新名字
            hdfsDao.renameFile(destFileMd5, newName);
            // 信息一个被禁用的文件信息（管理员查看被禁用文件时使用）
            File newBanFileInfo = new File();
            newBanFileInfo.setMd5(newName);
            newBanFileInfo.setFile_id("ban");
            if(banFileInfo.getFile_id().equals("banned")){
                return;
            }
            newBanFileInfo.setFile_name(newName);
            newBanFileInfo.setFile_type(banFileInfo.getFile_type());
            newBanFileInfo.setSize(banFileInfo.getSize());
            newBanFileInfo.setDate(System.currentTimeMillis());
            fileDao.addFileInfo(newBanFileInfo);
        }
        banFileInfo.setFile_id("banned");
        fileDao.addFileInfo(banFileInfo);
        hdfsDao.copyOrMove(banFileMd5, destFileMd5);
    }

    /**
     * 找到所有分享链接
     * @return
     */
    @Override
    public List<Share> findAllShare() {
        List<Share> shares = new ArrayList<>();
        List<Share> foreverShares = new ArrayList<>();
        List<Share> expiredShares = new ArrayList<>();
        List<Share> unexpiredShares = new ArrayList<>();
        List<String> list = hbaseDao.getRowKey(Constants.TABLE_SHARE);
        for(String shareMd5: list){
            Share share = shareDao.getShareInfoById(shareMd5);
            if(share.getStatus().equals("false")){
                share.setExpired("已失效");
                expiredShares.add(share);
            }
            else if(share.getExpired().equals("forever")){
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
        shares.addAll(foreverShares);
        shares.addAll(unexpiredShares);
        shares.addAll(expiredShares);
        return shares;
    }

    /**
     * 禁用分享链接
     * @param shareMd5
     */
    @Override
    public void banShare(String shareMd5) {
        Share share = shareDao.getShareInfoById(shareMd5);
        share.setStatus("false");
        shareDao.createShare(share);
    }
}
