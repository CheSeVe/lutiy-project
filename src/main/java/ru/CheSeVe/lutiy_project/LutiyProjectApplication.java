package ru.CheSeVe.lutiy_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class LutiyProjectApplication {

		public static void main(String[] args) {
		SpringApplication.run(LutiyProjectApplication.class, args);
	}

}
