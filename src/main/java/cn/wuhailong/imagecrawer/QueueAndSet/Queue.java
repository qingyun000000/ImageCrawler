/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.wuhailong.imagecrawer.QueueAndSet;

import java.util.LinkedList;

/**
 * 队列（对LinkedList进行包装）
 * @author Administrator
 */
public class Queue {
    
    private LinkedList queue = new LinkedList();
    
    /**
     * 入队
     * @param t
     */
    public void enQueue(Object t){
        queue.addLast(t);
    }
    
    /**
     * 出队
     * @return
     */
    public Object deQueue(){
        return queue.removeFirst();
    }
    
    /**
     * 判断空
     * @return
     */
    public boolean isQueueEmpty(){
        return queue.isEmpty();
    }
    
    /**
     * 包含查询
     * @param t
     * @return
     */
    public boolean contains(Object t){
        return queue.contains(t);
    }
    
    /**
     * 队列长度
     * @return
     */
    public int count(){
        return queue.size();
    }
    
    /**
     * 队列清空
     */
    public void clear(){
        queue.clear();
    }
    
}
