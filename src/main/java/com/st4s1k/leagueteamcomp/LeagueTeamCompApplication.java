package com.st4s1k.leagueteamcomp;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

@SpringBootApplication(exclude = {JacksonAutoConfiguration.class})
public class LeagueTeamCompApplication {

    public static void main(String[] args) {
        Application.launch(LeagueTeamCompStarter.class, args);
    }
}
