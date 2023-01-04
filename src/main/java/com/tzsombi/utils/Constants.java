package com.tzsombi.utils;

import org.springframework.core.io.ClassPathResource;

public class Constants {

    public static final ClassPathResource STATIC_FOLDER_CSV_PATH_RESOURCE =
            new ClassPathResource("static/logfiles.csv");

    public static final String EMAIL_SUBJECT = "Your task \"%s\" is due in 24 hours!";

    public static final String EMAIL_MESSAGE =
            "Dear %s,\na Todo is due at: %s\nHurry up and complete it before the deadline!";
}
