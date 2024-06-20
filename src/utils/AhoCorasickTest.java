package utils;

import org.junit.jupiter.api.Test;

class AhoCorasickTest {
    @Test
    void findKeywords() {
        AhoCorasick alg = new AhoCorasick();
        String[] strings = {"he", "she", "his", "hers"};
        alg.constructTrie(strings);
        alg.constructSuffixLink();

        alg.findKeywords("hewashersheys");
    }
}