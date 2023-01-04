package com.tzsombi.services;

import com.tzsombi.model.Todo;
import com.tzsombi.model.User;
import jakarta.mail.MessagingException;

public interface IEmailService {

    void sendEmail(User user, Todo todo) throws MessagingException;

}
