package lexer;

/**
 * A class representing the Number {@link Token}.
 */
public class Num extends Token{
    public final int value;
    public Num(int value) {
        super(Tag.NUM);
        this.value = value;
    }
}
