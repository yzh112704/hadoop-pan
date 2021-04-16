package com.pan.hadoop.entity;

import org.springframework.stereotype.Repository;

@Repository("dir")
public class Dir {
    private String dir_id;
    private String dir_name;
    private String parent_id;
    private String sub_ids;
    private String path;
    private String date;
    private String isDir;

    public void setDir_id(String dir_id) { this.dir_id = dir_id; }
    public String getDir_id() { return dir_id; }
    public void setDir_name(String dir_name) { this.dir_name = dir_name; }
    public String getDir_name() { return dir_name; }
    public void setParent_id(String parent_id) { this.parent_id = parent_id; }
    public String getParent_id() { return parent_id; }
    public void setPath(String path) { this.path = path; }
    public String getSub_ids() { return sub_ids; }
    public void setSub_ids(String sub_ids) { this.sub_ids = sub_ids; }
    public String getPath() { return path; }
    public void setDate(String date) { this.date = date; }
    public String getDate() { return date; }
    public void setIsDir(String isDir) { this.isDir = isDir; }
    public String getIsDir() { return isDir; }

    public String getDirInfo(){return getDir_id() + getDir_name() + getParent_id() + getPath() + getDate() + getIsDir();}

    @Override
    public String toString() {
        return "Dir{" +
                "dir_id='" + dir_id + '\'' +
                ", dir_name='" + dir_name + '\'' +
                ", parent_id='" + parent_id + '\'' +
                ", sub_ids='" + sub_ids + '\'' +
                ", path='" + path + '\'' +
                ", date='" + date + '\'' +
                ", isDir='" + isDir + '\'' +
                ", file_id='" + file_id + '\'' +
                ", file_size='" + file_size + '\'' +
                ", file_type='" + file_type + '\'' +
                '}';
    }

    private String file_id;
    private String file_size;

    public void setFile_id(String file_id) { this.file_id = file_id; }
    public String getFile_id() { return file_id; }
    public void setFile_size(String file_size) { this.file_size = file_size; }
    public String getFile_size() { return file_size; }

    // 显示图标使用（不存入数据库）
    public String file_type;
    public String getFile_type() { return file_type; }
    public void setFile_type(String file_type) { this.file_type = file_type; }
}
