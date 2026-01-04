import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'climbingLeaderboard' function below.
     *
     * The function is expected to return an INTEGER_ARRAY.
     * The function accepts following parameters:
     * 1. INTEGER_ARRAY ranked
     * 2. INTEGER_ARRAY player
     */

    public static List<Integer> climbingLeaderboard(List<Integer> ranked, List<Integer> player) {
        // Step 1: Create a list of distinct leaderboard scores to handle Dense Ranking.
        List<Integer> distinctRanked = new ArrayList<>();
        if (!ranked.isEmpty()) {
            distinctRanked.add(ranked.get(0));
            for (int i = 1; i < ranked.size(); i++) {
                // Only add if it's different from the previous score (deduplicate)
                if (!ranked.get(i).equals(ranked.get(i - 1))) {
                    distinctRanked.add(ranked.get(i));
                }
            }
        }

        List<Integer> result = new ArrayList<>();
        
        // Step 2: Use a pointer starting at the bottom (worst rank) of the leaderboard.
        int i = distinctRanked.size() - 1; 

        // Step 3: Iterate through each of the player's scores.
        for (int score : player) {
            // Climb up the leaderboard:
            // While we are not at the top (i >= 0) AND 
            // the player's score is better or equal to the current rank's score...
            while (i >= 0 && score >= distinctRanked.get(i)) {
                i--; // Move one rank up (to a lower index)
            }
            
            // Calculate Rank:
            // If i is -1, the loop finished meaning we beat the top score (Rank 1).
            // Otherwise, we are sitting just below the score at index i.
            // Since index 0 is Rank 1, index i corresponds to Rank i+1.
            // We are one step below that, so our rank is (i + 1) + 1 = i + 2.
            result.add(i + 2);
        }
        
        return result;
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int rankedCount = Integer.parseInt(bufferedReader.readLine().trim());

        String[] rankedTemp = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

        List<Integer> ranked = new ArrayList<>();

        for (int i = 0; i < rankedCount; i++) {
            int rankedItem = Integer.parseInt(rankedTemp[i]);
            ranked.add(rankedItem);
        }

        int playerCount = Integer.parseInt(bufferedReader.readLine().trim());

        String[] playerTemp = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

        List<Integer> player = new ArrayList<>();

        for (int i = 0; i < playerCount; i++) {
            int playerItem = Integer.parseInt(playerTemp[i]);
            player.add(playerItem);
        }

        List<Integer> result = Result.climbingLeaderboard(ranked, player);

        for (int i = 0; i < result.size(); i++) {
            bufferedWriter.write(String.valueOf(result.get(i)));

            if (i != result.size() - 1) {
                bufferedWriter.write("\n");
            }
        }

        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
