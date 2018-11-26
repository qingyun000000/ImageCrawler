/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.wuhailong.imagecrawer.QueueAndSet;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 队列工具
 * 包含：爬取完的网页集合Set visitedUrl
 *       爬取完的图片集合Set visitedImgUrl
 *       待爬取的网页队列Queue unVisitedUrl
 *       待爬取的图片队列Queue unVisitedImgUrl
 *       现在图片计数器：AtomicInteger downImgNum
 * @author Administrator
 */
public class LinkQueue {
    //爬完的网页
    private static Set visitedUrl = new HashSet();
    
    //爬完的图片页
    private static Set visitedImgUrl = new HashSet();
    
    //待爬的网页
    private static Queue unVisitedUrl = new Queue();
    
    //待下载的图片页
    private static Queue unVisitedImgUrl = new Queue();
    
    //下载图片数量，因为图片下载是多线程，所以使用原子类进行更新
    private static AtomicInteger downImgNum = new AtomicInteger(0);
    
    /*
    * 因为两个待爬取队列，均有一个线程读取，多个线程新增，因此加锁
    */
    //锁-待爬的网页,
    private static Lock urlLock = new ReentrantLock();
    
    //锁-待爬的图片
    private static Lock imgLock = new ReentrantLock();
    
    /**
     * 获取下载图片数
     * @return
     */
    public static int getDownImgNum() {
        return downImgNum.intValue();
    }
    
    /**
     * 下载图片数自增，每下载一张图片，调用此方法
     */
    public static void DownImgNumIncrement() {
        downImgNum.getAndIncrement();
    }
    
    /**
     * 存入爬完的网页
     * @param url
     */
    public static void addVisitedUrl(String url){
        visitedUrl.add(url);
    }
    
    /**
     * 爬完的网页数
     * @return
     */
    public static int getVisitedUrlNum(){
        return visitedUrl.size();
    }
    
    /**
     * 取出未爬的网页队列中（队首）
     * @return
     */
    public static Object unVisitedUrlDeQueue(){
        //加锁
        urlLock.lock();
        
        Object ob = unVisitedUrl.deQueue();
        
        //释放锁
        urlLock.unlock();
        
        return ob;
    }
    
    /**
     * 加入未爬的网页队列（对尾）
     * @param url
     */
    public static void addUnVisitedUrl(String url){
        //判断，空值（null和空字符串）不存入，已在队列和已在爬取完的集合中的不存入
        if(url != null && !url.trim().equals("") && !visitedUrl.contains(url) && !unVisitedUrl.contains(url)){
            urlLock.lock();
            unVisitedUrl.enQueue(url);
            urlLock.unlock();
        }
    }
    
    /**
     * 获取未爬的网页数
     * @return
     */
    public static int getUnVisitedUrlNum(){
        urlLock.lock();
        int count= unVisitedUrl.count();
        urlLock.unlock();
        return count;
    }
    
    /**
     * 查询未爬的队列是否为空
     * @return
     */
    public static boolean unVisitedUrlsEmpty(){
        urlLock.lock();
        boolean bool = unVisitedUrl.isQueueEmpty();
        urlLock.unlock();
        return bool;
    }
    
    /**
     * 存入爬完的图片地址
     * @param url
     */
    public static void addVisitedImgUrl(String url){
        visitedImgUrl.add(url);
    }
    
    /**
     * 爬完的图片地址数
     * @return
     */
    public static int getVisitedImgUrlNum(){
        return visitedImgUrl.size();
    }
    
    /**
     * 取出未爬的图片地址（队首）
     * @return
     */
    public static Object unVisitedImgUrlDeQueue(){
        imgLock.lock();
        Object ob = unVisitedImgUrl.deQueue();
        imgLock.unlock();
        return ob;
    }
    
    /**
     * 加入未爬的网片地址（队尾)
     * @param url
     */
    public static void addUnVisitedImgUrl(String url){
        if(url != null && !url.trim().equals("") && !visitedImgUrl.contains(url) && !unVisitedImgUrl.contains(url)){
            imgLock.lock();
            unVisitedImgUrl.enQueue(url);
            imgLock.unlock();
        }
    }
    
    /**
     * 获取未爬的图片地址数
     * @return
     */
    public static int getUnVisitedImgUrlNum(){
        imgLock.lock();
        int count = unVisitedImgUrl.count();
        imgLock.unlock();
        return count;
    }
    
    /**
     * 查询未爬的图片地址队列是否为空
     * @return
     */
    public static boolean unVisitedImgUrlsEmpty(){
        imgLock.lock();
        boolean bool = unVisitedImgUrl.isQueueEmpty();
        imgLock.unlock();
        return bool;
    }
    
    /**
     * 重置队列
     */
    public static void reset(){
        unVisitedImgUrl.clear();
        unVisitedUrl.clear();
        visitedImgUrl.clear();
        visitedUrl.clear();
    }

    /**
     * 重置下载图片数
     */
    public static void resetDownImgNum() {
        downImgNum.getAndSet(0);
    }
    
}
