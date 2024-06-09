package parser;

import lexer.Lexer;
import lexer.Word;

import java.io.*;

public class Postfix {
    public static void main(String[] args) throws IOException {
//        Parser parse = new Parser();
//        parse.expr(); System.out.write('\n');
//        PushbackReader in = new PushbackReader(new FileReader("postfix.txt"));
//        PushbackInputStream pp = new PushbackInputStream(System.in);
//        in.read();
//        int c1 = pp.read();
//        int c2 = pp.read();

//        System.out.println("pp read 1: " + (char) c1);
//        System.out.println("pp read 2: " + (char) c2);
//        pp.unread(c1);
//        pp.unread(c2);
//        System.out.println("pp unread twice");
//        System.out.println("pp read 2: " + (char) pp.read());

//        System.out.println((char) pp.read());
//        System.out.println("Std.in read 1:" + (char) System.in.read());
//        System.out.println("pp read 2:" + (char) pp.read());
//        System.out.println((char) System.in.read());
//        System.out.println((char) pp.read());
//        System.out.println((char) pp.read());
//        System.out.println("pp read 3:" + "decimal ascii " + (char) pp.read() + " is new line");
//        System.out.println((char) pp.read());
//        System.out.println(pp.read());

        Lexer lexer = new Lexer(System.in);
        // System.in is set BEFORE the new ByteArrayInputStream replaces the original. Meaning the lexer is reading from the original InputStream.
        ByteArrayInputStream bats = new ByteArrayInputStream("abc".getBytes());
        System.setIn(bats);
//        Lexer lexer = new Lexer(System.in);

//        System.in.read();

        System.out.println(((Word) lexer.scan()).lexeme);

    }
}
