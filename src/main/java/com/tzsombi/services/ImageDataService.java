package com.tzsombi.services;

import com.tzsombi.exceptions.ImageDataNotFoundException;
import com.tzsombi.exceptions.ImageNotFoundUnderUser;
import com.tzsombi.exceptions.UserNotFoundException;
import com.tzsombi.model.ImageData;
import com.tzsombi.model.User;
import com.tzsombi.repositories.ImageDataRepository;
import com.tzsombi.repositories.UserRepository;
import com.tzsombi.utils.CredentialChecker;
import com.tzsombi.utils.ImageUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.zip.DataFormatException;

@Service
public class ImageDataService {

    ImageDataRepository imageDataRepository;

    UserRepository userRepository;

    @Autowired
    public ImageDataService(ImageDataRepository imageDataRepository, UserRepository userRepository) {
        this.imageDataRepository = imageDataRepository;
        this.userRepository = userRepository;
    }

    public String uploadImage(Long modifierUserId, Long userIdToModify, MultipartFile file)
            throws IOException, UserNotFoundException {
        CredentialChecker.checkCredentialsOfModifierUser(modifierUserId, userIdToModify, userRepository);

        User userToModify = userRepository.findById(userIdToModify)
                .orElseThrow(() -> new UserNotFoundException("No user found with ID: " + userIdToModify + "!"));

        ImageData image = imageDataRepository.save(ImageData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .user(userToModify)
                .imageData(ImageUtil.compressImage(file.getBytes())).build());

        userToModify.setImage(image);
        userRepository.save(userToModify);

        return "Image uploaded successfully: " + file.getOriginalFilename();
    }

    public byte[] getImageById(Long imageId) throws IOException, DataFormatException {
        ImageData image = imageDataRepository.findById(imageId)
                .orElseThrow(() -> new ImageDataNotFoundException("No image found with id " + imageId + "!"));
        return ImageUtil.decompressImage(image.getImageData());
    }

    public void deleteImageById(Long modifierUserId, Long userIdToModify, Long imageId)
            throws ImageDataNotFoundException, UserNotFoundException {

        ImageData image = imageDataRepository.findById(imageId)
                .orElseThrow(() -> new ImageDataNotFoundException("No image found with id " + imageId + "!"));

        User userToModify = userRepository.findById(userIdToModify)
                .orElseThrow(() -> new UserNotFoundException("No user found with ID: " + userIdToModify + "!"));

        CredentialChecker.checkCredentialsOfModifierUser(modifierUserId, userIdToModify, userRepository);

        userToModify.setImage(null);
        userRepository.save(userToModify);
        imageDataRepository.delete(image);
    }
}
