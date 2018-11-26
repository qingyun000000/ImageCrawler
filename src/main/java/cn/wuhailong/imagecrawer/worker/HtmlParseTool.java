/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.wuhailong.imagecrawer.worker;

import cn.wuhailong.imagecrawer.QueueAndSet.LinkSet;
import cn.wuhailong.imagecrawer.domain.LinkEntry;
import cn.wuhailong.imagecrawer.settings.Settings;
import java.util.Set;
import java.util.HashSet;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * 网页链接分析工具
 * @author Administrator
 */
public class HtmlParseTool {
    
    /**
     * 分析网页，返回网页链接集合和图片地址链接集合的封装类
     * @param url
     * @return
     */
    public static LinkSet extracLinks(String url){
        //初始化两个集合
        Set<String> links = new HashSet<>();
        Set<String> imgLinks = new HashSet<>();
        
        //获取配置类
        Settings settings = Settings.getInstance();
        
        try{
            
            //网页链接分析器
            Parser parser = new Parser(url);
            parser.setEncoding("gb2312");
            
            //frame标签超链接
            NodeFilter frameFilter = new NodeFilter(){
                @Override
                public boolean accept(Node node) {
                    return node.getText().startsWith("frame src=");
                }
            };
            
            //过滤器：包含frame标签和a标签
            OrFilter linkFilter = new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);
            
            //获取满足过滤器的节点
            NodeList linkList = parser.extractAllNodesThatMatch(linkFilter);
            
            //遍历节点，判断
            for(int i =0 ; i< linkList.size(); i++ ){
                Node tag = linkList.elementAt(i);
                
                //a标签
                if(tag instanceof LinkTag){
                    //获取标签
                    LinkTag link = (LinkTag) tag;
                    
                    //获取链接字符串
                    String linkUrl = link.getLink();
                    
                    //筛选条件，必须满足配置类的urlStarts集合
                    if(new LinkFilter() {
                        @Override
                        public boolean accept(String url) {
                            filter: for(LinkEntry le : settings.getUrlStarts()){
                                if(le.getLink().isEmpty()){
                                    //空link跳过
                                    continue;
                                }
                                if(!url.startsWith(le.getLink())){
                                    //link开头不匹配，跳过
                                    continue;
                                }
                                if(le.getKeyOuts() != null && le.getKeyOuts().length != 0){
                                    for(String keyOut : le.getKeyOuts()){
                                        if(link.getChildrenHTML().contains(keyOut)){
                                            //匹配到屏蔽关键字，跳过
                                            continue filter;
                                        }
                                    }
                                }
                                if(le.getKeys() == null || le.getKeys().length == 0){
                                    //关键字未启用，合格
                                    return true;
                                }
                                for(String key : le.getKeys()){
                                    if(link.getChildrenHTML().contains(key)){
                                        //匹配到关键字，合格
                                        return true;
                                    }
                                }
                            }
                            //不合格情况：遍历结束link不匹配
                            return false;
                        }
                    }.accept(linkUrl)){
                        System.out.println("+++++++++"+linkUrl);
                        links.add(linkUrl);
                    }
                }else{ 
                    //frame标签，截取链接字符串
                    String frame = tag.getText();
                    int start = frame.indexOf("src=");
                    frame = frame.substring(start);

                    int end = frame.indexOf(" ");
                    if(end == -1){
                        end = frame.indexOf(">");
                    }
                    String frameUrl = frame.substring(5, end-1);
                    
                    //筛选条件，必须满足配置类的urlStarts集合
                    if(new LinkFilter() {
                        @Override
                        public boolean accept(String url) {
                            filter: for(LinkEntry le : settings.getUrlStarts()){
                                if(le.getLink().isEmpty()){
                                    //空link跳过
                                    continue;
                                }
                                if(!url.startsWith(le.getLink())){
                                    //link开头不匹配，跳过
                                    continue;
                                }
                                if(le.getKeyOuts() != null && le.getKeyOuts().length != 0){
                                    for(String keyOut : le.getKeyOuts()){
                                        if(tag.getText().contains(keyOut)){
                                            //匹配到屏蔽关键字，跳过
                                            continue filter;
                                        }
                                    }
                                }
                                if(le.getKeys() == null || le.getKeys().length == 0){
                                    //关键字未启用，合格
                                    return true;
                                }
                                for(String key : le.getKeys()){
                                    if(tag.getText().contains(key)){
                                        //匹配到关键字，合格
                                        return true;
                                    }
                                }
                            }
                            //不合格情况：遍历结束link不匹配
                            return false;
                        }
                    }.accept(frameUrl)){
                        System.out.println("+++++++++"+frameUrl);
                        links.add(frameUrl);
                    }
                }
            }
            
            //图片链接分析器
            Parser parser2 = new Parser(url);
            parser2.setEncoding("gb2312");
            
            //img标签图片链接
            NodeFilter imgFilter = new NodeFilter(){
                @Override
                public boolean accept(Node node) {
                    return node.getText().startsWith("img");
                }
            };
            
            //图片链接分析
            NodeList imageList = parser2.extractAllNodesThatMatch(imgFilter);
            for(int i =0 ; i< imageList.size(); i++ ){
                Node tag = imageList.elementAt(i);
                String imgUrl = tag.getText();
                
                //截取头部，先查找data-original=，如果没有再查找src=，没有在查找file=, 都没有跳过
                int startLength = 0;
                int start = imgUrl.indexOf("data-original=");
                startLength = 15;
                if(start == -1){
                    start = imgUrl.indexOf("src=");
                    startLength = 5;
                }
                if(start == -1){
                    start = imgUrl.indexOf("file=");
                    startLength = 6;
                }
                if(start == -1){
                    continue;
                }
                
                //先截取掉<img 和之前其他无关属性
                imgUrl = imgUrl.substring(start);

                //尾部判断，支持jpg,png,gif
                int end = imgUrl.indexOf("jpg");
                if(end == -1){
                    end = imgUrl.indexOf("png");
                }
                if(end == -1){
                    end = imgUrl.indexOf("gif");
                }
                if(end == -1){
                    continue;
                }
                
                //截取data-original=或src=，截取尾部至.jpg或者.png或者.gif
                String imageUrl = imgUrl.substring(startLength, end+3);
                
                
                //筛选条件，必须满足配置类的imgUrlStarts集合
                if(new LinkFilter() {
                        @Override
                        public boolean accept(String url) {
                            filter: for(LinkEntry le : settings.getImgUrlStarts()){
                                if(le.getLink().isEmpty()){
                                    //空link跳过
                                    continue;
                                }
                                if(!url.startsWith(le.getLink())){
                                    //link开头不匹配，跳过
                                    continue;
                                }
                                if(le.getKeyOuts() != null && le.getKeyOuts().length != 0){
                                    for(String keyOut : le.getKeyOuts()){
                                        if(tag.getText().contains(keyOut)){
                                            //匹配到屏蔽关键字，跳过
                                            continue filter;
                                        }
                                    }
                                }
                                if(le.getKeys() == null || le.getKeys().length == 0){
                                    //关键字未启用，合格
                                    return true;
                                }
                                for(String key : le.getKeys()){
                                    if(tag.getText().contains(key)){
                                        //匹配到关键字，合格
                                        return true;
                                    }
                                }
                            }
                            //不合格情况：遍历结束link不匹配
                            return false;
                        }
                    }.accept(imageUrl)){
                    imgLinks.add(imageUrl);
                }
            }
            
        }catch(ParserException e){
            e.printStackTrace();
        }
        
        //返回
        return new LinkSet(links, imgLinks);
    }
}
