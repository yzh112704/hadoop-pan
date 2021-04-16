package com.pan.hadoop.config;


import com.pan.hadoop.dao.Init;
import com.pan.hadoop.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class InitData {
    @Autowired
    Init init;
    // 初始化内容
    @PostConstruct
    public void init() throws IOException {
        // 表没有创建时创建该表
        init.createTable(Constants.TABLE_USER, Constants.FAMILY_USER);
        init.createTable(Constants.TABLE_MAIL, Constants.FAMILY_MAIL);
        init.createTable(Constants.TABLE_MAIL_USER, Constants.FAMILY_MAIL_USER);
        init.createTable(Constants.TABLE_FILE, Constants.FAMILY_FILE);
        init.createTable(Constants.TABLE_FILEMD5, Constants.FAMILY_FILEMD5);
        init.createTable(Constants.TABLE_DIR, Constants.FAMILY_DIR);
        init.createTable(Constants.TABLE_TICKET, Constants.FAMILY_TICKET);
        init.createTable(Constants.TABLE_SHARE, Constants.FAMILY_SHARE);
        init.createTable(Constants.TABLE_ANALYSIS, Constants.FAMILY_ANALYSIS);
        init.createTable(Constants.TABLE_ADMIN, Constants.FAMILY_ADMIN);
        // 禁用文件信息是否已上传
        init.createBanFile("document");
        init.createBanFile("img");
        init.createBanFile("video");
        // 检查超级管理员是否已创建
        init.checkSuperAdmin();
        // 程序启用初始日期
        init.initStartDate();
    }
}
