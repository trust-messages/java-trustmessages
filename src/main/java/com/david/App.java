package com.david;

import com.david.antlr.QueryLexer;
import com.david.antlr.QueryParser;
import com.david.antlr.Visitor;
import com.david.messages.Entity;
import com.david.messages.Query;
import com.david.messages.Value;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class App {
    public static void main(String[] args) throws IOException {
        // final Query q = getQuery("target = david@fri.si");
        // final Query q = getQuery("target = david@fri.si AND service = seller");
        // final Query q = getQuery("target = david@fri.si OR service = seller");
        //final Query q = getQuery("target = david@fri.si AND (service = seller OR service = letter)");
        //final AssessmentRequest ar = new AssessmentRequest(new BerInteger(0), q);
        //final Message o = new Message(ar, null, null, null, null, null, null);

        final Value o = new Value(new Entity("david@fri.si".getBytes()), null, null, null);
        System.out.println(o);
        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        o.encode(baos, false);
        final String data = Base64.getEncoder().encodeToString(baos.getArray());
        System.out.println(data);

        final ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(data));
        final Value v = new Value();
        v.decode(bais, null);
        System.out.println(v);
    }

    private static Query getQuery(String query) {
        final ANTLRInputStream is = new ANTLRInputStream(query);
        final QueryLexer lexer = new QueryLexer(is);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final QueryParser parser = new QueryParser(tokens);
        final ParseTree tree = parser.expr();
        final Visitor v = new Visitor();
        return v.visit(tree);
    }
}
