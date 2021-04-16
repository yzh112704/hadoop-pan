package com.pan.hadoop.entity;

import org.springframework.stereotype.Repository;

@Repository("share")
public class Share {
    private String share_md5;
    private String user_id;
    private String dir_ids;
    private String key;
    private String expired;
    private String status;

    public String getShare_md5() { return share_md5; }
    public void setShare_md5(String share_md5) { this.share_md5 = share_md5; }
    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }
    public String getDir_ids() { return dir_ids; }
    public void setDir_ids(String dir_ids) { this.dir_ids = dir_ids; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getExpired() { return expired; }
    public void setExpired(String expired) { this.expired = expired; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Share{" +
                "share_md5='" + share_md5 + '\'' +
                ", user_id='" + user_id + '\'' +
                ", dir_ids='" + dir_ids + '\'' +
                ", key='" + key + '\'' +
                ", expired='" + expired + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
