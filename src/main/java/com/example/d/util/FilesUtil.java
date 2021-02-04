package com.example.d.util;

import java.text.DecimalFormat;

public class FilesUtil {
    /**
     * 格式化文件大小
     * @param fileS
     * @return
     */
    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        if(fileSizeString.equals(".00B"))
            return "0.00B";
        return fileSizeString;
    }

    /**
     * 比较文件大小
     * @param size1
     * @param size2
     * @return
     */
    public static int compareSize(String size1, String size2){
        int sizeUnit1 = getSizeUnit(size1);
        int sizeUnit2 = getSizeUnit(size2);
        if (sizeUnit1 == sizeUnit2){
            int length1 = size1.length();
            int length2 = size2.length();
            if(sizeUnit1 == 0){
                Float num1 = Float.valueOf(size1.substring(0, length1 - 1));
                Float num2 = Float.valueOf(size2.substring(0, length2 - 1));
                Float outcome = num1 - num2;
                return outcome>=0?1:-1;
            }
            else{
                Float num1 = Float.valueOf(size1.substring(0, length1 - 2));
                Float num2 = Float.valueOf(size2.substring(0, length2 - 2));
                Float outcome = num1 - num2;
                return outcome>=0?1:-1;
            }
        }
        return sizeUnit1 - sizeUnit2;
    }
    // 单位比重
    public final static int getSizeUnit(String size){
        String unit = size.substring(size.length() - 2);
        if(unit.equals("KB")){
            return 1;
        }
        else if(unit.equals("MB")){
            return 2;
        }
        else if(unit.equals("GB")){
            return 3;
        }
        else
            return 0;
    }
    /**
     * 获得文件名前缀
     * @param fileName
     * @return
     */
    public static String getFilePrefix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, splitIndex);
    }

    /**
     * 获得文件名后缀
     * @param fileName
     * @return
     */
    public static String getFileSuffix(String fileName){
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(splitIndex + 1);
    }
}