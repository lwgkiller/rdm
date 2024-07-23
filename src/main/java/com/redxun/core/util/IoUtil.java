package com.redxun.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.core.io.ClassPathResource;

/**
 * 文件读写工具类
 * <pre> 
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2015年1月31日-下午4:17:45
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
public class IoUtil {
	
	/**
	 * 从文件中读取内容
	 * @param relateFilePath 相对class path的路径
	 * @return
	 * @throws IOException
	 */
	public static String readFileFromClassPath(String relateFilePath) throws IOException {
		
		org.springframework.core.io.Resource fileRource = new ClassPathResource(relateFilePath);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileRource.getInputStream()));

		StringBuffer sb = new StringBuffer();
		String lineSparator = System.getProperty("line.separator");
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append(lineSparator);
		}
		reader.close();

		return sb.toString();
	}
	
	public static void main(String[]args) throws IOException{
		String file="example.json";
		String result=readFileFromClassPath(file);
		//System.out.println("result:"+ result);
	}
	
}
