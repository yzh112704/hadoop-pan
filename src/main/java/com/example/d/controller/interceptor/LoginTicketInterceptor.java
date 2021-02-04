package com.example.d.controller.interceptor;

import com.example.d.dao.AdminDao;
import com.example.d.dao.UserDao;
import com.example.d.entity.Admin;
import com.example.d.entity.LoginTicket;
import com.example.d.entity.User;
import com.example.d.service.UserService;
import com.example.d.util.CookieUtil;
import com.example.d.util.FilesUtil;
import com.example.d.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private AdminDao adminDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private HostHolder hostHolder;

    // 在controller前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取用户凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if(ticket != null){
            // 查询凭证
            LoginTicket loginTicket = userService.fineLoginTicket(ticket);
            // 检查凭证是否有效
            if(loginTicket != null && loginTicket.getStatus().equals("true") && Long.valueOf(loginTicket.getExpired()) > System.currentTimeMillis()){
                // 根据凭证查询用户
                User user = userDao.getHostHolderUserById(loginTicket.getUserId());
                Long percent = Long.valueOf(user.getUse_space()) * 100 / Long.valueOf(user.getTotalSpace());
                user.setTotalSpace(String.valueOf(percent));
                user.setUse_space(FilesUtil.FormatFileSize(Long.valueOf(user.getUse_space()) + Long.valueOf(user.getSurplus_space())));
                if(percent < 60)
                    user.setSurplus_space("green");
                else if(percent >=60 && percent < 80)
                    user.setSurplus_space("orange");
                else if(percent >= 80)
                    user.setSurplus_space("red");
                // 在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }
        // 从cookie中获取管理员凭证
        String adminTicket = CookieUtil.getValue(request, "adminTicket");
        if(adminTicket != null) {
            // 查询凭证
            LoginTicket adminLoginTicket = userService.fineLoginTicket(adminTicket);
            // 检查凭证是否有效
            if (adminLoginTicket != null) {
                // 根据凭证查询用户
                Admin admin = adminDao.getAdminInfoById(adminLoginTicket.getUserId());
                // 在本次请求中持有用户
                hostHolder.setAdmin(admin);
            }
        }
        return true;
    }

    // 在controller后执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser", user);
        }
        Admin admin = hostHolder.getAdmin();
        if(admin != null && modelAndView != null){
            modelAndView.addObject("loginAdmin", admin);
        }
    }

    // 在TemplateEngine后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        hostHolder.clearAdmin();
    }
}
