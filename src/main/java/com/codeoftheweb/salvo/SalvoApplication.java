package com.codeoftheweb.salvo;

import com.sun.org.apache.xpath.internal.objects.XObject;
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
		public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository) {
			return (args) -> {
				// save a couple of customers
				Player player1 = new Player("wsnyder@comcast.net");
				Player player2 = new Player("pemungkah@hotmail.com");
				Player player3 = new Player("weidai@hotmail.com");
				Player player4 = new Player("kodeman@gmail.com");
				Player player5 = new Player("zeller@icloud.com");
				Player player6 = new Player("piolix@icloud.com");

				playerRepository.save(player1);
				playerRepository.save(player2);
				playerRepository.save(player3);
				playerRepository.save(player4);
				playerRepository.save(player5);
				playerRepository.save(player6);

				Game game1 = new Game();
				Game game2 = new Game(3600);
				Game game3 = new Game(7200);

				gameRepository.save(game1);
				gameRepository.save(game2);
				gameRepository.save(game3);

				GamePlayer gamePlayer1 = new GamePlayer(game1, player1);
				GamePlayer gamePlayer2 = new GamePlayer(game1, player2);
				GamePlayer gamePlayer3 = new GamePlayer(game2, player3);
				GamePlayer gamePlayer4 = new GamePlayer(game2, player4);
				GamePlayer gamePlayer5 = new GamePlayer(game3, player5);
				GamePlayer gamePlayer6 = new GamePlayer(game3, player6);

				gamePlayerRepository.save(gamePlayer1);
				gamePlayerRepository.save(gamePlayer2);
				gamePlayerRepository.save(gamePlayer3);
				gamePlayerRepository.save(gamePlayer4);
				gamePlayerRepository.save(gamePlayer5);
				gamePlayerRepository.save(gamePlayer6);

			};
		}
}
