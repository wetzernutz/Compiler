package parser;

import java.io.*;

class Parser {
    static int lookahead;

    public Parser() throws IOException {
        this.lookahead = System.in.read();
    }

    void expr() throws IOException {
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

    void term() throws IOException {
        if ( Character.isDigit((char)lookahead) ) {
            System.out.write((char) lookahead); match(lookahead);
        } else throw new Error("syntax error");
    }

    void match(int t) throws IOException {
        if(lookahead == t) lookahead = System.in.read();
        else throw new Error("syntax error");
    }
}
