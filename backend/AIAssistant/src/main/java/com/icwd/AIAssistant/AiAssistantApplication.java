package com.icwd.AIAssistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
		scanBasePackages = "com.icwd.AIAssistant"
)
@EnableFeignClients(
		basePackages = "com.icwd.AIAssistant.client"
)
public class AiAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				AiAssistantApplication.class,
				args
		);
	}
}