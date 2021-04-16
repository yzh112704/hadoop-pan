package com.pan.hadoop.dao.conn;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class HdfsConn {
    private FileSystem fileSystem = null;
    private Configuration configuration = null;

    private static class SingletonHolder {
        private static final HdfsConn INSTANCE = new HdfsConn();
    }

    // 连接hdfs
    private HdfsConn() {
        try {
            configuration = new Configuration();
            configuration.set("fs.defaultFS", "hdfs://node:8020/");
            System.setProperty("HADOOP_USER_NAME", "root");
            configuration.set("dfs.permissions", "false");
            configuration.set("dfs.support.append", "true");
            fileSystem = FileSystem.get(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileSystem getFileSystem() {
        return SingletonHolder.INSTANCE.fileSystem;
    }

    public static Configuration getConfiguration() {
        return SingletonHolder.INSTANCE.configuration;
    }

    public static void main(String[] args) throws Exception{
        String dir = "/hdfs/test";
        FileSystem fs = getFileSystem();
        if (!fs.exists(new Path(dir))) {
            fs.mkdirs(new Path(dir));
        }
    }
}

