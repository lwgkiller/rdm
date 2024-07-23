
package com.redxun.rdmZhgl.core.controller;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmZhgl.core.dao.BbsBaseInfoDao;
import com.redxun.rdmZhgl.core.service.BbsBaseInfoService;
import com.redxun.rdmZhgl.core.service.BbsDiscussService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.util.SysPropertiesUtil;

/**
 * 论坛
 * @author zz
 */
@WebServlet("/kindeditor/upload")
public class KindEditorController  extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置Response响应的编码
        resp.setContentType("text/html; charset=UTF-8");


        //获取一个Response的Write对象
        PrintWriter writer = resp.getWriter();


        //文件保存目录路径
        String picUrl = SysPropertiesUtil.getGlobalProperty("BBS_PIC_URL");
        String savePath = picUrl + File.separator;
        System.out.println(savePath);

        //文件保存目录URL
        String saveUrl = req.getContextPath() + "/attached/";
        System.out.print(saveUrl);

        //定义允许上传的文件扩展名
        HashMap<String, String> extMap = new HashMap<String, String>();
        extMap.put("image", "gif,jpg,jpeg,png,bmp");
        extMap.put("flash", "swf,flv");
        extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
        extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");


        //最大文件大小
        long maxSize = 100000000;


        //判断是否是一个文件
        if (!ServletFileUpload.isMultipartContent(req)) {
            writer.println(getError("请选择文件。"));
            return;
        }
        //检查目录
        File uploadDir = new File(savePath);
        if (!uploadDir.isDirectory()) {
            writer.println(getError("上传目录不存在。"));
            return;
        }
        //检查目录写权限
        if (!uploadDir.canWrite()) {
            writer.println(getError("上传目录没有写权限。"));
            return;
        }

        String dirName = req.getParameter("dir");
        if (dirName == null) {
            dirName = "image";
        }
        if (!extMap.containsKey(dirName)) {
            writer.println(getError("目录名不正确。"));
            return;
        }


        //创建文件夹
        savePath += dirName + "/";
        saveUrl += dirName + "/";
        File saveDirFile = new File(savePath);
        if (!saveDirFile.exists()) {
            saveDirFile.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String ymd = sdf.format(new Date());
        savePath +=  "/";
        saveUrl +=  "/";
        File dirFile = new File(savePath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        List items = null;
        try {
            items = upload.parseRequest(req);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        Iterator itr = items.iterator();
        while (itr.hasNext()) {
            FileItem item = (FileItem) itr.next();
            String fileName = item.getName();
            long fileSize = item.getSize();
            if (!item.isFormField()) {
                //检查文件大小
                if (item.getSize() > maxSize) {
                    writer.println(getError("上传文件大小超过限制。"));
                    return;
                }
                //检查扩展名
                String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                if (!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)) {
                    writer.println(getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。"));
                    return;
                }

                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
                try {
                    File uploadedFile = new File(savePath, newFileName);
                    item.write(uploadedFile);
                } catch (Exception e) {
                    writer.println(getError("上传文件失败。"));
                    return;
                }

                JSONObject obj = new JSONObject();
                obj.put("error", 0);
                obj.put("url", req.getContextPath()+"/rdmZhgl/core/bbs/imagePreview.do?newFileName="+newFileName);
                writer.println(obj.toJSONString());
            }
        }


        //将writer对象中的内容输出
        writer.flush();
        //关闭writer对象
        writer.close();
    }


    //一个私有的方法，用于响应错误信息
    private String getError(String message) {
        JSONObject obj = new JSONObject();
        obj.put("error", 1);
        obj.put("message", message);
        return obj.toJSONString();
    }
}
