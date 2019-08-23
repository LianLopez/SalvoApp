package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date creationDate= new Date();

    @OneToMany(mappedBy="game", fetch= FetchType.EAGER)
    Set<GamePlayer> games;

    public Game() {}

    public Game(int time) {
        Date newDate = new Date();
        this.creationDate = Date.from(newDate.toInstant().plusSeconds(time));
    }

    public long getId() {
        return id;
    }

    public Date getDate() {
        return creationDate;
    }

    public Set getGamePlayers(){return games;}

    public void addGames(GamePlayer games) {
        games.setGame(this);
        games.add(games);
    }

}
