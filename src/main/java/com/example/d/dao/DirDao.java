package com.example.d.dao;

import com.example.d.dao.basedao.HbaseDao;
import com.example.d.entity.Dir;
import com.example.d.util.Constants;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("dirDao")
public class DirDao {
    @Autowired
    private HbaseDao hbaseDao;

    /**
     * 向dir表中添加文件夹的信息
     * @param dir
     */
    public String addDirInfo(Dir dir) {
        String[] value = {dir.getDir_name(), dir.getParent_id(), dir.getSub_ids(), dir.getPath(),dir.getDate(), dir.getIsDir()};
        String rowKey = dir.getDir_id();
        hbaseDao.updateMoreData(Constants.TABLE_DIR, rowKey, Constants.FAMILY_DIR_INFO, Constants.COLUMN_DIR_INFO, value);
        if(dir.getIsDir().equals("false")){
            String[] value2 = {dir.getFile_id(), dir.getFile_size()};
            hbaseDao.updateMoreData(Constants.TABLE_DIR, rowKey, Constants.FAMILY_DIRFILE_INFO, Constants.COLUMN_DIRFILE_INFO, value2);
        }
        return rowKey;
    }

    /**
     * 向dir表中删除文件夹信息与文件信息,保留rowkey，isDir值为空值""
     * @param dirId
     */
    public void delDirInfo(String dirId) {
        String rowKey = dirId;
        hbaseDao.deleteDataByRow(Constants.TABLE_DIR, rowKey);
        hbaseDao.updateOneData(Constants.TABLE_DIR, rowKey, Constants.FAMILY_DIR_INFO, Constants.COLUMN_DIR_INFO[5], "");
    }

    /**
     * 根据文件夹id找到文件夹名、父文件夹id、目录级别、路径
     * @param id
     * @return
     */
    public Dir getDirInfoById(String id) {
        Dir dir = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_DIR, id);
        if(!result.isEmpty()) {
            dir = new Dir();
            dir.setDir_id(id);
            dir.setDir_name(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_DIR_INFO), Bytes.toBytes(Constants.COLUMN_DIR_INFO[0]))));
            dir.setParent_id(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_DIR_INFO), Bytes.toBytes(Constants.COLUMN_DIR_INFO[1]))));
            dir.setSub_ids(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_DIR_INFO), Bytes.toBytes(Constants.COLUMN_DIR_INFO[2]))));
            dir.setPath(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_DIR_INFO), Bytes.toBytes(Constants.COLUMN_DIR_INFO[3]))));
            dir.setDate(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_DIR_INFO), Bytes.toBytes(Constants.COLUMN_DIR_INFO[4]))));
            dir.setIsDir(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_DIR_INFO), Bytes.toBytes(Constants.COLUMN_DIR_INFO[5]))));
            dir.setFile_id(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_DIRFILE_INFO), Bytes.toBytes(Constants.COLUMN_DIRFILE_INFO[0]))));
            dir.setFile_size(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_DIRFILE_INFO), Bytes.toBytes(Constants.COLUMN_DIRFILE_INFO[1]))));

        }
        return dir;
    }

    /**
     * dir表按行数计算文件夹ID
     * @return
     */
    public String getDirIdByTableCount() {
        return String.valueOf(hbaseDao.getRowNum(Constants.TABLE_DIR));
    }
}
