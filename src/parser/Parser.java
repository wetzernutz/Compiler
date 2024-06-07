package parser;

import java.io.*;

/**
 * <h5>
 *   A simple predictive parser to convert infix notation to postfix notation.
 * </h5>
 * <ul>
 *   <li>top-down method of syntax analysis</li>
 *   <li>recursive-descent</li>
 * </ul>
 *
 * <p>
 *   For each production A -> ..., the first character determines the production to use.
 * </p>
 * <h6>
 *   Translation Scheme (after left-recursion elimination):
 * </h6>
 * <pre>
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
 * </pre>
 */
class Parser {
    static int lookahead;

    /**
     * <h5>
     *   Constructor for creating the Parser.
     * </h5>
     * Lookahead symbol unambiguously determines the flow of control.
     * <p>
     *   Characters (one byte) are stored as values
     *   between 0-255, and -1 if EOF.
     * </p>
     * <p>
     *   Extended ASCII uses the most significant bit and
     *   can allow for the representation of 256 characters.
     *   <div>
     *     <a href="https://www.bbc.co.uk/bitesize/guides/zsnbr82/revision/5#:~:text=So%20ASCII%20represents%20128%20characters,8%20bits%20rather%20than%20256">
     *       Website Article for additional info
     *     </a>
     *   </div>
     * </p>
     *
     * @throws IOException If an I/O error occurs.
     */
    public Parser() throws IOException {
        lookahead = System.in.read();
    }

    /**
     * <h5>
     *   Method representing the expr production
     * </h5>
     * <ul>
     *    <li>Inlined the rest production into expr</li>
     *    <li>empty str is used when no other productions can be used</li>
     * </ul>
     *
     * @throws IOException if an I/ O error occurs while matching.
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
     * <h5>Method representing the term production</h5>
     * <ul>
     *   <li>simply reads a digit and matches the digit.</li>
     * </ul>
     *
     * @throws IOException if an I/ O error occurs while matching.
     * @throws Error when the char is not a digit.
     */
    void term() throws IOException, Error{
        if ( Character.isDigit((char)lookahead) ) {
            System.out.write((char)lookahead); match(lookahead);
        } else throw new Error("syntax error");
    }

    /**
     * <h5>Matches the current lookahead with a character</h5>
     * <ul>
     *   <li>advances the lookahead ofter successful match</li>
     * </ul>
     * @param t char to match with lookahead
     * @throws IOException if an I/ O error occurs
     * @throws Error when t is not the lookahead.
     */
    void match(int t) throws IOException, Error{
        if(lookahead == t) lookahead = System.in.read();
        else throw new Error("syntax error");
    }
}
