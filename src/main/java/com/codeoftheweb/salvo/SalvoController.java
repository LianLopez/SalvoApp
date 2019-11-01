package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class SalvoController {

  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private GamePlayerRepository gamePlayerRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @RequestMapping("/games")
  public Map<String, Object> getGames(Authentication authentication) {
    Map<String, Object> dto = new LinkedHashMap<>();
    if (Guest(authentication)){
      Map<String, Object> guest = new LinkedHashMap<>();
      guest.put("email", "Guest");
      dto.put("player", guest);
    }else{
      Player player = playerRepository.findByUserName(authentication.getName());
      dto.put("player", player.getPlayerDto());
    }
    dto.put("games", gameRepository.findAll()
            .stream()
            .sorted(Comparator.comparing(Game::getId))
            .map(Game::getDto)
            .collect(toList()));
    return dto;
  }

  private boolean Guest(Authentication authentication) {
    return authentication == null || authentication instanceof AnonymousAuthenticationToken;
  }

  @RequestMapping("/game_view/{id}")
  public Map<String, Object> getGameView(@PathVariable long id, Authentication authentication) {
    GamePlayer gamePlayer = gamePlayerRepository.findById(id).get();
    if (Guest(authentication)) {
      Map<String, Object> dto = new LinkedHashMap<>();
      dto.put("error", "UNAUTHORIZED");
      dto.put("status", "401");
      return dto;
    } else {
      Player player = playerRepository.findByUserName(authentication.getName());
      if (gamePlayer.getPlayer().getId() == player.getId()) {
        Map<String, Object> dtoGame = gamePlayer.getGame().getDto();
        dtoGame.put("ships", getShipList(gamePlayer.getShips()));
        Set<GamePlayer> gamePlayers = gamePlayer.getGame().getGamePlayers();
        Set<Salvo> salvos = gamePlayers.stream()
                .flatMap(gp -> gp.getSalvos()
                        .stream())
                .collect(toSet());
        dtoGame.put("salvos", salvos.stream().map(Salvo::getDto));
        dtoGame.put("status", "200");
        return dtoGame;
      }
      return null;
    }
  }

  @RequestMapping(path = "/players", method = RequestMethod.POST)
  public ResponseEntity<Object> register(@RequestParam String email, @RequestParam String password) {

    if (email.isEmpty() || password.isEmpty()) {
      return new ResponseEntity<>("Missing data", HttpStatus.BAD_REQUEST);
    }
    else if (playerRepository.findByUserName(email) !=  null) {
      return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
    }

    Player player = new Player (email, password);
    playerRepository.save(player);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  //Crear nueva partida
  @RequestMapping(path = "/games", method = RequestMethod.POST)
  public ResponseEntity<Object> createGame(Authentication authentication){
    if (Guest(authentication)){
      return new ResponseEntity<>("Error: Not user logged", HttpStatus.FORBIDDEN);
    }else {
      Game game = new Game();
      Player player = playerRepository.findByUserName(authentication.getName());
      gameRepository.save(game);
      GamePlayer gamePlayer = new GamePlayer(game, player);
      gamePlayerRepository.save(gamePlayer);
      Map<String, Object> dto = new LinkedHashMap<>();
      dto.put("gpid", gamePlayer.getId());
      return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
  }

  @RequestMapping(path = "/game/{nn}/players", method = RequestMethod.POST)
  public ResponseEntity<Object> joinGame(Authentication authentication, @PathVariable long nn) {
    Game game = gameRepository.findById(nn).get();
    Player player = playerRepository.findByUserName(authentication.getName());
    if (Guest(authentication)) {
      return new ResponseEntity<>("Error: Not user logged", HttpStatus.FORBIDDEN);
    } else if (true) {
      Map<String, Object> dto = new LinkedHashMap<>();
      dto.put("status", "200");
      return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
    }
    return null;
  }


  @RequestMapping("/leaderboard")
  public List<Map<String, Object>> getPlayers() {
    return playerRepository.findAll()
            .stream()
            .sorted(Comparator.comparing(Player::getTotalScore).reversed())
            .map(Player::getLeaderboardDto)
            .collect(toList());
  }

  private List<Map<String, Object>> getShipList(Set<Ship> ships) {
    return ships
            .stream()
            .map(Ship::getDto)
            .collect(toList());
  }
}
