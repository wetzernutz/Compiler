package parser;

import java.io.*;

/**
 * A simple predictive parser to convert infix notation to postfix notation.
 * <p>
 * This parser uses the top-down method of syntax analysis called
 *  <a href="https://en.wikipedia.org/wiki/Top-down_parsing">top-down parsing</a>, where construction starts
 * from the root down to the leafs.     The parsing method chosen is the recursive descent parsing
 * method which uses a set of recursive procedures to process input.
 * <blockquote><pre>
 * Translation scheme:
 *
 * expr -> term rest
 *
 * rest -> + term { print('+') } rest
 *      |  - term { print('-') } rest
 *      |  ''
 *
 * term -> 0 { print('0') }
 *      |  1 { print('1') }
 *      ...
 *      |  9 { print('9') }
 * </pre></blockquote>
 *
 * <p>
 * This parser implements the simplest form of
 * <a href="https://en.wikipedia.org/wiki/Recursive_descent_parser">recursive-descent</a>
 * called predictive parsing,
 * where the first symbol in the input is always sufficient in determining which production
 * must be used. Hence, only a lookahead symbol that stores one char is used.
 * </p>
 *
 * Note:  White space is not skipped by this parser when matching.
 */
class Parser {
    static int lookahead;

    public Parser() throws IOException {
        lookahead = System.in.read();
    }

    /**
     * Method implementing the expr productions.
     * <blockquote><pre>
     * expr -> term rest
     * rest -> + term { print('+') } rest
     *      |  - term { print('-') } rest
     *      |  ''
     * </pre></blockquote>
     *
     * <p>
     * This implementation inlines the rest productions into expr,
     * the steps taken to do this are:
     * <ol>
     * <li>Initial rest method
     * <pre>{@code
     * void rest() {
     *     if (lookahead=='+') {
     *         match('+'); term(); print('+'); ;
     *     } else if (lookahead=='-') {
     *         match('-'); term(); print('-'); continue;
     *     } else {} // do nothing
     * }
     * }</pre>
     * </li>
     * <li>Eliminate Tail Recursion using iteration:
     * <pre>{@code
     * void rest() {
     *     while(true) {
     *         if (lookahead=='+') {
     *             match('+'); term(); print('+'); continue;
     *         } else if (lookahead=='-') {
     *             match('-'); term(); print('-'); continue;
     *         } else {}//do nothing
     *         break;
     *     }
     * }
     * }</pre>
     * </li>
     * <li>Inline rest into rest call in <b>expr -> term rest</b>:
     * <pre>{@code
     * void expr() throws IOException{
     *     term();
     *     // was previously a single rest() call below
     *     while(true){
     *         if(lookahead == '+') {
     *             match('+'); term(); System.out.write('+'); continue;
     *         } else if (lookahead == '-') {
     *             match('-'); term(); System.out.write('-'); continue;
     *         }
     *         break;
     *     }
     * }
     * }</pre>
     * </li>
     * </ol></p>
     * <p>Note: In this implementation empty str is defaulted when no
     * productions can be used for expr.
     * </p>
     * @throws IOException if an I/ O error occurs while matching
     */
    void expr() throws IOException{
        term();
        while(true){
            if(lookahead == '+') {
                match('+'); term(); System.out.write('+'); continue;
            } else if (lookahead == '-') {
                match('-'); term(); System.out.write('-'); continue;
            }
            break;
        }
    }

    /**
     * Method representing the term productions.
     * <blockquote><pre>
     * term -> 0 { print('0') }
     *      |  1 { print('1') }
     *      ...
     *      |  9 { print('9') }
     * </pre></blockquote>
     * <p> Simply matches a digit lookahead.</p>
     * @throws IOException – if an I/ O error occurs
     * @throws Error syntax error when lookahead is not a digit
     */
    void term() throws IOException, Error{
        if ( Character.isDigit((char)lookahead) ) {
            System.out.write((char)lookahead); match(lookahead);
        } else throw new Error("syntax error");
    }

    /**
     * Matches the current lookahead with a character and
     * forwards the lookahead.  Note that matching doesn't skip white space.
     * @param t character to match with lookahead
     * @throws IOException – if an I/ O error occurs
     * @throws Error Syntax error when the lookahead doesn't match with t
     */
    void match(int t) throws IOException, Error{
        if(lookahead == t) lookahead = System.in.read();
        else throw new Error("syntax error");
    }
}
