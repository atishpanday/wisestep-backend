package com.example.wisestepBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WisestepBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WisestepBackendApplication.class, args);
	}

	@GetMapping("/welcome")
	public String Welcome(@RequestParam(value="name", defaultValue="there") String name) {
		return "Hello " + name;
	}

}
