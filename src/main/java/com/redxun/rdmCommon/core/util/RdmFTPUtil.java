package com.redxun.rdmCommon.core.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

/**
 * @author zhangzhen
 * @date 2022年11月30日
 * */
public class RdmFTPUtil {
    public static void ftpTool(JSONObject paramJson){
        //创建客户端对象
        FTPClient ftp = new FTPClient();
        InputStream local=null;
        try {
            String ftpIp = paramJson.getString("ftpIp");
            String ftpPort = paramJson.getString("ftpPort");
            String ftpUserName = paramJson.getString("ftpUserName");
            String ftpUserPwd = paramJson.getString("ftpUserPwd");
            String ftpFilePath = paramJson.getString("ftpFilePath");
            //连接ftp服务器
            ftp.connect(ftpIp, Integer.parseInt(ftpPort));
            //登录
            ftp.login(ftpUserName, ftpUserPwd);
            //设置上传路径
            String path = ftpFilePath;
            //检查上传路径是否存在 如果不存在返回false
            boolean flag = ftp.changeWorkingDirectory(path);
            if(!flag){
                //创建上传的路径  该方法只能创建一级目录，在这里如果/home/ftpuser存在则可创建image
                ftp.makeDirectory(path);
            }
            //指定上传路径
            ftp.changeWorkingDirectory(path);
            //指定上传文件的类型  二进制文件
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.setControlEncoding("UTF-8");
            //读取本地文件
            String fullFilePath = paramJson.getString("fullFilePath");
            File file = new File(fullFilePath);
            local = new FileInputStream(file);
            //第一个参数是文件名
            String fileName = new String(paramJson.getString("fileName").getBytes("UTF-8"), "ISO8859-1");
            ftp.storeFile(fileName, local);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                //关闭文件流
                local.close();
                //退出
                ftp.logout();
                //断开连接
                ftp.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
