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
/**登录凭证实体
 * 该凭证信息存在Redis中
 * */
public class LoginTicket {
    private int id;//凭证id
    private int userId;//用户id
    private String ticket;//凭证内容
    private int status;//凭证状态 : 0表示登录,1表示退出登录
    private Date expired;//凭证过期时间
}
