import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'libraryFine' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     * 1. INTEGER d1
     * 2. INTEGER m1
     * 3. INTEGER y1
     * 4. INTEGER d2
     * 5. INTEGER m2
     * 6. INTEGER y2
     */

    public static int libraryFine(int d1, int m1, int y1, int d2, int m2, int y2) {
        // Case 1: Returned in a later year 
        if (y1 > y2) {
            return 10000;
        }
        
        // Case 2: Returned in an earlier year 
        if (y1 < y2) {
            return 0;
        }

        // Reaching here means y1 == y2. Now check months.
        
        // Case 3: Returned in a later month of the same year 
        if (m1 > m2) {
            return 500 * (m1 - m2);
        }
        
        // Case 4: Returned in an earlier month of the same year 
        if (m1 < m2) {
            return 0;
        }

        // Reaching here means y1 == y2 and m1 == m2. Now check days.
        
        // Case 5: Returned later in the same month 
        if (d1 > d2) {
            return 15 * (d1 - d2);
        }

        // Case 6: Returned on or before the due day 
        return 0;
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String[] firstMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

        int d1 = Integer.parseInt(firstMultipleInput[0]);

        int m1 = Integer.parseInt(firstMultipleInput[1]);

        int y1 = Integer.parseInt(firstMultipleInput[2]);

        String[] secondMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

        int d2 = Integer.parseInt(secondMultipleInput[0]);

        int m2 = Integer.parseInt(secondMultipleInput[1]);

        int y2 = Integer.parseInt(secondMultipleInput[2]);

        int result = Result.libraryFine(d1, m1, y1, d2, m2, y2);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
