package com.qin.dcesp.utils;

public interface CommunityConstant {

    //激活成功状态
    int ACTIVATION_SUCCESS = 0;
    //重复激活
    int ACTIVATION_REPEAT = 1;
    //激活失败
    int ACTIVATION_FAILURE = 2;
    //默认凭证的过期时长
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    //记住我的过期时长
    int REMEBER_SECONDS = 3600 * 12 * 12;
    //评论类型
    int ENTITY_TYPE_POST = 1;
    int ENTITY_TYPE_COMMENT = 2;
    //实体类型
    int ENTITY_TYPE_USER = 3;
    //主体:评论
    String TOPIC_COMMENT = "comment";
    //主体:点赞
    String TOPIC_LIKE = "like";
    //主体:关注
    String TOPIC_FOLLOW = "follow";
    //系统用户ID
    int SYSTEM_USER_ID = 1;
    //搜索主题,发帖
    String TOPIC_PUBLISH = "publish";
    //删帖事件
    String TOPIC_DELETE = "delete";
    //权限:普通用户
    String AUTHORITY_USER = "user";
    //权限:管理员
    String AUTHORITY_ADMIN = "admin";
    //权限:版主
    String AUTHORITY_MODERATOR = "moderator";
    //主题:分享
    String TOPIC_SHARE = "share";
}
