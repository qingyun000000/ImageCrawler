/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.wuhailong.imagecrawer.QueueAndSet;

import java.util.Set;

/**
 * 链接集合封装类：封装网页集合和图片地址集合，用于HtmlParseTool工具返回
 * @author Administrator
 */
public class LinkSet {
    //网页集合
    Set<String> links;
    
    //图片地址集合
    Set<String> imgLinks;

    public Set<String> getLinks() {
        return links;
    }

    public void setLinks(Set<String> links) {
        this.links = links;
    }

    public Set<String> getImgLinks() {
        return imgLinks;
    }

    public void setImgLinks(Set<String> imgLinks) {
        this.imgLinks = imgLinks;
    }

    /**
     * 根据网页集合和图片地址集合初始化封装类
     * @param links
     * @param imgLinks
     */
    public LinkSet(Set<String> links, Set<String> imgLinks) {
        this.links = links;
        this.imgLinks = imgLinks;
    }
    
    
}
