package utils.trie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class PrefixTrieTest {

    @Test
    void testInsert() {
        PrefixTrie trie = new PrefixTrie();
        String[] words = {"boba", "bobie", "abc", "bsda"};
        for (String word : words)
            trie.insert(word);
        for (String word : words)
            Assertions.assertTrue(trie.hasWord(word));
        Assertions.assertFalse(trie.hasWord("bob"));
    }

    @Test
    void testInsert2() {
        PrefixTrie trie = new PrefixTrie();
        String[] words = {"he", "she", "his", "hers"};
        for (String word : words)
            trie.insert(word);
        for (String word : words)
            Assertions.assertTrue(trie.hasWord(word));
        Assertions.assertFalse(trie.hasWord("bob"));
    }

    @Test
    void hasPrefix() {
        PrefixTrie trie = new PrefixTrie();
        String input = "boba";
        trie.insert(input);

        String[] prefixes = {"boba", "bob", "bo", "b"};
        for (String prefix : prefixes)
            Assertions.assertTrue(trie.hasPrefix(prefix));
    }
}