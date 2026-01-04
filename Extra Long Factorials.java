import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'extraLongFactorials' function below.
     *
     * The function accepts INTEGER n as parameter.
     */

    public static void extraLongFactorials(int n) {
        // Initialize a BigInteger with value 1
        BigInteger factorial = BigInteger.ONE;

        // Iterate from 1 to n and multiply
        for (int i = 1; i <= n; i++) {
            // BigInteger operations return a new BigInteger, so we must reassign
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }

        // Print the result directly as requested
        System.out.println(factorial);
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(bufferedReader.readLine().trim());

        Result.extraLongFactorials(n);

        bufferedReader.close();
    }
}
