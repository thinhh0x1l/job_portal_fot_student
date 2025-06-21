package com.jobportal.service.file;

import com.jobportal.exception.StorageException;
import com.jobportal.exception.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class StorageService {
    private Path rootLocation = Paths.get("photos/post-cvs/3");


    public Path getPath(){
        return this.rootLocation;
    }
    public void store(String uploadDir,MultipartFile file) {
        Path rootDir = Paths.get(uploadDir);
        if(Files.notExists(rootDir))
            try{
                Files.createDirectories(rootDir);
            } catch (IOException e) {
                throw new StorageException("Could not initialize storage", e);
            }
        try{

            if(file.isEmpty())
                throw new StorageException("Failed to store empty file.");
            Path destinationFile = rootDir.resolve(
                            Paths.get(Objects.requireNonNull(file.getOriginalFilename(),"Filename must not be null")))
                    .normalize().toAbsolutePath();
            if(!destinationFile.getParent().equals(rootDir.toAbsolutePath()))
                throw new StorageException("Cannot store file outside current directory.");
            try(InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }
    public void changeUrl(String url){
        url = url.trim();
        if(url.isEmpty())
            throw new StorageException("File upload location cannot be empty");
        this.rootLocation = Paths.get(url);
        try{
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }


    public Stream<Path> loadAll() {
        try{
            return Files.walk(this.rootLocation,1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }


    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }


    public Resource loadAsResource(String filename) {
        try{
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists()||resource.isReadable())
                return resource;
            else
                throw new StorageFileNotFoundException("Could not read file: " + filename);
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }


    public void deleteAll() {
        FileSystemUtils.deleteRecursively(this.rootLocation.toFile());
    }
}
