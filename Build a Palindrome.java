import java.io.*;
import java.util.*;

class Result {
    // Rolling Hash Constants
    private static final long MOD = 1_000_000_007L;
    private static final int BASE = 313; // Prime larger than 26
    private static long[] POWERS;

    /*
     * Complete the 'buildPalindrome' function below.
     */
    public static String buildPalindrome(String a, String b) {
        int nA = a.length();
        int nB = b.length();
        int maxLenTotal = Math.max(nA, nB);
        
        // Precompute powers for hashing
        POWERS = new long[maxLenTotal * 2 + 5];
        POWERS[0] = 1;
        for (int i = 1; i < POWERS.length; i++) {
            POWERS[i] = (POWERS[i - 1] * BASE) % MOD;
        }

        // Prepare Hashing Helpers
        String bRevStr = new StringBuilder(b).reverse().toString();
        String aRevStr = new StringBuilder(a).reverse().toString();
        
        RollingHash hashA = new RollingHash(a);
        RollingHash hashB = new RollingHash(b);
        RollingHash hashARev = new RollingHash(aRevStr);
        RollingHash hashBRev = new RollingHash(bRevStr);

        // Precompute palindrome lengths
        int[] maxPalStartA = computeMaxPalStarts(a);
        int[] maxPalEndB = computeMaxPalEnds(b); // uses Manacher's on b

        // Best candidate tracking
        // type 1: Center in A. type 2: Center in B.
        int bestType = -1; 
        int bestI = -1;    // index i from the loop
        int bestL = -1;    // match length l
        int bestPalLen = -1; // palindrome length
        int maxLen = 0;

        // --- Case 1: Center in A ---
        // s = Q + P + Q^R
        // Q is suffix of A[0...i] of length l (matched with prefix of bRev)
        SAM samBRev = new SAM(bRevStr);
        int curr = 0; 
        int l = 0;
        for (int i = 0; i < nA; i++) {
            int c = a.charAt(i) - 'a';
            while (curr != 0 && samBRev.nodes[curr].next[c] == -1) {
                curr = samBRev.nodes[curr].link;
                l = samBRev.nodes[curr].len;
            }
            if (samBRev.nodes[curr].next[c] != -1) {
                curr = samBRev.nodes[curr].next[c];
                l++;
            }

            if (l > 0) {
                int palLen = (i + 1 < nA) ? maxPalStartA[i + 1] : 0;
                int totalLen = 2 * l + palLen;

                if (totalLen > maxLen) {
                    maxLen = totalLen;
                    bestType = 1; bestI = i; bestL = l; bestPalLen = palLen;
                } else if (totalLen == maxLen) {
                    // Compare current candidate with best found so far
                    if (compare(1, i, l, palLen, 
                                bestType, bestI, bestL, bestPalLen, 
                                hashA, hashB, hashARev, hashBRev, nA, nB) < 0) {
                        bestType = 1; bestI = i; bestL = l; bestPalLen = palLen;
                    }
                }
            }
        }

        // --- Case 2: Center in B ---
        // s = Q + P + Q^R
        // Q is suffix of bRev[0...i] of length l (matched with prefix of A)
        // This Q (from bRev) corresponds to Q^R in B.
        // So s_a = Q (from A match), s_b = P + Q^R (from B).
        SAM samA = new SAM(a);
        curr = 0;
        l = 0;
        for (int i = 0; i < nB; i++) {
            int c = bRevStr.charAt(i) - 'a';
            while (curr != 0 && samA.nodes[curr].next[c] == -1) {
                curr = samA.nodes[curr].link;
                l = samA.nodes[curr].len;
            }
            if (samA.nodes[curr].next[c] != -1) {
                curr = samA.nodes[curr].next[c];
                l++;
            }

            if (l > 0) {
                int startInB = nB - 1 - i;
                int palLen = (startInB - 1 >= 0) ? maxPalEndB[startInB - 1] : 0;
                int totalLen = 2 * l + palLen;

                if (totalLen > maxLen) {
                    maxLen = totalLen;
                    bestType = 2; bestI = i; bestL = l; bestPalLen = palLen;
                } else if (totalLen == maxLen) {
                    if (compare(2, i, l, palLen, 
                                bestType, bestI, bestL, bestPalLen, 
                                hashA, hashB, hashARev, hashBRev, nA, nB) < 0) {
                        bestType = 2; bestI = i; bestL = l; bestPalLen = palLen;
                    }
                }
            }
        }

        if (bestType == -1) return "-1";
        return constructString(bestType, bestI, bestL, bestPalLen, a, b, bRevStr);
    }

    // --- Helpers ---

    // Compares two candidates lexicographically. Returns < 0 if c1 < c2.
    private static int compare(int type1, int i1, int l1, int p1,
                               int type2, int i2, int l2, int p2,
                               RollingHash hA, RollingHash hB, RollingHash hAR, RollingHash hBR,
                               int nA, int nB) {
        int len = 2 * l1 + p1; // Lengths are equal here
        
        // Binary search for Longest Common Prefix
        int low = 0, high = len;
        int lcp = 0;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (mid == 0) {
                low = 1; continue;
            }
            long hash1 = getCompositeHash(type1, i1, l1, p1, mid, hA, hB, hAR, hBR, nA, nB);
            long hash2 = getCompositeHash(type2, i2, l2, p2, mid, hA, hB, hAR, hBR, nA, nB);
            if (hash1 == hash2) {
                lcp = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        if (lcp == len) return 0; // Strings are identical

        // Compare characters at index lcp
        char c1 = getChar(type1, i1, l1, p1, lcp, hA.str, hB.str, hAR.str, hBR.str, nA, nB);
        char c2 = getChar(type2, i2, l2, p2, lcp, hA.str, hB.str, hAR.str, hBR.str, nA, nB);
        return c1 - c2;
    }

    // Get hash of prefix of length 'len' of the palindrome candidate
    private static long getCompositeHash(int type, int i, int l, int p, int len,
                                         RollingHash hA, RollingHash hB, RollingHash hAR, RollingHash hBR,
                                         int nA, int nB) {
        // Candidate structure: Q + P + Q^R
        // len can span across Q, P, or Q^R
        
        long totalHash = 0;
        int currentLen = 0;

        // Part 1: Q (length l)
        int qLen = Math.min(len, l);
        if (qLen > 0) {
            long hQ = 0;
            if (type == 1) {
                // Q is substring of A ending at i: A[i-l+1 ... i-l+qLen]
                // Full Q is A[i-l+1 ... i]. Prefix of length qLen is A[i-l+1 ... i-l+qLen]
                hQ = hA.getHash(i - l + 1, i - l + qLen);
            } else {
                // Q is substring of BRev ending at i: BRev[i-l+1 ... i-l+qLen]
                hQ = hBR.getHash(i - l + 1, i - l + qLen);
            }
            totalHash = hQ;
            currentLen += qLen;
        }

        if (currentLen == len) return totalHash;

        // Part 2: P (length p)
        int pLen = Math.min(len - currentLen, p);
        if (pLen > 0) {
            long hP = 0;
            if (type == 1) {
                // P is substring of A starting at i+1: A[i+1 ... i+pLen]
                hP = hA.getHash(i + 1, i + pLen);
            } else {
                // P is substring of B ending at startInB-1. 
                // startInB = nB - 1 - i. P range: B[startInB-p ... startInB-1]
                int startInB = nB - 1 - i;
                // Full P: B[startInB - p ... startInB - 1]
                // Prefix of length pLen: B[startInB - p ... startInB - p + pLen - 1]
                hP = hB.getHash(startInB - p, startInB - p + pLen - 1);
            }
            // Append hP to totalHash
            totalHash = (totalHash * POWERS[pLen]) % MOD;
            totalHash = (totalHash + hP) % MOD;
            currentLen += pLen;
        }

        if (currentLen == len) return totalHash;

        // Part 3: Q^R (remaining length)
        int qrLen = len - currentLen;
        if (qrLen > 0) {
            long hQR = 0;
            if (type == 1) {
                // Q is A[i-l+1 ... i]. Q^R is reverse of this.
                // Q^R is substring of ARev. 
                // A indices [start, end] map to ARev indices [N-1-end, N-1-start].
                // Q indices in A: start=i-l+1, end=i.
                // Q^R full indices in ARev: [nA-1-i, nA-1-(i-l+1)].
                // We need prefix of Q^R of length qrLen.
                // This corresponds to ARev[nA-1-i ... nA-1-i + qrLen - 1].
                hQR = hAR.getHash(nA - 1 - i, nA - 1 - i + qrLen - 1);
            } else {
                // Q is BRev[i-l+1 ... i]. Q^R is reverse of this.
                // Q^R is substring of B (since reverse of reverse of B is B).
                // BRev indices [s, e] map to B indices [N-1-e, N-1-s].
                // Q indices in BRev: start=i-l+1, end=i.
                // Q^R full indices in B: [nB-1-i, nB-1-(i-l+1)].
                // Prefix of Q^R of length qrLen: B[nB-1-i ... nB-1-i + qrLen - 1].
                hQR = hB.getHash(nB - 1 - i, nB - 1 - i + qrLen - 1);
            }
            totalHash = (totalHash * POWERS[qrLen]) % MOD;
            totalHash = (totalHash + hQR) % MOD;
        }

        return totalHash;
    }

    private static char getChar(int type, int i, int l, int p, int k,
                                String strA, String strB, String strARev, String strBRev, int nA, int nB) {
        if (k < l) {
            // In Q
            if (type == 1) return strA.charAt(i - l + 1 + k);
            else return strBRev.charAt(i - l + 1 + k);
        } else if (k < l + p) {
            // In P
            int offset = k - l;
            if (type == 1) return strA.charAt(i + 1 + offset);
            else {
                int startInB = nB - 1 - i;
                return strB.charAt(startInB - p + offset);
            }
        } else {
            // In Q^R
            int offset = k - (l + p);
            if (type == 1) return strARev.charAt(nA - 1 - i + offset);
            else return strB.charAt(nB - 1 - i + offset);
        }
    }

    private static String constructString(int type, int i, int l, int p, String A, String B, String BRev) {
        String Q, P, QR;
        if (type == 1) {
            Q = A.substring(i - l + 1, i + 1);
            P = (p > 0) ? A.substring(i + 1, i + 1 + p) : "";
            QR = new StringBuilder(Q).reverse().toString();
        } else {
            Q = BRev.substring(i - l + 1, i + 1);
            int startInB = B.length() - 1 - i;
            P = (p > 0) ? B.substring(startInB - p, startInB) : "";
            QR = new StringBuilder(Q).reverse().toString();
        }
        return Q + P + QR;
    }

    static class RollingHash {
        long[] hash;
        String str;

        RollingHash(String s) {
            this.str = s;
            int n = s.length();
            hash = new long[n + 1];
            for (int i = 0; i < n; i++) {
                hash[i + 1] = (hash[i] * BASE + s.charAt(i)) % MOD;
            }
        }

        long getHash(int i, int j) { // inclusive i, j (0-based)
            if (i > j) return 0;
            long res = (hash[j + 1] - hash[i] * POWERS[j - i + 1]) % MOD;
            if (res < 0) res += MOD;
            return res;
        }
    }

    private static int[] computeMaxPalStarts(String s) {
        int n = s.length();
        int[] d1 = new int[n]; 
        int[] d2 = new int[n];
        for (int i = 0, l = 0, r = -1; i < n; i++) {
            int k = (i > r) ? 1 : Math.min(d1[l + r - i], r - i + 1);
            while (0 <= i - k && i + k < n && s.charAt(i - k) == s.charAt(i + k)) k++;
            d1[i] = k--;
            if (i + k > r) { l = i - k; r = i + k; }
        }
        for (int i = 0, l = 0, r = -1; i < n; i++) {
            int k = (i > r) ? 0 : Math.min(d2[l + r - i + 1], r - i + 1);
            while (0 <= i - k - 1 && i + k < n && s.charAt(i - k - 1) == s.charAt(i + k)) k++;
            d2[i] = k--;
            if (i + k > r) { l = i - k - 1; r = i + k; }
        }
        int[] starts = new int[n];
        for (int i = 0; i < n; i++) {
            int len = 2 * d1[i] - 1;
            int start = i - d1[i] + 1;
            if (len > starts[start]) starts[start] = len;
        }
        for (int i = 0; i < n; i++) {
            if (d2[i] == 0) continue;
            int len = 2 * d2[i];
            int start = i - d2[i];
            if (len > starts[start]) starts[start] = len;
        }
        for (int i = 1; i < n; i++) starts[i] = Math.max(starts[i], starts[i - 1] - 2);
        return starts;
    }

    private static int[] computeMaxPalEnds(String s) {
        int n = s.length();
        String rev = new StringBuilder(s).reverse().toString();
        int[] startsRev = computeMaxPalStarts(rev);
        int[] ends = new int[n];
        for (int i = 0; i < n; i++) ends[n - 1 - i] = startsRev[i];
        return ends;
    }

    static class SAM {
        static class Node {
            int len, link;
            int[] next = new int[26];
            Node() { Arrays.fill(next, -1); }
        }
        Node[] nodes;
        int sz, last;
        SAM(String s) {
            nodes = new Node[s.length() * 2 + 1];
            for(int i=0; i<nodes.length; i++) nodes[i] = new Node();
            sz = 1; last = 0; nodes[0].link = -1;
            for (char c : s.toCharArray()) extend(c - 'a');
        }
        void extend(int c) {
            int cur = sz++;
            nodes[cur].len = nodes[last].len + 1;
            int p = last;
            while (p != -1 && nodes[p].next[c] == -1) {
                nodes[p].next[c] = cur;
                p = nodes[p].link;
            }
            if (p == -1) nodes[cur].link = 0;
            else {
                int q = nodes[p].next[c];
                if (nodes[p].len + 1 == nodes[q].len) nodes[cur].link = q;
                else {
                    int clone = sz++;
                    nodes[clone].len = nodes[p].len + 1;
                    System.arraycopy(nodes[q].next, 0, nodes[clone].next, 0, 26);
                    nodes[clone].link = nodes[q].link;
                    while (p != -1 && nodes[p].next[c] == q) {
                        nodes[p].next[c] = clone;
                        p = nodes[p].link;
                    }
                    nodes[q].link = nodes[cur].link = clone;
                }
            }
            last = cur;
        }
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String outputPath = System.getenv("OUTPUT_PATH");
        BufferedWriter bufferedWriter;
        if (outputPath != null) bufferedWriter = new BufferedWriter(new FileWriter(outputPath));
        else bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        
        String line = bufferedReader.readLine();
        if (line != null) {
            int t = Integer.parseInt(line.trim());
            for (int tItr = 0; tItr < t; tItr++) {
                String a = bufferedReader.readLine();
                String b = bufferedReader.readLine();
                String result = Result.buildPalindrome(a, b);
                bufferedWriter.write(result);
                bufferedWriter.newLine();
            }
        }
        bufferedReader.close();
        bufferedWriter.close();
    }
}
