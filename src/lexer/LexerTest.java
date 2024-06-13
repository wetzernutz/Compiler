package lexer;

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
        String input = "12   1234567890";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertInstanceOf(Num.class, t);
        Assertions.assertEquals(12, ((Num) t).value);

        t = lexer.scan();
        Assertions.assertInstanceOf(Num.class, t);
        Assertions.assertEquals(1234567890, ((Num) t).value);

        t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);
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
        Assertions.assertEquals(Tag.TRUE, t.tag);
        Assertions.assertEquals("true", ((Word) t).lexeme);

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

        Assertions.assertEquals(Tag.TRUE, reserved.tag);
        Assertions.assertEquals("true", ((Word) reserved).lexeme);
    }

    @Test
    void testOperators() throws IOException {
        char[] chars = {'+', '*', '-', '/'};
        String input = "+ * - /";
        setInput(input);

        Token t;
        for (char c : chars) {
            t = lexer.scan();
            Assertions.assertEquals(c, t.tag);
        }
    }

    @Test
    void testScanArithmetic() throws IOException {
        String input = "1 +2 - 30   / 2";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertInstanceOf(Num.class, t);
        Assertions.assertEquals(1, ((Num) t).value);

        t = lexer.scan();
        Assertions.assertEquals('+', t.tag);

        t = lexer.scan();
        Assertions.assertInstanceOf(Num.class, t);
        Assertions.assertEquals(2, ((Num) t).value);

        t = lexer.scan();
        Assertions.assertEquals('-', t.tag);

        t = lexer.scan();
        Assertions.assertInstanceOf(Num.class, t);
        Assertions.assertEquals(30, ((Num) t).value);

        t = lexer.scan();
        Assertions.assertEquals('/', t.tag);

        t = lexer.scan();
        Assertions.assertInstanceOf(Num.class, t);
        Assertions.assertEquals(2, ((Num) t).value);

        t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);
    }

    @Test
    void testIgnoreInlineCommentAndEOF() throws IOException {
        String input = "//this is a comment";
        setInput(input);

        // the comment is ignored and EOF is reached.
        Token t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);
    }

    @Test
    void testIgnoreInlineCommentAndReadNextToken() throws IOException {
        String input = "//comments ... \n 12";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(Tag.NUM, t.tag);
        Assertions.assertEquals(12, ((Num) t).value);
    }

    @Test
    void testIgnoreMultilineCommentAndEOF() throws IOException {
        String input = "/* line1 \n line2 \n */";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);
    }

    @Test
    void testIgnoreMultilineCommentAndReadNextToken() throws IOException {
        String input = "/* beep bop \n ... \n\n   */\n bob";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(Tag.ID, t.tag);
        Assertions.assertEquals("bob", ((Word) t).lexeme);
    }

    @Test
    void testIgnoreMultilineAndInlineComment() throws IOException {
        String input = "    /*multiline-comments ... \n line1 \n line2 \n */\n //inline-comments \n 12";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(Tag.NUM, t.tag);
        Assertions.assertEquals(12, ((Num) t).value);
    }

    @Test
    void testRelationalOperatorsLtLe() throws IOException {
        // < > >= <= != ==
        String input = " < <= ";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals('<', t.tag);

        t = lexer.scan();
        Assertions.assertEquals(Tag.LE, t.tag);
        Assertions.assertEquals("<=", ((Word) t).lexeme);

        t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);
    }

    @Test
    void testRelationalOperatorsGtGe() throws IOException {
        String input = " > >= ";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals('>', t.tag);

        t = lexer.scan();
        Assertions.assertEquals(Tag.GE, t.tag);
        Assertions.assertEquals(">=", ((Word) t).lexeme);

        t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);
    }

    @Test
    void testRelationalOperatorsAssignEq() throws IOException {
        String input = " = == ";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals('=', t.tag);

        t = lexer.scan();
        Assertions.assertEquals(Tag.EQ, t.tag);
        Assertions.assertEquals("==", ((Word) t).lexeme);

        t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);
    }

    @Test
    void testRelationalOperatorsExclamationNe() throws IOException {
        String input = " ! != ";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals('!', t.tag);

        t = lexer.scan();
        Assertions.assertEquals(Tag.NE, t.tag);
        Assertions.assertEquals("!=", ((Word) t).lexeme);

        t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);
    }

    @Test
    void testRelationalOperatorsNeNum() throws IOException {
        String input = " 1!=12 ";
        setInput(input);

        Token t = lexer.scan();

        Assertions.assertEquals(Tag.NUM, t.tag);
        Assertions.assertEquals(1, ((Num) t).value);

        t = lexer.scan();
        Assertions.assertEquals(Tag.NE, t.tag);
        Assertions.assertEquals("!=", ((Word) t).lexeme);

        t = lexer.scan();
        Assertions.assertEquals(Tag.NUM, t.tag);
        Assertions.assertEquals(12, ((Num) t).value);
    }

    @Test
    void testRelationalOperatorsEqNum() throws IOException {
        String input = " 1=12 ";
        setInput(input);

        Token t = lexer.scan();

        Assertions.assertEquals(Tag.NUM, t.tag);
        Assertions.assertEquals(1, ((Num) t).value);

        t = lexer.scan();
        Assertions.assertEquals('=', t.tag);

        t = lexer.scan();
        Assertions.assertEquals(Tag.NUM, t.tag);
        Assertions.assertEquals(12, ((Num) t).value);
    }

    @Test
    void testFloatingLeftOnly() throws IOException {
        String input = " 1. ";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(Tag.REAL, t.tag);
        Assertions.assertEquals(1.0, ((Real) t).value);
    }

    @Test
    void testFloatingRightOnly() throws IOException {
        String input = " .21 ";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(Tag.REAL, t.tag);
        float a = 0.21f;
        float b = ((Real) t).value;
        float precisionRange = 1e-4F;

        Assertions.assertTrue(Math.abs(a - b) < precisionRange);
    }

    @Test
    void testFloatSimple() throws IOException {
        String input = " 22.213";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(Tag.REAL, t.tag);
        float a = 22.213f;
        float b = ((Real) t).value;
        float precisionRange = 1e-4F;
        Assertions.assertTrue(Math.abs(a - b) < precisionRange);
    }

    @Test
    void testFloating() throws IOException {
        String input = " 132.21+1/.21";
        setInput(input);

        Token t = lexer.scan();
        Assertions.assertEquals(Tag.REAL, t.tag);
        float a = 132.21f;
        float b = ((Real) t).value;
        float precisionRange = 1e-4F;
        Assertions.assertTrue(Math.abs(a - b) < precisionRange);

        t = lexer.scan();
        Assertions.assertEquals('+', t.tag);


        t = lexer.scan();
        Assertions.assertEquals(Tag.NUM, t.tag);
        Assertions.assertEquals(1, ((Num) t).value);

        t = lexer.scan();
        Assertions.assertEquals('/', t.tag);

        t = lexer.scan();
        a = .21f;
        b = ((Real) t).value;
        Assertions.assertTrue(Math.abs(a - b) < precisionRange);
    }

    @Test
    void testLineNumber() throws IOException {
        String input = "\n 2 \n\n 4 /* \n\n* */ 6 // bob \n 7";
        setInput(input);

        Assertions.assertEquals(1, lexer.line);
        lexer.scan();
        Assertions.assertEquals(2, lexer.line);
        lexer.scan();
        Assertions.assertEquals(4, lexer.line);
        Token t = lexer.scan();
        Assertions.assertEquals(6, lexer.line);
        lexer.scan();
        Assertions.assertEquals(7, lexer.line);
    }

}
