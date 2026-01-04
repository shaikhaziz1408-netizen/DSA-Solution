import java.io.*;
import java.util.*;

class Result {

    /*
     * Complete the 'gridlandProvinces' function below.
     * * Optimization:
     * 1. Uses Double Hashing (Mod 10^9+7 and 10^9+9) packed into a single long to prevent collisions.
     * 2. Uses Arrays.sort() instead of HashSet to avoid object overhead and TLE.
     */
    static final long M1 = 1_000_000_007L;
    static final long P1 = 313;
    static final long M2 = 1_000_000_009L;
    static final long P2 = 317;
    
    static long[] pow1, pow2;

    public static int gridlandProvinces(String s1, String s2) {
        int n = s1.length();
        
        // Precompute powers if not enough size
        if (pow1 == null || pow1.length <= 2 * n) {
            pow1 = new long[2 * n + 5];
            pow2 = new long[2 * n + 5];
            pow1[0] = 1;
            pow2[0] = 1;
            for (int i = 1; i < pow1.length; i++) {
                pow1[i] = (pow1[i - 1] * P1) % M1;
                pow2[i] = (pow2[i - 1] * P2) % M2;
            }
        }

        // Use a List to collect hashes, then sort to count distincts.
        // We estimate capacity to avoid resizing: approx 2 * N*N/2 entries.
        ArrayList<Long> hashes = new ArrayList<>(n * n + n);

        // Process original grid
        process(s1, s2, n, hashes);

        // Process mirrored grid (paths starting from the right)
        String s1Rev = new StringBuilder(s1).reverse().toString();
        String s2Rev = new StringBuilder(s2).reverse().toString();
        process(s1Rev, s2Rev, n, hashes);

        // Sort and count distinct
        Collections.sort(hashes);
        
        int distinctCount = 0;
        if (!hashes.isEmpty()) {
            distinctCount = 1;
            long prev = hashes.get(0);
            for (int i = 1; i < hashes.size(); i++) {
                long curr = hashes.get(i);
                if (curr != prev) {
                    distinctCount++;
                    prev = curr;
                }
            }
        }

        return distinctCount;
    }

    private static void process(String s1, String s2, int n, ArrayList<Long> hashes) {
        // Precompute hashes for basic rows and their reverses (Index 0 = Mod1, Index 1 = Mod2)
        long[][] h1 = buildHash(s1);
        long[][] h2 = buildHash(s2);
        long[][] h1r = buildHash(new StringBuilder(s1).reverse().toString());
        long[][] h2r = buildHash(new StringBuilder(s2).reverse().toString());

        // Precompute Zig-Zag hashes
        // ZigZag A: s1[0] s2[0] s2[1] s1[1] ...
        StringBuilder sbZ1 = new StringBuilder();
        for (int k = 0; k < n; k++) {
            if (k % 2 == 0) { sbZ1.append(s1.charAt(k)).append(s2.charAt(k)); } 
            else { sbZ1.append(s2.charAt(k)).append(s1.charAt(k)); }
        }
        long[][] hZ1 = buildHash(sbZ1.toString());

        // ZigZag B: s2[0] s1[0] s1[1] s2[1] ...
        StringBuilder sbZ2 = new StringBuilder();
        for (int k = 0; k < n; k++) {
            if (k % 2 == 0) { sbZ2.append(s2.charAt(k)).append(s1.charAt(k)); } 
            else { sbZ2.append(s1.charAt(k)).append(s2.charAt(k)); }
        }
        long[][] hZ2 = buildHash(sbZ2.toString());

        // Iterate split points
        // i: End column of Left U-Turn (-1 means no Left U-Turn)
        // j: Start column of Right U-Turn (n means no Right U-Turn)
        for (int i = -1; i < n; i++) {
            for (int j = i + 1; j <= n; j++) {
                // Try starting path at Row 0 and Row 1
                for (int startRow = 0; startRow < 2; startRow++) {
                    long currH1 = 0, currH2 = 0;
                    int len = 0;
                    int currentRow = startRow;

                    // 1. Left U-Turn (Cols 0 to i)
                    if (i >= 0) {
                        long p1_1, p1_2, p2_1, p2_2;
                        if (startRow == 0) {
                            // Row 0 -> Left -> Row 1
                            // Reverse(s1[0..i]) + s2[0..i]
                            // Rev(s1[0..i]) corresponds to suffix of s1Rev from n-1-i to n-1
                            p1_1 = getHash(h1r[0], n - 1 - i, n - 1, M1, pow1);
                            p1_2 = getHash(h1r[1], n - 1 - i, n - 1, M2, pow2);
                            p2_1 = getHash(h2[0], 0, i, M1, pow1);
                            p2_2 = getHash(h2[1], 0, i, M2, pow2);
                        } else {
                            // Row 1 -> Left -> Row 0
                            p1_1 = getHash(h2r[0], n - 1 - i, n - 1, M1, pow1);
                            p1_2 = getHash(h2r[1], n - 1 - i, n - 1, M2, pow2);
                            p2_1 = getHash(h1[0], 0, i, M1, pow1);
                            p2_2 = getHash(h1[1], 0, i, M2, pow2);
                        }
                        
                        currH1 = concat(p1_1, i + 1, p2_1, i + 1, M1, pow1);
                        currH2 = concat(p1_2, i + 1, p2_2, i + 1, M2, pow2);
                        len += 2 * (i + 1);
                        currentRow = 1 - startRow;
                    }

                    // 2. Zig-Zag (Cols i+1 to j-1)
                    if (j > i + 1) {
                        int zzLen = j - (i + 1); 
                        int totalChars = 2 * zzLen;
                        long zz1, zz2;
                        
                        // Select correct ZigZag pattern
                        if (currentRow == 0) {
                            // Moving Down at col i+1. Pattern A is Down at even, B is Down at odd.
                            if ((i + 1) % 2 == 0) {
                                zz1 = getHash(hZ1[0], 2 * (i + 1), 2 * j - 1, M1, pow1);
                                zz2 = getHash(hZ1[1], 2 * (i + 1), 2 * j - 1, M2, pow2);
                            } else {
                                zz1 = getHash(hZ2[0], 2 * (i + 1), 2 * j - 1, M1, pow1);
                                zz2 = getHash(hZ2[1], 2 * (i + 1), 2 * j - 1, M2, pow2);
                            }
                        } else {
                            // Moving Up at col i+1. Pattern A is Up at odd, B is Up at even.
                            if ((i + 1) % 2 != 0) {
                                zz1 = getHash(hZ1[0], 2 * (i + 1), 2 * j - 1, M1, pow1);
                                zz2 = getHash(hZ1[1], 2 * (i + 1), 2 * j - 1, M2, pow2);
                            } else {
                                zz1 = getHash(hZ2[0], 2 * (i + 1), 2 * j - 1, M1, pow1);
                                zz2 = getHash(hZ2[1], 2 * (i + 1), 2 * j - 1, M2, pow2);
                            }
                        }
                        
                        currH1 = concat(currH1, len, zz1, totalChars, M1, pow1);
                        currH2 = concat(currH2, len, zz2, totalChars, M2, pow2);
                        len += totalChars;
                        if (zzLen % 2 != 0) currentRow = 1 - currentRow;
                    }

                    // 3. Right U-Turn (Cols j to n-1)
                    if (j < n) {
                        long p1_1, p1_2, p2_1, p2_2;
                        if (currentRow == 0) {
                            // Row 0 -> Right -> Row 1
                            p1_1 = getHash(h1[0], j, n - 1, M1, pow1);
                            p1_2 = getHash(h1[1], j, n - 1, M2, pow2);
                            p2_1 = getHash(h2r[0], 0, n - 1 - j, M1, pow1);
                            p2_2 = getHash(h2r[1], 0, n - 1 - j, M2, pow2);
                        } else {
                            // Row 1 -> Right -> Row 0
                            p1_1 = getHash(h2[0], j, n - 1, M1, pow1);
                            p1_2 = getHash(h2[1], j, n - 1, M2, pow2);
                            p2_1 = getHash(h1r[0], 0, n - 1 - j, M1, pow1);
                            p2_2 = getHash(h1r[1], 0, n - 1 - j, M2, pow2);
                        }
                        
                        currH1 = concat(currH1, len, p1_1, n - j, M1, pow1);
                        currH2 = concat(currH2, len, p1_2, n - j, M2, pow2);
                        
                        len += n - j; // First leg
                        
                        currH1 = concat(currH1, len, p2_1, n - j, M1, pow1);
                        currH2 = concat(currH2, len, p2_2, n - j, M2, pow2);
                    }
                    
                    // Pack two 32-bit (approx) hashes into one long
                    // Since Mods are ~10^9, they fit in 30-31 bits.
                    hashes.add((currH1 << 32) | currH2);
                }
            }
        }
    }

    private static long[][] buildHash(String s) {
        long[] h1 = new long[s.length() + 1];
        long[] h2 = new long[s.length() + 1];
        for (int i = 0; i < s.length(); i++) {
            h1[i + 1] = (h1[i] * P1 + (s.charAt(i) - 'a' + 1)) % M1;
            h2[i + 1] = (h2[i] * P2 + (s.charAt(i) - 'a' + 1)) % M2;
        }
        return new long[][] { h1, h2 };
    }

    private static long getHash(long[] h, int left, int right, long M, long[] pow) {
        long res = (h[right + 1] - h[left] * pow[right - left + 1]) % M;
        if (res < 0) res += M;
        return res;
    }

    private static long concat(long hashA, int lenA, long hashB, int lenB, long M, long[] pow) {
        return (hashA * pow[lenB] + hashB) % M;
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
            int p = Integer.parseInt(line.trim());
            for (int pItr = 0; pItr < p; pItr++) {
                int n = Integer.parseInt(bufferedReader.readLine().trim());
                String s1 = bufferedReader.readLine();
                String s2 = bufferedReader.readLine();
                int result = Result.gridlandProvinces(s1, s2);
                bufferedWriter.write(String.valueOf(result));
                bufferedWriter.newLine();
            }
        }
        bufferedReader.close();
        bufferedWriter.close();
    }
}
