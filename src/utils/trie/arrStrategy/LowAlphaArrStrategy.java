package utils.trie.arrStrategy;


public class LowAlphaArrStrategy implements ArrStrategy {
    @Override
    public int size() {
        return index('z') + 1;
    }

    @Override
    public int index(int c) {
        assert isValid(c) : "This storage only stores lower case alphabets from a-z.";
        return c - (int) 'a';
    }

    @Override
    public boolean isValid(int c) {
        return c >= (int) 'a' && c <= (int) 'z';
    }
}
