package com.qin.dcesp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class User {
    private int id;
    private String username;
    private String password;
    private String salt;
    //0为普通用户,1为管理员
    private int type;
    private Date createTime;
    private String email;
}
