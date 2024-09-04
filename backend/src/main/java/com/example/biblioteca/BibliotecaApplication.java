package com.example.biblioteca;

import com.example.biblioteca.entity.PActivo;
import com.example.biblioteca.service.AuthService;
import com.example.biblioteca.service.PAService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.TemporalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.example.biblioteca.repository")
@EntityScan(basePackages = "com.example.biblioteca.entity")
//@ComponentScan(basePackages = "com.example.biblioteca")
@ComponentScan
@SpringBootApplication
public class BibliotecaApplication extends SpringBootServletInitializer {

	@Autowired
	private AuthService authService;
	@Autowired
	private PAService paService;
	public static void main(String[] args) {
		SpringApplication.run(BibliotecaApplication.class, args);
	}
	@PostConstruct
	public void init(){
		authService.initializeAdminUser();
		paService.initializeUserPrestamos();
	}


}
