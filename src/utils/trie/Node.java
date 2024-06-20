package utils.trie;

import utils.trie.arrStrategy.ArrStrategy;
import utils.trie.arrStrategy.LowAlphaArrStrategy;

import java.util.Iterator;

public class Node {
    public boolean isRoot;
    public boolean isEnd;
    public String lexeme;
    private Node[] nodes;
    private final ArrStrategy STRATEGY;

    // the node that is the longest proper prefix that is also a suffix
    public int c;
    public Node suffixLink;

    public Node(char c) {
        this.STRATEGY = new LowAlphaArrStrategy();
        this.nodes = new Node[STRATEGY.size()];
        this.isEnd = false;
        this.isRoot = false;
        this.c = c;
    }

    public int getSize() {
        return STRATEGY.size();
    }

    public Node getChild(int c) {
        assert STRATEGY.isValid(c) : String.format("%s is not valid character", c);
        int idx = STRATEGY.index(c);
        return nodes[idx];
    }

    public boolean hasChild(int c) {
        assert STRATEGY.isValid(c) : String.format("%s is not valid character", c);
        int idx = STRATEGY.index(c);
        return nodes[idx] != null;
    }

    public Node createChild(int c) {
        assert STRATEGY.isValid(c) : String.format("%s is not valid character", c);
        assert !hasChild(c) : "should be called when it doesnt have the child for c";

        int idx = STRATEGY.index(c);
        Node node = new Node((char) c);
        nodes[idx] = node;
        return node;
    }

    public Iterator<Node> getIterator() {
        return new NodeIterator(nodes);
    }

}
