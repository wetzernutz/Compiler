package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KMPTest {

    @Test
    void testKMP1Based() {
        String input = "abababaab";
        String keyword = "ababaa";

        boolean result = KMP.KMP1based(input, keyword);
        Assertions.assertTrue(result);
    }

    @Test
    void testKMP0Based() {
        String input = "abababaab";
        String keyword = "ababaa";

        boolean result = KMP.KMP1based(input, keyword);
        Assertions.assertTrue(result);


        input = "abababbaa";
        result = KMP.KMP1based(input, keyword);
        Assertions.assertFalse(result);
    }

    @Test
    void testPrefixTableGeneration1BasedIndex() {
        String keyword = "abababaab";
        int[] prefixTable = KMP.prefixTable1BasedIndex(keyword);
        // same as in the book, with an additional space at the front that is untouched.
        Assertions.assertEquals("-1" + "001234512", concatenateTable(prefixTable));

        keyword = "aaaaaa";
        prefixTable = KMP.prefixTable1BasedIndex(keyword);
        // same as in the book, with an additional space at the front that is untouched.
        Assertions.assertEquals("-1" + "012345", concatenateTable(prefixTable));
    }

    @Test
    void testPrefixTableGeneration0BasedIndex() {
        String keyword = "abababaab";
        int[] prefixTable = KMP.prefixTable0BasedIndex(keyword);

        // index are all 1 smaller than in the book, with no space at the start unused.
        Assertions.assertEquals("-1-10123401", concatenateTable(prefixTable));

        keyword = "aaaaaa";
        prefixTable = KMP.prefixTable0BasedIndex(keyword);
        // same as in the book, with an additional space at the front that is untouched.
        Assertions.assertEquals("-101234", concatenateTable(prefixTable));
    }

    /**
     * concatenates the result into a string for easy output and comparison.
     *
     * @param table
     * @return
     */
    String concatenateTable(int[] table) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < table.length; i++) {
            sb.append(table[i]);
        }
        return sb.toString();
    }
}