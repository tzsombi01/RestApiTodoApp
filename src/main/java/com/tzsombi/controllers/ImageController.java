package com.tzsombi.controllers;

import com.tzsombi.exceptions.ImageDataNotFoundException;
import com.tzsombi.exceptions.ImageNotFoundUnderUser;
import com.tzsombi.exceptions.UserNotFoundException;
import com.tzsombi.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    @Autowired
    private ImageService imageDataService;

    @PostMapping("/upload/{modifierUserId}")
    public ResponseEntity<String> uploadImage(
            @PathVariable("modifierUserId") Long modifierUserId,
            @RequestParam("userIdToModify") Long userIdToModify,
            @RequestParam("image") MultipartFile image) throws IOException, UserNotFoundException {
        String response = imageDataService.uploadImage(modifierUserId, userIdToModify, image);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{image_id}")
    public ResponseEntity<?> getImageById(@PathVariable("image_id") Long imageId)
            throws IOException, DataFormatException {
        byte[] image = imageDataService.getImageById(imageId);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @DeleteMapping("/delete/{modifierUserId}")
    public ResponseEntity<String> deleteImageById(
            @PathVariable("modifierUserId") Long modifierUserId,
            @RequestParam("userIdToModify") Long userIdToModify,
            @RequestParam("imageId") Long imageId)
            throws ImageDataNotFoundException, UserNotFoundException, ImageNotFoundUnderUser {

        imageDataService.deleteImageById(modifierUserId, userIdToModify, imageId);
        return new ResponseEntity<>("Image deleted successfully!", HttpStatus.OK);
    }
}
