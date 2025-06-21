package com.jobportal.controller.rest;

import com.jobportal.exception.StorageException;
import com.jobportal.exception.StorageFileNotFoundException;
import com.jobportal.service.file.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recruiter")
public class FileRestController {
    private final StorageService storageService;

//    @GetMapping("/files/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<Resource> serveFile(@PathVariable String filename){
//        Resource file = storageService.loadAsResource(filename);
//        if(file == null){
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity
//                .ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + file.getFilename() + "\"")
//                .body(file);
//    }

//    @GetMapping("/")
//    public String listUploadedFiles(Model model){
//        model.addAttribute("files",
//                storageService.loadAll()
//                        .map(path -> MvcUriComponentsBuilder.fromMethodName(
//                                        FileRestController.class,
//                                        "serveFile",
//                                        path.getFileName().toString())
//                                .build().toUri().toString())
//                        .collect(Collectors.toList()));
//        return "uploadForm";
//    }

    @GetMapping("/files/{id}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename,
                                              @PathVariable("id")int id) throws MalformedURLException {
        // Path file =storageService.getPath().resolve(filename);
        Path file= Paths.get("photos/post-cvs/"+id).resolve(filename);
        Resource resource = new UrlResource(file.toUri());

        String contentType = filename.endsWith(".pdf") ? "application/pdf" : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        storageService.store("",file);
        redirectAttributes.addFlashAttribute("message",
                "File successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/";
    }
    @PostMapping("/api")
    @ResponseBody
    public boolean handleFileUpload2(@RequestParam("file") MultipartFile file,
                                     RedirectAttributes redirectAttributes) {
        storageService.store("",file);
        redirectAttributes.addFlashAttribute("message",
                "File successfully uploaded " + file.getOriginalFilename() + "!");
        return true;
    }
    @PostMapping("/change")
    public String changeUrl(@RequestParam("chan") String change){
        storageService.changeUrl(change);
        return "redirect:/";
    }
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc){
        return ResponseEntity.notFound().build();
    }
    @ExceptionHandler(StorageException.class)
    public String f(RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("m","error");
        return "redirect:/";
    }
}
