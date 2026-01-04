import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'squares' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     * 1. INTEGER a
     * 2. INTEGER b
     */

    public static int squares(int a, int b) {
        // Calculate the square root of the lower bound and round up (ceil)
        // This gives us the starting integer whose square is inside the range
        int start = (int) Math.ceil(Math.sqrt(a));

        // Calculate the square root of the upper bound and round down (floor)
        // This gives us the ending integer whose square is inside the range
        int end = (int) Math.floor(Math.sqrt(b));

        // Calculate the count of integers between start and end (inclusive)
        int count = end - start + 1;

        // If start > end, it means there are no squares in the range (e.g., range 17-24)
        // We ensure we don't return a negative number
        return (count > 0) ? count : 0;
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int q = Integer.parseInt(bufferedReader.readLine().trim());

        for (int qItr = 0; qItr < q; qItr++) {
            String[] firstMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

            int a = Integer.parseInt(firstMultipleInput[0]);

            int b = Integer.parseInt(firstMultipleInput[1]);

            int result = Result.squares(a, b);

            bufferedWriter.write(String.valueOf(result));
            bufferedWriter.newLine();
        }

        bufferedReader.close();
        bufferedWriter.close();
    }
}
