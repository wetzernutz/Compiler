package lexer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Hashtable;

/**
 * A lexical analyzer groups characters into lexemes and produces
 * output {@link Token}s representing these lexemes.
 *
 *
 */
public class Lexer {
    public int line = 1;
    private char peek = ' ';
    private Hashtable<String, Token> words = new Hashtable();

    public Lexer() {
        reserve( new Word(Tag.TRUE, "true") );
        reserve( new Word(Tag.FALSE, "false") );
    }

    void reserve(Word t) {
        words.put(t.lexeme, t);
    }

    public Token scan() throws IOException {
        skipWhiteSpace();
        if (Character.isDigit(peek)) {
            return scanNum();
        }
        if (Character.isLetter(peek)) {
            return scanWord();
        }
        return scanOperator();
    }

    private @NotNull Token scanOperator() {
        Token t = new Token(peek);
        peek = ' ';
        return t;
    }

    private @NotNull Word scanWord() throws IOException {
        StringBuilder b = new StringBuilder();
        do {
            b.append(peek);
            peek = (char) System.in.read();
        } while (Character.isLetter(peek));
        String s = b.toString();

        return getReservedWordOrIdentifier(s);
    }

    private @NotNull Word getReservedWordOrIdentifier(String s) {
        Word w = (Word)words.get(s);
        if (w != null) return w;

        w = new Word(Tag.ID, s);
        words.put(s, w);
        return w;
    }

    private @NotNull Num scanNum() {
        int v = 0;
        do {
            v = v*10 + Character.digit(peek, 10);
        } while (Character.isDigit(peek));
        return new Num(v);
    }

    private void skipWhiteSpace() throws IOException {
        for ( ; ; peek = (char) System.in.read() ) {
           if (peek == ' ' || peek=='\t')
               continue;
           else if (peek == '\n')
               line += 1;
           else break;
        }
    }















}
