package com.example.d.entity;

import org.springframework.stereotype.Repository;

@Repository("LoginTicket")
public class LoginTicket {

    private String ticket;
    private String userId;
    private String status;
    private String expired;

    public String getTicket() {
        return ticket;
    }
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getExpired() {
        return expired;
    }
    public void setExpired(String expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "LoginTicket{" +
                "ticket='" + ticket + '\'' +
                ", userId='" + userId + '\'' +
                ", status='" + status + '\'' +
                ", expired='" + expired + '\'' +
                '}';
    }
}
