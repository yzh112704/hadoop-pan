package com.pan.hadoop.util;

import com.pan.hadoop.entity.Admin;
import com.pan.hadoop.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户或管理员信息，用于代替session对象
 */
@Component
public class HostHolder {

    // User线程
    private ThreadLocal<User> users = new ThreadLocal<>();

    // 设置用户
    public void setUser(User user){
        users.set(user);
    }

    // 获取用户
    public User getUser(){ return users.get(); }

    // 清除用户
    public void clear(){ users.remove(); }

    // 管理员线程
    private ThreadLocal<Admin> admins = new ThreadLocal<>();

    // 设置管理员
    public void setAdmin(Admin admin){
        admins.set(admin);
    }

    // 获取管理员
    public Admin getAdmin(){ return admins.get(); }

    // 清除管理员
    public void clearAdmin(){ admins.remove(); }
}
