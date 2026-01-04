import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'queensAttack' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     * 1. INTEGER n
     * 2. INTEGER k
     * 3. INTEGER r_q
     * 4. INTEGER c_q
     * 5. 2D_INTEGER_ARRAY obstacles
     */

    public static int queensAttack(int n, int k, int r_q, int c_q, List<List<Integer>> obstacles) {
        // Step 1: Initialize distances to the edge of the board
        // Cardinal directions
        int dUp = n - r_q;
        int dDown = r_q - 1;
        int dRight = n - c_q;
        int dLeft = c_q - 1;
        
        // Diagonal directions
        // Distance is limited by the closer of the two cardinal walls
        int dUpRight = Math.min(n - r_q, n - c_q);
        int dUpLeft = Math.min(n - r_q, c_q - 1);
        int dDownRight = Math.min(r_q - 1, n - c_q);
        int dDownLeft = Math.min(r_q - 1, c_q - 1);

        // Step 2: Iterate over obstacles to reduce distances
        for (List<Integer> obstacle : obstacles) {
            int r_o = obstacle.get(0);
            int c_o = obstacle.get(1);

            // Check if obstacle is in the same column (Vertical)
            if (c_o == c_q) {
                if (r_o > r_q) {
                    // Obstacle is Up. New distance is row diff - 1
                    dUp = Math.min(dUp, r_o - r_q - 1);
                } else {
                    // Obstacle is Down
                    dDown = Math.min(dDown, r_q - r_o - 1);
                }
            }
            // Check if obstacle is in the same row (Horizontal)
            else if (r_o == r_q) {
                if (c_o > c_q) {
                    // Obstacle is Right
                    dRight = Math.min(dRight, c_o - c_q - 1);
                } else {
                    // Obstacle is Left
                    dLeft = Math.min(dLeft, c_q - c_o - 1);
                }
            }
            // Check diagonals
            // An obstacle is on a diagonal if |row_diff| == |col_diff|
            else if (Math.abs(r_o - r_q) == Math.abs(c_o - c_q)) {
                if (r_o > r_q) {
                    if (c_o > c_q) {
                        // Up-Right
                        dUpRight = Math.min(dUpRight, c_o - c_q - 1);
                    } else {
                        // Up-Left
                        dUpLeft = Math.min(dUpLeft, c_q - c_o - 1);
                    }
                } else {
                    if (c_o > c_q) {
                        // Down-Right
                        dDownRight = Math.min(dDownRight, c_o - c_q - 1);
                    } else {
                        // Down-Left
                        dDownLeft = Math.min(dDownLeft, c_q - c_o - 1);
                    }
                }
            }
        }

        // Step 3: Sum all distances
        return dUp + dDown + dRight + dLeft + dUpRight + dUpLeft + dDownRight + dDownLeft;
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String[] firstMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

        int n = Integer.parseInt(firstMultipleInput[0]);

        int k = Integer.parseInt(firstMultipleInput[1]);

        String[] secondMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

        int r_q = Integer.parseInt(secondMultipleInput[0]);

        int c_q = Integer.parseInt(secondMultipleInput[1]);

        List<List<Integer>> obstacles = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            String[] obstaclesRowTempItems = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

            List<Integer> obstaclesRowItems = new ArrayList<>();

            for (int j = 0; j < 2; j++) {
                int obstaclesItem = Integer.parseInt(obstaclesRowTempItems[j]);
                obstaclesRowItems.add(obstaclesItem);
            }

            obstacles.add(obstaclesRowItems);
        }

        int result = Result.queensAttack(n, k, r_q, c_q, obstacles);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
