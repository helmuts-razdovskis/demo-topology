package com.example.demo;

import com.example.demo.database.DatabaseHandle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class DemoApplication {
	@Value("${db.dir:./data}")
	private String databaseDir;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	DatabaseHandle getDatabase() { return new DatabaseHandle(databaseDir, false); }

}
