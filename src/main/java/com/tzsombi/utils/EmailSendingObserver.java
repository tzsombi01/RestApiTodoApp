package com.tzsombi.utils;

import com.tzsombi.model.User;
import com.tzsombi.repositories.TodoRepository;
import com.tzsombi.services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailSendingObserver {

    List<User> users = new ArrayList<>();

    private final EmailService emailService;

    private final Clock clock = Clock.systemDefaultZone();
    private final TodoRepository todoRepository;

    @Autowired
    public EmailSendingObserver(EmailService emailService,
                                TodoRepository todoRepository) {
        this.emailService = emailService;
        this.todoRepository = todoRepository;
    }

    public void addObserver(User user) {
        users.add(user);
    }

    public void removeObserver(User user) {
        users.remove(user);
    }

    @Scheduled(cron = "0 0/30 * * * *")
    private void alertUsers() {
        users.forEach(user -> {
            todoRepository.findAllByUserId(user.getId())
                    .stream()
                    .filter(todo -> !todo.isNotified() && todo.isItDueInADay(clock))
                    .forEach(todo -> {
                        try {
                            emailService.sendEmail(user, todo);
                        } catch (MessagingException exception) {
                            throw new RuntimeException(exception);
                        }
                        todo.setNotified(true);
                        todoRepository.save(todo);
                    });
        });
    }
}
