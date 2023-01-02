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
                .orElseThrow(() -> new UserNotFoundException("No user found with ID: " + modifierUserId + " !"));

        if(!modifyingUser.isAdmin() && !modifierUserId.equals(userIdToModify)) {
            throw new AuthException("You do not have permission to modify user!");
        }
    }
}
