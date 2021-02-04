package com.example.d.util;

import com.example.d.entity.User;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NavigableMap;

public class UserUtil {
    // 计算空间剩余大小
    public static void countSurplusSpace(User user){
        BigInteger getSpace = new BigInteger(user.getUse_space());
        BigInteger totalSpace = new BigInteger(user.getTotalSpace());
//        BigInteger totalSpace = new BigInteger("2147483648");
        user.setSurplus_space(totalSpace.subtract(getSpace).toString());
    }

    // 可获得列簇内所有的key值（IDs）和对应的value值（文件ID的count）
    // 只获得列簇内所有的key值（IDs）
    public static String[] getColumnsByResult(Result result, String family){
        String ids = "";
        final NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap(Bytes.toBytes(family));
        if(familyMap != null && familyMap.size() > 0){
            Iterator<byte[]> it = familyMap.keySet().iterator();
            while (it.hasNext()){
                byte[] qualifier = it.next();
                byte[] value = familyMap.get(qualifier);
                ids += Bytes.toString(qualifier) + "\t";
            }
        }
        return ids.split("\t");
    }
}

