package com.tzsombi.services;

import com.tzsombi.exceptions.ImageDataNotFoundException;
import com.tzsombi.exceptions.ImageNotFoundUnderUser;
import com.tzsombi.exceptions.UserNotFoundException;
import com.tzsombi.model.Image;
import com.tzsombi.model.User;
import com.tzsombi.repositories.ImageRepository;
import com.tzsombi.repositories.UserRepository;
import com.tzsombi.utils.CredentialChecker;
import com.tzsombi.utils.ErrorConstants;
import com.tzsombi.utils.ImageUtil;
import com.tzsombi.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.zip.DataFormatException;

@Service
public class ImageService {

    private final ImageRepository imageDataRepository;

    private final UserRepository userRepository;

    private final Logger logger;

    @Autowired
    public ImageService(ImageRepository imageDataRepository, UserRepository userRepository, Logger logger) {
        this.imageDataRepository = imageDataRepository;
        this.userRepository = userRepository;
        this.logger = logger;
    }

    public String uploadImage(Long modifierUserId, Long userIdToModify, MultipartFile file)
            throws IOException, UserNotFoundException {
        CredentialChecker.checkCredentialsOfModifierUser(modifierUserId, userIdToModify, userRepository);

        User userToModify = userRepository.findById(userIdToModify)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(ErrorConstants.USER_NOT_FOUND_MESSAGE, userIdToModify)));

        Image image = imageDataRepository.save(Image.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .user(userToModify)
                .imageData(ImageUtil.compressImage(file.getBytes())).build());

        userToModify.setImage(image);
        userRepository.save(userToModify);

        String logLine = String.join(",",
                "add image",
                modifierUserId.toString(),
                userIdToModify.toString(),
                LocalDateTime.now().toString());
        logger.convertDataToCSvAndWriteToFile(logLine);

        return "Image uploaded successfully: " + file.getOriginalFilename();
    }

    public byte[] getImageById(Long imageId) throws IOException, DataFormatException {
        Image image = imageDataRepository.findById(imageId)
                .orElseThrow(() -> new ImageDataNotFoundException(
                        String.format(ErrorConstants.IMAGE_NOT_FOUND_MESSAGE, imageId)));

        return ImageUtil.decompressImage(image.getImageData());
    }

    public void deleteImageById(Long modifierUserId, Long userIdToModify, Long imageId)
            throws ImageDataNotFoundException, UserNotFoundException, ImageNotFoundUnderUser, IOException {

        Image image = imageDataRepository.findById(imageId)
                .orElseThrow(() -> new ImageDataNotFoundException(
                        String.format(ErrorConstants.IMAGE_NOT_FOUND_MESSAGE, imageId)));

        User userToModify = userRepository.findById(userIdToModify)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(ErrorConstants.USER_NOT_FOUND_MESSAGE, userIdToModify)));

        if(userToModify.getImage() == null ||!userToModify.getImage().equals(image)) {
            throw new ImageNotFoundUnderUser(ErrorConstants.IMAGE_NOT_FOUND_UNDER_USER);
        }

        CredentialChecker.checkCredentialsOfModifierUser(modifierUserId, userIdToModify, userRepository);

        userToModify.setImage(null);
        userRepository.save(userToModify);
        imageDataRepository.delete(image);

        String logLine = String.join(",",
                "delete image",
                modifierUserId.toString(),
                userIdToModify.toString(),
                LocalDateTime.now().toString());
        logger.convertDataToCSvAndWriteToFile(logLine);
    }
}
