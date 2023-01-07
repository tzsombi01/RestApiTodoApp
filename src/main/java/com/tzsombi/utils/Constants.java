package com.tzsombi.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class Constants {

    public static final Resource STATIC_FOLDER_CSV_PATH_RESOURCE =
            new ClassPathResource("resources/logfiles.csv");

    public static final String EMAIL_SUBJECT = "Your task \"%s\" is due in 24 hours!";

    public static final String EMAIL_MESSAGE =
            """
                    <p>Dear <strong> %s </strong>, </p>
                    <p>a Todo is due at: <em>%s </em> </p>
                    <p>Hurry up and complete it before the deadline!</p>
                    Sincerely,\040
                    your favourite Todo App!
            """;
}
