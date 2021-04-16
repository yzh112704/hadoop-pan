package com.pan.hadoop.dao;


import com.pan.hadoop.dao.basedao.HdfsDao;
import com.pan.hadoop.dao.conn.HbaseConn;
import com.pan.hadoop.entity.Analysis;
import com.pan.hadoop.entity.File;
import com.pan.hadoop.util.DateUtil;
import com.pan.hadoop.util.MD5Util;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository("init")
public class Init{
    @Autowired
    private FileDao fileDao;
    @Autowired
    private AnalysisDao analysisDao;
    @Autowired
    private HdfsDao hdfsDao;
    @Autowired
    private AdminDao adminDao;
    /**
     * 创建表
     * @category create 'tableName','family1','family2','family3', ···
     * @param tableName
     * @param family
     * @throws Exception
     */
    public void createTable(String tableName, String[] family) throws IOException {
        Admin admin = HbaseConn.getConn().getAdmin();

        TableName tn = TableName.valueOf(tableName);
        HTableDescriptor desc = new HTableDescriptor(tn);
        for (int i = 0; i < family.length; i++) {
            desc.addFamily(new HColumnDescriptor(family[i]));
        }
        if (admin.tableExists(tn)) {
            System.out.println("createTable => table "+ tn + " Exists!");
            //  删除表
//            admin.disableTable(tn);
//            admin.deleteTable(tn);
//            admin.createTable(desc);
        } else {
            admin.createTable(desc);
            System.out.println("createTable => create Success! => " + tn);
        }
    }

    /**
     * 创建禁用文件信息
     * @throws Exception
     */
    public void createBanFile(String type){
        if(fileDao.getFileInfoByRowkey(type) == null){
            File file = new File();
            file.setMd5(type);
            file.setFile_id("init");
            fileDao.addFileInfo(file);
            System.out.println("upload banFileInfo: " + file.toString());
            String path = "static/ban/" + type;
            ClassPathResource cpr = new ClassPathResource(path);
            try {
                hdfsDao.put(cpr.getInputStream(), type);
                System.out.println("upload " + type + " to hdfs");
            } catch (IOException e) {
                System.out.println(type + " IOException!!");
            }
        }
    }

    /**
     *  检测超级管理员用户
     */
    public void checkSuperAdmin(){
        com.pan.hadoop.entity.Admin admin = adminDao.getAdminInfoById("administrator");
        if(admin == null){
            System.out.println("*****************************************************");
            System.out.println("*  Super admin 'administrator' is not found!\t\t*");
            System.out.println("*  Create super admin\t\t\t\t\t\t\t\t*");
            System.out.println("*  name:     administrator\t\t\t\t\t\t\t*");
            System.out.println("*  phone:    12345678901\t\t\t\t\t\t\t*");
            System.out.println("*  password: 123456\t\t\t\t\t\t\t\t\t*");
            System.out.println("*  Please change the super admin's password and\t\t*");
            System.out.println("*  \tphone as soon as possible!!\t\t\t\t\t\t*");
            System.out.println("*****************************************************\n");

            System.out.println("*****************************************");
            System.out.println("*  没有找到超级管理员！！\t\t\t\t\t*");
            System.out.println("*  创建超级管理员\t\t\t\t\t\t\t*");
            System.out.println("*  名字: administrator\t\t\t\t\t*");
            System.out.println("*  电话: 12345678901\t\t\t\t\t\t*");
            System.out.println("*  密码: 123456\t\t\t\t\t\t\t*");
            System.out.println("*  请尽快修改超级管理员的密码和电话！！\t\t\t*");
            System.out.println("*****************************************");
            admin = new com.pan.hadoop.entity.Admin();
            admin.setId("administrator");
            admin.setPwd(MD5Util.encodePwd("123456"));
            admin.setPhone("12345678901");
            admin.setLevel("admin");
            adminDao.addAdmin(admin);
        }
    }

    public void initStartDate(){
        Analysis analysis = analysisDao.getAnalysisInfoByRowKey("createDate");
        if(analysis == null){
            analysis = new Analysis();
            analysis.setRowKey("createDate");
            analysis.setCount(DateUtil.longToString("yyyy-MM-dd", System.currentTimeMillis()));
            System.out.println("Run start date: " + analysis.getCount());
        }
    }
}

