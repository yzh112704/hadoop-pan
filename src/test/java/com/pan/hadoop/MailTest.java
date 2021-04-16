package com.pan.hadoop;

import com.pan.hadoop.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@SpringBootTest
public class MailTest {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void mailTest(){
        mailClient.sendMail("910421824@qq.com", "TestTitle", "welcome2");
    }

    @Test
    public void htmlMailTest(){
        Context context = new Context();
        context.setVariable("id","17031110106");
        context.setVariable("url","http://www.baidu.com");
//        context.setVariable("data", DateUtil.longToString("yyyy年MM月dd日",System.currentTimeMillis()));
//        String content = templateEngine.process("/mail/register", context);
        String content = templateEngine.process("/mail/mailForget", context);
        System.out.println(content);

        mailClient.sendMail("1803413819@qq.com", "xx网盘忘记密码邮件", content);
    }
}
