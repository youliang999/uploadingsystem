package com.youliang.controller.upload;


import com.youliang.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/upload")
public class MyFileUploadController {
    private static final Logger log = LoggerFactory.getLogger(MyFileUploadController.class);

    @Autowired
    private StorageService storageService;

    @RequestMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("multifile");
    }

    @RequestMapping("/files")
    @ResponseBody
    public String lists() {
        Stream stream = storageService.loadAll();
        List<String> fileNames = (List<String>) stream.map((path) -> path.toString()).collect(Collectors.toList());
        log.info("query all files success");
        StringBuffer sb = new StringBuffer();
        fileNames.stream().map(fileName -> {
            if(!StringUtils.isEmpty(fileName)) {
                String downloadPath = this.linkedToServeFile(fileName);
                String deletePath = this.linkedToDeleteFile(fileName);
                String ns = "<div style=\"margin-left: 200px;\">" + fileName + "&nbsp; &nbsp; &nbsp; <a href= " + downloadPath + "> 下载 </a> &nbsp; &nbsp; &nbsp; " +
                        "<a href ="+ deletePath + "> 删除 </a>  </br></div>";
                return ns;
            } else {
                return "";
            }
        }).collect(Collectors.toList()).forEach(sb::append);
        return sb.toString();
    }

    @RequestMapping("/file")
    @ResponseBody
    public String upload(@RequestParam("fileName") MultipartFile file) {
        if(file.isEmpty()){
            return "false";
        }
        String fileName = storageService.store(file);
        log.info("upload file " + fileName + " success");
        return "&nbsp; &nbsp;   success  </br> <a href=\"/upload/index\"> 返回上一级 </a> &nbsp; &nbsp;</br> <a href=\"/upload/files\"> 所有文件 </a>";
    }


    @RequestMapping("/multifile")
    @ResponseBody
    public String uploadmulti(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("fileName");

        for(MultipartFile file : files) {
            upload(file);
        }
        return "&nbsp; &nbsp;  success  </br>  <a href=\"/upload/index\"> 返回上一级 </a>  &nbsp; &nbsp;</br>  <a href=\"/upload/files\"> 所有文件 </a>";
    }

    @GetMapping({"/files/{filename:.+}"})
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws UnsupportedEncodingException {
        Resource file = this.storageService.loadAsResource(filename);
        log.info("download file " + filename + " success");
        String myfileName = new String(file.getFilename().getBytes(), "ISO-8859-1");
        return (ResponseEntity.ok().header("Content-Disposition", new String[]{"attachment; filename=\"" + myfileName + "\""})
        .contentType(MediaType.APPLICATION_OCTET_STREAM)).body(file);
    }

    @RequestMapping({"/delete/{filename:.+}"})
    public ModelAndView deleteFile(@PathVariable String filename) {
        log.info("===>>> fileName:{}", filename);
//        String path = linkedToServeFile(filename);
//        log.info("===>>> file path:{}", path);
        storageService.delete(filename);
        return new ModelAndView("redirect:/upload/files") ;
    }

    @RequestMapping("/uploadStr")
    @ResponseBody
    public String makeContentToFile(String content) {
        if(StringUtils.isEmpty(content))
            return "content is null";
        String fileName = String.valueOf(System.currentTimeMillis());
        String s = storageService.makeToFile(content, fileName);
        return "success. fileName : " + fileName +".txt" + "</br>  <a href=\"/upload/index\"> 返回上一级 </a>  &nbsp; &nbsp;</br>  <a href=\"/upload/files\"> 所有文件 </a>";
    }

    private String linkedToServeFile(String fileName) {
        UriComponents serveFile = MvcUriComponentsBuilder.fromMethodName(MyFileUploadController.class, "serveFile", new Object[]{fileName}).build();
        String url = serveFile.toString();
        return url;
    }


    private String linkedToDeleteFile(String fileName) {
        UriComponents serveFile = MvcUriComponentsBuilder.fromMethodName(MyFileUploadController.class, "deleteFile", new Object[]{fileName}).build();
        String url = serveFile.toString();
        return url;
    }

}
