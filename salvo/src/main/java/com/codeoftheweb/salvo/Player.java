package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity // tells Spring to create a player table for this class
public class Player {
    @Id //says that the id instance variable holds the database key for this class.
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String eMail;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<Score> scores;



    public Player(){};

    // ADD ENUM FOR TEAM CHOSEN
    public Player(String first, String last, String userName, String password, String eMail) {
        this.firstName = first;
        this.lastName = last;
        this.userName = userName;
        this.password = password;
        this.eMail = eMail;
    }
    public Player(String userName, String password) {

        this.userName = userName;
        this.password = password;
    }
    //Each player can create multiple GamePlayer instances;
    //code below allows each of these instances to be added to each Player's record.
    public void addGamePlayer(GamePlayer gameplayer) {
        gameplayer.setPlayer(this);
        gamePlayers.add(gameplayer);
    }

    //Method to create a list of a player's games.
    public List<Game> getGames() {
        return gamePlayers.stream().map(gp -> gp.getGame()).collect(toList()); //Fx Lambda (== Reference Method @Game Class)
    }

    public Map<String, Object> playerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("userName", this.userName);
        return dto;
    }

    public Map<String, Object> authenticatedDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("userName", this.userName);
        return dto;

    }



    public Score getPoints(Game game){
       return scores.stream().filter(score -> score.getGame().getId() == game.getId()).findFirst().orElse(null);
    }


    // Getters & Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName (String firstName) {
        this.firstName = firstName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName (String lastName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public String toString() {
        return firstName + " " + userName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }
}
