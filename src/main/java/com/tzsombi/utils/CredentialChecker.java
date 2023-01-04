package com.tzsombi.utils;

import com.tzsombi.exceptions.AuthException;
import com.tzsombi.exceptions.UserNotFoundException;
import com.tzsombi.model.User;
import com.tzsombi.repositories.UserRepository;

public class CredentialChecker {

    public static void checkCredentialsOfModifierUser(
            Long modifierUserId,
            Long userIdToModify,
            UserRepository userRepository) throws UserNotFoundException, AuthException {
        User modifyingUser = userRepository.findById(modifierUserId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(ErrorConstants.USER_NOT_FOUND_MESSAGE, userIdToModify)));

        if(modifyingUser.isAdmin() || modifierUserId.equals(userIdToModify)) {
            return;
        }
        throw new AuthException(ErrorConstants.NO_PERMISSION_TO_MODIFY_USER);
    }
}
