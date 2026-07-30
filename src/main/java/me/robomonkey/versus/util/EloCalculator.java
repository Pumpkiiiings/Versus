package me.robomonkey.versus.util;

import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;

public class EloCalculator {
    private static int getKFactor() {
        return Settings.getNumber(Setting.RANKED_K_FACTOR);
    }

    /**
     * Calculates the new ELO ratings after a match.
     * @param winnerElo The ELO of the winner before the match
     * @param loserElo The ELO of the loser before the match
     * @return An array containing [newWinnerElo, newLoserElo]
     */
    public static int[] calculateElo(int winnerElo, int loserElo) {
        double expectedWinner = 1.0 / (1.0 + Math.pow(10, (loserElo - winnerElo) / 400.0));
        double expectedLoser = 1.0 / (1.0 + Math.pow(10, (winnerElo - loserElo) / 400.0));

        int kFactor = getKFactor();
        int newWinnerElo = (int) Math.round(winnerElo + kFactor * (1.0 - expectedWinner));
        int newLoserElo = (int) Math.round(loserElo + kFactor * (0.0 - expectedLoser));

        return new int[]{newWinnerElo, Math.max(0, newLoserElo)}; // Prevent negative ELO
    }
}
