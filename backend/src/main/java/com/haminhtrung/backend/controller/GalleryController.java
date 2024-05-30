package com.haminhtrung.backend.controller;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.haminhtrung.backend.service.GalleryService;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.haminhtrung.backend.entity.Gallery;
// import com.haminhtrung.backend.repository.GalleryRepository;

@RestController
@AllArgsConstructor
@RequestMapping("api/galleries")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" }, exposedHeaders = "Content-Range")

public class GalleryController {
    private GalleryService galleryService;

    // get gallery by productId
    private final String UPLOAD_DIR = "E:/WEB_SPRINGBOOT/JAVA_WEBSHOES/backend/src/main/resources/static/upload";

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Gallery>> getImagesByProductId(@PathVariable Long productId) {
        List<Gallery> galleries = galleryService.getImagesByProductId(productId);
        return ResponseEntity.ok(galleries);
    }

    // get image by filename
    @GetMapping("/image/{fileName:.+}")
    public ResponseEntity<String> getImageUrl(@PathVariable String fileName) {
        try {
            String imageUrl = "/upload/" + fileName; // Tạo đường dẫn URL của ảnh
            return ResponseEntity.ok().body(imageUrl); // Trả về URL của ảnh
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // get all
    @GetMapping
    public ResponseEntity<List<Gallery>> getAllGalleries() {
        List<Gallery> galleries = galleryService.getAllGalleries();
        return new ResponseEntity<>(galleries, HttpStatus.OK);
    }

    // GET galleries BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Gallery> getGalleryById(@PathVariable("id") Long id) {
        Gallery gallery = galleryService.getGalleryById(id);
        if (gallery != null) {
            return new ResponseEntity<>(gallery, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // post image by productid
    @PostMapping("/uploadImage/{productId}")
    public Gallery uploadImage(@PathVariable Long productId, @RequestParam("file") MultipartFile file) {
        return galleryService.saveImage(productId, file, 0);
    }

    // post nhiều image by productid
    @PostMapping("/uploadImages/{productId}")
    public List<Gallery> uploadImages(@PathVariable Long productId, @RequestParam("files") MultipartFile[] files) {
        return galleryService.saveImages(productId, files);
    }
    
    @PostMapping("/update/{productId}")
    public ResponseEntity<String> updateGallery(@PathVariable Long productId, @RequestParam("files") MultipartFile[] newFiles) {
        try {
            // Xóa toàn bộ ảnh cũ của sản phẩm
            galleryService.deleteGallery(productId);

            // Lưu ảnh mới
            galleryService.saveImages(productId, newFiles);

            return ResponseEntity.ok("Update gallery successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update gallery");
        }
    }

    // update Gallery REST API
    // @PutMapping("{id}")
    // // http://localhost:8080/api/gallerys/1
    // public ResponseEntity<Gallery> updateGallery(@PathVariable("id") Long galleryId, @RequestBody Gallery Gallery) {
    //     Gallery.setId(galleryId);
    //     Gallery updateGallery = galleryService.updateGallery(Gallery);
    //     return new ResponseEntity<>(updateGallery, HttpStatus.OK);
    // }

    // delete gallery REST API
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteGallery(@PathVariable("id") Long galleryId) {
        galleryService.deleteGallery(galleryId);
        return new ResponseEntity<>("Gallery successfully deleted!", HttpStatus.OK);
    }
}
