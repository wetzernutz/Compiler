package lexer;

/**
 * A class representing the Word {@link Token} used for reserved words and
 * identifiers.
 * <p>Example Usage:
 * <pre>{@code
 * // reserved word for "true"
 * new Word(Tag.TRUE, "true");
 * // identifier for the variable "temp"
 * new Word(Tag.ID, "temp");
 * }</pre>
 * </p>
 */
public class Word extends Token {
    public final String lexeme;
    public static Word
            ge = new Word(Tag.GE, ">="), le = new Word(Tag.LE, "<="),
            eq = new Word(Tag.EQ, "=="), ne = new Word(Tag.NE, "!=");

    public Word(int tag, String lexeme) {
        super(tag);
        this.lexeme = lexeme;
    }
}
