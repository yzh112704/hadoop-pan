package com.pan.hadoop.service;

import com.pan.hadoop.entity.LoginTicket;


public interface LoginTicketService {
    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectByTicket(String ticket);

    int updateStatus(String ticket, String status);
}
