package com.qin.dcesp.utils;

import com.qin.dcesp.entity.User;
import org.springframework.stereotype.Component;

/**持有用户的信息,代替Seesion的,而且是线程隔离的*/
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    //threadlocal是通过为每个线程对应一个map,然后往这个map里面存值实现线程隔离的.这个map就是以线程为key的.
    public void setUser(User user){
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
