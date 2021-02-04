package com.example.d.entity;

import org.springframework.stereotype.Repository;

@Repository("mail")
public class Mail {
    private String mail_md5;
    private String mail;
    private String user_id;
    private String action;
    private String expired;
    private String content;

    public String getMail_md5() { return mail_md5; }
    public void setMail_md5(String mail_md5) { this.mail_md5 = mail_md5; }
    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }
    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getExpired() { return expired; }
    public void setExpired(String expired) { this.expired = expired; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @Override
    public String toString() {
        return "Mail{" +
                "mail_md5='" + mail_md5 + '\'' +
                ", mail='" + mail + '\'' +
                ", user_id='" + user_id + '\'' +
                ", action='" + action + '\'' +
                ", expired='" + expired + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
