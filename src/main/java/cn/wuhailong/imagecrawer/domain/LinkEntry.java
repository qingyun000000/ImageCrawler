/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.wuhailong.imagecrawer.domain;

/**
 * 链接实体类：用于封装界面传入的链接限制、关键字限制和屏蔽字限制
 * @author Administrator
 */
public class LinkEntry {
    //链接限制
    private String link;
    //关键字限制
    private String[] keys;
    //屏蔽字限制
    private String[] keyOuts;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] key) {
        this.keys = key;
    }

    public String[] getKeyOuts() {
        return keyOuts;
    }

    public void setKeyOuts(String[] keyOut) {
        this.keyOuts = keyOut;
    }
    
}
