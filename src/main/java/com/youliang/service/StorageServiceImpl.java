package com.youliang.service;

import com.youliang.exception.StorageException;
import com.youliang.exception.StorageFileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.stream.Stream;


@Service
public class StorageServiceImpl implements StorageService {
    private static final Logger log = LoggerFactory.getLogger(StorageServiceImpl.class);
    private static final String rootPath = "/data/apps/upload-files/upload-dir" ;
    private static final String deletePath = "/data/apps/upload-files/upload-dir/delete" ;
//    private static final String rootPath = "D:\\myDownload" ;
//    private static final String deletePath = "D:\\myDownload\\delete" ;
    private static final Path rootLocation = Paths.get(rootPath);
    private static final Path deleteLocation = Paths.get(deletePath);
    public String store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        int size = (int) file.getSize();
        log.info(filename + "-->" + size);
        String path = rootPath;
        File dest = new File(path + "/" + filename);
        if(!dest.getParentFile().exists()){ //判断文件父目录是否存在
            dest.getParentFile().mkdir();
        }

        try {
            InputStream inputStream = file.getInputStream();
            Files.copy(inputStream, rootLocation.resolve(filename), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            inputStream.close();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return dest.getAbsolutePath();

    }

    @Override
    public Stream<Path> loadAll() {
        try {
            Stream var10000 = Files.walk(rootLocation, 1, new FileVisitOption[0]).filter((path) -> {
               return  (!path.equals(rootPath) && new File(String.valueOf(path)).isFile() );

            });
            Path var10001 = rootLocation;
            var10001.getClass();
            return var10000.map(other -> var10001.relativize((Path) other));
        } catch (IOException var2) {
            throw new StorageException("Failed to read stored files", var2);
        }
    }
    public Resource loadAsResource(String filename) {
        try {
            Path file = this.load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() && !resource.isReadable()) {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            } else {
                return resource;
            }
        } catch (MalformedURLException var4) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, var4);
        }
    }

    @Override
    public String delete(String filename) {
        String path = rootPath + "/" + filename;
        File file = new File(path);
        InputStream inputStream = null;
        try {
            String deletePath1 = deletePath;
            File dest = new File(deletePath1 + "/" + filename);
            if(!dest.getParentFile().exists()){ //判断文件父目录是否存在
                dest.getParentFile().mkdir();
            }
            inputStream = new FileInputStream(file);
            Files.copy(inputStream, deleteLocation.resolve(filename), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            log.info("backup file:{} success.", filename);
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Path deletePath = Paths.get(path);
        try {
            Files.delete(deletePath);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        }
        log.info("===>>> delete success. filename:{}", filename);
        return "success";
    }

    public Path load(String filename) {
        return this.rootLocation.resolve(filename);
    }



    public String makeToFile(String content, String fileName) {
        String filenameTemp = "";
        try {
            filenameTemp = creatTxtFile(fileName);
            writeTxtFile(content, filenameTemp);
        } catch (IOException e) {
            e.printStackTrace();
            log.info(e.getMessage(), e);
        }
        return filenameTemp;
    }

    /**
     * 创建文件
     *
     * @throws IOException
     */
    public String creatTxtFile(String name) throws IOException {
        String filenameTemp = rootPath +"/" +  name + ".txt";
        log.info("filenameTemp:{}", filenameTemp);
        File filename = new File(filenameTemp);
        if (!filename.exists()) {
            filename.createNewFile();
        }
        return filenameTemp;
    }


        /**
         * 写文件
         *
         * @param newStr
         *            新内容
         * @throws IOException
         */
    public  boolean writeTxtFile(String newStr, String filenameTemp) throws IOException {
        // 先读取原有文件内容，然后进行写入操作

        boolean flag = false;
        String filein = newStr + "\r\n";
        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            // 文件路径
            File file = new File(filenameTemp);
            // 将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();

            // 保存该文件原有的内容
            for (int j = 1; (temp = br.readLine()) != null; j++) {
                buf = buf.append(temp);
                // System.getProperty("line.separator")
                // 行与行之间的分隔符 相当于“\n”
                buf = buf.append(System.getProperty("line.separator"));
            }
            buf.append(filein);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            flag = true;
        } catch (IOException e1) {
            // TODO 自动生成 catch 块
            throw e1;
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return flag;
    }


}
