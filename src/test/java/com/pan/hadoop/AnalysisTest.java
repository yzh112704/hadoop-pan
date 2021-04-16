package com.pan.hadoop;

import com.pan.hadoop.dao.AnalysisDao;
import com.pan.hadoop.dao.DirDao;
import com.pan.hadoop.dao.FileDao;
import com.pan.hadoop.dao.UserDao;
import com.pan.hadoop.dao.basedao.HbaseDao;
import com.pan.hadoop.service.AdminService;
import com.pan.hadoop.service.FileService;
import com.pan.hadoop.service.UserService;
import com.pan.hadoop.util.Constants;
import com.pan.hadoop.util.DateUtil;
import com.pan.hadoop.entity.Analysis;
import com.pan.hadoop.entity.Dir;
import com.pan.hadoop.entity.File;
import com.pan.hadoop.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class AnalysisTest {
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
    @Autowired
    private AnalysisDao analysisDao;

    @Test
    public void test(){
        List<String> share_ids = hbaseDao.getRowKey(Constants.TABLE_SHARE);
        for(String share_id: share_ids){
            System.out.println(share_id);
        }
        System.out.println("\n\n");
        Random random = new Random();
        String id = "";
        int i = 10;
        while (i >= 0) {
            int randomNum = random.nextInt(100);
            id = share_ids.get(randomNum % share_ids.size());
            System.out.println(id);
            i--;
        }
        //        System.out.println("*****************************************************");
//        System.out.println("*  Super admin 'administrator' is not found!\t\t*");
//        System.out.println("*  Create super admin\t\t\t\t\t\t\t\t*");
//        System.out.println("*  name:     administrator\t\t\t\t\t\t\t*");
//        System.out.println("*  phone:    12345678901\t\t\t\t\t\t\t*");
//        System.out.println("*  password: 123456\t\t\t\t\t\t\t\t\t*");
//        System.out.println("*  Please change the super admin's password and\t\t*");
//        System.out.println("*  \tphone as soon as possible!!\t\t\t\t\t\t*");
//        System.out.println("*****************************************************\n");
//
//        System.out.println("*****************************************");
//        System.out.println("*  没有找到超级管理员！！\t\t\t\t\t*");
//        System.out.println("*  创建超级管理员\t\t\t\t\t\t\t*");
//        System.out.println("*  名字: administrator\t\t\t\t\t*");
//        System.out.println("*  电话: 12345678901\t\t\t\t\t\t*");
//        System.out.println("*  密码: 123456\t\t\t\t\t\t\t*");
//        System.out.println("*  请尽快修改超级管理员的密码和电话！！\t\t\t*");
//        System.out.println("*****************************************");

    }

    @Test
    public void addAnalysisTest(){
        String userUpload = "user_upload_";
        String userDownload = "user_download_";
        String userShare = "user_share_";

        String fileUpload = "file_upload_";
        String fileDownload = "file_download_";

        String shareOpen = "share_open_";

        String startDate = "2020-11-20";
        String endDate = "2020-11-22";
        String choice = userUpload;
        String id = "17031110139";
        String date = "2020-11-23";
        String count = "23";
        Analysis analysis = new Analysis();
        analysis.setRowKey(choice + id + "_" + date);
        analysis.setCount(count);
        analysisDao.addAnalysis(analysis);
    }

    @Test
    public void ChangeAnalysisStartDateTest(){
        Analysis analysis = new Analysis();
        analysis.setRowKey("createDate");
        analysis.setCount("2021-01-01");
        analysisDao.addAnalysisStartDate(analysis, DateUtil.StringToLong(analysis.getCount()) + 1);
    }

    @Test
    public void randomAddAnalysisDate(){
//        每日生成数据次数
        int day_count = 10;
//        每条的数据次数范围
        int count_size = 3;
//        开始结束日期，不填写默认（开始：数据库记录的程序运行起始日期；结束：当前日期）
        String start_date = "";
        String end_date = "";

        String choice = "";
        String id = "";
        String date = "";
        String count = "";

        String[] filterHeadList = {"file_download_", "file_upload_", "share_open_", "user_download_", "user_share_", "user_upload_", "user_upload_~"};

        List<String> user_ids = hbaseDao.getRowKey(Constants.TABLE_USER);
        List<String> file_ids = hbaseDao.getRowKey(Constants.TABLE_FILEMD5);
        List<String> share_ids = hbaseDao.getRowKey(Constants.TABLE_SHARE);

        Analysis analysis = analysisDao.getAnalysisInfoByRowKey("createDate");
        long startTime = 0l;
        long endTime = 0l;

        if(start_date.equals(""))
            startTime = DateUtil.StringToLong(analysis.getCount());
        else
            startTime = DateUtil.StringToLong(start_date);
        long time = startTime + 1;

        if(end_date.equals(""))
            endTime = System.currentTimeMillis();
        else
            endTime = DateUtil.StringToLong(end_date);

        analysis = new Analysis();
        Random random = new Random();
        while(time < endTime){
            int randomNum = random.nextInt(100);
            choice = filterHeadList[randomNum % (filterHeadList.length - 1)];
            if(choice.startsWith("user")){
                id = user_ids.get(randomNum % user_ids.size());
            }
            if(choice.startsWith("file")){
                id = file_ids.get(randomNum % file_ids.size());
            }
            if(choice.startsWith("share")){
                id = share_ids.get(randomNum % share_ids.size());
            }
            time += 3600l * 24 * 1000 / day_count;
            date = DateUtil.longToString("yyyy-MM-dd", time);
            count = String.valueOf(random.nextInt(count_size) + 1);
            System.out.println(choice + id + "_" + date + " : " + count);
            analysis.setRowKey(choice + id + "_" + date);
            analysis.setCount(count);
            analysisDao.addRandomAnalysis(analysis, time);
            try {
                TimeUnit.MILLISECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void analysisDateTest(){
        String userUpload = "user_upload_";
        String userDownload = "user_download_";
        String userShare = "user_share_";

        String fileUpload = "file_upload_";
        String fileDownload = "file_download_";

        String shareOpen = "share_open_";

        String startDate = "2020-11-20";
        String endDate = "2020-11-22";
        String choice = userUpload;
        int top = 5;
        // datas = [[['count', 'top', 'id'],[]],[]]
        List<List<String>> datas = adminService.analysisTopDataByDate(choice, top, startDate, endDate);
        for(List<String> data: datas){
            for(String s : data){
                System.out.println(s);
            }
        }
        System.out.println("----------------------------------------------------");
        // datas = [[['date','count'],[]],[]]
        datas = adminService.analysisDay("user_share_", startDate, endDate);
        for(List<String> data: datas){
            for(String s : data){
                System.out.println(s);
            }
        }
        System.out.println("----------------------------------------------------");
        // list = id:Top:count:choice
        List<String> list = adminService.analysisTopLineByDate("share_open_",Integer.valueOf(top), startDate, endDate);
        for(String s: list){
            System.out.println(s);
        }
    }
}
