package com.youliang.service;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Stream;

public interface StorageService {

    String store(MultipartFile file);

    Stream loadAll();

    Resource loadAsResource(String filename);

    String delete(String filename);

    String makeToFile(String content, String str);

}
