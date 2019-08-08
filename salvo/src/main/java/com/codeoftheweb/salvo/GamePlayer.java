package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class GamePlayer {

    // Attributes START

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Salvo> salvos = new HashSet<>();


    private LocalDateTime joinDate;

    // Attributes END

    public GamePlayer() { }     // Empty Constructor for DB

    //Constructor START

    public GamePlayer (Game game, Player player, LocalDateTime joinDate, Set<Ship> ships, Set<Salvo> salvos) {
        this.game = game;
        this.player = player;
        this.joinDate = joinDate;
        this.setShips(ships);
        this.setSalvos(salvos);
    }
    public GamePlayer (Game game, Player player, LocalDateTime joinDate) {
        this.game = game;
        this.player = player;
        this.joinDate = joinDate;
    }
    //Constructor END

    //Methods

    //Method to access Player Data Transfer Object for /api/games/
    public Map<String, Object> gamePlayerDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().playerDTO());
        if(this.getScore() == null)
            dto.put("score", null);
        else
            dto.put("score", this.getScore().getPoints());
        return dto;
    }

    //Method to access Player Data Transfer Object for /api/game_view/{player_id}
    public Map<String, Object> gameViewDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("joinDate", this.getJoinDate());
        dto.put("state", this.determineGameState());
        dto.put("gamePlayers", this.game.gamePlayers.stream().map(GamePlayer::gamePlayerDTO) );
        dto.put("ships", this.getShips().stream().map(Ship::shipDTO));
        dto.put("salvos", this.game.gamePlayers.stream().flatMap(gp -> gp.salvos.stream().map(Salvo::salvoDTO)));
        return dto;
    }


    //Method to assign ships in the set to the current gamePlayer
    public void addShip(Ship ship){
        ship.setGamePlayer(this);
        this.ships.add(ship);
    }

    //Method to assign salvos in the set to the current gamePlayer
    public void addSalvo(Salvo salvo){
        salvo.setGamePlayer(this);
        this.salvos.add(salvo);
    }

    public Score getScore(){
      return  this.player.getPoints(this.game);
    }

    public Integer determineLastTurn(){
        Integer lastTurn = this.getSalvos().size() + 1;
        return lastTurn;
    }

    public Optional<GamePlayer> getOpponent(){
       Optional <GamePlayer> opponent = this.game.gamePlayers.stream().filter(gamePlayer -> gamePlayer.getId()!= this.getId()).findFirst();

       return opponent;
    }

    public int getSunkenShips(){

        Optional<Salvo> mySalvo = this.getSalvos().stream().filter(salvo -> salvo.getTurn() == determineLastTurn()-1).findFirst();

        if(!mySalvo.isPresent()){
            return 0;
        } else {
            return  mySalvo.get().determineSink().size();
        }

    }

    //Method to determine Game State
    public Enum<GameState> determineGameState() {
        Enum<GameState> gameStateEnum = GameState.UNDEFINED;
        //1 WAIT_FOR_OPPONENT
        if (!this.getOpponent().isPresent()) {
            gameStateEnum = GameState.WAIT_FOR_OPPONENT;
        }
        else {
            //2 SHIPS
            if (this.getShips().size() == 0) {
                gameStateEnum = GameState.PLACE_YOUR_SHIPS;
            }
            //3 OPPONENT_SHIPS
            else if (this.getOpponent().get().getShips().size() == 0) {
                gameStateEnum = GameState.WAIT_FOR_OPPONENT_SHIPS;
            }
            else {
                //4 Player SALVOES
                if (this.getId() < this.getOpponent().get().getId() && this.determineLastTurn() == getOpponent().get().determineLastTurn()) {

                    gameStateEnum = GameState.SHOOT_SALVOES;
                }else if (this.getId() < this.getOpponent().get().getId() && this.determineLastTurn() != getOpponent().get().determineLastTurn()) {

                    gameStateEnum = GameState.WAIT_FOR_OPPONENT_SALVOES;
                }else if (this.getId() > this.getOpponent().get().getId() && this.determineLastTurn() == getOpponent().get().determineLastTurn() - 1) {

                    gameStateEnum = GameState.SHOOT_SALVOES;
                }else if (this.getId() > this.getOpponent().get().getId() && this.determineLastTurn() != getOpponent().get().determineLastTurn() - 1) {

                    gameStateEnum = GameState.WAIT_FOR_OPPONENT_SALVOES;
                }
                if (getSunkenShips() > 0) {

                    //5 WON
                    if (this.getSunkenShips() == 5
                            && this.determineLastTurn() == this.getOpponent().get().determineLastTurn()
                            && this.getOpponent().get().getSunkenShips() != 5) {
                        gameStateEnum = GameState.WON;
                    }

                    //6 LOST
                    else if (this.getSunkenShips() != 5
                            && this.determineLastTurn() == this.getOpponent().get().determineLastTurn()
                            && this.getOpponent().get().getSunkenShips() == 5) {
                        gameStateEnum = GameState.LOST;

                    }
                    //7 TIED
                    else if (this.getSunkenShips() == 5
                            && this.determineLastTurn() == this.getOpponent().get().determineLastTurn()
                            && this.getOpponent().get().getSunkenShips() == 5) {
                        gameStateEnum = GameState.TIED;
                    }
                }
            }
        }
        return gameStateEnum;
    }

    //Getters y Setters
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        ships.forEach(this::addShip);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public void setSalvos(Set<Salvo> salvos) {
        salvos.forEach(this::addSalvo);;
    }
}



