package lexer;

import org.junit.Ignore;
import org.junit.jupiter.api.*;

import java.io.*;

public class LexerTest {
    private Lexer lexer;
    private static InputStream originalInputStream;

    @BeforeAll
    static void initialise() {
        originalInputStream = System.in;
    }

    @AfterAll
    static void restore() {
        System.setIn(originalInputStream);
    }

    @BeforeEach
    void setup() {
        lexer = new Lexer(System.in);
    }

    void setInput(String input) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(byteArrayInputStream);
        lexer.setInputStream(byteArrayInputStream);
    }

    @Test
    void testScanNum() throws IOException {
        String input = "   12     1234567890";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertInstanceOf(Num.class, t);
        Assertions.assertEquals(12, ((Num) t).value);

        t = lexer.scan();
        Assertions.assertInstanceOf(Num.class, t);
        Assertions.assertEquals(1234567890, ((Num) t).value);

        t = lexer.scan();
        Assertions.assertEquals(t.tag, Tag.EOF);
    }

    @Test
    void testScanEmpty() throws IOException {
        String input = "    ";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);

        t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);
    }

    @Test
    void testScanIdentifier() throws IOException {
        String input = "var bob car";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertInstanceOf(Word.class, t);
        Assertions.assertEquals(t.tag, Tag.ID);
        Assertions.assertEquals("var", ((Word) t).lexeme);

        t = lexer.scan();
        Assertions.assertInstanceOf(Word.class, t);
        Assertions.assertEquals(t.tag, Tag.ID);
        Assertions.assertEquals("bob", ((Word) t).lexeme);

        t = lexer.scan();
        Assertions.assertInstanceOf(Word.class, t);
        Assertions.assertEquals(t.tag, Tag.ID);
        Assertions.assertEquals("car", ((Word) t).lexeme);
    }

    @Test
    void testScanSameIdentifierTokenFromStringTable() throws IOException {
        String input = "var   var";
        setInput(input);

        Token var1 = lexer.scan();
        Token var2 = lexer.scan();
        Assertions.assertEquals(var1, var2);
    }

    @Test
    void testScanReservedWords() throws IOException {
        String input = "true false";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertInstanceOf(Word.class, t);
        Assertions.assertEquals(t.tag, Tag.TRUE);
        Assertions.assertEquals(((Word) t).lexeme, "true");

        t = lexer.scan();
        Assertions.assertInstanceOf(Word.class, t);
        Assertions.assertEquals(t.tag, Tag.FALSE);
        Assertions.assertEquals(((Word) t).lexeme, "false");
    }

    @Test
    void testScanDiffTokens() throws IOException {
        String input = "var 12 true";
        setInput(input);

        Token id = lexer.scan();
        Token num = lexer.scan();
        Token reserved = lexer.scan();

        Assertions.assertEquals(id.tag, Tag.ID);
        Assertions.assertEquals("var", ((Word) id).lexeme);

        Assertions.assertEquals(12, ((Num) num).value);

        Assertions.assertEquals(reserved.tag, Tag.TRUE);
        Assertions.assertEquals(((Word) reserved).lexeme, "true");
    }

    @Test
    void testOperators() throws IOException {
        char[] chars = {'+', '*', '-', '/'};
        String input = "+ * - /";
        setInput(input);

        Token t;
        for (char c : chars) {
            t = lexer.scan();
            Assertions.assertEquals(t.tag, c);
        }
    }

    @Test
    void testScanArithmetic() throws IOException {
        String input = "1 +2 - 30   / 2";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertInstanceOf(Num.class, t);
        Assertions.assertEquals(((Num) t).value, 1);

        t = lexer.scan();
        Assertions.assertEquals(t.tag, '+');

        t = lexer.scan();
        Assertions.assertInstanceOf(Num.class, t);
        Assertions.assertEquals(((Num) t).value, 2);

        t = lexer.scan();
        Assertions.assertEquals(t.tag, '-');

        t = lexer.scan();
        Assertions.assertInstanceOf(Num.class, t);
        Assertions.assertEquals(((Num) t).value, 30);

        t = lexer.scan();
        Assertions.assertEquals(t.tag, '/');

        t = lexer.scan();
        Assertions.assertInstanceOf(Num.class, t);
        Assertions.assertEquals(((Num) t).value, 2);

        t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);
    }

    @Test
    void testIgnoreInlineCommentAndEOF() throws IOException {
        String input = "//this is a comment";
        setInput(input);

        // the comment is ignored and EOF is reached.
        Token t = lexer.scan();
        Assertions.assertEquals(t.tag, Tag.EOF);
    }

    @Test
    void testIgnoreInlineCommentAndReadNextToken() throws IOException {
        String input = "//comments ... \n 12";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(t.tag, Tag.NUM);
        Assertions.assertEquals(((Num) t).value, 12);
    }

    @Test
    void testIgnoreMultilineCommentAndEOF() throws IOException {
        String input = "/* line1 \n line2 \n */";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(t.tag, Tag.EOF);
    }

    @Test
    void testIgnoreMultilineCommentAndReadNextToken() throws IOException {
        String input = "/* beep bop \n ... \n\n   */\n bob";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(t.tag, Tag.ID);
        Assertions.assertEquals(((Word) t).lexeme, "bob");
    }

    @Test
    void testIgnoreMultilineAndInlineComment() throws IOException {
        String input = "    /*multiline-comments ... \n line1 \n line2 \n */\n //inline-comments \n 12";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(Tag.NUM, t.tag);
        Assertions.assertEquals(12, ((Num) t).value);
    }
}
