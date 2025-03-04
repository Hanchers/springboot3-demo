package com.hancher.springboot3.graalvmnativeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class GraalvmNativeAppApplication {

	@RequestMapping("/{name}")
	String home(@PathVariable String name) {
		String x = "Hello " + name;
		System.out.println(x);
		return x;
	}
	public static void main(String[] args) {
		SpringApplication.run(GraalvmNativeAppApplication.class, args);
	}

}
