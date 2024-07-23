package com.redxun.rdmCommon.core.util;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/8/18 8:48
 */
public class FileOperateUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileOperateUtil.class);

    /**
     * 删除一个目录及下面的所有文件（仅限于目录下只有文件，没有二级目录）
     * 
     * @param dirPaths
     */
    public static void deleteDirAndFiles(List<String> dirPaths) {
        if (dirPaths == null || dirPaths.isEmpty()) {
            return;
        }
        for (String oneDir : dirPaths) {
            try {
                File dirFileObj = new File(oneDir);
                if (!dirFileObj.exists()) {
                    continue;
                }
                File[] filesUnderDir = dirFileObj.listFiles();
                if (filesUnderDir != null && filesUnderDir.length != 0) {
                    for (File oneFileObj : filesUnderDir) {
                        oneFileObj.delete();
                    }
                }
                dirFileObj.delete();
            } catch (Exception e) {
                logger.error("Exception in deleteDirAndFiles, dir path is " + oneDir, e);
            }
        }
    }

    /**
     * 拷贝一个文件夹中的文件到另一个文件夹（仅限于文件，没有二级目录）
     * 
     * @param sourcePath
     * @param targetPath
     */
    public static void copyDir(String sourcePath, String targetPath) {
        if (StringUtils.isBlank(sourcePath) || StringUtils.isBlank(targetPath)) {
            return;
        }

        File sourceDir = new File(sourcePath);
        if (!sourceDir.exists()) {
            return;
        }
        File targetDir = new File(targetPath);
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        String[] sourceFilePath = sourceDir.list();
        if (sourceFilePath == null || sourceFilePath.length == 0) {
            return;
        }
        for (int i = 0; i < sourceFilePath.length; i++) {
            String sourceFileFullPath = sourcePath + File.separator + sourceFilePath[i];
            File sourceFile = new File(sourceFileFullPath);
            if (sourceFile.isFile()) {
                File targetFile = new File(targetPath + File.separator + sourceFilePath[i]);
                try {
                    Files.copy(sourceFile.toPath(), targetFile.toPath());
                } catch (Exception e) {
                    logger.error("Exception in copyDir, sourceFilePath is " + sourceFileFullPath, e);
                }
            }

        }
    }

    /**
     * 拷贝目录中的文件到另一个目录，同时将文件名字更换
     * 
     * @param sourcePath
     * @param targetPath
     */
    public static void copyDirAndReplaceName(String sourcePath, String targetPath, Map<String, String> old2NewName) {
        if (StringUtils.isBlank(sourcePath) || StringUtils.isBlank(targetPath)) {
            return;
        }

        File sourceDir = new File(sourcePath);
        if (!sourceDir.exists()) {
            return;
        }
        File targetDir = new File(targetPath);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        String[] sourceFilePath = sourceDir.list();
        if (sourceFilePath == null || sourceFilePath.length == 0) {
            return;
        }
        for (int i = 0; i < sourceFilePath.length; i++) {
            String sourceFileFullPath = sourcePath + File.separator + sourceFilePath[i];
            File sourceFile = new File(sourceFileFullPath);
            if (sourceFile.isFile()) {
                String[] sourceFileNameAndSuffix = toGetFileNameAndSuffix(sourceFilePath[i]);
                if (!old2NewName.containsKey(sourceFileNameAndSuffix[0])) {
                    logger.error("can't find {} in map", sourceFileNameAndSuffix[0]);
                    continue;
                }
                String targetFilePath = old2NewName.get(sourceFileNameAndSuffix[0]) + "." + sourceFileNameAndSuffix[1];
                File targetFile = new File(targetPath + File.separator + targetFilePath);
                try {
                    Files.copy(sourceFile.toPath(), targetFile.toPath());
                } catch (Exception e) {
                    logger.error("Exception in copyDir, sourceFilePath is " + sourceFileFullPath, e);
                }
            }

        }
    }

    /**
     * 从源文件拷贝到目标文件
     * 
     * @param sourcePath
     * @param targetDir
     * @param targetFileName
     */
    public static void copyFileFromSourceToTarget(String sourcePath, String targetDir, String targetFileName) {
        if (StringUtils.isBlank(sourcePath) || StringUtils.isBlank(targetDir) || StringUtils.isBlank(targetFileName)) {
            return;
        }

        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            return;
        }
        File targetDirPath = new File(targetDir);
        if (!targetDirPath.exists()) {
            targetDirPath.mkdirs();
        }
        String targetFullPath = targetDir + targetFileName;
        File targetFile = new File(targetFullPath);
        try {
            Files.copy(sourceFile.toPath(), targetFile.toPath());
        } catch (Exception e) {
            logger.error("Exception in copyDir, sourceFilePath is " + sourcePath, e);
        }
    }

    public static String[] toGetFileNameAndSuffix(String fileName) {
        String[] result = new String[2];
        if (StringUtils.isNotBlank(fileName)) {
            String[] arr = fileName.split("\\.", -1);
            if (arr.length > 1) {
                result = arr;
            }
        }
        return result;
    }
}
