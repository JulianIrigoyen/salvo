package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) { SpringApplication.run(SalvoApplication.class, args);
	}
	//This class relies on
	@Autowired
	PasswordEncoder passwordEncoder;

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
			GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, ScoreRepository scoreRepository){
		return (args) -> {

			//Sample Player data
				Player player1 = new Player ("Jack", "Bauer", "jBauer", passwordEncoder.encode("24"), "j.bauer@ctu.gov");
				playerRepository.save(player1);

				Player player2 = new Player("Chloe", "O'Brian", "cObrian", passwordEncoder.encode("42"), "c.obrian@ctu.gov");
				playerRepository.save(player2);

				Player player3 = new Player("Kim", "Baur", "kBauer", passwordEncoder.encode("kb"), "kim_bauer@gmai.com");
				playerRepository.save(player3);

				Player player4 = new Player("Tony", "Almeida", "tAlmeida", passwordEncoder.encode("mole"),	 "t.almeida@mail.com");
				playerRepository.save(player4);


			//Sample Game data
				Game game1 = new Game(LocalDateTime.now());
				gameRepository.save(game1);

				Game game2 = new Game(LocalDateTime.now().plusHours(1));
				gameRepository.save(game2);

				Game game3 = new Game(LocalDateTime.now().plusHours(2));
				gameRepository.save(game3);

				Game game4 = new Game(LocalDateTime.now().plusHours(3));
				gameRepository.save(game4);



			//Sample Ship Data - Letters Y-AXIS, Numbers X-AXIS
				// --- Ships for Game 1 ---
				Ship ship1 = new Ship("threeA", new ArrayList<>(Arrays.asList("H2","H3","H4")));
				Ship ship2 = new Ship("threeB", new ArrayList<>(Arrays.asList("E1","F1","G1")));
				Ship ship3 = new Ship("two", new ArrayList<>(Arrays.asList("B4","B5")));
				Ship ship4 = new Ship("threeA", new ArrayList<>(Arrays.asList("B5","C5","D5")));
				Ship ship5 = new Ship("two", new ArrayList<>(Arrays.asList("F1","F2")));

				Set<Ship> jShipsInGame1 = new HashSet<>();
				jShipsInGame1.add(ship1);
				jShipsInGame1.add(ship2);
				jShipsInGame1.add(ship3);

				Set<Ship> cShipsInGame1 = new HashSet<>();
				cShipsInGame1.add(ship4);
				cShipsInGame1.add(ship5);


				//Shorter method to create sample ship data
				// --- Ships for Game 2 ---
				Set<Ship> jShipsInGame2 = new HashSet<>();
				jShipsInGame2.add (new Ship ("threeA", new ArrayList<>(Arrays.asList("B5, C5","D5"))));
				jShipsInGame2.add (new Ship ("two", new ArrayList<>(Arrays.asList("C6", "C7"))));

				Set<Ship> cShipsInGame2 = new HashSet<>();
				cShipsInGame2.add (new Ship ("threeB", new ArrayList<>(Arrays.asList("A2","A3", "A4"))));
				cShipsInGame2.add (new Ship ("two", new ArrayList<>(Arrays.asList("G6","H6"))));

				// --- Ships for Game 3 ---
				Set<Ship> cShipsInGame3 = new HashSet<>();
				cShipsInGame3.add (new Ship ("threeA", new ArrayList<>(Arrays.asList("B5","C5", "D5"))));
				cShipsInGame3.add (new Ship ("two", new ArrayList<>(Arrays.asList("C6","C7"))));

				Set<Ship> tShipsInGame3 = new HashSet<>();
				tShipsInGame3.add (new Ship ("threeB", new ArrayList<>(Arrays.asList("A2","A3", "A4"))));
				tShipsInGame3.add (new Ship ("two", new ArrayList<>(Arrays.asList("G6","H6"))));

				// --- Ships for Game 4 ---
				Set<Ship> cShipsInGame4 = new HashSet<>();
				cShipsInGame4.add (new Ship ("threeA", new ArrayList<>(Arrays.asList("B5","C5", "D5"))));
				cShipsInGame4.add (new Ship ("two", new ArrayList<>(Arrays.asList("C6","C7"))));

				Set<Ship> jShipsInGame4 = new HashSet<>();
				jShipsInGame4.add (new Ship ("threeA", new ArrayList<>(Arrays.asList("A2","A3", "A4"))));
				jShipsInGame4.add (new Ship ("two", new ArrayList<>(Arrays.asList("G6","H6"))));

				// --- Ships for Game 5 ---
				Set<Ship> tShipsInGame5 = new HashSet<>();
				tShipsInGame5.add (new Ship ("threeA", new ArrayList<>(Arrays.asList("B5","C5", "D5"))));
				tShipsInGame5.add (new Ship ("two", new ArrayList<>(Arrays.asList("C6","C7"))));

				Set<Ship> jShipsInGame5 = new HashSet<>();
				jShipsInGame5.add (new Ship ("threeB", new ArrayList<>(Arrays.asList("A2","A3", "A4"))));
				jShipsInGame5.add (new Ship ("two", new ArrayList<>(Arrays.asList("G6","H6"))));

				// --- Ships for Game 6 ---
				Set<Ship> kShipsInGame6 = new HashSet<>();
				kShipsInGame6.add (new Ship ("Destroyer", new ArrayList<>(Arrays.asList("B5","C5", "D5"))));
				kShipsInGame6.add (new Ship ("Patrol", new ArrayList<>(Arrays.asList("C6","C7"))));

				// --- Ships for Game 8 ---
				Set<Ship> kShipsInGame8 = new HashSet<>();
				kShipsInGame8.add (new Ship ("threeA", new ArrayList<>(Arrays.asList("B5","C5", "D5"))));
				kShipsInGame8.add (new Ship ("two", new ArrayList<>(Arrays.asList("C6","C7"))));

				Set<Ship> tShipsInGame8 = new HashSet<>();
				tShipsInGame8.add (new Ship ("threeA", new ArrayList<>(Arrays.asList("A2","A3", "A4"))));
				tShipsInGame8.add (new Ship ("two", new ArrayList<>(Arrays.asList("G6","H6"))));


			//Sample Salvo data
				// --- Salvoes for Game 1 ---
				Set<Salvo> jSalvoesInGame1 = new HashSet<>();
				jSalvoesInGame1.add(new Salvo(1, new ArrayList<>(Arrays.asList("B5", "C5", "F1"))));
				jSalvoesInGame1.add(new Salvo(2, new ArrayList<>(Arrays.asList("F2", "D5"))));

				Set<Salvo> cSalvoesInGame1 = new HashSet<>();
				cSalvoesInGame1.add(new Salvo(1, new ArrayList<>(Arrays.asList("B4", "B5", "B6"))));
				cSalvoesInGame1.add(new Salvo(2, new ArrayList<>(Arrays.asList("E1", "H3", "A2"))));

				// --- Salvoes for Game 2 ---
				Set<Salvo> jSalvoesInGame2 = new HashSet<>();
				jSalvoesInGame2.add(new Salvo(1, new ArrayList<>(Arrays.asList("A2", "A4", "G6"))));
				jSalvoesInGame2.add(new Salvo(2, new ArrayList<>(Arrays.asList("A3", "H6"))));

				Set<Salvo> cSalvoesInGame2 = new HashSet<>();
				cSalvoesInGame2.add(new Salvo(1, new ArrayList<>(Arrays.asList("B5", "D5", "C7"))));
				cSalvoesInGame2.add(new Salvo(2, new ArrayList<>(Arrays.asList("C5", "C6"))));

				// --- Salvoes for Game 3 ---
				Set<Salvo> cSalvoesInGame3 = new HashSet<>();
				cSalvoesInGame3.add(new Salvo(1, new ArrayList<>(Arrays.asList("G6", "H6", "A4"))));
				cSalvoesInGame3.add(new Salvo(2, new ArrayList<>(Arrays.asList("A2", "A3", "D8"))));

				Set<Salvo> tSalvoesInGame3 = new HashSet<>();
				tSalvoesInGame3.add(new Salvo(1, new ArrayList<>(Arrays.asList("H1", "H2", "H3"))));
				tSalvoesInGame3.add(new Salvo(2, new ArrayList<>(Arrays.asList("E1", "F2", "G3"))));

				// --- Salvoes for Game 4 ---
				Set<Salvo> cSalvoesInGame4 = new HashSet<>();
				cSalvoesInGame4.add(new Salvo(1, new ArrayList<>(Arrays.asList("A3", "A4", "F7"))));
				cSalvoesInGame4.add(new Salvo(2, new ArrayList<>(Arrays.asList("A2", "G6", "H6"))));

				Set<Salvo> jSalvoesInGame4 = new HashSet<>();
				jSalvoesInGame4.add(new Salvo(1, new ArrayList<>(Arrays.asList("B5", "C6", "H1"))));
				jSalvoesInGame4.add(new Salvo(2, new ArrayList<>(Arrays.asList("C5", "C7", "D5"))));

				// --- Salvoes for Game 5 ---
				Set<Salvo> tSalvoesInGame5 = new HashSet<>();
				tSalvoesInGame5.add(new Salvo(1, new ArrayList<>(Arrays.asList("A1", "A2", "A3"))));
				tSalvoesInGame5.add(new Salvo(2, new ArrayList<>(Arrays.asList("G6", "G7", "G8"))));

				Set<Salvo> jSalvoesInGame5 = new HashSet<>();
				jSalvoesInGame5.add(new Salvo(1, new ArrayList<>(Arrays.asList("B5", "B6", "B7"))));
				jSalvoesInGame5.add(new Salvo(2, new ArrayList<>(Arrays.asList("C6", "D6", "E6"))));
				jSalvoesInGame5.add(new Salvo(3, new ArrayList<>(Arrays.asList("H1", "H8"))));



			//Sample Score Data
				// --- Game 1 ---
				scoreRepository.save(new Score(game1, player1, 1.0F, LocalDateTime.now().plusHours(1)));
				scoreRepository.save(new Score(game1, player2, 0.0F, LocalDateTime.now().plusHours(1)));

				// --- Game 2 ---
				scoreRepository.save(new Score(game2, player1, 0.5F, LocalDateTime.now().plusHours(1)));
				scoreRepository.save(new Score(game2, player2, 0.5F, LocalDateTime.now().plusHours(1)));

				// --- Game 3 ---
				scoreRepository.save(new Score(game3, player2, 1.0F, LocalDateTime.now().plusHours(1)));
				scoreRepository.save(new Score(game3, player4, 0.0F, LocalDateTime.now().plusHours(1)));

				// --- Game 4 ---
				scoreRepository.save(new Score(game4, player2, 0.5F, LocalDateTime.now().plusHours(1)));
				scoreRepository.save(new Score(game4, player1, 0.5F, LocalDateTime.now().plusHours(1)));



			//Sample GamePlayer data
				// --- Game 1 ---
				gamePlayerRepository.save(new GamePlayer(game1, player1, LocalDateTime.now(), jShipsInGame1, jSalvoesInGame1));
				gamePlayerRepository.save(new GamePlayer(game1,player2,LocalDateTime.now(),cShipsInGame1, cSalvoesInGame1));


				// --- Game 2 ---
				gamePlayerRepository.save(new GamePlayer(game2, player1, LocalDateTime.now(), jShipsInGame2, jSalvoesInGame2));
				gamePlayerRepository.save(new GamePlayer(game2,player2,LocalDateTime.now(),cShipsInGame2, cSalvoesInGame2));

				// --- Game 3 ---
				gamePlayerRepository.save(new GamePlayer(game3, player2, LocalDateTime.now(), cShipsInGame3, cSalvoesInGame3));
				gamePlayerRepository.save(new GamePlayer(game3,player4,LocalDateTime.now(),tShipsInGame3, tSalvoesInGame3));


				// --- Game 4 ---
				gamePlayerRepository.save(new GamePlayer(game4, player2, LocalDateTime.now(), cShipsInGame4, cSalvoesInGame4));
				gamePlayerRepository.save(new GamePlayer(game4,player1,LocalDateTime.now(),jShipsInGame4, jSalvoesInGame4));




		};
	}

}





