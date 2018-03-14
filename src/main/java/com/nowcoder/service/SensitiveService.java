package com.nowcoder.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    //构造一个字典树结点类
    private class TrieNode{
        private boolean end = false;
        private Map<Character, TrieNode> SubNodes = new HashMap<>();

        public void addSubNode(Character c, TrieNode node){
            SubNodes.put(c,node);
        }

        public TrieNode getSubNode(Character c){
            return SubNodes.get(c);
        }

        public void setKeywordEnd(boolean end){
            this.end=end;
        }

        public boolean isKeywordEnd(){
            return end;
        }
    }

    private TrieNode rootNode = new TrieNode();

    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    private void addWord(String word){
        TrieNode tempNode = rootNode;
        for(int i =0;i<word.length();i++){
            Character c = word.charAt(i);
            if(tempNode.getSubNode(c)==null){
                TrieNode node = new TrieNode();
                tempNode.addSubNode(c,node);
            }
            tempNode = tempNode.getSubNode(c);
            if(i==word.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String word;
            while((word = bufferedReader.readLine())!=null){
                addWord(word.trim());
            }
            read.close();
        }catch (Exception e){
            logger.error("初始化错误"+e.getMessage());
        }
    }

    public String filter(String text){
        StringBuilder result = new StringBuilder();
        String replace = "***";
        if(StringUtils.isBlank(text))
            return text;
        TrieNode node = rootNode;
        int begin =0;
        int position = 0;
        while(position<text.length()){
            Character c = text.charAt(position);
            if(isSymbol(c)){
                if(node == rootNode){
                    result.append(c);
                    position++;begin++;
                    continue;
                }
                position++;
                continue;
            }
            node = node.getSubNode(c);
            if(node ==null){
                result.append(text.charAt(begin));
                node = rootNode;
                position = begin+1;
                begin = position;
            }else if(!node.isKeywordEnd()){
                position++;
            }else{
                result.append(replace);
                node = rootNode;
                position++;
                begin=position;
            }
        }
        result.append(text.substring(begin));
        return result.toString();
    }

    //判断是否为无意义的字符
    public boolean isSymbol(Character c){
        int ic = (int)c;
        return !(CharUtils.isAsciiAlphanumeric(c)) && !(ic>=0x2E80&&ic<=0x9FFF);
    }


    public static void main(String[] args){
        SensitiveService sensitiveService = new SensitiveService();
        sensitiveService.addWord("色情");
        System.out.println(sensitiveService.isSymbol('就'));
        String result = sensitiveService.filter("hi##, 拒绝$$色####情");
        System.out.println(result);
    }
}
