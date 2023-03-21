package ru.yandex.practicum.javafilmorate;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class JavaFilmorateApplication {

	//develop git
	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(JavaFilmorateApplication.class, args);
	}
}
