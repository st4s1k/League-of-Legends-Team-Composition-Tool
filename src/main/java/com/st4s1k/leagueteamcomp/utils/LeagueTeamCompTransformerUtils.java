package com.st4s1k.leagueteamcomp.utils;

import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.st4s1k.leagueteamcomp.model.SummonerData;

public class LeagueTeamCompTransformerUtils {

    private LeagueTeamCompTransformerUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static SummonerData convertToSummonerData(Summoner summoner) {
        SummonerData summonerData = new SummonerData();
        summonerData.setSummonerName(summoner.getName());
        return summonerData;
    }
}
