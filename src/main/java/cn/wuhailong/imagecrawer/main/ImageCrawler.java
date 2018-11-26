/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.wuhailong.imagecrawer.main;

import cn.wuhailong.imagecrawer.worker.HtmlParseTool;
import cn.wuhailong.imagecrawer.worker.DownLoadFile;
import cn.wuhailong.imagecrawer.QueueAndSet.LinkQueue;
import cn.wuhailong.imagecrawer.QueueAndSet.LinkSet;
import cn.wuhailong.imagecrawer.settings.Settings;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 爬虫核心类
 * @author Administrator
 */
public class ImageCrawler {
    //获取配置
    private Settings settings = Settings.getInstance();
    
    /*
    * 初始化方法：将初始网页加入到队列
    */
    private void initCrawlerWithSeeds(String[] seeds){
        for(int i=0; i < seeds.length; i++){
            LinkQueue.addUnVisitedUrl(seeds[i]);
        }
    }
    
    /**
     * 开启爬虫运行
     */
    public void startCrawler(){
        //初始化起始网页
        initCrawlerWithSeeds(settings.getSeeds());
        
        //读取网页队列的线程
        Thread parseThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //当信号量为真时，才读取网页队列
                while(settings.isStart()){
                    
                    //如果待爬取队列为空，睡500ms再判断
                    while(LinkQueue.unVisitedUrlsEmpty()){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ImageCrawler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    //取出网页
                    String visitUrl = (String)LinkQueue.unVisitedUrlDeQueue();
                    
                    //保障，如果为空，则跳过
                    if(visitUrl == null){
                        continue;
                    }
                    
                    //分析网页
                    htmlParse(visitUrl);
                    
                    //将此网页加入到已爬取的网页
                    LinkQueue.addVisitedUrl(visitUrl);
                    
                    //根据设定时间睡眠，控制爬取速度：这里是唯一可调整爬取速度的地方
                    try {
                        Thread.sleep(settings.getSleepTime());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ImageCrawler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        parseThread.start();
        
        //下载图片队列的线程
        Thread downThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    
                    //如果待下载图片地址队列为空，睡0.5s再判断
                    while(LinkQueue.unVisitedImgUrlsEmpty()){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ImageCrawler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    //获取图片地址
                    String imgUrl = (String)LinkQueue.unVisitedImgUrlDeQueue();
                    
                    //保障，如果为空，则跳过
                    if(imgUrl == null){
                        continue;
                    }
                    
                    //下载
                    downImage(imgUrl);
                    
                    //存入爬取完队列
                    LinkQueue.addVisitedImgUrl(imgUrl);
                }
            }
        });
        downThread.start();
        
    }
    
    //线程池
    private ExecutorService htmlParsePool;
    private ExecutorService downImagePool;
    
    /*
    分析网页链接，创建新线程分析
    */
    private void htmlParse(String visitUrl){
        
        //为了重复运行，线程池未初始化，或者已经关闭后，新建线程池
        if(htmlParsePool == null || htmlParsePool.isTerminated()){
            //分析网页的线程池大小为10
            htmlParsePool = Executors.newFixedThreadPool(10);
        }
        
        //提交线程
        htmlParsePool.execute(new Runnable() {
                @Override
                public void run() {
                    //分析链接,返回集合封装类
                    LinkSet linkSet = HtmlParseTool.extracLinks(visitUrl);
                    
                    //分别存入到待爬取网页队列和待爬取图片地址队列
                    for(String link : linkSet.getLinks()){
                        LinkQueue.addUnVisitedUrl(link);
                    }
                    for(String link : linkSet.getImgLinks()){
                        LinkQueue.addUnVisitedImgUrl(link);
                    }
                }
            });
    }
    
    /*
    下载图片，创建新线程下载
    */
    private void downImage(String imgUrl){
        //为了重复运行，线程池未初始化，或者已经关闭后，新建线程池
        if(downImagePool == null || downImagePool.isTerminated()){
            downImagePool = Executors.newFixedThreadPool(5);
        }
        
        //提交线程
        downImagePool.execute(new Runnable() {
                @Override
                public void run() {
                    
                    //获取下载类，下载
                    DownLoadFile downLoader = new DownLoadFile();
                    downLoader.downloadImage(imgUrl);
                }
            });
    }
    
    /**
     * 停止爬虫运行（关闭线程池）
     */
    public void stopCrawler(){
        //关闭线程池
        htmlParsePool.shutdown();
        downImagePool.shutdown();
        
        while (true) {
            //当确认两个线程池均关闭，返回。否则睡眠200ms后循环判断
            if (htmlParsePool.isTerminated() && downImagePool.isTerminated()) {
                return;
            }else{
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ImageCrawler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    
}
