package utils.trie;

public class PrefixTrie {
    public Node root;

    public PrefixTrie() {
        this.root = new Node('\0');
        root.isRoot = true;
    }

    public void insert(String s) {
        Node curr = root;
        for (char c : s.toCharArray()) {
            if (curr.hasChild(c))
                curr = curr.getChild(c);
            else
                curr = curr.createChild(c);
        }
        curr.lexeme = s;
        curr.isEnd = true;
    }

    public boolean hasPrefix(String s) {
        Node curr = root;
        for (char c : s.toCharArray()) {
            if (curr.hasChild(c))
                curr = curr.getChild(c);
            else
                return false;

        }
        return true;
    }


    public boolean hasWord(String s) {
        Node curr = root;
        for (char c : s.toCharArray()) {
            if (curr.hasChild(c))
                curr = curr.getChild(c);
            else
                return false;
        }
        return curr.isEnd;
    }
}
