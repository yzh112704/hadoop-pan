package com.pan.hadoop.dao;

import com.pan.hadoop.dao.basedao.HbaseDao;
import com.pan.hadoop.entity.Share;
import com.pan.hadoop.util.Constants;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("shareDao")
public class ShareDao {
    @Autowired
    private HbaseDao hbaseDao;

    /**
     * 添加分享信息
     * @param share
     */
    public void createShare(Share share){
        String[] value = {share.getUser_id(), share.getDir_ids(), share.getKey(), share.getExpired(), share.getStatus()};
        String rowKey = share.getShare_md5();
        hbaseDao.updateMoreData(Constants.TABLE_SHARE, rowKey, Constants.FAMILY_SHARE_INFO, Constants.COLUMN_SHARE_INFO, value);
    }

    /**
     * 根据分享链接的md5值找到文件夹ids
     * @param share_md5
     * @return
     */
    public Share getShareInfoById(String share_md5) {
        Share share = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_SHARE, share_md5);
        if(!result.isEmpty()) {
            share = new Share();
            share.setShare_md5(share_md5);
            share.setUser_id(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_SHARE_INFO), Bytes.toBytes(Constants.COLUMN_SHARE_INFO[0]))));
            share.setDir_ids(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_SHARE_INFO), Bytes.toBytes(Constants.COLUMN_SHARE_INFO[1]))));
            share.setKey(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_SHARE_INFO), Bytes.toBytes(Constants.COLUMN_SHARE_INFO[2]))));
            share.setExpired(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_SHARE_INFO), Bytes.toBytes(Constants.COLUMN_SHARE_INFO[3]))));
            share.setStatus(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_SHARE_INFO), Bytes.toBytes(Constants.COLUMN_SHARE_INFO[4]))));
        }
        return share;
    }

    // 删除分享
    public void delShareInfoById(String share_md5){
        hbaseDao.deleteDataByRow(Constants.TABLE_SHARE, share_md5);
    }
}
