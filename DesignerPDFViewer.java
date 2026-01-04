import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'designerPdfViewer' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     * 1. INTEGER_ARRAY h
     * 2. STRING word
     */

    public static int designerPdfViewer(List<Integer> h, String word) {
        int maxHeight = 0;

        // Iterate through each character in the word
        for (char c : word.toCharArray()) {
            // Calculate the index for the alphabet (a=0, b=1, etc.)
            int index = c - 'a';
            
            // Retrieve the height for this character from the list h
            int charHeight = h.get(index);
            
            // Update the maximum height found so far
            if (charHeight > maxHeight) {
                maxHeight = charHeight;
            }
        }

        // Calculate area: max height * width (length of word)
        return maxHeight * word.length();
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String[] hTemp = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

        List<Integer> h = new ArrayList<>();

        for (int i = 0; i < 26; i++) {
            int hItem = Integer.parseInt(hTemp[i]);
            h.add(hItem);
        }

        String word = bufferedReader.readLine();

        int result = Result.designerPdfViewer(h, word);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
