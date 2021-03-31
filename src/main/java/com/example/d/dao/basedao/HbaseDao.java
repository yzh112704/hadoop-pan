package com.example.d.dao.basedao;


import com.example.d.dao.conn.HbaseConn;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Repository("hbaseDao")
public class HbaseDao {

    /**
     * 一次性插入或修改多条数据,针对列族中有多个列
     * @category put 'tableName','rowKey','familyName:columnName'
     * @param tableName     表名
     * @param rowKey        行键
     * @param family        列簇名
     * @param column        列名
     * @param value         列值
     * @throws IOException
     */
    public void updateMoreData(String tableName, String rowKey,  String family, String[] column, String[] value) {
        try {
            Table table = HbaseConn.getConn().getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey));
            for (int i = 0; i < column.length; i++) {
                if(value[i] != null)
                    put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column[i]), Bytes.toBytes(value[i]));
            }
            table.put(put);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得一行数据，行健为字符串类型
     * @category get 'tableName','rowKey'
     * @param tableName         表名
     * @param rowKey            行键
     * @return Result||null
     * @throws IOException
     */
    public Result getResultByRow(String tableName, String rowKey) {
        Result result = null;
        try {
            Table table = HbaseConn.getConn().getTable(TableName.valueOf(tableName));
            Get get = new Get(rowKey.getBytes());
            result = table.get(get);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 插入或修改一条数据，针对列族中有一个列，value为String类型
     * @category put 'tableName','rowKey','familyName:columnName'
     * @param tableName         表名
     * @param rowKey            行键
     * @param family            列簇名
     * @param column            列名
     * @param value             列值
     * @throws IOException
     */
    public void updateOneData(String tableName, String rowKey, String family, String column, String value) {
        try {
            Table table = HbaseConn.getConn().getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));
            table.put(put);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入或修改一条数据，针对列族中有一个列，value为String类型
     * @category put 'tableName','rowKey','familyName:columnName',t='timestamp'
     * @param tableName         表名
     * @param rowKey            行键
     * @param family            列簇名
     * @param column            列名
     * @param value             列值
     * @param timestamp              时间戳
     * @throws IOException
     */
    public void updateOneDataWithTimeStamp(String tableName, String rowKey, String family, String column, String value, Long timestamp) {
        try {
            Table table = HbaseConn.getConn().getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey), timestamp);
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));
            table.put(put);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算行数，得到唯一的id值
     * @param tableName         表名
     * @return long
     */
    public long getRowNum(String tableName){
        long rowCount = 0;
        try {
            Table table = HbaseConn.getConn().getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            //FirstKeyOnlyFilter只会取得每行数据的第一个kv，提高count速度
            scan.setFilter(new FirstKeyOnlyFilter());
            ResultScanner rs = table.getScanner(scan);
            for (Result result : rs) {
                rowCount += result.size();
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rowCount + 1;
    }

    /**
     * 得到表内所有rowkey
     * @param tableName     表名
     * @return List<String>
     */
    public List<String> getRowKey(String tableName){
        List<String> list = new ArrayList<>();
        try {
            Table table = HbaseConn.getConn().getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            //FirstKeyOnlyFilter只会取得每行数据的第一个kv，提高count速度
            scan.setFilter(new FirstKeyOnlyFilter());
            ResultScanner rs = table.getScanner(scan);
            for (Result result : rs) {
                list.add(Bytes.toString(result.getRow()));
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 删除某一行的数据，行健为String类型
     * @category deleteAll 'tableName','rowKey'
     * @param tableName         表名
     * @param rowKey            行键
     * @throws IOException
     */
    public void deleteDataByRow(String tableName, String rowKey) {
        try {
            Table table = HbaseConn.getConn().getTable(TableName.valueOf(tableName));
            Delete deleteAll = new Delete(Bytes.toBytes(rowKey));
            table.delete(deleteAll);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除某一行中某一列的数据，行健为String类型，列名为String类型
     * @category delete 'tableName','rowKey','familyName:columnName'
     * @param tableName         表名
     * @param rowKey            行键
     * @param familyName        列簇名
     * @param columnName        列名
     * @throws IOException
     */
    public void deleteDataByColumn(String tableName, String rowKey, String familyName, String columnName) {
        try {
            Table table = HbaseConn.getConn().getTable(TableName.valueOf(tableName));
            Delete deleteColumn = new Delete(Bytes.toBytes(rowKey));
            deleteColumn.addColumns(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
            table.delete(deleteColumn);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按照一定规则扫描表（根据开始、结束字段进行过滤，节省时间）
     * @param tableName             表名
     * @param filter                过滤器
     * @param startRow              起始行键
     * @param stopRow               结束行键
     * @return ResultScanner
     * @throws IOException
     */
    public ResultScanner getResultScannerByFilterAndRow(String tableName
            , Filter filter
            , String startRow
            , String stopRow) {
        ResultScanner resultScanner = null;
        try {
            Table table = HbaseConn.getConn().getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(startRow));
            scan.setStopRow(Bytes.toBytes(stopRow));
            if (filter != null) {
                scan.setFilter(filter);
            }
            resultScanner = table.getScanner(scan);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultScanner;
    }

    /**
     * 按照一定规则扫描表（根据开始、结束字段进行过滤，节省时间；根据时间过滤获得倒某个时间段内的数据）
     * @param tableName             表名
     * @param filter                过滤器
     * @param startRow              起始行键
     * @param stopRow               结束行键
     * @param startTime             起始时间
     * @param stopTime              结束时间
     * @return ResultScanner
     */
    public ResultScanner getResultScannerByFilterAndRowAndTime(String tableName, Filter filter
            , String startRow, String stopRow
            , Long startTime, Long stopTime) {
        ResultScanner resultScanner = null;
        try {
            Table table = HbaseConn.getConn().getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(startRow));
            scan.setStopRow(Bytes.toBytes(stopRow));
            scan.setTimeRange(startTime,stopTime);
            if (filter != null) {
                scan.setFilter(filter);
            }
            resultScanner = table.getScanner(scan);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultScanner;
    }
}
