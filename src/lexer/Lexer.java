package lexer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Hashtable;

/**
 * A lexical analyzer groups characters into lexemes and produces
 * output {@link Token}s representing these lexemes.
 * The task of a lexical analyzer is to read a stream of characters making
 * up a program and output a token stream.
 * <p>Conceptually, given the following sequence of characters:
 * <pre>{@code
 * "position = initial + rate * 60"
 * }</pre>
 * <p>
 * lexemes grouped as the following, with <<i>token-name</i>, <i>attribute-value</i>> denoting the Tokens:
 * <blockquote><pre>
 * <id,1> <'='> <id,2> <'+'> <id,3> <'*'> <60>
 * </pre></blockquote>
 * With the new additions to the Symbol table as:
 * <blockquote><pre>
 * 1 | position | .. |
 * 2 | initial  | .. |
 * 3 | rate     | .. |
 * . | .        | .. |
 * . | .        | .. |
 * </pre></blockquote>
 * </p>
 * <p>Implementation:
 * <ul>
 * <li>peek is implemented as an int(ascii decimal representation), -1 denotes the EOF.</li>
 * <li>Identifiers(with tag as {@link Tag#ID}) and reserved words(with tag as {@link Tag#FALSE} etc) are implemented
 * as a {@link Word Word(int tag, String lexeme)}
 * class which extends this Token class. </li>
 * <li>The String table as a Hashtable in {@link Lexer} maps the String lexeme itself onto their corresponding Tokens,
 * which are then used as reference when another instance of the lexeme is encountered.
 * <li>Numbers are implemented as a {@link Num Num(int value)}</li>
 * class which extends this Token class with the {@link Tag#NUM}.</li>
 * <li>Operators are implemented as a {@link Token Token(int tag)} with the tag as the
 * character itself. The character is stored as a ascii value in the range [0,255],
 * which is also why the int values set to {@link Tag}s for the reserved words are always bigger than 255. </li>
 * </ul>
 * </p>
 * <p>
 * This lexical analyzer is tasked to insulate the parser from the lexeme representation of tokens.
 *
 * <p>Example <b>parser</b> syntax-directed translation scheme without using a lexer, this translation
 * translates in-fix notation into postfix notation. This translation also hasn't undergone
 * left-recursion elimination.
 * <blockquote><pre>
 * expr -> expr + term {print('+')}
 *      |  expr - term {print('-')}
 *      |  term
 *
 * term -> term * factor {print('*')}
 *      |  term / factor {print('/')}
 *      |  factor
 *
 * factor -> digit
 *        |  char
 * digit -> 0 {print{'0'}} | 1 {print('1')} ...
 * char -> 'a' {print{''}}
 * </pre></blockquote>
 * </p>
 * <p>The <b>parser</b> translation scheme using a lexer, the digits are now abstracted. <br>
 * Instead of handling a single digit multiple digits forming a number is now implemented. <br>
 * Instead of handling a single char a String can now be implemented easily. <br>
 * Essentially, the responsibility of grouping the chars into strings etc are now handled by the Lexer, and
 * the parser now only handles structure translation.
 * <blockquote><pre>
 * expr -> expr + term {print('+')}
 *      |  expr - term {print('-')}
 *      |  term
 *
 * term -> term * factor {print('*')}
 *      |  term / factor {print('/')}
 *      |  factor
 *
 * factor -> num {print('num.value')}
 *        |  id  {print('id.lexeme')}
 * </pre></blockquote>
 * </p>
 */
public class Lexer {
    public int line;
    private int peek;
    private Hashtable<String, Token> words;

    /**
     * Resets the lexer and clears the historical data. Will also initialise all reserve words into
     * the String Table.<br>
     * Warning: This includes <b>clears the Words stored in the String Table</b>, call with caution.
     */
    public void resetAndClearData() {
        this.line = 1;
        this.peek = ' ';
        this.words = new Hashtable<>();
        reserve(new Word(Tag.TRUE, "true"));
        reserve(new Word(Tag.FALSE, "false"));
    }

    public Lexer() {
        resetAndClearData();
    }

    /**
     * Add a reserve word into the String Table.
     *
     * @param t the {@link Word} token to store as the reserve word.
     */
    void reserve(Word t) {
        words.put(t.lexeme, t);
    }

    /**
     * Scans and returns the next token.
     *
     * <p>Things to note:
     * <ul>
     * <li>Leading or Trailing Whitespace is ignored.</li>
     * <li>Returns a special {@link Token} with tag as {@link Tag#EOF} no tokens left to read.</li>
     * </ul></p>
     *
     * @return the next Token representing the next lexeme.
     * A special Token with {@link Tag#EOF} is returned when end of file is reached.
     * @throws IOException if an I/ O error occurs.
     * @throws Error       If an Undefined Character is read.
     */
    public Token scan() throws IOException, Error {
        skipWhiteSpace();
        if (Character.isDigit(peek)) {
            return scanNum();
        }
        if (Character.isLetter(peek)) {
            return scanWord();
        }
        if (peek >= 0 && peek <= 255) {
            return scanOperator();
        }
        if (peek == -1) {
            peek = ' ';
            return new Token(Tag.EOF);
        }
        throw new Error(String.format("Undefined Character: '%s'", peek));
    }

    /**
     * Moves the peek forward continuously when encountering whitespace.
     * Stops at the first occurrence of a character that is not:
     * <ul>
     * <li>{@code '\t'}</li>
     * <li>{@code ' '}</li>
     * <li>{@code '\n'}</li>, also updates line count.
     * </ul>
     *
     * @throws IOException if an I/ O error occurs.
     */
    private void skipWhiteSpace() throws IOException {
        for (; ; peek = System.in.read()) {
            if (peek == (int) ' ' || peek == (int) '\t') continue;
            else if (peek == (int) '\n') line += 1;
            else break;
        }
    }

    /**
     * Continuously reads digits from the standard input stream to form a number.
     * Only recognizes positive integers, signs are not supported as of now.
     *
     * @return the number scanned.
     * @throws IOException if an I/ O error occurs.
     */
    private @NotNull Num scanNum() throws IOException {
        int v = 0;
        do {
            v = v * 10 + Character.digit(peek, 10);
            peek = System.in.read();
        } while (Character.isDigit(peek));
        return new Num(v);
    }

    /**
     * Continuously reads letters from the standard input stream to form a word.
     * Stops at the first encounter of a non-alphabet.
     *
     * @return the Word representing the lexeme.
     * @throws IOException if an I/ O error occurs.
     */
    private @NotNull Word scanWord() throws IOException {
        StringBuilder b = new StringBuilder();
        do {
            b.append((char) peek);
            peek = System.in.read();
        } while (Character.isLetter(peek));
        String s = b.toString();

        return getReservedWordOrIdentifier(s);
    }

    /**
     * Retrieves the Word from the String table.
     * A new entry is added to the String table if the String Table does not contain it.
     *
     * @param s the lexeme to retrieve.
     * @return the Word representing the lexeme.
     */
    private @NotNull Word getReservedWordOrIdentifier(String s) {
        Word w = (Word) words.get(s);
        if (w != null)
            return w;

        w = new Word(Tag.ID, s);
        words.put(s, w);
        return w;
    }

    /**
     * Scans the next character and simply returns the Token representing that character.
     * The character itself is used as the tag of the {@link Token}. <br>
     * <p>Usage Example:
     * the token <*> is represented as a Token((int)'*');
     * </p>
     *
     * @return the Token representing the character.
     * @throws IOException if an I/ O error occurs.
     */
    private @NotNull Token scanOperator() throws IOException {
        Token t = new Token(peek);
        peek = System.in.read();
        return t;
    }
}
