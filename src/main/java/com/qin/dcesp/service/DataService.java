package com.qin.dcesp.service;

import com.qin.dcesp.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {
    @Autowired
    RedisTemplate redisTemplate;

    private SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");

    //将用户的IP计入UV
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUVkey(simpleDate.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }

    //统计日期范围内的UV
    public long calculatoUV(Date start,Date end){
        if(start == null || end == null){
            throw new IllegalArgumentException("参数不可为空");
        }
        //整理该日期范围内的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getUVkey(simpleDate.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE,1);//日期+1
        }
        //合并这些数据
        String redisKey = RedisKeyUtil.getUVkey(simpleDate.format(start),simpleDate.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());
        //返回统计结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    //将用户计入DAU
    public void recordDAU(int userid){
        String redisKey = RedisKeyUtil.getDAUkey(simpleDate.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,userid,true);
    }

    //统计区间内的DAU
    public long calculatoDAU(Date start,Date end) {
        if(start == null || end == null){
            throw new IllegalArgumentException("日期不可为空");
        }
        //整理该日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getDAUkey(simpleDate.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE,1);
        }
        //进行或运算
        return (long) redisTemplate.execute((RedisCallback) redisConnection -> {
            String redisKey = RedisKeyUtil.getDAUkey(simpleDate.format(start),simpleDate.format(end));
            redisConnection.bitOp(RedisStringCommands.BitOperation.OR,redisKey.getBytes(),keyList.toArray(new byte[0][0]));
            return redisConnection.bitCount(redisKey.getBytes());
        });
    }
}
