package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Integer turn;

    @ElementCollection
    @Column(name="location")
    private List<String>  location = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Salvo() { }

    public Salvo(Integer turn, List<String> location){
        this.turn = turn;
        this.location = location;
    }

    public Map<String, Object> salvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", this.getTurn());
        dto.put("player", this.gamePlayer.getPlayer().getId());
        dto.put("location", this.getLocation());
        dto.put("Hits",this.determineHit());
        dto.put("Sunk", this.determineSink());
        return dto;
    }

    public List<String> determineHit(){
        Optional <GamePlayer> opponent = gamePlayer.getGame().getGamePlayers().stream().filter(gamePlayer ->(gamePlayer.getId()!=this.gamePlayer.getId())).findFirst();

        List <String> salvoLocs = new ArrayList<>();
        return this.getLocation().stream().filter(shot -> opponent.get().getShips().stream().anyMatch(ship -> ship.getLocation().contains(shot))).collect(toList());

    }

    public List<Map<String, Object>> determineSink(){
        //Set opponent to get ship locations to compare with all salvoes
        Optional <GamePlayer> opponent = gamePlayer.getGame().getGamePlayers().stream()
                .filter(gamePlayer ->(gamePlayer.getId()!=this.gamePlayer.getId())).findFirst();

        //Create list with all salvoes to compare with ship locations
        List <String> allSalvoShots = new ArrayList<>();
        this.gamePlayer.getSalvos().stream().forEach(salvo -> allSalvoShots.addAll(salvo.getLocation()));

        //Create object to be displayed in DTO
        List<Map<String, Object>> sunkenShips = new ArrayList<>();
        sunkenShips = opponent.get().getShips().stream().filter
                (ship -> allSalvoShots.containsAll(ship.getLocation()))//if all ship loc appear in allSalvoShots
                .map(Ship::shipDTO).collect(toList()); //bring me that Ship's DTO (type and loc)

    return sunkenShips;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
}
