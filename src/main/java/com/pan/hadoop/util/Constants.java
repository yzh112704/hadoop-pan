package com.pan.hadoop.util;

import java.util.Arrays;
import java.util.List;

public class Constants {
    // 文件后缀
    // 文档
    public static final List<String> DOCUMENT_SUFFIX = Arrays.asList("csv","txt","doc","docx","xls","xlsx","ppt","pptx","pdf");
    // 图片
    public static final List<String> IMG_SUFFIX = Arrays.asList("jpg","jpeg","gif","bmp","png");
    // 视频
    public static final List<String> VIDEO_SUFFIX = Arrays.asList("avi","mp4","mp3","rmvb","flv","m4a");
    // 代码
    public static final List<String> CODE_SUFFIX = Arrays.asList("c","cpp","java","py","xml","html","php","css","js","h");
    // 其他
    public static final List<String> OTHER_SUFFIX = Arrays.asList("apk","exe","swf","torrent","zip");

    // 网盘空间大小B
    // 500MB    注册信息不完善
    public static final String SMALL_TOTAL_SPACE = "524288000";
    // 2GB      信息完善普通用户
    public static final String DEFAULT_TOTAL_SPACE = "2147483648";
    // 5GB      VIP？
    public static final String MIDDLE_TOTAL_SPACE = "5368709120";
    // 20GB     SVIP？
    public static final String LARGE_TOTAL_SPACE = "21474836480";

    // 用户表
    public static final String TABLE_USER = "user";
    // 用户信息列
    public static final String FAMILY_USER_INFO = "user_info";
    public static final String[] COLUMN_USER_INFO = { "pwd", "name", "sex", "institute", "grade", "major", "date", "phone", "address", "email", "status" };
    // 用户文件列
    public static final String FAMILY_USER_FILE = "user_file";
    // 用户文件夹列
    public static final String FAMILY_USER_DIR = "user_dir";
    // 用户分享列
    public static final String FAMILY_USER_SHARE = "user_share";
    // 用户空间列
    public static final String FAMILY_USER_SPACE = "user_space";
    public static final String[] COLUMN_USER_SPACE = { "use_space", "surplus_space", "total_space"};
    // 用户表列簇名
    public static final String[] FAMILY_USER = {FAMILY_USER_INFO, FAMILY_USER_FILE, FAMILY_USER_DIR, FAMILY_USER_SHARE, FAMILY_USER_SPACE};
//    public static final String FAMILY_USER_FRIEND = "user_friend";
//    public static final String[] COLUMN_USER_FRIEND = { "friend_id", "friend_name" };
    public static final String[] INSTITUTE = {"", "机械工程学院", "电子信息与电气工程学院", "计算机科学与信息工程学院", "土木与建筑工程学院", "化学与环境工程学院", "生物与食品工程学院", "经济管理学院", "文法学院", "外国语学院", "艺术设计学院", "数理学院","国际教育学院", "飞行学院"};

    // 邮件表
    public static final String TABLE_MAIL = "mail";
    public static final String FAMILY_MAIL_INFO = "mail_info";
    public static final String[] COLUMN_MAIL_INFO = {"mail", "user_id", "action", "expired", "content"};
    // 邮件表列簇
    public static final String[] FAMILY_MAIL = {FAMILY_MAIL_INFO};

    // 邮箱用户表
    public static final String TABLE_MAIL_USER = "mail_user";
    public static final String FAMILY_MAIL_USER_INFO = "mail_user_info";
    public static final String COLUMN_MAIL_USER_INFO = "user_id";
    // 邮箱用户表列簇
    public static final String[] FAMILY_MAIL_USER = {FAMILY_MAIL_USER_INFO};


    // 文件表
    public static final String TABLE_FILE = "file";
    public static final String FAMILY_FILE_INFO = "file_info";
    public static final String[] COLUMN_FILE_INFO = { "file_id", "file_name", "file_type", "size", "currentChunk", "totalChunks", "date" };
    // 文件表列簇
    public static final String[] FAMILY_FILE = {FAMILY_FILE_INFO};

    // 文件ID与其MD5值关联表
    public static final String TABLE_FILEMD5 = "file_MD5";
    public static final String FAMILY_FILEMD5_INFO = "MD5_info";
    public static final String[] COLUMN_FILEMD5_INFO = { "MD5", "size" };
    // 文件ID与其MD5值关联表列簇
    public static final String[] FAMILY_FILEMD5 = {FAMILY_FILEMD5_INFO};

    // 文件夹表
    public static final String TABLE_DIR = "dir";
    // 文件夹信息列
    public static final String FAMILY_DIR_INFO = "dir_info";
    public static final String[] COLUMN_DIR_INFO = { "dir_name", "parent_id", "sub_id", "path", "dir_date", "isDir" };
    // 文件夹的文件信息列
    public static final String FAMILY_DIRFILE_INFO = "dir_file";
    public static final String[] COLUMN_DIRFILE_INFO = { "file_id", "file_size" };
    // 文件夹表列簇
    public static final String[] FAMILY_DIR = {FAMILY_DIR_INFO, FAMILY_DIRFILE_INFO};

    // cookie过期时间
    // 12小时
    public static final int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    // 100天
    public static final int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
    // 登录凭证表
    public static final String TABLE_TICKET = "ticket";
    public static final String FAMILY_TICKET_INFO = "ticket_info";
    public static final String[] COLUMN_TICKET_INFO = { "user_id", "status", "expired" };
    // 登录凭证表列簇
    public static final String[] FAMILY_TICKET = {FAMILY_TICKET_INFO};

    // 分享失效时间
    // 1天、7天、30天、永久
    public static final Long DAY = 3600 * 24l;
    public static final Long WEEK = 3600 * 24 * 7l;
    public static final Long MONTH = 3600 *24 * 30l;
    public static final String FOREVER = "forever";
    // 分享表
    public static final String TABLE_SHARE = "share";
    public static final String FAMILY_SHARE_INFO = "share_info";
    public static final String[] COLUMN_SHARE_INFO = { "user_id", "dir_ids", "key", "expired" , "status" };
    // 分享表列簇
    public static final String[] FAMILY_SHARE = {FAMILY_SHARE_INFO};

    // 管理员表
    public static final String TABLE_ADMIN = "Admin";
    public static final String FAMILY_ADMIN_INFO = "info";
    public static final String[] COLUMN_ADMIN_INFO = { "phone", "pwd", "level"};
    // 管理员列簇
    public static final String[] FAMILY_ADMIN = {FAMILY_ADMIN_INFO};

    // 分析数据表
    public static final String TABLE_ANALYSIS = "analysis";
    public static final String FAMILY_ANALYSIS_INFO = "analysis_info";
    public static final String COLUMN_ANALYSIS_INFO = "count";
    // 分析数据表列簇
    public static final String[] FAMILY_ANALYSIS = {FAMILY_ANALYSIS_INFO};
}