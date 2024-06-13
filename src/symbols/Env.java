package symbols;

import java.util.Hashtable;

public class Env {
    private Hashtable<String, Symbol> symbolTable;
    public Env prev;

    public Env(Env prev) {
        this.prev = prev;
        this.symbolTable = new Hashtable<>();
    }

    public void put(String name, Symbol symbol) {
        symbolTable.put(name, symbol);
    }

    public Symbol get(String name) {
        for (Env env = this; env != null; env = env.prev) {
            Symbol symbol = env.symbolTable.get(name);
            if (symbol != null) return symbol;
        }
        return null;
    }
}
