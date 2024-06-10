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
    private int readAhead;
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
     * @return int the ascii decimal representation of the next char which ranges between [0,255] inclusive.
     * -1 is returned when EOF.
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private int readCh() throws IOException {
        return bufferedInputStream.read();
    }

    private boolean readCh(char c) throws IOException {
        peek = readCh();
        if (peek != c) return false;
        peek = ' ';
        return true;
    }

    /**
     * Marks the current position in this input stream.
     * A subsequent call to the reset method repositions this stream at
     * the last marked position so that subsequent reads re-read the same bytes.
     * <p>
     * The read limit argument tells this input stream to allow that many
     * bytes to be read before the mark position gets invalidated.
     * </p>
     *
     * @param readLimit the max amount of bytes that the read from the input stream before this mark gets invalidated.
     */
    private void markBuffer(int readLimit) {
        bufferedInputStream.mark(readLimit);
    }

    /**
     * Repositions this stream to the position at the time the mark method was last called on this input stream.
     * <p>
     * See the general contract of the {@link InputStream#reset() reset} method of {@link InputStream}.
     * </p>
     *
     * @throws IOException if this stream has not been marked or, if the mark has been invalidated,
     *                     or the stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private void resetBufferToMark() throws IOException {
        bufferedInputStream.reset();
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
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     * @throws Error       If an Undefined Character is read.
     */
    public Token scan() throws IOException, Error {
        skipIgnoredItems();
        if (Character.isDigit(peek)) {
            return scanNum();
        }
        if (Character.isLetter(peek)) {
            return scanWord();
        }
        
        switch (peek) {
            case ('>'):
                return readCh('=') ? Word.ge : new Token('>');
            case ('<'):
                return readCh('=') ? Word.le : new Token('<');
            case ('='):
                return readCh('=') ? Word.eq : new Token('=');
            case ('!'):
                return readCh('=') ? Word.ne : new Token('!');
        }

        if (peek >= 0 && peek <= 255) {
            return scanChar();
        }
        if (peek == -1) {
            peek = ' ';
            return new Token(Tag.EOF);
        }
        throw new Error(String.format("Undefined Character: '%s'", peek));
    }

    /**
     * Moves the peek forward continuously when encountering characters that should be ignored.
     * Skips all characters that are:
     * <ul>
     * <li>{@code '\t'}</li>
     * <li>{@code ' '}</li>
     * <li>{@code '\n'}</li>, also updates line count.
     * <li>inline-comments starting from //
     * <li>multiline comments starting from /* and ending with *{@literal /}
     * </ul>
     *
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private void skipIgnoredItems() throws IOException {
        for (; ; peek = readCh()) {
            if (peek == ' ' || peek == '\t') continue;
            else if (peek == '\n') line += 1;
            else if (peek == '/') {
                boolean isComment = detectAndSkipComments();
                if (!isComment) break;
            } else break;
        }
    }

    /**
     * Skips inline and multiline comments when peek suggests a possible comment.
     * <p>Used when peek is {@code '/'}, the next read ahead would determine what type of comments it is:
     * <ul>
     * <li>read ahead is {@code '/'}, is a inline comment {@code '//'}.</li>
     * <li>read ahead is {@code '*'}, is a multiline comment {@code '/*'}.</li>
     * <li>else it is <b>not</b> a comment. Returns False for the result of the skip comment execution.</li>
     * </ul>
     * </p>
     *
     * @return True if a comment is found and skipped. False if it is not a comment.
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private boolean detectAndSkipComments() throws IOException {
        assert peek == '/' : "Only called when peek might a multiline comment";

        markBuffer(1);
        readAhead = readCh();
        switch (readAhead) {
            case '/':
                skipUntilEndInlineComment();
                break;
            case '*':
                skipUntilEndMultiComment();
                break;
            default:
                resetBufferToMark();
                return false;
        }
        return true;
    }

    private void skipUntilNewLine() throws IOException {
        while (peek != -1 && peek != '\n') {
            peek = readCh();
        }
    }

    /**
     * Called in {@link #detectAndSkipComments()} to skip characters and ignore the rest of the multiline comment.
     *
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private void skipUntilEndMultiComment() throws IOException {
        assert peek == '/' && readAhead == '*' : "Only called when peek is a multiline comment";

        while (peek != -1) {
            if (peek == '*' && readCh() == '/') break;
            peek = readCh();
        }
    }

    /**
     * Called in {@link #detectAndSkipComments()} to skip characters and ignore the rest of the inline comment.
     *
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private void skipUntilEndInlineComment() throws IOException {
        assert peek == '/' && readAhead == '/' : "Only called when peek is a inline comment";

        while (peek != -1 && peek != '\n') {
            peek = readCh();
        }
    }

    /**
     * Continuously reads digits from the standard input stream to form a number.
     * Only recognizes positive integers, signs are not supported as of now.
     *
     * @return the number scanned.
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private @NotNull Num scanNum() throws IOException {
        int v = 0;
        do {
            v = v * 10 + Character.digit(peek, 10);
            peek = readCh();
        } while (Character.isDigit(peek));
        return new Num(v);
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
            peek = readCh();
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
     * @throws IOException if this input stream has been closed by invoking its close() method, or an I/ O error occurs.
     */
    private @NotNull Token scanChar() throws IOException {
        Token t = new Token(peek);
        peek = readCh();
        return t;
    }
}
