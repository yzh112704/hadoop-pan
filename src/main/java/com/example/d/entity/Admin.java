package com.example.d.entity;

import org.springframework.stereotype.Repository;

@Repository("admin")
public class Admin {
    private String id;
    private String pwd;
    private String phone;
    private String level;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPwd() { return pwd; }
    public void setPwd(String pwd) { this.pwd = pwd; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + id + '\'' +
                ", pwd='" + pwd + '\'' +
                ", phone='" + phone + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
