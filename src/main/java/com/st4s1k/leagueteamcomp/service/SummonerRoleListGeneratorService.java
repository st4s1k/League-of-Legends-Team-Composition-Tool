package com.st4s1k.leagueteamcomp.service;

import com.st4s1k.leagueteamcomp.model.enums.SummonerRoleEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.swap;

@Service
public class SummonerRoleListGeneratorService {

    public List<Map<String, SummonerRoleEnum>> getCombinations(Map<String, List<SummonerRoleEnum>> playerToRoles) {
        if (playerToRoles.isEmpty() || playerToRoles.size() < 5) {
            return emptyList();
        }
        List<List<String>> allPlayerPermutations = new ArrayList<>();
        List<String> players = new ArrayList<>(playerToRoles.keySet());
        generateAllPlayerPermutations(playerToRoles.size(), players, allPlayerPermutations);
        return allPlayerPermutations.stream().map(playersInOrder -> Map.ofEntries(
                Map.entry(playersInOrder.get(0), SummonerRoleEnum.TOP),
                Map.entry(playersInOrder.get(1), SummonerRoleEnum.MID),
                Map.entry(playersInOrder.get(2), SummonerRoleEnum.ADC),
                Map.entry(playersInOrder.get(3), SummonerRoleEnum.SUP),
                Map.entry(playersInOrder.get(4), SummonerRoleEnum.JGL)
            )).filter(playersToRoles -> arePlayersOnCorrectRoles(playerToRoles, playersToRoles))
            .collect(Collectors.toList());
    }

    private boolean arePlayersOnCorrectRoles(
        Map<String, List<SummonerRoleEnum>> playerToRoles,
        Map<String, SummonerRoleEnum> newPlayerToRoleList
    ) {
        return newPlayerToRoleList.entrySet().stream()
            .allMatch(newPlayerToRole -> isPlayerOnCorrectRole(playerToRoles, newPlayerToRole));
    }

    private boolean isPlayerOnCorrectRole(
        Map<String, List<SummonerRoleEnum>> playerToRoles,
        Entry<String, SummonerRoleEnum> playerToRole
    ) {
        return playerToRoles.get(playerToRole.getKey()).contains(playerToRole.getValue());
    }

    public void generateAllPlayerPermutations(
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
