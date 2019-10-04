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
            .map(Game::getDto)
            .collect(toList()));
    return dto;
  }

  private boolean Guest(Authentication authentication) {
    return authentication == null || authentication instanceof AnonymousAuthenticationToken;
  }

  @RequestMapping("/game_view/{id}")
  public Map<String, Object> getGameView(@PathVariable long id) {
    GamePlayer gamePlayer = gamePlayerRepository.findById(id).get();
    Map<String, Object> dto = gamePlayer.getGame().getDto();
    dto.put("ships", getShipList(gamePlayer.getShips()));
    Set<GamePlayer> gamePlayers = gamePlayer.getGame().getGamePlayers();
    Set<Salvo> salvos = gamePlayers.stream()
            .flatMap(gp -> gp.getSalvos()
                    .stream())
            .collect(toSet());
    dto.put("salvos", salvos.stream().map(Salvo::getDto));
    return dto;
  }

  @RequestMapping(path = "/players", method = RequestMethod.POST)
  public ResponseEntity<Object> register(
          @RequestParam String email, @RequestParam String password) {

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
