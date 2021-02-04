package com.example.d.entity;

import org.springframework.stereotype.Repository;

@Repository("fileMd5")
public class File_MD5 {
    private String file_id;
    private String md5;
    private String size;

    public String getFile_id() { return file_id; }
    public void setFile_id(String file_id) { this.file_id = file_id; }
    public String getMd5() { return md5; }
    public void setMd5(String md5) { this.md5 = md5; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}
