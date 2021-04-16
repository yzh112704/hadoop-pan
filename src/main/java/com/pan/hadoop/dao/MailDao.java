package com.pan.hadoop.dao;

import com.pan.hadoop.dao.basedao.HbaseDao;
import com.pan.hadoop.entity.Mail;
import com.pan.hadoop.entity.Mail_User;
import com.pan.hadoop.util.Constants;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("mailDao")
public class MailDao {
    @Autowired
    private HbaseDao hbaseDao;

    /**
     * 添加邮件信息
     * @param mail
     */
    public void createMail(Mail mail){
        String[] value = {mail.getMail(), mail.getUser_id(), mail.getAction(), mail.getExpired(), mail.getContent()};
        String rowKey = mail.getMail_md5();
        hbaseDao.updateMoreData(Constants.TABLE_MAIL, rowKey, Constants.FAMILY_MAIL_INFO, Constants.COLUMN_MAIL_INFO, value);
    }

    /**
     * 根据邮件链接的md5值找到邮件信息
     * @param mail_md5
     * @return
     */
    public Mail getMailInfoById(String mail_md5) {
        Mail mail = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_MAIL, mail_md5);
        if(!result.isEmpty()) {
            mail = new Mail();
            mail.setMail_md5(mail_md5);
            mail.setMail(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_MAIL_INFO), Bytes.toBytes(Constants.COLUMN_MAIL_INFO[0]))));
            mail.setUser_id(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_MAIL_INFO), Bytes.toBytes(Constants.COLUMN_MAIL_INFO[1]))));
            mail.setAction(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_MAIL_INFO), Bytes.toBytes(Constants.COLUMN_MAIL_INFO[2]))));
            mail.setExpired(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_MAIL_INFO), Bytes.toBytes(Constants.COLUMN_MAIL_INFO[3]))));
            mail.setContent(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_MAIL_INFO), Bytes.toBytes(Constants.COLUMN_MAIL_INFO[4]))));

        }
        return mail;
    }

    // 删除邮件信息
    public void delMailInfoById(String mail_md5){
        hbaseDao.deleteDataByRow(Constants.TABLE_MAIL, mail_md5);
    }

    /**
     * 添加邮箱用户信息（邮箱与用户绑定，一个邮箱只能注册一个账号）
     * @param mail_user
     */
    public void createMailUser(Mail_User mail_user){
        String value = mail_user.getUserId();
        String rowKey = mail_user.getMail();
        hbaseDao.updateOneData(Constants.TABLE_MAIL_USER, rowKey, Constants.FAMILY_MAIL_USER_INFO, Constants.COLUMN_MAIL_USER_INFO, value);
    }

    /**
     * 根据邮箱查找邮箱用户信息
     * @param mail
     * @return
     */
    public Mail_User getMailUserInfoById(String mail) {
        Mail_User mail_user = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_MAIL_USER, mail);
        if(!result.isEmpty()) {
            mail_user = new Mail_User();
            mail_user.setMail(mail);
            mail_user.setUserId(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_MAIL_USER_INFO), Bytes.toBytes(Constants.COLUMN_MAIL_USER_INFO))));
        }
        return mail_user;
    }

    // 删除邮箱用户信息
    public void delMailUserInfoById(String mail){
        hbaseDao.deleteDataByRow(Constants.TABLE_MAIL_USER, mail);
    }

}
