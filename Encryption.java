import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'encryption' function below.
     *
     * The function is expected to return a STRING.
     * The function accepts STRING s as parameter.
     */

    public static String encryption(String s) {
        // Step 1: Remove spaces from the text 
        String cleanString = s.replaceAll("\\s+", "");
        int L = cleanString.length();
        
        // Step 2: Determine rows and columns based on constraints 
        double sqrt = Math.sqrt(L);
        int rows = (int) Math.floor(sqrt);
        int cols = (int) Math.ceil(sqrt);
        
        // Ensure rows * cols >= L 
        if (rows * cols < L) {
            rows++;
        }
        
        StringBuilder encodedMessage = new StringBuilder();
        
        // Step 3: Read column by column 
        for (int j = 0; j < cols; j++) {
            // Iterate through rows for the current column
            for (int i = 0; i < rows; i++) {
                // Calculate the index in the flattened string
                // The grid is filled row by row, so row i is offset by i * cols
                int index = i * cols + j;
                
                // Check if the index is valid (since the last row might not be full)
                if (index < L) {
                    encodedMessage.append(cleanString.charAt(index));
                }
            }
            // Add a space between column texts
            encodedMessage.append(" ");
        }
        
        return encodedMessage.toString().trim();
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String s = bufferedReader.readLine();

        String result = Result.encryption(s);

        bufferedWriter.write(result);
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
