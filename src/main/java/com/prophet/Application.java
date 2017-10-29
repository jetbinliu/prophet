package com.prophet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@ComponentScan
@EnableTransactionManagement
public class Application extends SpringBootServletInitializer{

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		SpringApplication.run(Application.class, args);
	}

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
}
