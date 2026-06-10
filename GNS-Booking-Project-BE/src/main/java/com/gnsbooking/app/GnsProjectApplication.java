package com.gnsbooking.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@SpringBootApplication
@EnableAsync
public class GnsProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(GnsProjectApplication.class, args);
	}

	// ✅ Swagger Config inside main class
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GNS API")
                        .version("1.0")
                        .description("GNS Event Booking System"));
    }
}