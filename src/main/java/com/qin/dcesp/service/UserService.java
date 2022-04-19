package com.qin.dcesp.service;

import com.qin.dcesp.dao.UserMapper;
import com.qin.dcesp.entity.LoginTicket;
import com.qin.dcesp.entity.User;
import com.qin.dcesp.utils.CommunityConstant;
import com.qin.dcesp.utils.CommunityUtil;
import com.qin.dcesp.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //通过id查用户
    public User selectUserById(int id){
        return userMapper.searchUserById(id);
    }
    //通过用户名查用户
    public User selectUserByName(String username){
        return userMapper.searchUserByName(username);
    }

    //插入一个用户
    public int insertUser(User user){
        if(user != null){
            return userMapper.insertUser(user);
        }
        return -1;
    }

    //更新密码
    public int updatePassword(int id,String password){
        return userMapper.updatePassword(id,password);
    }

    //注册用户
    public Map<String,Object> registerAUser(User user){
        Map<String, Object> map = new HashMap<>();
        if(user == null){
            throw new IllegalArgumentException("参数不可为空!");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不可为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不可为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不可为空!");
            return map;
        }
        if(userMapper.searchUserByName(user.getUsername()) != null){
            map.put("usernameMsg","该用户名已存在!");
            return map;
        }
        //设置盐
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        //密码加密
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        //设置类型,默认为0(普通用户)
        user.setType(0);
        user.setCreateTime(new Date());
        insertUser(user);
        return map;
    }

    //验证登录
    public Map<String,Object> login(String username,String password,int expireSeconds){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不可为空!");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不可为空!");
            return map;
        }
        User user = selectUserByName(username);
        if(user == null){
            map.put("usernameMsg","该账号不存在!");
            return map;
        }
        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码错误!");
            return map;
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expireSeconds * 1000));

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);
        map.put("ticket",loginTicket.getTicket());
        map.put("loginUser",user);
        return map;
    }

    //退出登录
    public void logout(String loginTicket){
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket);
        LoginTicket ticket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        ticket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, ticket);
    }

    //通过登录凭证获取凭证内容
    public LoginTicket getLoginTicket(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    //需要把用户的权限存储到一个权限上下文里面.
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.selectUserById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add((new GrantedAuthority() {
            @Override
            public String getAuthority() {
                int type = user.getType();
                if(type == 0){
                    return "USER";
                }else{
                    return "ADMIN";
                }
            }
        }));
        return list;
    }
}
