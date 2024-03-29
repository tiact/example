package com.tiact.websocket.controller;

import com.tiact.websocket.entity.Chat;
import com.tiact.websocket.entity.FileInfo;
import com.tiact.websocket.entity.TiaResult;
import com.tiact.websocket.service.ChatService;
import com.tiact.websocket.utils.CommonUtils;
import com.tiact.websocket.utils.util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Tia_ct
 */
@Controller
@CrossOrigin
@Slf4j
public class ChatController {

    @Autowired
    ChatService service;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Chat sendMessage(@Payload Chat chat) {
        return chat;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Chat addUser(@Payload Chat chat,
                        SimpMessageHeaderAccessor headerAccessor) {
        System.out.println(chat.toString());
        util.setUser(chat.getSender());
        headerAccessor.getSessionAttributes().put("username", chat.getSender());
        return chat;
    }

    @GetMapping(value = "send")
    @ResponseBody
    public TiaResult send(@Payload Chat chat) {
        chat = service.send(chat);
        return TiaResult.success("发送", chat);
    }

    @GetMapping("/list")
    @ResponseBody
    public TiaResult list(@RequestParam(defaultValue="1")Integer page, @RequestParam(defaultValue = "5") Integer limit){
        List<Chat> list = new ArrayList<>();
        for(int i =1;i<18;i++){
            Chat chat = new Chat();
            chat.setContent(Integer.toString(i));
            chat.setSender("a");
            chat.setType(Chat.MessageType.CHAT);
            list.add(chat);
        }
        Object[] result = new Object[2];
        result[0] = list.size();
        if(page*limit>list.size()) {
            list =list.subList((page-1)*limit, list.size());
        }else {
            list =list.subList((page-1)*limit, page*limit);
        }
        result[1] = list;
        return TiaResult.success("获取成功",result);
    }



    @RequestMapping("/disk")
    public ModelAndView helloMarker(ModelAndView model){
        model.addObject("urls","/");
        model.setViewName("disk");
        return model;
    }

    public String initPath(String route){
        String path = "/data/tomcat/web8080/webapps/s"+route;
        if(util.chkPlatform()){
            path = "D:/data/s"+route;
        }
        return path;
    }

    /**
     * 上传
     *
     * @param file
     */
    @RequestMapping("/upload")
    @ResponseBody
    public TiaResult upload(@RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "/") String root, @RequestParam(defaultValue = "0") String over) throws IOException {
        if(root.contains("/")){
            Map<String, String> resUrl = new HashMap<>();
            //上传文件路径
            String path = initPath(root);
            String name = file.getOriginalFilename();
            String suffixName = name.substring(name.lastIndexOf("."));
            File filepath = new File(path, name);
            if (!filepath.getParentFile().exists()) {
                filepath.getParentFile().mkdirs();
            }
            log.info("[path] "+path);
            String key = "/".equals(root)?name:root.substring(1)+ "/"+name;
            if(!"1".equals(over)){
                if (filepath.exists()) {
                    filepath.delete();
                    //name = name.replace(suffixName,"-副本"+suffixName);
                }
            }
            //String localFilePath = path + File.separator + name;
            //File tempFile = new File(localFilePath);
            file.transferTo(filepath);
            resUrl.put("src", "/s/" + key);
            // qiniu
            //resUrl.put("oss", qiniuUtils.upload(tempFile,key));


            return TiaResult.success("上传成功", resUrl);
        }else{
            return TiaResult.error("路径异常");
        }
    }


    /**
     * 路径
     *
     * @param route
     */
    @RequestMapping("/route")
    @ResponseBody
    public TiaResult find( @RequestParam(defaultValue = "/") String route,@RequestParam(defaultValue = "list")String mode,String name) throws IOException {
        Map<String, List<FileInfo>> result = new HashMap<>();
        String path = initPath(route);
        log.info("[route] "+path);
        List<FileInfo> fileList = new ArrayList<>();
        File file = new File(path);
        if("list".equals(mode)){
            if ("/".equals(route)&&!file.exists()) {
                log.info("init...");
                File initFile = new File(file,"data.ini");
                initFile.getParentFile().mkdirs();
                initFile.createNewFile();
            }
            if(file.isDirectory()){
                File[] files = file.listFiles();
                fileList.addAll(loadFileList(files,mode));
                result.put("file",fileList);
                return TiaResult.success("路径存在", result);
            }else{
                return TiaResult.error("路径不存在");
            }
        }else if("search".equals(mode)){
            List<File> searchList = new ArrayList<>();
            searchFile(file,name,searchList);
            if(searchList.size()>0){
                File[] files = new File[searchList.size()];
                searchList.toArray(files);
                fileList.addAll(loadFileList(files,mode));
                result.put("file",fileList);
                return TiaResult.success("查询成功",result);
            }
            return TiaResult.error("无结果");
        }
        return TiaResult.error("模式错误");
    }

    public List<FileInfo> loadFileList(File[] files,String mode){
        List<FileInfo> fileList = new ArrayList<>();
        for(File f : files){
            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(f.getName());
            if(mode.equals("search")){
                String str = f.getAbsolutePath().replace("\\","/");
                fileInfo.setName(str.substring(str.indexOf("/s/")+3));
            }
            fileInfo.setEditTime(new Date(f.lastModified()));
            if(f.isDirectory()){
                fileInfo.setType(0);
            }else{
                fileInfo.setType(1);
                fileInfo.setSize(util.getFileSize(f));
            }
            fileList.add(fileInfo);
        }
        fileList.sort(Comparator.comparingInt(FileInfo::getType));
        return fileList;
    }

    public void searchFile(File file, String name,List<File> fileList) {
        File[] files = file.listFiles(pathname -> pathname.getName().contains(name) || pathname.isDirectory());
        for (File f : files) {
            if (f.exists()&&f.getName().contains(name)) {
                fileList.add(f);
                log.info("[search] " + f.getAbsolutePath());
            } else {
                searchFile(f, name,fileList);
            }
        }
    }

    /**
     * 文件操作
     *
     * @param route
     */
    @RequestMapping("/opt")
    @ResponseBody
    public TiaResult opt( @RequestParam(defaultValue = "/") String route,String name,String newName,@RequestParam(defaultValue = "mkdir")String opt) throws Exception {
        //String root = "/".equals(route)?"":route.substring(1)+ "/";
        String path = initPath(route);
        File filePath = new File(path);
        if(filePath.exists()){
            if(CommonUtils.isNotEmpty(name)){
                File file = new File(path,name);
                if(file.exists()){
                    switch (opt){
                        case "del":
                            deleteFile(file);
                            // qiniu
                            //qiniuUtils.delete(root+name);
                            return TiaResult.success("操作成功");
                        case "mkdir":
                            return TiaResult.error("文件已存在");
                        case "edit":
                            if(CommonUtils.isNotEmpty(newName)){
                                File editFile = new File(path,newName);
                                //qiniuUtils.move(root+name,root+newName);
                                if(editFile.exists()){
                                    return TiaResult.error("名称重复");
                                }
                                if(file.renameTo(editFile)){
                                    return TiaResult.success("操作成功");
                                }else{
                                    return TiaResult.error("Error");
                                }
                            }
                    }
                    return TiaResult.error("错误操作");
                }
                if("mkdir".equals(opt)){
                    file.mkdirs();
                    return TiaResult.success("创建成功");
                }
            }
        }
        return TiaResult.error("路径错误");
    }



    public static void deleteFile(File folder) throws Exception {
        if (!folder.exists()) {
            throw new Exception("文件不存在");
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    //递归直到目录下没有文件
                    deleteFile(file);
                } else {
                    //删除
                    file.delete();
                }
            }
        }
        //删除
        folder.delete();
    }


    /**
     * 文件下载
     * @param route
     * @param name
     * @param mode
     * @param response
     * @return
     */
    @RequestMapping("/download")
    @ResponseBody
    public HttpServletResponse download(@RequestParam(defaultValue = "/") String route,String name,@RequestParam(defaultValue = "download")String mode, HttpServletResponse response) {
        try {
            String path = initPath(route);
            File file = new File(path);
            if(file.exists()&&CommonUtils.isNotEmpty(name)){
                File downFile = new File(path,name);
                if(downFile.exists()){
                    String fileName = downFile.getName();
                    if("batchZip".equals(mode)){
                        fileName = fileName + ".zip";
                    }
                    response.setHeader("content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, "utf-8"));
                    if("download".equals(mode)){
                        if(downFile.isFile()){
                            InputStream fis = new BufferedInputStream(new FileInputStream(downFile));
                            byte[] buffer = new byte[fis.available()];
                            fis.read(buffer);
                            fis.close();
//                            response.reset();
//                        response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
                            response.setContentType("application/octet-stream");
//                        response.addHeader("Content-Length", "" + file.length());
                            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                            toClient.write(buffer);
                            toClient.flush();
                            toClient.close();
                        }
                    }else if("batchZip".equals(mode)){
                        response.setContentType("application/x-msdownload");
                        ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
                        List<File> fileList = new ArrayList<>();
                        batchFile(downFile,fileList);
                        for(File f : fileList){
                            int len = path.length();
                            if(path.lastIndexOf("/")!=len-1){
                                len += 1;
                            }
                            InputStream input = new FileInputStream(f);
                            zipOut.putNextEntry(new ZipEntry(f.getAbsolutePath().substring(len)));
                            int temp = 0;
                            //读取相关的文件
                            while((temp = input.read()) != -1){
                                //写入输出流中
                                zipOut.write(temp);
                            }
                            input.close();
                        }
                        // 设置注释
                        zipOut.setComment("hello");
                        //关闭流
                        zipOut.close();
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }


    public void batchFile(File file,List<File> fileList) {
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.exists()&&f.isFile()) {
                fileList.add(f);
            } else {
                batchFile(f,fileList);
            }
        }
    }

}
