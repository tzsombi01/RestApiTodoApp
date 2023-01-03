package com.tzsombi.services;

import com.tzsombi.exceptions.AuthException;
import com.tzsombi.exceptions.UserNotFoundException;
import com.tzsombi.model.User;
import com.tzsombi.repositories.UserRepository;
import com.tzsombi.utils.CredentialChecker;
import com.tzsombi.utils.Logger;
import com.tzsombi.utils.UserEmailObserver;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final Logger logger;

    private final UserEmailObserver userEmailObserver;
    @Autowired
    public UserService(UserRepository userRepository, Logger logger, UserEmailObserver userEmailObserver) {
        this.userRepository = userRepository;
        this.logger = logger;
        this.userEmailObserver = userEmailObserver;
    }

    public void registerUser(User user) throws AuthException, IOException {
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        if(user.getEmail() != null && user.getEmail().length() > 0) {
            user.setEmail(user.getEmail().toLowerCase());
            if(!pattern.matcher(user.getEmail()).matches()) {
                throw new AuthException("Invalid Email Format!");
            }
        }
        ifUserPresentWithEmailThrowAuthException(user.getEmail());

        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
        user.setPassword(hashedPassword);

        User createdUser = userRepository.save(user);
    }

    private void ifUserPresentWithEmailThrowAuthException(String email) throws AuthException {
        if(userRepository.existsByEmail(email)) {
            throw new AuthException("Email is already in use!");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUserById(Long deleterUserId, Long userIdToDelete) throws UserNotFoundException {
        CredentialChecker.checkCredentialsOfModifierUser(deleterUserId, userIdToDelete, userRepository);

        if(!userRepository.existsById(userIdToDelete)) {
            throw new UserNotFoundException("No user found with ID: " + userIdToDelete + "!");
        }

        userRepository.deleteById(userIdToDelete);
    }



    @Transactional
    public void updateUser(Long modifierUserId,
                           Long userIdToModify,
                           String name,
                           String email)
            throws UserNotFoundException, AuthException {
        CredentialChecker.checkCredentialsOfModifierUser(modifierUserId, userIdToModify, userRepository);

        User userToModify = userRepository.findById(userIdToModify)
                .orElseThrow(() -> new UserNotFoundException("No user found with ID: " + userIdToModify + " !"));

        if (name != null && name.length() > 0 && !Objects.equals(userToModify.getName(), name)) {
            userToModify.setName(name);
        }

        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        if(email != null && email.length() > 0) {
            email = email.toLowerCase();

            if(!pattern.matcher(email).matches()) {
                throw new AuthException("Invalid Email Format!");
            }
            ifUserPresentWithEmailThrowAuthException(email);

            userToModify.setEmail(email);
        }
    }

    public User validateUser(String email, String password) throws AuthException {
        if(email != null && email.length() > 0) {
            email = email.toLowerCase();
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Invalid email/password!"));
        if(!BCrypt.checkpw(password, user.getPassword())) {
            throw new AuthException("Invalid email/password!");
        }
        return user;
    }
}
