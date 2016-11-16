package com.david.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class TestApp {
    public static void main(String[] args) throws IOException {
        final ANTLRInputStream is = new ANTLRInputStream("source = david AND service = seller");
        final QueryLexer lexer = new QueryLexer(is);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final QueryParser parser = new QueryParser(tokens);

        final ParseTree tree = parser.expr();
        System.out.println(tree.toStringTree(parser));
    }
}
