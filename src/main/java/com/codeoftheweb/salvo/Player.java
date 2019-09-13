package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Entity
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  private long id;

  private String userName;

  private String name;

  private String password;

  @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
  Set<GamePlayer> gamePlayers;

  @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
  Set<Score> scores;

  public Player() {
  }

  public Player(String name, String email, String password) {
    this.userName = email;
    this.name = name;
    this.password = password;
  }

  public Long getId() {
    return id;
  }

  public String getUserName() {
    return userName;
  }

  public String getPassword() {
    return password;
  }

  public Set<GamePlayer> getGamePlayers() {
    return gamePlayers;
  }

  public Set<Score> getScores() {
    return scores;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<Score> getWon() {
    return getScores().stream().filter(score -> score.getScore() == 1).collect(toSet());
  }

  public Set<Score> getTied() {
    return getScores().stream().filter(score -> score.getScore() == 0.5).collect(toSet());
  }

  public Set<Score> getLosses() {
    return getScores().stream().filter(score -> score.getScore() == 0).collect(toSet());
  }

  public double getTotalScore() {
    return getWon().size() + getTied().size() * 0.5;
  }

  public Map<String, Object> getPlayerDto() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", getId());
    dto.put("email", getUserName());
    return dto;
  }

  public Map<String, Object> getLeaderboardDto() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", getId());
    dto.put("email", getUserName());
    dto.put("games", getScores().size());
    dto.put("total", getTotalScore());
    dto.put("won", getWon().size());
    dto.put("lost", getLosses().size());
    dto.put("ties", getTied().size());
    return dto;
  }
}