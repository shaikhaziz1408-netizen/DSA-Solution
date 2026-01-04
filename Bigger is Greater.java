import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'biggerIsGreater' function below.
     *
     * The function is expected to return a STRING.
     * The function accepts STRING w as parameter.
     */

    public static String biggerIsGreater(String w) {
        char[] charArray = w.toCharArray();
        int n = charArray.length;
        
        // Step 1: Find the pivot (first char from right that is smaller than its next)
        int i = n - 2;
        while (i >= 0 && charArray[i] >= charArray[i + 1]) {
            i--;
        }
        
        // If no such pivot exists, we are at the last permutation
        if (i < 0) {
            return "no answer";
        }
        
        // Step 2: Find the smallest character in the suffix that is larger than the pivot
        int j = n - 1;
        while (charArray[j] <= charArray[i]) {
            j--;
        }
        
        // Step 3: Swap them
        swap(charArray, i, j);
        
        // Step 4: Reverse the suffix to make it the smallest possible
        reverse(charArray, i + 1, n - 1);
        
        return new String(charArray);
    }
    
    // Helper method to swap two characters
    private static void swap(char[] array, int i, int j) {
        char temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    
    // Helper method to reverse a portion of the array
    private static void reverse(char[] array, int start, int end) {
        while (start < end) {
            swap(array, start, end);
            start++;
            end--;
        }
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int T = Integer.parseInt(bufferedReader.readLine().trim());

        for (int TItr = 0; TItr < T; TItr++) {
            String w = bufferedReader.readLine();

            String result = Result.biggerIsGreater(w);

            bufferedWriter.write(result);
            bufferedWriter.newLine();
        }

        bufferedReader.close();
        bufferedWriter.close();
    }
}
