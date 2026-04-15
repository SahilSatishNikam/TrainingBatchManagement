package com.example.Training_system;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TrainingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainingSystemApplication.class, args);
	}

	 @Bean
	    CommandLineRunner run(PasswordEncoder encoder) {
	        return args -> {
	            System.out.println("1234 -> " + encoder.encode("1234"));
	            System.out.println("5678 -> " + encoder.encode("5678"));
	        };
	    }
}
