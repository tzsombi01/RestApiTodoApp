package com.tzsombi.services;

import com.tzsombi.exceptions.AtAuthException;
import com.tzsombi.model.User;
import com.tzsombi.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) throws AtAuthException {
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        if(user.getEmail() != null && user.getEmail().length() > 0) {
            user.setEmail(user.getEmail().toLowerCase());
            if(!pattern.matcher(user.getEmail()).matches()) {
                throw new AtAuthException("Invalid Email Format!");
            }
        }
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
        user.setPassword(hashedPassword);
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if(optionalUser.isPresent()) {
            throw new AtAuthException("Email is already in use!");
        }
        userRepository.save(user);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public void deleteUserById(Integer userId) throws AtAuthException {
        if(!userRepository.existsById(userId)) {
            throw new AtAuthException("No user found with ID: " + userId + "!");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUser(Integer userId, String name, String email, String profilePictureUrl) throws AtAuthException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AtAuthException("No user found with ID: " + userId + "!"));
        if (name != null && name.length() > 0 && !Objects.equals(user.getName(), name)) {
            user.setName(name);
        }
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        if(email != null && email.length() > 0) {
            email = email.toLowerCase();
            if(!pattern.matcher(email).matches()) {
                throw new AtAuthException("Invalid Email Format!");
            }
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if(optionalUser.isPresent()) {
                throw new AtAuthException("Email is already in use!");
            }
            user.setEmail(email);
        }
        if(profilePictureUrl != null
                && profilePictureUrl.length() > 0
                && !Objects.equals(user.getProfilePictureUrl(), profilePictureUrl)) {
            user.setProfilePictureUrl(profilePictureUrl);
        }
    }
}