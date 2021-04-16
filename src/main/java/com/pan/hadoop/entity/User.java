package com.pan.hadoop.entity;

import org.springframework.stereotype.Repository;

@Repository("user")
public class User {
    // User_Info
    private String id; // 学号
    private String pwd; // 密码
    private String name; // 姓名
    private String sex; // 性别
    private String institute; // 学院
    private String grade; // 年级
    private String major; // 专业
    private String date; // 生日
    private String phone; // 电话
    private String address; // 地址
    private String mail; // 邮箱
    private String status; // 状态（是否已激活）

    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
    }
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) { this.pwd = pwd; }
    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public String getInstitute() { return institute; }
    public void setInstitute(String institute) { this.institute = institute; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade;}
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getMail() { return mail; }
    public void setMail(String mail) {
        this.mail = mail;
    }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserInfo() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", pwd='" + pwd + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", institute='" + institute + '\'' +
                ", grade='" + grade + '\'' +
                ", major='" + major + '\'' +
                ", date='" + date + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", mail='" + mail + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    // User_File
    private String file_id; //  文件ID
    public String getFile_id() { return file_id; }
    public void setFile_id(String file_id) { this.file_id = file_id; }
    private String file_count;
    public String getFile_count() { return file_count; }
    public void setFile_count(String file_count) { this.file_count = file_count; }
    private String[] file_ids; //  获取到用户所有文件ID
    public String[] getFile_ids() { return file_ids; }
    public void setFile_ids(String[] file_ids) { this.file_ids = file_ids; }

    // User_Dir
    private String dir_id;
    private String[] dir_ids; //  获取到用户所有文件夹ID
    public String getDir_id() { return dir_id; }
    public void setDir_id(String dir_id) { this.dir_id = dir_id; }
    public String[] getDir_ids() { return dir_ids; }
    public void setDir_ids(String[] dir_ids) { this.dir_ids = dir_ids; }

    // User_Share
    private String share_md5;    // 分享链接的MD5
    private String[] share_md5s; //  获取到用户所有分享链接
    public String getShare_md5() { return share_md5; }
    public void setShare_md5(String share_md5) { this.share_md5 = share_md5; }
    public String[] getShare_md5s() { return share_md5s; }
    public void setShare_md5s(String[] share_md5s) { this.share_md5s = share_md5s; }


    // User_Space
    private String use_space; // 已用空间（B）
    private String surplus_space;// 剩余空间（B）
    private String totalSpace;// 总空间2GB
    public String getUse_space() { return use_space; }
    public void setUse_space(String use_space) { this.use_space = use_space; }
    public String getSurplus_space() { return surplus_space; }
    public void setSurplus_space(String surplus_space) { this.surplus_space = surplus_space; }
    public String getTotalSpace() { return totalSpace; }
    public void setTotalSpace(String totalSpace) { this.totalSpace = totalSpace; }

}