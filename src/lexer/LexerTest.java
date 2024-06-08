package lexer;

import org.junit.jupiter.api.*;

import java.io.*;

public class LexerTest {
    private static Lexer lexer;

    @BeforeAll
    static void setup() {
        lexer = new Lexer();
    }

    @BeforeEach
    void reset() {
        lexer.resetAndClearData();
    }

    @Test
    void testScanNum() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("   12     1234567890".getBytes());
        System.setIn(inputStream);
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
        ByteArrayInputStream inputStream = new ByteArrayInputStream("    ".getBytes());
        System.setIn(inputStream);

        Token t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);

        t = lexer.scan();
        Assertions.assertEquals(Tag.EOF, t.tag);
    }

    @Test
    void testScanIdentifier() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("var bob car".getBytes());
        System.setIn(inputStream);

        Token t = lexer.scan();
        Token var = t;
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
        ByteArrayInputStream inputStream = new ByteArrayInputStream("var   var".getBytes());
        System.setIn(inputStream);
        Token var1 = lexer.scan();
        Token var2 = lexer.scan();
        Assertions.assertEquals(var1, var2);
    }

    @Test
    void testScanReservedWords() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("true false".getBytes());
        System.setIn(inputStream);

        Token t = lexer.scan();
        Assertions.assertInstanceOf(Word.class, t);
        Assertions.assertEquals(((Word) t).tag, Tag.TRUE);
        Assertions.assertEquals(((Word) t).lexeme, "true");

        t = lexer.scan();
        Assertions.assertInstanceOf(Word.class, t);
        Assertions.assertEquals(((Word) t).tag, Tag.FALSE);
        Assertions.assertEquals(((Word) t).lexeme, "false");
    }

    @Test
    void testScanDiffTokens() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("var 12 true".getBytes());
        System.setIn(inputStream);
        Token id = lexer.scan();
        Token num = lexer.scan();
        Token reserved = lexer.scan();

        Assertions.assertEquals(id.tag, Tag.ID);
        Assertions.assertEquals("var", ((Word) id).lexeme);

        Assertions.assertEquals(12, ((Num) num).value);

        Assertions.assertEquals(((Word) reserved).tag, Tag.TRUE);
        Assertions.assertEquals(((Word) reserved).lexeme, "true");
    }

    @Test
    void testOperators() throws IOException {
        char[] chars = {'+', '*', '-', '/'};
        ByteArrayInputStream inputStream = new ByteArrayInputStream("+ * - /".getBytes());
        System.setIn(inputStream);
        Token t;
        for (char c : chars) {
            t = lexer.scan();
            Assertions.assertEquals(t.tag, (int) c);
        }
    }

    @Test
    void testScanArithmetic() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("1 +2 - 30   / 2".getBytes());
        System.setIn(inputStream);

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
}
