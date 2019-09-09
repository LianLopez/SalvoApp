package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date creationDate = new Date();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> games;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score>  scores;

    public Game() {
    }

    public Game(int seconds) {
        if (seconds >= 0) {
            Date newDate = new Date();
            this.creationDate = Date.from(newDate.toInstant().plusSeconds(seconds));
        } else {
            this.creationDate = new Date();
        }
    }

    public long getId() {
        return id;
    }

    public Date getDate() {
        return creationDate;
    }

    public Set<GamePlayer> getGamePlayers() {
        return games;
    }

    public Map<String, Object> getDto() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("created", this.getDate().getTime());
        dto.put("gamePlayers", getGamePlayers().stream().map(gamePlayer -> gamePlayer.getDto()));
        return dto;
    }
}
