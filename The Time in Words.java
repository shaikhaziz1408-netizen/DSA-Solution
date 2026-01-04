import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'timeInWords' function below.
     *
     * The function is expected to return a STRING.
     * The function accepts following parameters:
     * 1. INTEGER h
     * 2. INTEGER m
     */

    public static String timeInWords(int h, int m) {
        // Array to map numbers 0-29 to words. 
        // We do not need indices beyond 29 because 30 is "half" and >30 uses (60-m).
        String[] words = {
            "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
            "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty",
            "twenty one", "twenty two", "twenty three", "twenty four", "twenty five", "twenty six", "twenty seven", "twenty eight", "twenty nine"
        };

        // Case: On the hour
        if (m == 0) {
            return words[h] + " o' clock";
        }

        // Logic for the "past" side of the clock (1 to 30 minutes)
        if (m <= 30) {
            if (m == 15) {
                return "quarter past " + words[h];
            }
            if (m == 30) {
                return "half past " + words[h];
            }
            if (m == 1) {
                return "one minute past " + words[h];
            }
            // General case for minutes < 30
            return words[m] + " minutes past " + words[h];
        } 
        
        // Logic for the "to" side of the clock (31 to 59 minutes)
        else {
            // Calculate remaining minutes to the next hour
            int remaining = 60 - m;
            // Calculate the next hour, wrapping 12 to 1
            int nextHour = (h == 12) ? 1 : h + 1;

            if (remaining == 15) {
                return "quarter to " + words[nextHour];
            }
            if (remaining == 1) { // Implicit edge case, though not shown in examples usually
                return "one minute to " + words[nextHour];
            }
            // General case for minutes > 30
            return words[remaining] + " minutes to " + words[nextHour];
        }
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int h = Integer.parseInt(bufferedReader.readLine().trim());

        int m = Integer.parseInt(bufferedReader.readLine().trim());

        String result = Result.timeInWords(h, m);

        bufferedWriter.write(result);
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
