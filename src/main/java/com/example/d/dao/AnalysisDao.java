package com.example.d.dao;

import com.example.d.dao.basedao.HbaseDao;
import com.example.d.entity.Analysis;
import com.example.d.util.Constants;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Repository("analysisDao")
public class AnalysisDao {
    @Autowired
    private HbaseDao hbaseDao;

    /**
     * 向analysis表添加分析数据
     * @param analysis
     */
    public void addAnalysis(Analysis analysis){
        String rowKey = analysis.getRowKey();
        String count = String.valueOf(Integer.valueOf(analysis.getCount()) + 1);
        hbaseDao.updateOneData(Constants.TABLE_ANALYSIS, rowKey, Constants.FAMILY_ANALYSIS_INFO, Constants.COLUMN_ANALYSIS_INFO, count);
    }

    /**
     * 向analysis表添加起始日期
     * @param analysis
     * @param timestamp
     */
    public void addAnalysisStartDate(Analysis analysis, Long timestamp){
        String rowKey = analysis.getRowKey();
        String count = analysis.getCount();
        hbaseDao.updateOneDataWithTimeStamp(Constants.TABLE_ANALYSIS, rowKey, Constants.FAMILY_ANALYSIS_INFO, Constants.COLUMN_ANALYSIS_INFO, count, timestamp);
    }

    /**
     * 获取到分析信息
     * @param rowKey
     * @return
     */
    public Analysis getAnalysisInfoByRowKey(String rowKey) {
        Analysis analysis = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_ANALYSIS, rowKey);
        if(!result.isEmpty()) {
            analysis = new Analysis();
            analysis.setRowKey(rowKey);
            analysis.setCount(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_ANALYSIS_INFO), Bytes.toBytes(Constants.COLUMN_ANALYSIS_INFO))));
        }
        return analysis;
    }

    // 获取分析数据的count值
    public String getAnalysisCount(String rowKey){
        String count = "0";
        Result result = hbaseDao.getResultByRow(Constants.TABLE_ANALYSIS, rowKey);
        if(!result.isEmpty()) {
            count = Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_ANALYSIS_INFO), Bytes.toBytes(Constants.COLUMN_ANALYSIS_INFO)));
            if(count == null)
                count = "0";
        }
        return count;
    }

    /**
     * 获取分析数据列表
     * @param filter        过滤器
     * @param startRow      起始行键
     * @param stopRow       结束行键
     * @return
     */
    public List<Analysis> getMyAnalysisList(Filter filter, String startRow, String stopRow) {
        List<Analysis> list = new ArrayList<Analysis>();
        Analysis analysis = null;
        ResultScanner resultScanner = hbaseDao.getResultScannerByFilterAndRow(Constants.TABLE_ANALYSIS, filter, startRow, stopRow);
        Iterator<Result> iter = resultScanner.iterator();
        while(iter.hasNext()) {
            Result result = iter.next();
            if(!result.isEmpty()) {
                analysis = new Analysis();
                analysis.setRowKey(Bytes.toString(result.getRow()));
                analysis.setCount(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_ANALYSIS_INFO), Bytes.toBytes(Constants.COLUMN_ANALYSIS_INFO))));
                list.add(analysis);
            }
        }
        return list;
    }

    /**
     * 根据日期获取分析数据
     * @param filter        过滤器
     * @param startRow      开始行键
     * @param stopRow       结束行键
     * @param startTime     开始时间
     * @param stopTime      结束时间
     * @return
     */
    public List<Analysis> getMyAnalysisListByDate(Filter filter, String startRow, String stopRow, Long startTime, Long stopTime) {
        List<Analysis> list = new ArrayList<Analysis>();
        Analysis analysis = null;
        ResultScanner resultScanner = hbaseDao.getResultScannerByFilterAndRowAndTime(Constants.TABLE_ANALYSIS, filter, startRow, stopRow, startTime, stopTime + 3600 * 24 * 1000);
        Iterator<Result> iter = resultScanner.iterator();
        while(iter.hasNext()) {
            Result result = iter.next();
            if(!result.isEmpty()) {
                analysis = new Analysis();
                analysis.setRowKey(Bytes.toString(result.getRow()));
                analysis.setCount(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_ANALYSIS_INFO), Bytes.toBytes(Constants.COLUMN_ANALYSIS_INFO))));
                list.add(analysis);
            }
        }
        return list;
    }

    /**
     * 向analysis表添加分析数据
     * @param analysis
     */
    public void addRandomAnalysis(Analysis analysis, Long time){
        String rowKey = analysis.getRowKey();
        String count = String.valueOf(Integer.valueOf(analysis.getCount()) + 1);
        hbaseDao.updateOneDataWithTimeStamp(Constants.TABLE_ANALYSIS, rowKey, Constants.FAMILY_ANALYSIS_INFO, Constants.COLUMN_ANALYSIS_INFO, count, time);
    }
}
