package com.example.d.dao;


import com.example.d.dao.basedao.HbaseDao;
import com.example.d.dao.basedao.HdfsDao;
import com.example.d.entity.File;
import com.example.d.entity.File_MD5;
import com.example.d.util.Constants;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.InputStream;

@Repository("fileDao")
public class FileDao {
    @Autowired
    private HbaseDao hbaseDao;

    @Autowired
    private HdfsDao hdfsDao;

    /**
     * 上传文件到hdfs中
     * @param inputStream
     * @param md5
     */
    public String upload(InputStream inputStream, String md5) {
        return hdfsDao.put(inputStream, md5);
    }

    /**
     * 下载文件，从hdfs中
     * @param downloadName
     * @param fileMd5
     * @param local
     * @return
     */
    public boolean downloadFile(String downloadName, String fileMd5, String local) {
        return hdfsDao.download(downloadName, fileMd5, local);
    }


    /**
     * 向file表中添加文件的信息
     * @param file
     */
    public String addFileInfo(File file) {
        String[] value = {file.getFile_id(), file.getFile_name() , file.getFile_type(), String.valueOf(file.getSize()), String.valueOf(file.getCurrentChunk()), String.valueOf(file.getTotalChunks()), String.valueOf(file.getDate())};
        String rowKey = file.getMd5();
        hbaseDao.updateMoreData(Constants.TABLE_FILE, rowKey, Constants.FAMILY_FILE_INFO, Constants.COLUMN_FILE_INFO, value);
        return rowKey;
    }


    /**
     * 根据文件md5值找到原文件信息，文件ID、文件名、文件类型、文件大小、上传日期
     * @param md5
     * @return
     */
    public File getFileInfoByRowkey(String md5) {
        File file = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_FILE, md5);
        if(!result.isEmpty()) {
            file = new File();
            file.setMd5(md5);
            file.setFile_id(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_FILE_INFO), Bytes.toBytes(Constants.COLUMN_FILE_INFO[0]))));
            file.setFile_name(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_FILE_INFO), Bytes.toBytes(Constants.COLUMN_FILE_INFO[1]))));
            file.setFile_type(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_FILE_INFO), Bytes.toBytes(Constants.COLUMN_FILE_INFO[2]))));
            file.setSize(Long.valueOf(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_FILE_INFO), Bytes.toBytes(Constants.COLUMN_FILE_INFO[3])))));
            file.setCurrentChunk(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_FILE_INFO), Bytes.toBytes(Constants.COLUMN_FILE_INFO[4]))));
            file.setTotalChunks(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_FILE_INFO), Bytes.toBytes(Constants.COLUMN_FILE_INFO[5]))));
            file.setDate(Long.valueOf(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_FILE_INFO), Bytes.toBytes(Constants.COLUMN_FILE_INFO[6])))));
        }
        return file;
    }
    /**
     * 向file表中添加文件的信息
     * @param file
     */
    public String updateFileCurrentChunkAndTotalChunks(File file) {
        String rowKey = file.getMd5();
        hbaseDao.updateOneData(Constants.TABLE_FILE, rowKey, Constants.FAMILY_FILE_INFO, Constants.COLUMN_FILE_INFO[4], String.valueOf(file.getCurrentChunk()));
        hbaseDao.updateOneData(Constants.TABLE_FILE, rowKey, Constants.FAMILY_FILE_INFO, Constants.COLUMN_FILE_INFO[5], String.valueOf(file.getTotalChunks()));
        return rowKey;
    }
    /**
     * file表按行数计算文件ID
     * @return
     */
    public String getFileIdByTableCount() {
        return String.valueOf(hbaseDao.getRowNum(Constants.TABLE_FILE));
    }

    /**
     * 向fileMD5表中添加文件的信息
     * @param file
     */
    public String addFileMD5(File file) {
        String rowKey = file.getFile_id();
        hbaseDao.updateOneData(Constants.TABLE_FILEMD5, rowKey, Constants.FAMILY_FILEMD5_INFO, Constants.COLUMN_FILEMD5_INFO[0], file.getMd5());
        hbaseDao.updateOneData(Constants.TABLE_FILEMD5, rowKey, Constants.FAMILY_FILEMD5_INFO, Constants.COLUMN_FILEMD5_INFO[1], String.valueOf(file.getSize()));
        return rowKey;
    }

    /**
     * 根据文件ID找到文件MD5值、文件大小
     * @param id
     * @return
     */
    public File_MD5 getFileMD5ById(String id) {
        File_MD5 file_md5 = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_FILEMD5, id);
        if(!result.isEmpty()) {
            file_md5 = new File_MD5();
            file_md5.setFile_id(id);
            file_md5.setMd5(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_FILEMD5_INFO), Bytes.toBytes(Constants.COLUMN_FILEMD5_INFO[0]))));
            file_md5.setSize(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_FILEMD5_INFO), Bytes.toBytes(Constants.COLUMN_FILEMD5_INFO[1]))));
        }
        return file_md5;
    }
}