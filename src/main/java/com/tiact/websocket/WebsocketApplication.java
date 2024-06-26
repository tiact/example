package com.tiact.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author Tia_ct
 */
@SpringBootApplication
public class WebsocketApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebsocketApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(WebsocketApplication.class, args);
	}
}
