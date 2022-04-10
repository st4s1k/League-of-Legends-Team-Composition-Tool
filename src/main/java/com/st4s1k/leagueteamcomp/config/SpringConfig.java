package com.st4s1k.leagueteamcomp.config;

import com.st4s1k.leagueteamcomp.repository.ChampionRepository;
import com.st4s1k.leagueteamcomp.service.ChampionSuggestionService;
import com.st4s1k.leagueteamcomp.service.LeagueTeamCompService;
import com.st4s1k.leagueteamcomp.service.SummonerRoleListGeneratorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SpringConfig {

    @Bean
    public ChampionRepository getChampionRepository() {
        return new ChampionRepository();
    }

    @Bean
    public LeagueTeamCompService getLeagueTeamCompService(
        ChampionRepository championRepository
    ) {
        return new LeagueTeamCompService(championRepository);
    }

    @Bean
    public ChampionSuggestionService getChampionSuggestionService() {
        return new ChampionSuggestionService();
    }

    @Bean
    public SummonerRoleListGeneratorService getSummonerRoleListGeneratorService() {
        return new SummonerRoleListGeneratorService();
    }
}
