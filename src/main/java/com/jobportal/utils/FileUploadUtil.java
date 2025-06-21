package com.jobportal.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class FileUploadUtil {
    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile)  {
        Path uploadPath = Paths.get(uploadDir);

        if(!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(!multipartFile.isEmpty()){
            try(InputStream inputStream = multipartFile.getInputStream()){
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e){
                throw new RuntimeException("Could not save file " + fileName, e);
            }
        }
    }
    public static void cleanDir(String dir){
        Path dirPath = Paths.get(dir);
        try(Stream<Path> files = Files.list(dirPath)){
            files.forEach(file ->{
                        try{
                            if(!Files.isDirectory(file))
                                Files.delete(file);
                        } catch (IOException e) {
                            System.out.println("Could not delete file " + file.toString());
                        }
                    }
            );
        } catch (IOException e) {
            System.out.println("Could not list directory: " + dirPath.toString());
        }
    }

    public static void uploadAvatar(String dir, String fileName, MultipartFile multipartFile, String delete) {
        Path rootDir = Paths.get(dir);

        try {
            // Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(rootDir)) {
                Files.createDirectories(rootDir);
            }

            // Nếu file cần xóa tồn tại trên server thì xóa
            if (delete != null && !delete.isBlank()) {
                Path deleteFile = rootDir.resolve(delete);
                if (Files.exists(deleteFile)) {
                    Files.delete(deleteFile);
                }
            }

            if(!multipartFile.isEmpty()){
                try(InputStream inputStream = multipartFile.getInputStream()){
                    Path filePath = rootDir.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }catch (IOException e){
                    throw new IOException("Could not save file " + fileName, e);
                }        }

        } catch (IOException e) {
            System.out.println("Lỗi xử lý file: " + e.getMessage());
        }
    }
}
