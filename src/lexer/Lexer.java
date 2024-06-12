package lexer;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private BufferedInputStream bufferedInputStream;

    /**
     * Creates a Lexical Analyzer that reads from an {@link InputStream} like
     * {@link System#in}.
     *
     * @param inputStream the input stream to be read.
     */
    public Lexer(InputStream inputStream) {
        this.line = 1;
        this.peek = ' ';
        this.words = new Hashtable<>();
        setInputStream(inputStream);
        reserve(new Word(Tag.TRUE, "true"));
        reserve(new Word(Tag.FALSE, "false"));
    }

    /**
     * Setter injection to set and replace the {@link InputStream}.
     * Can be used to change the input stream into a custom stream containing
     * TestInput or FileData etc.
     *
     * @param inputStream the input stream to be scanned by the lexical analyzer.
     */
    public void setInputStream(InputStream inputStream) {
        this.bufferedInputStream = new BufferedInputStream(inputStream);
    }

    /**
     * Reads the next byte from the {@link BufferedInputStream}.
     * <p>
     * Note: this method should be used instead of {@link System#in}'s {@code read} method.
     * </p>
     *
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private void readCh() throws IOException {
        peek = bufferedInputStream.read();
    }

    /**
     * Reads the next char and compares the char read with the char provided.
     * Returns True and sets peek to blank when they match, False otherwise.
     * Used to help identify composite tokens like >= and !=.
     * <p>
     * Setting peek to blank when matching is when crucial since the composite token
     * should be processed using the boolean result,
     * and peek should be set to the next char to be processed(blank will be ignored on the next call).
     * </p>
     * <p>
     * Note:
     * <p>
     * <li>peek is set to blank instead of reading next char just in case peek is eof.</li>
     * <li>the char is skipped when case fails, must process fail result using the result of this method
     * (like case in scan).
     * </li>
     * </p>
     *
     * @param c character to expect and compare
     * @return result of matching
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private boolean readCh(char c) throws IOException {
        readCh();
        if (peek != c) return false;
        peek = ' ';
        return true;
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
     * Invariant: all token detection will leave with peek as the next char to be processed,
     * or until eof is detected.
     *
     * @return the next Token representing the next lexeme.
     * A special Token with {@link Tag#EOF} is returned when end of file is reached.
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     * @throws Error       If an Unexpected peek character is read that isn't within the (0~255) range, and not eof(-1).
     */
    public Token scan() throws IOException, Error {
        skipWhiteSpace();
        ignoreComments();

        if (Character.isLetter(peek)) {
            return scanWord();
        }

        if (Character.isDigit(peek) || peek == '.') {
            return scanNum();
        }

        return switch (peek) {
            case ('/') -> {
                readCh();
                yield new Token('/');
            }
            case ('>') -> readCh('=') ? Word.ge : new Token('>');
            case ('<') -> readCh('=') ? Word.le : new Token('<');
            case ('=') -> readCh('=') ? Word.eq : new Token('=');
            case ('!') -> readCh('=') ? Word.ne : new Token('!');
            case (-1) -> {
                peek = ' ';
                yield new Token(Tag.EOF);
            }
            default -> scanChar();
        };
    }

    /**
     * Ignores all consecutive inline or multiline comments.
     * <p>
     * Restores the bufferedInputStream and peak if
     * there are no comments detected anymore (leaves at the state when peek is '/').
     * </p>
     * <p> Implementation of marking and reset and restoring state is chosen over returning a division Token
     * on default is for separation of concerns, better modularity and such that the ignoreComments function
     * follows the SRP principle.
     * </p>
     *
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private void ignoreComments() throws IOException {
        outerLoop:
        while (peek == '/') {
            bufferedInputStream.mark(1);
            readCh();
            switch (peek) {
                case ('/'):
                    skipUntilNewLine();
                    skipWhiteSpace();
                    break;
                case ('*'):
                    skipUntilEndMultiComment();
                    skipWhiteSpace();
                    break;
                default:
                    bufferedInputStream.reset();
                    peek = '/';
                    break outerLoop;
            }
        }
    }

    /**
     * Skips all characters until a white space is detected.
     * <ul>Valid white space are:
     * <li>{@code ' '} blank space</li>
     * <li>{@code '\t'} tabs</li>
     * <li>{@code '\n'} newline</li>
     * </ul>
     *
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private void skipWhiteSpace() throws IOException {
        for (; ; readCh()) {
            if (peek == ' ' || peek == '\t') continue;
            else if (peek == '\n') line += 1;
            else break;
        }
    }

    /**
     * Skips until a newline or eof is detected.
     *
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private void skipUntilNewLine() throws IOException {
        while (peek != '\n' && peek != -1) {
            readCh();
        }
    }

    /**
     * Skips any characters until the end of a multiline comment is detected o eof.
     * Note: case ('\n') also runs the default case.
     *
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private void skipUntilEndMultiComment() throws IOException {
        outerLoop:
        while (peek != -1) {
            switch (peek) {
                case ('*'):
                    if (readCh('/')) break outerLoop;
                case ('\n'):
                    line += 1;
                default:
                    readCh();
            }
        }
    }

    /**
     * Continuously reads digits from the standard input stream to form a number.
     * Only recognizes positive integers, signs are not supported as of now.
     *
     * @return the number scanned, a Floating Point Number or Integer.
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private @NotNull Token scanNum() throws IOException {
        int n = 0;

        if (peek != '.') {
            do {
                n = n * 10 + Character.digit(peek, 10);
                readCh();
            } while (Character.isDigit(peek));
            if (peek != '.') return new Num(n);
        }

        readCh();
        float f = n;
        int d = 10;
        // initial test is crucial for left only floating point (12.)
        while (Character.isDigit(peek)) {
            f = f + ((float) Character.digit(peek, 10) / d);
            d *= 10;
            readCh();
        }
        return new Real(f);
    }

    /**
     * Continuously reads letters from the standard input stream to form a word.
     * Stops at the first encounter of a non-alphabet.
     *
     * @return the Word representing the lexeme.
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private @NotNull Word scanWord() throws IOException {
        StringBuilder b = new StringBuilder();
        do {
            b.append((char) peek);
            readCh();
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
     * Scans the next character that is not recognized as part of a predefined token and
     * simply returns a new Token representing that character.
     * The character itself is used as the tag of the {@link Token}. <br>
     * <p>Usage Example:
     * the token <*> is represented as a Token((int)'*');
     * </p>
     *
     * @return the Token representing the character.
     */
    private @NotNull Token scanChar() {
        Token t = new Token(peek);
        peek = ' ';
        return t;
    }
}
