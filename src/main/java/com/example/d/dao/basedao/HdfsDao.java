package com.example.d.dao.basedao;

import com.example.d.dao.conn.HdfsConn;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Repository("hdfsDao")
public class HdfsDao {
    // hdfs文件存储路径
    private final String basePath = "/OnlineDisk/";

    /**
     * 上传文件
     * @param inputStream   输入流（文件IO流）
     * @param md5           文件md5
     * @return
     */
    public String put(InputStream inputStream, String md5) {
        try {
            OutputStream outputStream = HdfsConn.getFileSystem().create(new Path(basePath + "file/" + md5));
            IOUtils.copyBytes(inputStream, outputStream, 4096, true);
            return "end";
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return "error";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * 下载文件
     * @param downloadName          下载名字
     * @param fileMd5               文件md5
     * @param local                 下载到的路径（服务器运行的路径）
     */
    public boolean download(String downloadName, String fileMd5, String local) {
        try {
            String formatPath = basePath + "file/" + fileMd5;
            if (HdfsConn.getFileSystem().exists(new Path(formatPath))) {
                FSDataInputStream inputStream = HdfsConn.getFileSystem().open(new Path(formatPath));
                OutputStream outputStream = new FileOutputStream(local + '/' + downloadName);
                IOUtils.copyBytes(inputStream, outputStream, 4096, true);
                return true;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * hdfs重命名文件（存储文件重命名）
     * @param fileMd5           文件md5
     * @param newName           新文件名
     */
    public void renameFile(String fileMd5,String newName) {
        try {
            String formatPath = basePath + "file/" + fileMd5;
            String newformatPath = basePath + "file/" + newName;
            if (HdfsConn.getFileSystem().exists(new Path(formatPath))) {
                HdfsConn.getFileSystem().rename(new Path(formatPath), new Path(newformatPath));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制或者移动文件（禁用文件）
     * @param banFileId     要替换成的文件
     * @param destFileId    需要被替换的文件
     */
    public void copyOrMove(String banFileId, String destFileId) {
        try {
            String banFormatPath = basePath + "file/" + banFileId;
            String destFormatPath = basePath + "file/" + destFileId;
            FileUtil.copy(HdfsConn.getFileSystem(), new Path(banFormatPath), HdfsConn.getFileSystem(), new Path(destFormatPath), false,true, HdfsConn.getConfiguration());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
