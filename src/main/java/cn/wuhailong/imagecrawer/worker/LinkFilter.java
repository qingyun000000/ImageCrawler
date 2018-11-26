/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.wuhailong.imagecrawer.worker;

/**
 * 接口，链接筛选条件
 * @author Administrator
 */
public interface LinkFilter {
    public boolean accept(String url);
}
