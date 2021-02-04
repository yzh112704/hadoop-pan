package com.example.d;

import com.example.d.dao.AnalysisDao;
import com.example.d.dao.DirDao;
import com.example.d.dao.FileDao;
import com.example.d.dao.UserDao;
import com.example.d.dao.basedao.HbaseDao;
import com.example.d.entity.*;
import com.example.d.service.AdminService;
import com.example.d.service.FileService;
import com.example.d.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

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
