package com.pan.hadoop.entity;

import org.springframework.stereotype.Repository;

@Repository("file")
public class File {
    // File_Info
    private String md5;
    private String file_id;
    private String file_name;
    private String file_type;
    private long size;
    private String currentChunk;
    private String totalChunks;
    private long date;


    public String getMd5() { return md5; }
    public void setMd5(String md5) { this.md5 = md5; }
    public String getFile_id() { return file_id; }
    public void setFile_id(String file_id) { this.file_id = file_id; }
    public String getFile_name() { return file_name; }
    public void setFile_name(String file_name) { this.file_name = file_name; }
    public String getFile_type() { return file_type; }
    public void setFile_type(String file_type) { this.file_type = file_type; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public String getCurrentChunk() { return currentChunk; }
    public void setCurrentChunk(String currentChunk) { this.currentChunk = currentChunk; }
    public String getTotalChunks() { return totalChunks; }
    public void setTotalChunks(String totalChunks) { this.totalChunks = totalChunks; }
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    @Override
    public String toString() {
        return "File{" +
                "md5='" + md5 + '\'' +
                ", file_id='" + file_id + '\'' +
                ", file_name='" + file_name + '\'' +
                ", file_type='" + file_type + '\'' +
                ", size=" + size +
                ", currentChunk=" + currentChunk +
                ", totalChunks=" + totalChunks +
                ", date=" + date +
                '}';
    }
}
