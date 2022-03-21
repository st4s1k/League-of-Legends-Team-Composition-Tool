package com.st4s1k.leagueteamcomp.service;

import com.st4s1k.leagueteamcomp.model.SummonerRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.swap;

public class SummonerRoleListGeneratorService {

    public static List<List<Entry<String, SummonerRole>>> getCombinations(Map<String, List<SummonerRole>> playerToRoles) {
        if (playerToRoles.isEmpty() || playerToRoles.size() < 5) {
            return emptyList();
        }
        List<List<String>> allPlayerPermutations = new ArrayList<>();
        List<String> players = new ArrayList<>(playerToRoles.keySet());
        generateAllPlayerPermutations(playerToRoles.size(), players, allPlayerPermutations);
        return allPlayerPermutations.stream().map(playersInOrder -> List.of(
                Map.entry(playersInOrder.get(0), SummonerRole.TOP),
                Map.entry(playersInOrder.get(1), SummonerRole.MID),
                Map.entry(playersInOrder.get(2), SummonerRole.ADC),
                Map.entry(playersInOrder.get(3), SummonerRole.SUP),
                Map.entry(playersInOrder.get(4), SummonerRole.JGL)
            )).filter(newPlayerToRoleList -> arePlayersOnCorrectRoles(playerToRoles, newPlayerToRoleList))
            .collect(Collectors.toList());
    }

    private static boolean arePlayersOnCorrectRoles(
        Map<String, List<SummonerRole>> playerToRoles,
        List<Entry<String, SummonerRole>> newPlayerToRoleList
    ) {
        return newPlayerToRoleList.stream().allMatch(newPlayerToRole -> isPlayerOnCorrectRole(playerToRoles, newPlayerToRole));
    }

    private static boolean isPlayerOnCorrectRole(
        Map<String, List<SummonerRole>> playerToRoles,
        Entry<String, SummonerRole> playerToRole
    ) {
        return playerToRoles.get(playerToRole.getKey()).contains(playerToRole.getValue());
    }

    public static <T> void generateAllPlayerPermutations(
        int n,
        List<String> playerPermutation,
        List<List<String>> allPlayerPermutations
    ) {
        if (n == 1) {
            allPlayerPermutations.add(new ArrayList<>(playerPermutation));
        } else {
            for (int i = 0; i < n - 1; i++) {
                generateAllPlayerPermutations(n - 1, playerPermutation, allPlayerPermutations);
                if (n % 2 == 0) {
                    swap(playerPermutation, i, n - 1);
                } else {
                    swap(playerPermutation, 0, n - 1);
                }
            }
            generateAllPlayerPermutations(n - 1, playerPermutation, allPlayerPermutations);
        }
    }
}
