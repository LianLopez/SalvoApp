package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository,
                                      GameRepository gameRepository,
                                      GamePlayerRepository gamePlayerRepository,
                                      ShipRepository shipRepository,
                                      SalvoRepository salvoRepository) {
        return (args) -> {
            Player player1 = new Player("j.bauer@ctu.gov");
            Player player2 = new Player("c.obrian@ctu.gov");
            Player player3 = new Player("t.almeida@ctu.gov");
            Player player4 = new Player("d.palmer@whitehouse.gov");
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

            Set<String> shipL1 = new HashSet<>(Arrays.asList("H2", "H3", "H4"));
            Set<String> shipL2 = new HashSet<>(Arrays.asList("E1", "F1", "G1"));
            Set<String> shipL3 = new HashSet<>(Arrays.asList("B4", "B5"));
            Set<String> shipL4 = new HashSet<>(Arrays.asList("B5", "C5", "D5"));
            Set<String> shipL5 = new HashSet<>(Arrays.asList("F1", "F2"));

            Ship ship1 = new Ship("Destroyer", shipL1, gamePlayer1);
            Ship ship2 = new Ship("Submarine", shipL2, gamePlayer1);
            Ship ship3 = new Ship("Patrol Boat", shipL3, gamePlayer1);
            Ship ship4 = new Ship("Destroyer", shipL4, gamePlayer2);
            Ship ship5 = new Ship("Destroyer", shipL5, gamePlayer2);

            shipRepository.saveAll(Arrays.asList(ship1, ship2, ship3, ship4, ship5));

            Set<String> salvoL1 = new HashSet<>(Arrays.asList("H1", "H2", "H3"));
            Set<String> salvoL2 = new HashSet<>(Arrays.asList("A3", "A4"));
            Set<String> salvoL3 = new HashSet<>(Arrays.asList("F5", "F6"));
            Set<String> salvoL4 = new HashSet<>(Arrays.asList("B3", "H4", "B5"));

            Salvo salvo1 = new Salvo(gamePlayer1, 1, salvoL1);
            Salvo salvo2 = new Salvo(gamePlayer2, 1, salvoL2);
            Salvo salvo3 = new Salvo(gamePlayer1, 2, salvoL3);
            Salvo salvo4 = new Salvo(gamePlayer2, 2, salvoL4);

            salvoRepository.saveAll(Arrays.asList(salvo1, salvo2, salvo3, salvo4));
        };
    }
}
