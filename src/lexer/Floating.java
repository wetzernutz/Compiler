package lexer;

public class Floating extends Token {
    public final float value;

    public Floating(float value) {
        super(Tag.REAL);
        this.value = value;
    }
}
