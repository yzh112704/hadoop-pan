package com.example.d.dao;

import com.example.d.dao.basedao.HbaseDao;
import com.example.d.entity.Admin;
import com.example.d.util.Constants;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("adminDao")
public class AdminDao {
    @Autowired
    private HbaseDao hbaseDao;

    /**
     * 向Admin表添加管理员
     * @param admin
     */
    public void addAdmin(Admin admin){
        String[] value = {admin.getPhone(), admin.getPwd(), admin.getLevel()};
        String rowKey = admin.getId();
        hbaseDao.updateMoreData(Constants.TABLE_ADMIN, rowKey, Constants.FAMILY_ADMIN_INFO, Constants.COLUMN_ADMIN_INFO, value);
    }

    /**
     * 根据管理员ID获取管理员信息
     * @param id
     * @return
     */
    public Admin getAdminInfoById(String id) {
        Admin admin = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_ADMIN, id);
        if(!result.isEmpty()) {
            admin = new Admin();
            admin.setId(id);
            admin.setPhone(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_ADMIN_INFO), Bytes.toBytes(Constants.COLUMN_ADMIN_INFO[0]))));
            admin.setPwd(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_ADMIN_INFO), Bytes.toBytes(Constants.COLUMN_ADMIN_INFO[1]))));
            admin.setLevel(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_ADMIN_INFO), Bytes.toBytes(Constants.COLUMN_ADMIN_INFO[2]))));
        }
        return admin;
    }

    // 删除管理员信息
    public void delAdminInfoById(String id){ hbaseDao.deleteDataByRow(Constants.TABLE_ADMIN, id); }
}
