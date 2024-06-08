package lexer;

/**
 * A Class representing a token with the tag attribute.
 * <p>Concept: A Token consists of 2 components,
 * a token name and an attribute value.<br>
 * Given the following sequence of characters:
 * <pre>{@code
 * "position = initial + rate * 60"
 * }</pre>
 *
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
 *
 * <p>Implementation:
 * <ul>
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
 *</p>
 *
 * @see Tag
 * @see Num
 * @see Word
 * @see Lexer
 */
public class Token {
    public final int tag;
    public Token(int tag) {
        this.tag = tag;
    }
}
