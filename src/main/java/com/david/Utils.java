package com.david;

import com.david.antlr.QueryLexer;
import com.david.antlr.QueryParser;
import com.david.antlr.Visitor;
import com.david.messages.Query;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.Base64;

public class Utils {
    private final static Base64.Decoder DECODER = Base64.getDecoder();

    public static byte[] decode(String in) {
        return DECODER.decode(in);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
    }

    public static Query getQuery(String query) {
        final ANTLRInputStream is = new ANTLRInputStream(query);
        final QueryLexer lexer = new QueryLexer(is);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final QueryParser parser = new QueryParser(tokens);
        final ParseTree tree = parser.expr();
        final Visitor v = new Visitor();
        return v.visit(tree);
    }
}
