package com.tzsombi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@EnableScheduling
public class RestApiTodoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApiTodoAppApplication.class, args);
	}

}
