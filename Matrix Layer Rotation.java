import java.io.*;
import java.util.*;

class Result {

    /*
     * Complete the 'matrixRotation' function below.
     * The function accepts following parameters:
     * 1. 2D_INTEGER_ARRAY matrix
     * 2. INTEGER r
     */
    public static void matrixRotation(List<List<Integer>> matrix, int r) {
        int m = matrix.size();
        int n = matrix.get(0).size();
        // The number of layers is determined by the minimum dimension [cite: 161]
        int layers = Math.min(m, n) / 2;

        for (int layer = 0; layer < layers; layer++) {
            List<Integer> ring = new ArrayList<>();

            // 1. Extract elements of the current layer (Anti-clockwise) [cite: 103]
            // Top Row (Left to Right)
            for (int j = layer; j < n - layer; j++) ring.add(matrix.get(layer).get(j));
            // Right Column (Top + 1 to Bottom)
            for (int i = layer + 1; i < m - layer; i++) ring.add(matrix.get(i).get(n - layer - 1));
            // Bottom Row (Right - 1 to Left)
            for (int j = n - layer - 2; j >= layer; j--) ring.add(matrix.get(m - layer - 1).get(j));
            // Left Column (Bottom - 1 to Top + 1)
            for (int i = m - layer - 2; i > layer; i--) ring.add(matrix.get(i).get(layer));

            // 2. Calculate effective rotations using modulo 
            int size = ring.size();
            int actualRotation = r % size;

            // 3. Reinsert elements at rotated positions
            int currentPos = actualRotation;
            
            // Re-traverse the layer in the same order and pull from the rotated index
            // Top Row
            for (int j = layer; j < n - layer; j++) {
                matrix.get(layer).set(j, ring.get(currentPos));
                currentPos = (currentPos + 1) % size;
            }
            // Right Column
            for (int i = layer + 1; i < m - layer; i++) {
                matrix.get(i).set(n - layer - 1, ring.get(currentPos));
                currentPos = (currentPos + 1) % size;
            }
            // Bottom Row
            for (int j = n - layer - 2; j >= layer; j--) {
                matrix.get(m - layer - 1).set(j, ring.get(currentPos));
                currentPos = (currentPos + 1) % size;
            }
            // Left Column
            for (int i = m - layer - 2; i > layer; i--) {
                matrix.get(i).set(layer, ring.get(currentPos));
                currentPos = (currentPos + 1) % size;
            }
        }

        // 4. Print the resultant matrix [cite: 151, 153]
        for (List<Integer> row : matrix) {
            for (int j = 0; j < row.size(); j++) {
                System.out.print(row.get(j) + (j == row.size() - 1 ? "" : " "));
            }
            System.out.println();
        }
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        // Reading m, n, and r [cite: 155]
        String[] firstMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");
        int m = Integer.parseInt(firstMultipleInput[0]);
        int n = Integer.parseInt(firstMultipleInput[1]);
        int r = Integer.parseInt(firstMultipleInput[2]);

        List<List<Integer>> matrix = new ArrayList<>();

        // Reading the matrix elements [cite: 156]
        for (int i = 0; i < m; i++) {
            String[] matrixRowTempItems = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");
            List<Integer> matrixRowItems = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                matrixRowItems.add(Integer.parseInt(matrixRowTempItems[j]));
            }
            matrix.add(matrixRowItems);
        }

        Result.matrixRotation(matrix, r);

        bufferedReader.close();
    }
}
