/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.wuhailong.imagecrawer.settings;

import cn.wuhailong.imagecrawer.domain.LinkEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置类，单例模式
 * @author Administrator
 */
public class Settings {
    
    //支持6个起始地址
    private String[] seeds = new String[6];
    
    //速度，默认为1（慢）
    private int speed = 1;
    
    //睡眠时间，默认为50s, 由速度控制更改
    private long sleepTime = 50000;
    
    //连接实体类队列，保存链接限制、关键字、屏蔽字
    private List<LinkEntry> urlStarts = new ArrayList<>();
    private List<LinkEntry> imgUrlStarts = new ArrayList<>();
    
    //图片大小限制，默认2K-20M
    private int imgMinSize = 2*1024;
    private int imgMaxSize = 20*1024*1024;
    
    //获取图片存放目录
    private String filePath = "temp\\";
    
    //信号量，控制爬取状态
    private volatile boolean start;
    
    //饿汉式单例
    private static Settings settings = new Settings();
    
    /**
     * 获取配置类实例
     * @return
     */
    public static Settings getInstance(){
        return settings;
    }

    public int getSpeed() {
        return speed;
    }

    /**
     * 设置速度（同时会自动修改睡眠时间）
     * @param speed
     */
    public void setSpeed(int speed) {
        this.speed = speed;
        this.sleepTime = 50000 / speed;
    }

    public long getSleepTime() {
        return sleepTime;
    }
    
    public int getImgMinSize() {
        return imgMinSize;
    }

    public void setImgMinSize(int imgMinSize) {
        this.imgMinSize = imgMinSize;
    }

    public int getImgMaxSize() {
        return imgMaxSize;
    }

    public void setImgMaxSize(int imgMaxSize) {
        this.imgMaxSize = imgMaxSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public String[] getSeeds() {
        return seeds;
    }

    public void setSeeds(String[] seeds) {
        this.seeds = seeds;
    }

    public List<LinkEntry> getUrlStarts() {
        return urlStarts;
    }

    public void setUrlStarts(List<LinkEntry> urlStarts) {
        this.urlStarts = urlStarts;
    }

    public List<LinkEntry> getImgUrlStarts() {
        return imgUrlStarts;
    }

    public void setImgUrlStarts(List<LinkEntry> imgUrlStarts) {
        this.imgUrlStarts = imgUrlStarts;
    }
    
}
