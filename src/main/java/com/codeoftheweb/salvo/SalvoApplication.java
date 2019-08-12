package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
		@Bean
		public CommandLineRunner initData(PlayerRepository repository) {
			return (args) -> {
				// save a couple of customers
				repository.save(new Player("wsnyder@comcast.net"));
				repository.save(new Player("pemungkah@hotmail.com"));
				repository.save(new Player("weidai@hotmail.com"));
				repository.save(new Player("kodeman@gmail.com"));
				repository.save(new Player("zeller@icloud.com"));
				
			};
		}
}
