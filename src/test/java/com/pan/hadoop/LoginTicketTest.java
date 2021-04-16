package com.pan.hadoop;

import com.pan.hadoop.entity.LoginTicket;
import com.pan.hadoop.service.LoginTicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LoginTicketTest {
    @Autowired
    private LoginTicketService loginTicketService;
    @Autowired
    private LoginTicket loginTicket;

    @Test
    public void testTicket(){
        String ticket = "01e86713a5d02a35ab5b357aee940ba0";
        String userId = "17031110112";
        String status = "true";
        String expired = "1604968131774";
        loginTicket.setUserId(userId);
        loginTicket.setTicket(ticket);
        loginTicket.setStatus(status);
        loginTicket.setExpired(expired);

        loginTicketService.insertLoginTicket(loginTicket);
        System.out.println("添加信息成功");
        loginTicket = loginTicketService.selectByTicket(ticket);
        System.out.println("查找");
        System.out.println(loginTicket.toString());

        loginTicketService.updateStatus(ticket, status);
        loginTicket = loginTicketService.selectByTicket(ticket);
        System.out.println("再次查找");
        System.out.println(loginTicket.toString());


    }
}
