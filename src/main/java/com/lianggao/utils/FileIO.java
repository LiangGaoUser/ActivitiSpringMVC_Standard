package com.lianggao.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author gm
 * 用于文件上传、删除、下载、移动、复制
 */
public class FileIO {
    /**
     * 上传文件
     * */
    public static void uploadFile(MultipartFile resource, String destPath) throws IOException {
        File destFile=new File(destPath);
        if (!destFile.getParentFile().exists()) {
            makeParentFile(destFile.getParentFile());
        }
        if (destFile.exists()) {
            destFile.delete();
        }
        resource.transferTo(destFile);
    }

    public static void makeParentFile(File file)
    {
        if(!file.getParentFile().exists())
        {
            makeParentFile(file.getParentFile());
        }
        if(!file.exists()){
            file.mkdir();
        }
    }

    /**
     * 文件转移 将文件复制之后 删除源文件
     */
    public static void doCopyFile(String srcFile, String destFile) throws IOException {
        File file = new File(srcFile);
        if (file.exists()) {
            File dest = new File(destFile);
            if (!dest.getParentFile().exists()) {
                makeParentFile(dest.getParentFile());
            }
            if (dest.exists()) {
                dest.delete();
            }
            Files.copy(file.toPath(), dest.toPath());
            if (file.exists()) {
                file.delete();
            }

        }
    }

    /**
     * 复制签名照 将文件复制之后 不删除源文件
     */
    public static void copySignPhoto(String srcFile, String destFile) throws IOException {
        File file = new File(srcFile);
        if (file.exists()) {
            File dest = new File(destFile);
            if (!dest.getParentFile().exists()) {
                makeParentFile(dest.getParentFile());
            }

            if (dest.exists()) {
                dest.delete();
            }
            Files.copy(file.toPath(), dest.toPath());

        }
    }

    /**
     * 删除n条记录后删除对应的文件
     *
     * @param path 路径
     * @param ID   ID
     */
    public void deleteRecords(String path, String ID) {
        String filepath = path + "/" + ID;
        deleteDirectory(filepath);
    }

    /**
     * 删除单个文件
     *
     * @param path 路径
     * @param ID   ID
     */
    public static void deleteFiles(String path, String ID, String filename) {
        String filepath = path  + ID + "/" + filename;
        String contentType = getContentType(filepath);
        String previewFilepath = "";
        String name = filename.substring(0, filename.lastIndexOf("."));
        //原始文件
        File file = new File(filepath);
        if (file.exists()) {
            file.delete();
        }

        switch (contentType) {
            case "docx":
            case "doc":
            case "txt":
            case "ppt":
            case "xls":
            case "xlsx":
            case "pptx":
                previewFilepath = path  + ID + "/preview/" + name + ".pdf";
                File previewFile = new File(previewFilepath);
                if (previewFile.exists()) {
                    previewFile.delete();
                }
                break;
/*                previewFilepath = path  + ID + "/preview/" + name + ".html";
                File xlspreviewFile = new File(previewFilepath);
                if (xlspreviewFile.exists()) {
                    xlspreviewFile.delete();
                }
                previewFilepath = path  + ID + "/preview/" + name + ".files";
                deleteDirectory(previewFilepath);
                break;*/
            default://
        }


    }

    /**
     * 删除单个文件
     *
     * @param path 路径
     */
    public static void deleteFile(String path, String filename) {
        String filepath = path + "/" + filename;
        //原始文件
        File file = new File(filepath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除整个文件夹
     */
    public static void deleteDirectory(String path) {
        File dirfile = new File(path);
        if (dirfile.isDirectory()) {
            for (File file : dirfile.listFiles()) {
                deleteDirectory(file.getPath());
            }
        }
        dirfile.delete();
    }

    /**
     * 获取文件的类型
     */
    public static String getContentType(String filePath) {
        Path path = Paths.get(filePath);
        System.out.println("文件类型：" + filePath);
        String contentType = "";
        System.out.println("文件类型：" + contentType);
        try {
            contentType = Files.probeContentType(path);
            System.out.println("文件类型：" + contentType);
        } catch (IOException e) {
            //  e.printStackTrace();
        }
        try {
            switch (contentType) {
                case "application/msword":
                    contentType = "doc";
                    break;
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                    contentType = "docx";
                    break;
                case "application/vnd.ms-excel":
                    contentType = "xls";
                    break;
                case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                    contentType = "xlsx";
                    break;
                case "application/vnd.ms-powerpoint":
                    contentType = "ppt";
                    break;
                case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                    contentType = "pptx";
                    break;
                case "text/plain":
                    contentType = "txt";
                    break;
                default:
                    System.out.println("文件类型：" + contentType);
            }
            return contentType;
        } catch (Exception e) {
            return "error";
        }


    }


    public static void downloadFile(HttpServletResponse response, String path, String fileName) throws Exception {


        File file = new File(path + "\\" + fileName);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(), "ISO-8859-1"));
        //打开本地文件流
        InputStream inputStream = new FileInputStream(path + "\\" + fileName);
        //激活下载操作
        OutputStream os = response.getOutputStream();
        //循环写入输出流
        try {
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 这里主要关闭。
            os.close();
            inputStream.close();
        }

    }

}
