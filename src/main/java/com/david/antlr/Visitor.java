package com.david.antlr;

import com.david.messages.*;
import org.openmuc.jasn1.ber.types.BerEnum;

import java.util.HashMap;
import java.util.Map;

public class Visitor extends QueryBaseVisitor<Query> {
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
                source = new Entity(ctx.VALUE().getSymbol().getText().getBytes());
                break;
            case "target":
                target = new Entity(ctx.VALUE().getSymbol().getText().getBytes());
                break;
            case "date":
                final long time = Long.parseLong(ctx.VALUE().getSymbol().getText());
                date = new BinaryTime(time);
                break;
            case "service":
                service = new Service(ctx.VALUE().getSymbol().getText().getBytes());
                break;
            default:
                throw new Error("Should not happen!");
        }

        q.cmp.value = new Value(source, target, date, service);

        return q;
    }

    @Override
    public Query visitAnd(QueryParser.AndContext ctx) {
        final Query q = new Query();
        q.log = new Logical();
        q.log.op = new BerEnum(0);
        q.log.l = visit(ctx.expr(0));
        q.log.r = visit(ctx.expr(1));
        return q;
    }

    @Override
    public Query visitOr(QueryParser.OrContext ctx) {
        final Query q = new Query();
        q.log = new Logical();
        q.log.op = new BerEnum(1);
        q.log.l = visit(ctx.expr(0));
        q.log.r = visit(ctx.expr(1));
        return q;
    }

    @Override
    public Query visitParenthesis(QueryParser.ParenthesisContext ctx) {
        return super.visit(ctx.expr());
    }
}
