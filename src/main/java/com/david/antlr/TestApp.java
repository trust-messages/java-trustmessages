package com.david.antlr;

import com.david.messages.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerEnum;

import java.io.IOException;
import java.lang.Error;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TestApp {
    private static class Visitor extends QueryBaseVisitor<Query> {
        private final static Map<String, Long> OPERATORS = new HashMap<>();

        static {
            OPERATORS.put("=", 0L);
            OPERATORS.put("!=", 1L);
            OPERATORS.put("<", 2L);
            OPERATORS.put("<=", 3L);
            OPERATORS.put(">", 4L);
            OPERATORS.put(">=", 5L);
        }

        @Override
        public Query visitComparison(QueryParser.ComparisonContext ctx) {
            final Query q = new Query();
            q.cmp = new Comparison();
            q.cmp.op = new BerEnum(OPERATORS.get(ctx.OP().getSymbol().getText()));

            Entity source = null, target = null;
            BinaryTime date = null;
            Service service = null;

            switch (ctx.FIELD().getSymbol().getText()) {
                case "source":
                    source = new Entity(ctx.VALUE().getSymbol().getText());
                    break;
                case "target":
                    target = new Entity(ctx.VALUE().getSymbol().getText());
                    break;
                case "date":
                    final long time = Long.parseLong(ctx.VALUE().getSymbol().getText());
                    date = new BinaryTime(time);
                    break;
                case "service":
                    service = new Service(ctx.VALUE().getSymbol().getText());
                    break;
                default:
                    throw new Error("Should not happen!");
            }

            q.cmp.value = new Value(source, target, date, service);

            return q;
        }
    }

    public static void main(String[] args) throws IOException {
        //final Query o = getQuery("target = david@fri.si");

        final Value o = new Value(new Entity("david@fri.si"), null, null, null);

        System.out.println(o);
        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        o.encode(baos, true);
        System.out.println("'" + Base64.getEncoder().encodeToString(baos.getArray()) + "'");
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
