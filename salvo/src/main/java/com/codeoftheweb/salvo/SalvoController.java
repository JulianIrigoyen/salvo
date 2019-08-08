package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;
    //For every @RequestMapping, Spring creates a SERVLET
    @GetMapping ("/games")
    public Map<String, Object> getGames(Authentication authentication, GamePlayer gamePlayer) {

        //Determine if user is logged and updates API accordingly
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if (isGuest(authentication)) {
            dto.put("player", "guest");
        }
        else {
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", player.authenticatedDTO());
        }

        dto.put("games", gameRepository.findAll().stream().map(Game::gameDTO).collect(toList()));

        return dto;

    }

    //Handles new game creation when New Game button is clicked
    @PostMapping (path = "/games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication){
        if(isGuest(authentication)){
            return new ResponseEntity<>((makeMap("Message", "Log in first")), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(authentication.getName());
        Game game = gameRepository.save(new Game(LocalDateTime.now()));
        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game, player, LocalDateTime.now()));

        return new ResponseEntity<>(makeMap("gpId", gamePlayer.getId()), HttpStatus.CREATED);
    }

    //Determines if X player can access X game
    @GetMapping("/game_view/{gamePlayerId}")
    public ResponseEntity <Map<String, Object>> getGameView (@PathVariable Long gamePlayerId, Authentication authentication, Long gameId){
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);

        //gets current user;
        if(isGuest(authentication)){
            return new ResponseEntity<>((makeMap("Message", "Log in first.")), HttpStatus.FORBIDDEN);
        }
        //Check if gamePlayer ID is ok
        if(!gamePlayer.isPresent()){ //Checks whether the id is in GamePlayer repository
            return new ResponseEntity<>((makeMap("Message", "No such game.")), HttpStatus.FORBIDDEN);
        }

        //Determines if THIS player can access THIS game
        if(!gamePlayer.get().getPlayer().getUserName().equals(authentication.getName())){
            return new ResponseEntity<>((makeMap("Message", "This is not your game")), HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(gamePlayer.get().gameViewDTO(), HttpStatus.OK);
    }

    //Allows player to join a game
    @PostMapping (path = "/games/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joinGame (@PathVariable Long gameId, Authentication authentication) {

        //gets current user; if there is none, say unauthorized
        if(isGuest(authentication)){
            return new ResponseEntity<>((makeMap("Message", "Log in first.")), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByUserName(authentication.getName());

        //Get game's to join Id.
        Optional<Game> game = gameRepository.findById(gameId);
        if(!game.isPresent()){ //Checks whether the id is in Game repository
            return new ResponseEntity<>((makeMap("Message", "No such game.")), HttpStatus.FORBIDDEN);
        }

        //Checks No of players in game
        if(game.get().gamePlayers.size()>1){
            return new ResponseEntity<>((makeMap("Message", "Game full.")), HttpStatus.FORBIDDEN);
        }

        //Creates and saves new GamePlayer for this game and current user.
        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game.get(), player, LocalDateTime.now()));

        return new ResponseEntity<>(makeMap("gpId", gamePlayer.getId()), HttpStatus.CREATED);
    }

    //Handles new Player registration when Sign Up button is clicked
    @PostMapping(path ="/players")
    public ResponseEntity<Map<String,Object>> register(@RequestParam String password, @RequestParam String userName){

        if (password.isEmpty() || userName.isEmpty()) {
            return new ResponseEntity<>((makeMap("error", "Username field empty")), HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(userName) !=  null) {
            return new ResponseEntity<>(makeMap("error", "player already exists"), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.save(new Player( userName, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap("Registered", player.getUserName()), HttpStatus.CREATED);
    }


    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


    // Receives a list of ship objects --> saves to ship repository
    @PostMapping (path ="/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> placeShips(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody Set<Ship> ships){

        //gets current user; if there is none, say unauthorized
        if(isGuest(authentication)){
            return new ResponseEntity<>((makeMap("Message", "Log in first.")), HttpStatus.FORBIDDEN);
        }

        //Checks if GamePlayer is not empty
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if(!gamePlayer.isPresent()){
            return new ResponseEntity<>(makeMap("Message", "This gamePlayer is empty"), HttpStatus.FORBIDDEN);
        }

        //Determines if THIS player can place ships in THIS game
        if(!gamePlayer.get().getPlayer().getUserName().equals(authentication.getName())){
            return new ResponseEntity<>((makeMap("Message", "This is not your game")), HttpStatus.FORBIDDEN);
        }


        //Determines if ships have already been placed
        if(gamePlayer.get().getShips().size() > 0){ // Checks gamePlayer class for existence of a List of <Ships>
            return new ResponseEntity<>((makeMap("Message", "Your Ships are already in place")), HttpStatus.FORBIDDEN);
        }

        //When all conditions pass -->
        gamePlayer.get().setShips(ships);
        gamePlayerRepository.save(gamePlayer.get());

        return new ResponseEntity<>((makeMap("Created", "Your Ships have been placed")), HttpStatus.CREATED);
    }

    // Receives a list of Salvo objects --> saves to Salvo repository
    @PostMapping (path ="/games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String, Object>> placeSalvos(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody List<String> location){

        //gets current user; if there is none, say unauthorized
        if(isGuest(authentication)){
            return new ResponseEntity<>((makeMap("Message", "Log in first.")), HttpStatus.FORBIDDEN);
        }

        //Checks if GamePlayer is not empty
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if(!gamePlayer.isPresent()){
            return new ResponseEntity<>(makeMap("Message", "This gamePlayer is empty"), HttpStatus.FORBIDDEN);
        }
        //Make sure there is someone to fire salvos at
        Optional<GamePlayer> opponent = gamePlayer.get().getGame().gamePlayers.stream().filter(gp -> gp.getId() != gamePlayer.get().getId()).findFirst();
        if(!opponent.isPresent()){
            return new ResponseEntity<>(makeMap("Message", "You need an opponent"), HttpStatus.FORBIDDEN);
        }

        //Determines if THIS player can shoot salvos in THIS game
        if(!gamePlayer.get().getPlayer().getUserName().equals(authentication.getName())){
            return new ResponseEntity<>((makeMap("Message", "This is not your game")), HttpStatus.FORBIDDEN);
        }

        //Determines if salvos have already been placed for x turn
        int turn = gamePlayer.get().getSalvos().size() + 1;
        int opponentTurn = opponent.get().getSalvos().size() + 1;
        if (gamePlayer.get().getId() < opponent.get().getId()) {
            if (turn != opponentTurn) {
                return new ResponseEntity<>((makeMap("Message", "Wait for your turn")), HttpStatus.FORBIDDEN);
            }
        }
        else {
            if (opponentTurn - 1 != turn){
                return new ResponseEntity<>((makeMap("Message", "Wait for your turn")), HttpStatus.FORBIDDEN);
            }
        }

        //When all conditions pass -->
        Salvo salvo = new Salvo(turn, location);
        gamePlayer.get().addSalvo(salvo);
        gamePlayerRepository.save(gamePlayer.get());

        return new ResponseEntity<>((makeMap("Created", "Your Salvos have been shot")), HttpStatus.CREATED);
    }

}






