package utils;

public class KMP {


    /***
     * Similar to generating prefix table.
     * - a[1...m] is the input string.
     * - b[1...s] is the prefix matched for the keyword.
     * - a[1...i] is the inputs that have been processed.
     * - if s=n, keyword exist with the last char at i+1.
     *
     * initialisation:
     * - i = 0, nothing is processed.
     * - s = 0, nothing is matched.
     *
     * @param input
     * @param keyword
     * @return
     */
    public static boolean KMP1based(String input, String keyword) {
        int[] prefixTable = prefixTable1BasedIndex(keyword);
        int n = keyword.length();
        int m = input.length();

        int s = 0;
        for (int i = 1; i <= m; i++) {
            while (s > 0 && keyword.charAt(s + 1 - 1) != input.charAt(i - 1))
                s = prefixTable[s];

            if (keyword.charAt(s + 1 - 1) == input.charAt(i - 1))
                s += 1;
            if (s == n) return true;
        }
        return false;
    }

    /**
     * a[0...i-1] is processed.
     * b[0...s-1] is longest proper prefix that is suffix of a[0...i-1].
     * <p>
     * initialisation:
     * s = -1, b[0...-1] is matched.
     * i = 0, a[0...-1] is processed
     * <p>
     * termination condition:
     * - b[0...n-1] is matched, when s = n.
     * - a[0...m-1] is processed, when i = m.
     *
     * @param input
     * @param keyword
     * @return
     */
    public static boolean KMP0based(String input, String keyword) {
        int[] prefixTable = prefixTable0BasedIndex(keyword);
        int n = keyword.length();
        int m = input.length();

        int s = -1;
        for (int i = 0; i < m; i++) {
            while (s > -1 && keyword.charAt(s + 1) != input.charAt(i))
                s = prefixTable[s];

            if (keyword.charAt(s + 1) == input.charAt(i))
                s += 1;
            if (s == n) return true;
        }
        return false;
    }


    /**
     * 1 Based Index:
     * initialisation:
     * - t = 0, b[1...0] = epsilon, there is no longest proper prefix for s = 1.
     * - f(1) = 0, there is no proper prefix for s = 1.
     * <p>
     * invariants:
     * - b[1...s] = the characters that have their failure function calculated.
     * - b[1...t] = current longest proper prefix that is also the suffix of b[1...s]
     * - nextSToCalculate = s+1
     * - nextTToMatch = t+1
     * <p>
     * termination:
     * - when b[1...n] is calculated for the failure function, stop when s = n+1.
     * <p>
     * The implementation implements 1-based index algorithm for prefix table generation.
     *
     * @param keyword
     * @return prefix table for the 1-based index keyword
     */
    public static int[] prefixTable1BasedIndex(String keyword) {
        int n = keyword.length();
        int[] table = new int[n + 1];
        table[0] = -1;  // the first is not used;

        int t = 0;
        table[1] = 0;
        for (int s = 1; s < n; s++) {
            // note: java is zero based, so to access b[s+1] we must call b[s+1-1]
            while (t > 0 && keyword.charAt(t + 1 - 1) != keyword.charAt(s + 1 - 1))
                t = table[t];

            if (keyword.charAt(s + 1 - 1) == keyword.charAt(t + 1 - 1)) {
                t += 1;
                table[s + 1] = t;
            } else {
                table[s + 1] = 0;
            }
        }
        return table;
    }

    /**
     * 0 Based Index:
     * initialisation:
     * - t = -1, b[0...-1]
     * - f(0) = -1
     * <p>
     * goal:
     * - need to calculate s=1~n-1
     * <p>
     * invariants:
     * - b[0...s-1] is calculated
     * - b[0...t-1] is calculated
     * - nextToCalculate = s
     * - nextToMatch = t
     * <p>
     * termination:
     * - when b[0...n-1] is calculated, stop when s = n-1. (s=0 calculates f(1), s=1 calc f(2), ... s=n-2 calc f(n-1) )
     * <p>
     *
     * @param keyword
     * @return prefix table for the 0-based index keyword
     */
    public static int[] prefixTable0BasedIndex(String keyword) {
        int n = keyword.length();
        int[] table = new int[n];

        int t = -1;
        table[0] = t;

        for (int s = 0; s <= n - 2; s++) {
            while (t >= 0 && keyword.charAt(t + 1) != keyword.charAt(s + 1))
                t = table[t];

            if (keyword.charAt(s + 1) == keyword.charAt(t + 1)) {
                t += 1;
                table[s + 1] = t;
            } else {
                table[s + 1] = -1;
            }
        }
        return table;
    }
}
