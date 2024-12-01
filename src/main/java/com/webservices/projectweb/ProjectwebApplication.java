package com.webservices.projectweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.webservices.projectweb")
public class ProjectwebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectwebApplication.class, args);
	}

}
