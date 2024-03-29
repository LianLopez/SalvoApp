package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

  @Autowired
  private ShipRepository shipRepository;

  @Autowired
  private SalvoRepository salvoRepository;

  @RequestMapping("/games")
  public Map<String, Object> getGames(Authentication authentication) {
    Map<String, Object> dto = new LinkedHashMap<>();
    if (isGuest(authentication)){
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

  private boolean isGuest(Authentication authentication) {
    return authentication == null || authentication instanceof AnonymousAuthenticationToken;
  }

  @RequestMapping("/game_view/{id}")
  public ResponseEntity<Map<String, Object>> getGameView(@PathVariable long id, Authentication authentication) {

    Player authenticathedPlayer = getAuthentication(authentication);
    GamePlayer gamePlayer = gamePlayerRepository.findById(id).get();
    if (gamePlayer.getPlayer().getId() != authenticathedPlayer.getId() ) {
      return  new ResponseEntity<>(makeMap("error", "This is not you game"), HttpStatus.FORBIDDEN);
    }

    Map<String, Object> dto = gamePlayer.getGame().getDto();
    dto.put("ships", getShipList(gamePlayer.getShips()));
    Set<GamePlayer> gamePlayers = gamePlayer.getGame().getGamePlayers();
    Set<Salvo> salvos = gamePlayers.stream()
            .flatMap(gp -> gp.getSalvos()
                    .stream())
            .collect(toSet());
    dto.put("salvos", salvos.stream().map(salvo -> salvo.getDto())
    );
    return new ResponseEntity<>(dto,HttpStatus.CREATED) ;
  }

  @RequestMapping(path = "/players", method = RequestMethod.POST)
  public ResponseEntity<Object> register(@RequestParam String username, @RequestParam String password) {

    if (username.isEmpty() || password.isEmpty()) {
      return new ResponseEntity<>("Missing data", HttpStatus.BAD_REQUEST);
    }
    else if (playerRepository.findByUserName(username) !=  null) {
      return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
    }

    Player player = new Player (username, password);
    playerRepository.save(player);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  //Crear nueva partida
  @RequestMapping(path = "/games", method = RequestMethod.POST)
  public ResponseEntity<Object> createGame(Authentication authentication){
    if (isGuest(authentication)){
      return new ResponseEntity<>(makeMap("error","No player logged in"), HttpStatus.FORBIDDEN);
    }else {
      Game game = new Game();
      Player player = playerRepository.findByUserName(authentication.getName());
      gameRepository.save(game);
      GamePlayer gamePlayer = new GamePlayer(game, player);
      gamePlayerRepository.save(gamePlayer);
      Map<String, Object> dto = new LinkedHashMap<>();
      dto.put("gpid", gamePlayer.getId());
      dto.put("status", "200");
      return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
  }

  //Unirse a la partida
  @RequestMapping(path = "/games/{id}/players", method = RequestMethod.POST)
  public ResponseEntity<Map<String, Object>> joinGame (Authentication authentication, @PathVariable long id) {
    Game game = gameRepository.findById(id).orElse(null);
    if (game == null) {
      return new ResponseEntity<>(makeMap("error", "That game doesn't exist"), HttpStatus.FORBIDDEN);
    }

    if (isGuest(authentication)) {
      return new ResponseEntity<>(makeMap("error", "No player logged in"), HttpStatus.FORBIDDEN);
    }

    if  ((long) game.getGamePlayers().size() >1){
      return new ResponseEntity<>(makeMap("error", "Game full"), HttpStatus.FORBIDDEN);
    }

    if (game.getGamePlayers().stream().map(gamePlayer -> gamePlayer.getPlayer().getUserName()).collect(Collectors.toList()).contains(authentication.getName())){
      return new ResponseEntity<>(makeMap("error", "You can't join your game"),HttpStatus.FORBIDDEN);
    }

    Player player = playerRepository.findByUserName(authentication.getName());
    GamePlayer gamePlayer = new GamePlayer(game, player);

    gamePlayerRepository.save(gamePlayer);

    return new ResponseEntity<>(makeMap("gpId", gamePlayer.getId()), HttpStatus.ACCEPTED);
  }


  @RequestMapping("/games/players/{gpId}/ships")
  public ResponseEntity<Map> addShip(@PathVariable long gpId, Authentication authentication, @RequestBody List<Ship> ships) {
    if (isGuest(authentication)) {
      return new ResponseEntity<>(makeMap("Error", "You are a guest"), HttpStatus.UNAUTHORIZED);
    }
    Player player = playerRepository.findByUserName(authentication.getName());
    GamePlayer gamePlayer = gamePlayerRepository.getOne(gpId);

    if (player == null) {
      return new ResponseEntity<>(makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
    }
    if (gamePlayer == null) {
      return new ResponseEntity<>(makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
    }
    if (gamePlayer.getPlayer().getId() != player.getId()) {
      return new ResponseEntity<>(makeMap("error", "Player doesn't exist"), HttpStatus.FORBIDDEN);
    }
    if (!gamePlayer.getShips().isEmpty()) {
      return new ResponseEntity<>(makeMap("error", "Unauthorized, already have ships"), HttpStatus.UNAUTHORIZED);
    }
    ships.forEach(ship -> {
      ship.setGamePlayer(gamePlayer);
      shipRepository.save(ship);
    });
    return new ResponseEntity<>(makeMap("OK", "Ship created"), HttpStatus.CREATED);
  }

  @RequestMapping("/games/players/{id}/salvoes")
  private ResponseEntity<Map<String,Object>> AddSalvoes(@PathVariable long id,
                                                        @RequestBody Salvo salvo,
                                                        Authentication authentication) {

    GamePlayer gamePlayer = gamePlayerRepository.findById(id).orElse(null);
    Player loggedPlayer = getAuthentication(authentication);

    if (loggedPlayer == null)
      return new ResponseEntity<>(makeMap("error", "No player logged in"), HttpStatus.UNAUTHORIZED);
    if (gamePlayer == null)
      return new ResponseEntity<>(makeMap("error", "No such gamePlayer"), HttpStatus.FORBIDDEN);
    if (WrongGamePlayer(gamePlayer, loggedPlayer))
      return new ResponseEntity<>(makeMap("error", "Wrong GamePlayer"), HttpStatus.FORBIDDEN);
    if (salvo.getSalvosLocation().size()> 5){
      return new ResponseEntity<>(makeMap("error", "Wrong GamePlayer"), HttpStatus.FORBIDDEN);
    } else {
      if (!turnHasSalvoes(salvo, gamePlayer.getSalvos())) {
        salvo.setTurn(gamePlayer.getSalvos().size() + 1);
        salvo.setGamePlayers(gamePlayer);
        salvoRepository.save(salvo);
        return new ResponseEntity<>(makeMap("ok", "Salvoes added"), HttpStatus.CREATED);
      } else {
        return new ResponseEntity<>(makeMap("error", "Player has fired salvoes in this turn"), HttpStatus.FORBIDDEN);
      }
    }
  }

  private boolean turnHasSalvoes(Salvo salvo, Set<Salvo> salvos) {
    boolean hasSalvoes = false;
    for (Salvo paraSalvo: salvos) {
      if(paraSalvo.getTurn() == salvo.getTurn()){
        hasSalvoes = true;
      }
    }
    return hasSalvoes;

  }

  private boolean WrongGamePlayer(GamePlayer gamePlayer, Player loggedPlayer) {
    boolean incorrectGP = gamePlayer.getPlayer().getId() != loggedPlayer.getId();
    return incorrectGP;


  }

  @RequestMapping("/leaderboard")
  public List<Map<String, Object>> getPlayers() {
    return playerRepository.findAll()
            .stream()
            .sorted(Comparator.comparing(Player::getTotalScore).reversed())
            .map(Player::getLeaderboardDto)
            .collect(toList());
  }

  private Map<String, Object> makeMap(String key, Object value) {
    Map<String, Object> map = new HashMap<>();
    map.put(key, value);
    return map;
  }
  private List<Map<String, Object>> getShipList(Set<Ship> ships) {
    return ships
            .stream()
            .map(Ship::getDto)
            .collect(toList());
  }
  private Player getAuthentication(Authentication authentication) {
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
    {
      return null;
    }else   {
      return playerRepository.findByUserName((authentication.getName()));
    }

  }

}
