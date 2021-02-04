package com.example.d.service;

import com.example.d.entity.LoginTicket;


public interface LoginTicketService {
    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectByTicket(String ticket);

    int updateStatus(String ticket, String status);
}
