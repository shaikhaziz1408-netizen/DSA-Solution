import java.io.*;
import java.util.*;

class Result {

    /*
     * Complete the 'insertionSort' function below.
     * * CHANGED return type to 'long' to prevent overflow.
     */
    public static long insertionSort(List<Integer> arr) {
        int n = arr.size();
        int[] a = new int[n];
        // Convert List to primitive array for performance
        for (int i = 0; i < n; i++) {
            a[i] = arr.get(i);
        }
        
        int[] temp = new int[n];
        return mergeSortAndCount(a, temp, 0, n - 1);
    }

    private static long mergeSortAndCount(int[] arr, int[] temp, int left, int right) {
        long count = 0;
        if (left < right) {
            int mid = (left + right) / 2;

            // Recursively count inversions in the left and right halves
            count += mergeSortAndCount(arr, temp, left, mid);
            count += mergeSortAndCount(arr, temp, mid + 1, right);

            // Count split inversions while merging
            count += mergeAndCount(arr, temp, left, mid, right);
        }
        return count;
    }

    private static long mergeAndCount(int[] arr, int[] temp, int left, int mid, int right) {
        int i = left;    // Left subarray index
        int j = mid + 1; // Right subarray index
        int k = left;    // Temp array index
        long inversions = 0;

        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) {
                temp[k++] = arr[i++];
            } else {
                // If arr[i] > arr[j], then arr[i] and all subsequent elements 
                // in the left subarray form inversions with arr[j].
                temp[k++] = arr[j++];
                inversions += (long)(mid + 1 - i);
            }
        }

        while (i <= mid) {
            temp[k++] = arr[i++];
        }

        while (j <= right) {
            temp[k++] = arr[j++];
        }

        for (i = left; i <= right; i++) {
            arr[i] = temp[i];
        }

        return inversions;
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        
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
                String nLine = bufferedReader.readLine();
                if (nLine == null) break;
                int n = Integer.parseInt(nLine.trim());

                String[] arrTemp = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");

                List<Integer> arr = new ArrayList<Integer>();

                for (int i = 0; i < n; i++) {
                    int arrItem = Integer.parseInt(arrTemp[i]);
                    arr.add(arrItem);
                }

                long result = Result.insertionSort(arr);

                bufferedWriter.write(String.valueOf(result));
                bufferedWriter.newLine();
            }
        }

        bufferedReader.close();
        bufferedWriter.close();
    }
}
