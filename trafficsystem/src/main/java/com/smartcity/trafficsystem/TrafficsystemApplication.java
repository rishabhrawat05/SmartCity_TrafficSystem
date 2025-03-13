package com.smartcity.trafficsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TrafficsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrafficsystemApplication.class, args);
	}

}
