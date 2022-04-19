package com.qin.dcesp.config;


import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KaptchaConfig {
    @Bean
    public Producer kaptchaProducer() {
        Properties props = new Properties();
        //指定生成图片宽度
        props.setProperty("kaptcha.image.width","100");
        //指定生成图片长度
        props.setProperty("keptcha.image.height","40");
        //指定生成字体大小
        props.setProperty("kaptcha.textproducer.font.size","32");
        //指定生成字体颜色
        props.setProperty("kaptcha.textproducer.color","0,0,0");
        //指定生成字符范围
        props.setProperty("kaptcha.textproducer.char.string","0123456789abcdefghijklmnopqrstuvwxyz");
        //指定生成字符长度
        props.setProperty("kaptcha.textproducer.char.length","4");
        //指定字符模糊形式
        props.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(props);//转载配置
        kaptcha.setConfig(config);//实例化配置
        return kaptcha;//返回Bean
    }
}
