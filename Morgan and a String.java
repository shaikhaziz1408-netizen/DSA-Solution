import java.io.*;
import java.util.*;

class Result {

    /*
     * Complete the 'morganAndString' function below.
     *
     * The function is expected to return a STRING.
     * The function accepts following parameters:
     * 1. STRING a
     * 2. STRING b
     */
    public static String morganAndString(String a, String b) {
        StringBuilder result = new StringBuilder();
        int n = a.length();
        int m = b.length();
        int i = 0;
        int j = 0;

        // Append sentinel characters to simplify boundary checks
        // 'z' + 1 is larger than any valid uppercase letter.
        a += (char)('z' + 1);
        b += (char)('z' + 1);

        while (i < n || j < m) {
            if (i < n && j < m) {
                if (a.charAt(i) < b.charAt(j)) {
                    result.append(a.charAt(i));
                    i++;
                } else if (a.charAt(i) > b.charAt(j)) {
                    result.append(b.charAt(j));
                    j++;
                } else {
                    // Characters are equal, perform lexicographical comparison of suffixes
                    // Note: In a production environment with huge constraints, this 
                    // sub-comparison is often optimized with Suffix Arrays or ranks, 
                    // but for typical contest limits, efficient skipping is key.
                    int tempI = i;
                    int tempJ = j;
                    char charA = a.charAt(tempI);
                    char charB = b.charAt(tempJ);
                    
                    // Find the first non-matching character
                    while (charA == charB && tempI < n && tempJ < m) {
                        tempI++;
                        tempJ++;
                        charA = a.charAt(tempI);
                        charB = b.charAt(tempJ);
                    }

                    if (charA < charB) {
                        // 'a' wins: append consecutive identical characters from 'a'
                        char current = a.charAt(i);
                        while (i < n && a.charAt(i) == current) {
                            result.append(a.charAt(i));
                            i++;
                        }
                    } else {
                        // 'b' wins (or tied until end): append consecutive identical characters from 'b'
                        char current = b.charAt(j);
                        while (j < m && b.charAt(j) == current) {
                            result.append(b.charAt(j));
                            j++;
                        }
                    }
                }
            } else if (i < n) {
                // Only 'a' has characters left
                result.append(a.charAt(i));
                i++;
            } else {
                // Only 'b' has characters left
                result.append(b.charAt(j));
                j++;
            }
        }

        return result.toString();
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        
        // Use standard output if OUTPUT_PATH is not set
        String outputPath = System.getenv("OUTPUT_PATH");
        BufferedWriter bufferedWriter;
        if (outputPath != null) {
            bufferedWriter = new BufferedWriter(new FileWriter(outputPath));
        } else {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        }

        String line = bufferedReader.readLine();
        if (line != null) {
            int t = Integer.parseInt(line.trim());

            for (int tItr = 0; tItr < t; tItr++) {
                String a = bufferedReader.readLine();
                String b = bufferedReader.readLine();

                String result = Result.morganAndString(a, b);

                bufferedWriter.write(result);
                bufferedWriter.newLine();
            }
        }

        bufferedReader.close();
        bufferedWriter.close();
    }
}
