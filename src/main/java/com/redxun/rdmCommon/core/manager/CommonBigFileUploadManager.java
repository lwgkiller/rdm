package com.redxun.rdmCommon.core.manager;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.FileUtil;
import com.redxun.rdmCommon.core.dao.CommonFilesDao;
import com.redxun.saweb.util.IdUtil;

@Service
public class CommonBigFileUploadManager {
    private static final Logger logger = LoggerFactory.getLogger(CommonBigFileUploadManager.class);
    public static final int BUF_SIZE = 1024 * 1024;
    @Autowired
    private CommonFilesDao commonFilesDao;

    /**
     * 普通IO分片方式
     *
     * @param sourceFile
     *            要分片的文件
     * @param targetPath
     *            输出路径
     * @param multipartSize
     *            文件被切割的大小，单位：byte
     *            <p>
     *            model各个参数详解 r 代表以只读方式打开指定文件 rw 以读写方式打开指定文件 rws 读写方式打开，并对内容或元数据都同步写入底层存储设备 rwd
     *            读写方式打开，对文件内容的更新同步更新至底层存储设备
     */

    public static List<String> fileSpilt(String sourceFile, String targetPath, long multipartSize) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        List<String> fileNames = new LinkedList<>();
        try {
            RandomAccessFile raf = new RandomAccessFile(sourceFile, "r");
            long fileLength = raf.length();
            // 每个文件分的片数。
            double total = (double)fileLength / multipartSize;
            // 如果小数点大于1，整数加一 例如4.1 =》5
            int num = (int)Math.ceil(total);
            long pointe = 0;
            for (int i = 1; i <= num; i++) {
                // 获取RandomAccessFile对象文件指针的位置，初始位置是0
                // 移动文件指针位置，pointe是跳过的长度
                raf.seek(pointe);
                // 每次读取200M的大小
                byte[] buff = new byte[(int)multipartSize];
                int byteCount = 0;
                if ((byteCount = raf.read(buff)) > 0) {
                    inputStream = new ByteArrayInputStream(buff, 0, byteCount);
                    String fileName = UUID.randomUUID().toString().replaceAll("-", "");
                    outputStream = new FileOutputStream(targetPath + fileName);
                    fileNames.add(fileName);
                    int ch = 0;
                    byte[] bytes = new byte[BUF_SIZE];
                    while ((ch = inputStream.read(bytes)) > 0) {
                        outputStream.write(bytes, 0, ch);
                    }
                    outputStream.close();
                    inputStream.close();
                }
                pointe = pointe + multipartSize;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileNames;
    }

    /**
     * 高效NIO分片方式
     *
     * @param sourceFile
     *            要分片的文件
     * @param targetPath
     *            分片后存放的位置
     * @param multipartSize
     *            按多大分片
     */
    public static List<String> fileSpiltWithNio(String sourceFile, String targetPath, long multipartSize) {

        File file = new File(sourceFile);
        FileOutputStream outputStream = null;
        FileChannel outputChannel = null;
        FileInputStream inputStream = null;
        FileChannel inputChannel = null;
        List<String> fileNames = new LinkedList<>();
        try {
            long splitSize = multipartSize;
            long startPoint = 0;
            long fileLength = file.length();
            // 每个文件分的片数。
            double total = (double)fileLength / multipartSize;
            // 如果小数点大于1，整数加一 例如4.1 =》5
            int num = (int)Math.ceil(total);

            inputStream = new FileInputStream(file);
            inputChannel = inputStream.getChannel();
            for (int i = 1; i <= num; i++) {
                String fileName = UUID.randomUUID().toString().replaceAll("-", "");
                outputStream = new FileOutputStream(targetPath + fileName);
                fileNames.add(fileName);
                outputChannel = outputStream.getChannel();
                inputChannel.transferTo(startPoint, splitSize, outputChannel);
                startPoint += splitSize;
                outputChannel.close();
                outputStream.close();
            }
            inputChannel.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputChannel != null) {
                    outputChannel.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputChannel != null) {
                    inputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return fileNames;
    }

    /**
     * 合成方法：RandomAccessFile方式
     *
     * @param files
     *            要合并的文件
     * @param newFilePath
     *            新文件路径
     */
    public static void mergeFile(File[] files, String newFilePath) {

        RandomAccessFile in = null;
        RandomAccessFile out = null;
        try {
            out = new RandomAccessFile(newFilePath, "rw");
            for (File file : files) {
                in = new RandomAccessFile(file, "r");
                int len = 0;
                byte[] bt = new byte[BUF_SIZE];
                while (-1 != (len = in.read(bt))) {
                    out.write(bt, 0, len);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 合成方法：NIO方式
     *
     * @param files
     *            要合并的文件
     * @param newFilePath
     *            新文件路径
     */
    public static void mergeFileWithNio(File[] files, String newFilePath) {
        FileChannel outChannel = null;
        FileChannel inChannel = null;
        if (files == null || files.length == 0) {
            return;
        }
        try {
            outChannel = new FileOutputStream(newFilePath).getChannel();
            for (File file : files) {
                inChannel = new FileInputStream(file).getChannel();
                ByteBuffer bb = ByteBuffer.allocate(BUF_SIZE);
                while (inChannel.read(bb) != -1) {
                    bb.flip();
                    outChannel.write(bb);
                    bb.clear();
                }
                inChannel.close();
            }
        } catch (IOException e) {
            logger.error("Exception in mergeFileWithNio", e);
        } finally {
            try {
                if (outChannel != null) {
                    outChannel.close();
                }
                if (inChannel != null) {
                    inChannel.close();
                }
            } catch (IOException e) {
                logger.error("Exception in mergeFileWithNio", e);
            }
        }
    }

    /**
     * 上传文件，兼容大文件上传，如果最终上传完成则返回文件相关的信息(fileId/fileName/fileSize是固定的，其他的可以传递otherParamNames)用于外部调用方的存储
     * 适合直接向filePathBase目录写入文件的场景
     * 
     * @param request
     * @param filePathBase
     * @param otherParamNames
     * @return
     */
    public JSONObject saveCommonUploadFilesH5(HttpServletRequest request, String filePathBase,
        List<String> otherParamNames) {
        JSONObject resultData = new JSONObject();
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return resultData;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty() || !fileMap.containsKey("file")) {
            logger.warn("没有找到上传的文件");
            return resultData;
        }
        try {
            if (!filePathBase.endsWith("/") && !filePathBase.endsWith("\\")) {
                filePathBase += "/";
            }
            MultipartFile mf = fileMap.get("file");
            String fileName = parameters.get("fileName")[0];
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String finish = parameters.get("finish")[0];
            String sliceIndex = parameters.get("sliceIndex")[0];
            String fileUUID = parameters.get("uuid")[0];
            String fileSize = parameters.get("fileSize")[0];
            // 文件总大小不够一次分片的，直接上传完成
            if (sliceIndex.equalsIgnoreCase("0") && "yes".equalsIgnoreCase(finish)) {
                String finalFileId = IdUtil.getId();
                String finalFileName = finalFileId + "." + suffix;
                FileCopyUtils.copy(mf.getBytes(), new File(filePathBase + finalFileName));
                resultData.put("fileId", finalFileId);
                resultData.put("fileName", fileName);
                resultData.put("fileSize", fileSize);
                if (otherParamNames != null && !otherParamNames.isEmpty()) {
                    for (String key : otherParamNames) {
                        String[] oneKeyData = parameters.get(key);
                        if (oneKeyData != null && oneKeyData.length == 1) {
                            resultData.put(key, oneKeyData[0]);
                        }
                    }
                }
            } else {
                // 先将分片数据保存到临时文件夹
                File tempDir = new File(filePathBase + fileUUID);
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                int sliceIndexInt = Integer.parseInt(sliceIndex);
                if (sliceIndexInt < 10) {
                    sliceIndex = "0000" + sliceIndex;
                } else if (sliceIndexInt >= 10 && sliceIndexInt < 100) {
                    sliceIndex = "000" + sliceIndex;
                } else if (sliceIndexInt >= 100 && sliceIndexInt < 1000) {
                    sliceIndex = "00" + sliceIndex;
                } else if (sliceIndexInt >= 1000 && sliceIndexInt < 10000) {
                    sliceIndex = "0" + sliceIndex;
                }
                File partFile = new File(filePathBase + fileUUID + "/" + sliceIndex);
                FileCopyUtils.copy(mf.getBytes(), partFile);

                // 如果分片传输结束，则合并文件删除临时文件夹
                if ("yes".equalsIgnoreCase(finish)) {
                    String finalFileId = IdUtil.getId();
                    String finalFileName = finalFileId + "." + suffix;
                    File dir = new File(filePathBase + fileUUID);
                    mergeFileWithNio(dir.listFiles(), filePathBase + finalFileName);
                    FileUtil.deleteDir(dir);

                    resultData.put("fileId", finalFileId);
                    resultData.put("fileName", fileName);
                    resultData.put("fileSize", fileSize);
                    if (otherParamNames != null && !otherParamNames.isEmpty()) {
                        for (String key : otherParamNames) {
                            String[] oneKeyData = parameters.get(key);
                            if (oneKeyData != null && oneKeyData.length == 1) {
                                resultData.put(key, oneKeyData[0]);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Exception in saveCommonUploadFilesH5", e);
        }
        return resultData;
    }
}
