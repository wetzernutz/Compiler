package utils.trie;

import java.util.Iterator;

public class NodeIterator implements Iterator<Node> {
    private Node[] nodes;
    private int idx;

    public NodeIterator(Node[] nodes) {
        this.nodes = nodes;
        this.idx = 0;
        if (idx < nodes.length && nodes[idx] == null)
            goToNextNode();
    }

    @Override
    public boolean hasNext() {
        return idx < nodes.length && nodes[idx] != null;
    }

    @Override
    public Node next() {
        Node node = nodes[idx];
        goToNextNode();
        return node;
    }

    private void goToNextNode() {
        do {
            idx++;
        } while (idx < nodes.length && nodes[idx] == null);
    }
}
