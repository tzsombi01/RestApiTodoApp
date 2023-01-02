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
                .imageData(ImageUtil.compressImage(file.getBytes())).build());

        setUserProfilePicture(userToModify, image);
        System.out.println("Image's userId: " + image.getUser().getUserId());
        System.out.println("User's imageId: " + userToModify.getImage().getImageId());

        return "Image uploaded successfully: " +  file.getOriginalFilename();
    }

    @Transactional
    private void setUserProfilePicture(User userToModify, ImageData image) {
        image.setUser(userToModify);
        userToModify.setImage(image);
    }

    @Transactional
    public byte[] getImageById(Long imageId) throws IOException, DataFormatException {
        ImageData dbImage = imageDataRepository.findById(imageId)
                .orElseThrow(() -> new ImageDataNotFoundException("No image data found with id " + imageId + "!"));
        return ImageUtil.decompressImage(dbImage.getImageData());
    }

    public void deleteImageById(Long modifierUserId, Long userIdToModify, Long imageId)
            throws ImageDataNotFoundException, UserNotFoundException, ImageNotFoundUnderUser {
        if(!imageDataRepository.existsById(imageId)) {
            throw new ImageDataNotFoundException("No image data found with id " + imageId + "!");
        }

        User userToModify = userRepository.findById(userIdToModify)
                .orElseThrow(() -> new UserNotFoundException("No user found with ID: " + userIdToModify + "!"));

        // if(!userToModify.getImage().getImageId().equals(imageId)) {
        //     throw new ImageNotFoundUnderUser("The user does not have this image!");
        // }

        CredentialChecker.checkCredentialsOfModifierUser(modifierUserId, userIdToModify, userRepository);

        imageDataRepository.deleteById(imageId);
    }
}
