package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    public Player() { }

    public Player(String email) {
        this.userName = email;
    }
    public Long getId(){return id;}
    public String getUserName() {
        return userName;
    }

    public void setUserName(String email) {
        this.userName = email;
    }
    public void addPlayer(GamePlayer gamePlayers) {
        gamePlayers.setPlayer(this);
        gamePlayers.add(gamePlayers);
    }

}