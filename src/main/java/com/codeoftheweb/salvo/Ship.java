package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Set;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;

    @ElementCollection
    @Column(name = "locations")
    private Set<String> shipLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Ship() {

    }

    public Ship(String type, Set<String> shipLocation, GamePlayer gamePlayer) {
        this.type = type;
        this.shipLocation = shipLocation;
        this.gamePlayer = gamePlayer;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Set<String> getShipLocation() {
        return shipLocation;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }
}
