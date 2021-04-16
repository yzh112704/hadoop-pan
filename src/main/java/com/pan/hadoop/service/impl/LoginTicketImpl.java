package com.pan.hadoop.service.impl;

import com.pan.hadoop.dao.basedao.HbaseDao;
import com.pan.hadoop.entity.LoginTicket;
import com.pan.hadoop.service.LoginTicketService;
import com.pan.hadoop.util.Constants;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("LoginTicketService")
public class LoginTicketImpl implements LoginTicketService {
    @Autowired
    private HbaseDao hbaseDao;

    /**
     * 插入凭证数据
     * @param loginTicket
     * @return
     */
    @Override
    public int insertLoginTicket(LoginTicket loginTicket) {
        String[] value = {loginTicket.getUserId(), loginTicket.getStatus(), loginTicket.getExpired()};
        String rowKey = loginTicket.getTicket();
        hbaseDao.updateMoreData(Constants.TABLE_TICKET, rowKey, Constants.FAMILY_TICKET_INFO, Constants.COLUMN_TICKET_INFO, value);
        return 1;
    }

    /**
     * 查找凭证数据
     * @param ticket
     * @return
     */
    @Override
    public LoginTicket selectByTicket(String ticket) {
        LoginTicket loginTicket = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_TICKET, ticket);
        if(!result.isEmpty()) {
            loginTicket = new LoginTicket();
            loginTicket.setTicket(ticket);
            loginTicket.setUserId(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_TICKET_INFO), Bytes.toBytes(Constants.COLUMN_TICKET_INFO[0]))));
            loginTicket.setStatus(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_TICKET_INFO), Bytes.toBytes(Constants.COLUMN_TICKET_INFO[1]))));
            loginTicket.setExpired(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_TICKET_INFO), Bytes.toBytes(Constants.COLUMN_TICKET_INFO[2]))));
        }
        return loginTicket;
    }

    /**
     * 更新凭证数据
     * @param ticket
     * @param status
     * @return
     */
    @Override
    public int updateStatus(String ticket, String status) {
        hbaseDao.updateOneData(Constants.TABLE_TICKET, ticket, Constants.FAMILY_TICKET_INFO, Constants.COLUMN_TICKET_INFO[1], status);
        return 1;
    }
}
