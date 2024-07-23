package com.redxun.core.util;

import java.io.IOException;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import com.redxun.saweb.util.WebAppUtil;

/**
 * FASTDFS 文件上传下载。
 * @author RAY
 *
 */
public class FastClient {
	
	public  FastClient() throws IOException, MyException{
		String config=WebAppUtil.getClassPath() +"config/fdfs_client.conf";
				
		ClientGlobal.init(config);
	}
	
	/**
	 * 上传文件
	 * @param bytes		文件内容
	 * @param extName	扩展名
	 * @return
	 * @throws IOException
	 * @throws MyException
	 */
	public String uploadFile(byte[] bytes,String extName) throws IOException, MyException{

        TrackerClient tracker = new TrackerClient(); 
        TrackerServer trackerServer = tracker.getConnection(); 
        StorageServer storageServer = null;

        StorageClient storageClient = new StorageClient(trackerServer, storageServer); 
        
        String fileIds[] = storageClient.upload_file(bytes, extName, null);
        
        return fileIds[0] +"#" + fileIds[1];
	}
	
	/**
	 * 根据文件路径删除文件。
	 * path 组#文件路径
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws MyException
	 */
	public int delFile(String path) throws IOException, MyException{
		String[] ary=path.split("#");
		 TrackerClient tracker = new TrackerClient(); 
         TrackerServer trackerServer = tracker.getConnection(); 
         StorageServer storageServer = null;

         StorageClient storageClient = new StorageClient(trackerServer, 
                 storageServer); 
         int i = storageClient.delete_file(ary[0], ary[1]); 
         return i;
	}
	
	/**
	 * 根据文件的组和路径获取文件
	 * path 组#文件路径
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws MyException
	 */
	public byte[] getFile(String path) throws IOException, MyException{
		String[] ary=path.split("#");
		 TrackerClient tracker = new TrackerClient(); 
         TrackerServer trackerServer = tracker.getConnection(); 
         StorageServer storageServer = null;
         
         StorageClient storageClient = new StorageClient(trackerServer, storageServer); 
         byte[] bytes = storageClient.download_file(ary[0], ary[1]);

       
         return bytes;
	}

}
