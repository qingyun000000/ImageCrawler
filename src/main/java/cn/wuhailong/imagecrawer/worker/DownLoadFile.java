/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.wuhailong.imagecrawer.worker;

import cn.wuhailong.imagecrawer.QueueAndSet.LinkQueue;
import cn.wuhailong.imagecrawer.settings.Settings;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * 下载类，执行下载
 * @author Administrator
 */
public class DownLoadFile {
    
    /*
    * 根据url生成图片文件名
    */
    private String getFileNameByUrl(String url, String contentType){
        url = url.substring(7);
        if(contentType.indexOf("jpg") != -1){
            url = url.replaceAll("[\\?/:*|<>\"]", "_")+".jpg";
            return url;
        }
        else{
            return url.replaceAll("[\\?/:*|<>\"]", "_")+"." + contentType.substring(contentType.lastIndexOf("/")+1);
        }
    }
    
    /*
    * 使用输出流保存图片
    */
    private void saveToLocal(byte[] data, String filePath){
        try{
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
            for(int i = 0; i < data.length; i++){
                out.write(data[i]);
            }
            out.flush();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(DownLoadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 下载图片
     * @param url
     */
    public void downloadImage(String url){
        
        //获取配置
        Settings settings = Settings.getInstance();
        
        //获取保存目录
        String fileDirectory = settings.getFilePath();
        
        //文件路径
        String filePath = null;
        
        //http客户端下载（Get方法）
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
        GetMethod getMethod;
        try{getMethod = new GetMethod(url);
            getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
            getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            try{
                //判断返回状态
                int statusCode = httpClient.executeMethod(getMethod);
                if(statusCode != HttpStatus.SC_OK){
                    System.err.println("Method failed: " + getMethod.getStatusLine());
                    filePath = null;
                }
                
                //根据http协议header信息判断是否是图片类型，如果是获取body信息保存
                if(getMethod.getResponseHeader("Content-Type").getValue().contains("image")){
                    byte[] responseBody = getMethod.getResponseBody();
                    
                    //图片大小限制
                    if(responseBody.length >= settings.getImgMinSize() && responseBody.length <= settings.getImgMaxSize()){
                        
                        //生成文件路径
                        filePath = fileDirectory +"\\" + getFileNameByUrl(url, getMethod.getResponseHeader("Content-Type").getValue());
                        
                        //保存图片到本地
                        saveToLocal(responseBody, filePath);
                        
                        //修改下载图片数（原子类自增）
                        LinkQueue.DownImgNumIncrement();
                    }

                }


            }catch(HttpException e){
                System.err.println("Please check your provided http address");
            } catch (IOException ex) {
                Logger.getLogger(DownLoadFile.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                getMethod.releaseConnection();
            }
            
        }catch(Exception e){}
        
    }
}
