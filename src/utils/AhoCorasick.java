package utils;

import utils.trie.Node;
import utils.trie.PrefixTrie;

import java.util.Iterator;

public class AhoCorasick {
    private PrefixTrie trie;

    public AhoCorasick() {
        this.trie = new PrefixTrie();
    }

    public void constructTrie(String[] keywords) {
        for (String s : keywords)
            trie.insert(s);
    }

    /**
     * f(s) for children of root have been calculated.
     */
    public void constructSuffixLink() {
        Node root = trie.root;
        Iterator<Node> rootChildren = root.getIterator();
        while (rootChildren.hasNext()) {
            Node child = rootChildren.next();
            child.suffixLink = root;
        }
        constructSuffixLinkAux(root);
    }

    private void constructSuffixLinkAux(Node curr) {
        Iterator<Node> children = curr.getIterator();
        while (children.hasNext()) {
            Node child = children.next();
            int nextChar = child.c;
            // calculate f(s) for this children, unless it's a child of root(which is already calculated.)
            if (!curr.isRoot) {
                Node t = curr.suffixLink;
                while (!t.isRoot && !t.hasChild(nextChar))
                    t = t.suffixLink;

                if (t.hasChild(nextChar)) {
                    t = t.getChild(nextChar);
                }
                child.suffixLink = t;
            }
            constructSuffixLinkAux(child);
        }
    }

    public void findKeywords(String input) {
        int n = input.length();
        Node t = trie.root;

        for (int i = 0; i < n; i++) {
            char nextChar = input.charAt(i);
            while (!t.isRoot && !t.hasChild(nextChar))
                t = t.suffixLink;

            if (t.hasChild(nextChar)) {
                t = t.getChild(nextChar);
            }

            if (t.isEnd) {
                String lexeme = t.lexeme;
                String out = String.format("%s appears in input[%d, %d].", lexeme, i - (lexeme.length() - 1), i);
                System.out.println(out);
            }
        }
    }
}
