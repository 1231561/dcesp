package com.qin.dcesp.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**敏感词过滤器*/
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(TrieNode.class);

    private static final String REPLACEMENT = "*";

    private class TrieNode{
        //前缀树
        //关键词结束标志
        private boolean isKeyWordEnd = false;

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //子节点,键表示子节点里的字符,值表示子节点
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        //添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }

    //根节点
    private TrieNode rootNode = new TrieNode();

    //初始化前缀树
    @PostConstruct//表示在容器初始化这个bean的时候就执行这个方法
    public void init(){
        //加载字节流
        try{
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String keyword;
            while ((keyword = reader.readLine()) != null){
                //添加到前缀树里
                addKeyword(keyword);
            }
        }catch (IOException e){
            logger.error("加载文件失败" + e.getMessage());
        }
    }

    /**过滤敏感词的方法,提供给外界调用的接口*/
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        TrieNode temp = rootNode;//指针1
        int begin = 0;//指针2
        int end = 0;//指针3
        StringBuilder sb = new StringBuilder();//结果
        while (end < text.length()) {
            char c = text.charAt(end);
            //跳过符号,也就是要跳过一些特殊符号.
            if(isSymbol(c)){
                //如果指针1在根节点
                if(temp == rootNode){
                    sb.append(c);
                    begin++;
                }
                end++;
                continue;
            }
            //检查下级节点
            temp = temp.getSubNode(c);
            if(temp == null){
                //表示以begin开头的字符不是敏感词
                sb.append(text.charAt(begin));
                end = ++begin;
                temp = rootNode;
            }else if(temp.isKeyWordEnd()){
                //发现敏感词,就键begin到end之间的字符替换成字符串
                sb.append(REPLACEMENT);
                begin = ++end;
                temp = rootNode;
            }else{
                //检查下一个字符
                end++;
            }
        }
        //还要把最后的几个字符加入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断字符是不是特殊符号
    private boolean isSymbol(Character c){
        //这个c的范围表示是东亚范围内的字
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //添加字符串到前缀树里
    private void addKeyword(String keyword){
        TrieNode temp = rootNode;
        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = temp.getSubNode(c);
            if(subNode == null){
                //初始化子节点
                subNode = new TrieNode();
                temp.addSubNode(c,subNode);
            }
            temp = subNode;
            //设置结束标志
            if(i == keyword.length() - 1){
                temp.setKeyWordEnd(true);
            }
        }
    }

}
