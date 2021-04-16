package com.pan.hadoop.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Util {

    // 盐
    private static final String staticSalt = "onlinedisk";

    /**
     * 计算输入流的MD5值
     * @param in
     * @return MD5字符串
     */
    public static String getFileMD5(InputStream in) {
        String md5=null;
        MessageDigest digest = null;//messagedigest一定要为局部变量，为了保证线程安全。写成全局变量在多线程情况下，md5值计算将出现问题。
        byte buffer[] = new byte[8192];
        int len;
        try {
            digest =MessageDigest.getInstance("MD5");
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            md5= bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return md5;
    }

    /**
     * 根据用户id和用户输入的原始密码，进行MD5加密
     * @param userPwd
     * @return 加密后的字符串
     */
    public static String encodePwd(String userPwd) {
        return DigestUtils.md5Hex(userPwd + staticSalt);
    }

    /**
     * 判断用户输入的密码是否正确
     * @param userPwd：当前输入的密码
     * @param dbPwd：数据库中存储的密码
     * @return true:输入正确 false：输入错误
     */
    public static boolean isPwdRight(String userPwd, String dbPwd) {
        boolean rs = false;
        if (encodePwd(userPwd).equals(dbPwd)) {
            rs = true;
        }
        return rs;
    }
}

