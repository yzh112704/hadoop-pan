package com.example.d.entity;

import org.springframework.stereotype.Repository;

@Repository("mail_user")
public class Mail_User {
    private String mail;
    private String userId;

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "Mail_User{" +
                "mail='" + mail + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
