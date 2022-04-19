package com.qin.dcesp.utils;

public class RedisKeyUtil {
    //生成Rediskey的工具类
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_POST = "post";

    //生成某个实体的赞
    //最终生成的可以长这样:like:entity:entityType:entityId.用一个set来存,这个set里面存点赞的人的id
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //生成某个用户的赞的key
    //最终生成的key长这样:like:user:userId
    public static String geUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //生成某个用户关注实体的key
    //它的形式:followee:userId:entityType 存 Zset类型的,里面存的是(entityId,now)一个是实体id,一个是当前时间
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //生成某个实体拥有的粉丝
    //它的形式:follower:entityType:entityId,存Zset类型的,里面存的是userid和时间
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //优化登录
    //验证码key
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //登录凭证的key
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    //用户的key
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

    //生成uv相关的key
    public static String getUVkey(String date){
        return PREFIX_UV + SPLIT + date;
    }

    //生成区间UV
    public static String getUVkey(String startDate,String endDate){
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    //生成当日活跃用户的key
    public static String getDAUkey(String date){
        return PREFIX_DAU + SPLIT + date;
    }

    //生成区间活跃用户的key
    public static String getDAUkey(String startDate,String endDate){
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    //统计帖子分数的key
    public static String getPostScoreKey(){
        return PREFIX_POST + SPLIT + "score";
    }

}
