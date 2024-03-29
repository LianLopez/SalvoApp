package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class SalvoApplication {

  public static void main(String[] args) {
    SpringApplication.run(SalvoApplication.class, args);
  }

  @Bean
  public CommandLineRunner initData(PlayerRepository playerRepository,
                                    GameRepository gameRepository,
                                    GamePlayerRepository gamePlayerRepository,
                                    ShipRepository shipRepository,
                                    SalvoRepository salvoRepository,
                                    ScoreRepository scoreRepository) {
    return (args) -> {
      Player player1 = new Player( "j.bauer@ctu.gov", "24");
      Player player2 = new Player("c.obrian@ctu.gov", "42");
      Player player3 = new Player("kim_bauer@gmail.com", "kb");
      Player player4 = new Player(" t.almeida@ctu.gov", "mole");

      playerRepository.saveAll(Arrays.asList(player1, player2, player3, player4));

      Game game1 = new Game();
      Game game2 = new Game(3600);
      Game game3 = new Game(3600 * 2);
      Game game4 = new Game(3600 * 3);


      gameRepository.saveAll(Arrays.asList(game1, game2, game3, game4));

      GamePlayer gamePlayer1 = new GamePlayer(game1, player1);
      GamePlayer gamePlayer2 = new GamePlayer(game1, player2);
      GamePlayer gamePlayer3 = new GamePlayer(game2, player3);
      GamePlayer gamePlayer4 = new GamePlayer(game2, player4);
      GamePlayer gamePlayer5 = new GamePlayer(game3, player1);
      GamePlayer gamePlayer6 = new GamePlayer(game3, player3);
      GamePlayer gamePlayer7 = new GamePlayer(game4, player4);
      GamePlayer gamePlayer8 = new GamePlayer(game4, player2);

      gamePlayerRepository.saveAll(Arrays.asList(gamePlayer1, gamePlayer2, gamePlayer3, gamePlayer4, gamePlayer5, gamePlayer6, gamePlayer7, gamePlayer8));

      Set<String> shipL1 = new HashSet<>(Arrays.asList("H2", "H3", "H4"));
      Set<String> shipL2 = new HashSet<>(Arrays.asList("E1", "F1", "G1"));
      Set<String> shipL3 = new HashSet<>(Arrays.asList("B4", "B5"));
      Set<String> shipL4 = new HashSet<>(Arrays.asList("B5", "C5", "D5"));
      Set<String> shipL5 = new HashSet<>(Arrays.asList("F1", "F2"));

      Ship ship1 = new Ship("Destroyer", shipL1, gamePlayer1);
      Ship ship2 = new Ship("Submarine", shipL2, gamePlayer1);
      Ship ship3 = new Ship("Patrol Boat", shipL3, gamePlayer1);
      Ship ship4 = new Ship("Destroyer", shipL4, gamePlayer2);
      Ship ship5 = new Ship("Destroyer", shipL5, gamePlayer2);

      shipRepository.saveAll(Arrays.asList(ship1, ship2, ship3, ship4, ship5));

      Set<String> salvoL1 = new HashSet<>(Arrays.asList("H1", "H2", "H3"));
      Set<String> salvoL2 = new HashSet<>(Arrays.asList("A3", "A4"));
      Set<String> salvoL3 = new HashSet<>(Arrays.asList("F5", "F6"));
      Set<String> salvoL4 = new HashSet<>(Arrays.asList("B3", "H4", "B5"));

      Salvo salvo1 = new Salvo(gamePlayer1, 1, salvoL1);
      Salvo salvo2 = new Salvo(gamePlayer2, 1, salvoL2);
      Salvo salvo3 = new Salvo(gamePlayer1, 2, salvoL3);
      Salvo salvo4 = new Salvo(gamePlayer2, 2, salvoL4);

      salvoRepository.saveAll(Arrays.asList(salvo1, salvo2, salvo3, salvo4));

      Score score1 = new Score(1, game1, player1);
      Score score2 = new Score(0, game1, player2);
      Score score3 = new Score(0.5, game2, player3);
      Score score4 = new Score(0.5, game2, player4);
      Score score5 = new Score(1, game3, player1);
      Score score6 = new Score(0, game3, player3);
      Score score7 = new Score(0.5, game4, player4);
      Score score8 = new Score(0.5, game4, player2);

      scoreRepository.saveAll(Arrays.asList(score1, score2, score3, score4, score5, score6, score7, score8));
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

  @Autowired
  PlayerRepository playerRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Override
  public void init(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(inputName -> {
      Player player = playerRepository.findByUserName(inputName);
      if (player != null) {
        return new User(player.getUserName(), passwordEncoder.encode(player.getPassword()),
                AuthorityUtils.createAuthorityList("USER"));
      } else {
        throw new UsernameNotFoundException("Unknown user: " + inputName);
      }
    });
  }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests()
            .antMatchers("/web/games.html/**").permitAll()
            .antMatchers("/web/css/**").permitAll()
            .antMatchers("/web/js/**").permitAll()
            .antMatchers("/web/assets/**").permitAll()
            .antMatchers("/api/leaderboard").permitAll()
            .antMatchers("/api/games", "/api/login/").permitAll()
            .antMatchers("/api/players").permitAll()
            .antMatchers("/api/games/players/*/ships").hasAuthority("USER")
            .antMatchers("/api/games/players/*/salvoes").hasAuthority("USER")
            .antMatchers("/api/games/*/players").hasAuthority("USER")
            .antMatchers("/web/grid.html*").hasAuthority("USER")
            .antMatchers("/api/game_view/**", "/web/game.html*", "/api/games/*", "/api/logout/" ).hasAuthority("USER")
            .antMatchers("/rest/**").hasAuthority("ADMIN")
            .anyRequest().denyAll();

    http.formLogin()
            .usernameParameter("username")
            .passwordParameter("password")
            .loginPage("/api/login");

    http.logout().logoutUrl("/api/logout");

    // turn off checking for CSRF tokens
    http.csrf().disable();

    // if user is not authenticated, just send an authentication failure response
    http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

    // if login is successful, just clear the flags asking for authentication
    http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

    // if login fails, just send an authentication failure response
    http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

    // if logout is successful, just send a success response
    http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());



  }
  private void clearAuthenticationAttributes(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
  }
}

