import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'gridSearch' function below.
     *
     * The function is expected to return a STRING.
     * The function accepts following parameters:
     * 1. STRING_ARRAY G
     * 2. STRING_ARRAY P
     */

    public static String gridSearch(List<String> G, List<String> P) {
        int R = G.size();
        int C = G.get(0).length();
        int r = P.size();
        int c = P.get(0).length();

        // Iterate through all possible top-left starting positions (i, j)
        // Rows: only go up to R - r (otherwise the pattern runs off the bottom)
        for (int i = 0; i <= R - r; i++) {
            // Columns: only go up to C - c (otherwise the pattern runs off the right)
            for (int j = 0; j <= C - c; j++) {
                
                // Optimization: Check if the first row of pattern matches current row segment
                // This 'indexOf' check is generally faster than manual char comparison
                // Check if current row G[i] starting at j matches P[0]
                if (G.get(i).substring(j, j + c).equals(P.get(0))) {
                    
                    // If first row matches, check the remaining rows
                    boolean match = true;
                    for (int k = 1; k < r; k++) {
                        if (!G.get(i + k).substring(j, j + c).equals(P.get(k))) {
                            match = false;
                            break;
                        }
                    }
                    
                    // If all rows matched, we found the pattern
                    if (match) {
                        return "YES";
                    }
                }
            }
        }

        return "NO";
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int t = Integer.parseInt(bufferedReader.readLine().trim());

        for (int tItr = 0; tItr < t; tItr++) {
            String[] firstMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

            int R = Integer.parseInt(firstMultipleInput[0]);

            int C = Integer.parseInt(firstMultipleInput[1]);

            List<String> G = new ArrayList<>();

            for (int i = 0; i < R; i++) {
                String GItem = bufferedReader.readLine();
                G.add(GItem);
            }

            String[] secondMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

            int r = Integer.parseInt(secondMultipleInput[0]);

            int c = Integer.parseInt(secondMultipleInput[1]);

            List<String> P = new ArrayList<>();

            for (int i = 0; i < r; i++) {
                String PItem = bufferedReader.readLine();
                P.add(PItem);
            }

            String result = Result.gridSearch(G, P);

            bufferedWriter.write(result);
            bufferedWriter.newLine();
        }

        bufferedReader.close();
        bufferedWriter.close();
    }
}
