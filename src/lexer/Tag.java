package lexer;

/**
 * A class to store the integer values of each tag.
 * Used as the <i>token-name</i> in a token <<i>token-name</i>,<i>attribute-value</i>> which
 * differentiates the type of Token that it is.
 * <p>Example Usage:
 * <pre>{@code
 * // reserved word for "true"
 * new Word(Tag.TRUE, "true");
 * // identifier for the variable "temp"
 * new Word(Tag.ID, "temp");
 * // number
 * new Num(Tag.NUM, 12);
 * // eof
 * new Token(Tag.EOF);
 * }</pre>
 * </p>
 *
 * @see Token
 * @see Word
 * @see Num
 * @see Lexer
 */
public class Tag {
    public final static int EOF = -1, NUM = 256, ID = 257, TRUE = 258, FALSE = 259;
}
