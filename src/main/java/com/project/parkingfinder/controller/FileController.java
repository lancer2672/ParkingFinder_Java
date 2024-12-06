package com.project.parkingfinder.controller;

import java.io.IOException;

import com.project.parkingfinder.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.parkingfinder.service.FileStorageService;




@CrossOrigin
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;
    private final MinioService minioService;

    public FileController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/mupload")
    public String mUploadFile(@RequestParam("file") MultipartFile file) {
        return minioService.uploadFile(file, "/");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileStorageService.storeFile(file);

            return ResponseEntity.ok("Tệp đã được tải lên thành công: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Không thể tải lên tệp: " + e.getMessage());
        }
    }


    @GetMapping("/stream/{fileName:.+}")
    public ResponseEntity<Resource> streamFile(@PathVariable("fileName") String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
