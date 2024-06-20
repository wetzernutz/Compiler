package utils.trie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

class NodeTest {

    @Test
    void testNode() {
        Node node = new Node('\0');

        String input = "hagfza";
        String expected = "afghz";
        for (char c : "hagfza".toCharArray()) {
            if (!node.hasChild(c))
                node.createChild(c);
            Node child = node.getChild(c);
            child.lexeme = "" + c;
        }

        int i = 0;
        Iterator<Node> nodeIterator = node.getIterator();
        while (nodeIterator.hasNext()) {
            Assertions.assertTrue(node.hasChild(expected.charAt(i)));
            Assertions.assertEquals(expected.charAt(i) + "", nodeIterator.next().lexeme);
            i++;
        }
    }

}