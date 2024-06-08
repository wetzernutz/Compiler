package parser;

import org.junit.jupiter.api.*;

import java.io.*;

/**
 * This class implements unit tests for {@link Parser} using
 * <a href="https://junit.org/junit5/docs/current/api/">JUnit</a>.
 *
 * <p>Notable attributes in this class that
 * enable the unit tests to work are:</p>
 * <ul>
 * <li>
 * A byte array output stream that serves as a temporary buffer to capture the output data
 * produced by the Parser class during testing. This is used to manually set the input read by
 * {@link java.io.InputStream#read() System.in.read()}. The stream is reset before each test
 * to ensure it only contains data generated during the current test execution.
 * <pre>{@code
 *    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
 * }</pre>
 * </li>
 *
 * <li>
 * A reference to the original standard output stream {@link java.lang.System#out System.out}before it is redirected.
 * Used to restore the standard output to its original state after the tests have
 * been executed.
 * <pre>{@code
 *    PrintStream originalOut = System.out;
 * }</pre>
 * </li>
 *
 * <li>
 * A reference to the original standard input stream {@link java.lang.System#in System.in}before it is redirected.
 * Used to restore the standard output to its original state after the tests have
 * been executed.
 * <pre>{@code
 *   InputStream originalIn = System.in;
 * }</pre>
 * </li>
 * </ul>
 */
class ParserTest {
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    private static final InputStream originalIn = System.in;

    /**
     * Reassigns the "standard" output stream into a new {@link java.io.PrintStream} using our {@link ByteArrayOutputStream} that
     * we have created as the output data.
     * <pre>{@code
     *    System.setOut(new PrintStream(ByteArrayOutputStream outContent));
     * }</pre>
     * This is such that instead of fetching the result from the standard output stream and calling
     * {@link java.io.PrintStream#println()} (System.out is the standard output stream as type PrintStream)
     * as usual, it would use a Stream that has the data defined in our own custom {@link PrintStream}.
     * We would later use {@link PrintStream#toString() toString} to fetch and verify the output.
     *
     * @see java.io.PrintStream#toString()
     * @see java.io.PrintStream#println()
     */
    @BeforeAll
    static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    /**
     * Reassigns back the Standard input and output streams to normal after testing has completed.
     */
    @AfterAll
    static void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /**
     * Resets the data stored within the byteArray to clear the previous results.
     */
    @BeforeEach
    void setUp() {
        outContent.reset();
    }

    /**
     * Sets up the inputStream with a predefined input to mimic a user's input.
     * Used to mimic the input fetched by {@link java.io.InputStream#read() System.in.read()}.
     *
     * @param input the input that is expected to be 'read' from the user.
     */
    private void setInputStream(String input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
    }

    @org.junit.jupiter.api.Test
    void testExpr() throws IOException {
        setInputStream("1+2-4");
        Parser parser = new Parser();
        parser.expr();
        Assertions.assertEquals("12+4-", outContent.toString().trim());
    }

    @org.junit.jupiter.api.Test
    void testTerm() throws IOException {
        String input = "7";
        setInputStream(input);

        Parser parser = new Parser();
        parser.term();
        Assertions.assertEquals(input, outContent.toString().trim());
    }

    @Test
    void testTermInvalid() throws IOException {
        String input = "a";
        setInputStream(input);

        Parser parser = new Parser();
        Assertions.assertThrows(Error.class, parser::term);
    }


    @Test
    void testMatchSimple() throws IOException {
        String input = "78";
        setInputStream(input);

        Parser parser = new Parser();
        parser.match('7');
        parser.match('8');
        Assertions.assertEquals(-1, Parser.lookahead);
    }

    @Test
    void testMatchWhiteSpace() throws IOException {
        String input = " 7 8";
        setInputStream(input);

        Parser parser = new Parser();
        Assertions.assertThrows(Error.class, () -> parser.match('7'));
        for (char c : input.toCharArray()) {
            parser.match(c);
        }
        Assertions.assertEquals(-1, Parser.lookahead);
    }

}