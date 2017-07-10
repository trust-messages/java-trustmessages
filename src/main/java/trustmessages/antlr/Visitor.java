package trustmessages.antlr;

import org.openmuc.jasn1.ber.types.BerEnum;
import trustmessages.asn.*;

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
    public Query visitConstraint(QueryParser.ConstraintContext ctx) {
        final Query q = new Query();
        q.con = new Constraint();
        q.con.operator = new BerEnum(OPERATORS.get(ctx.OP().getSymbol().getText()));

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

        q.con.value = new Value(source, target, date, service);

        return q;
    }

    @Override
    public Query visitAnd(QueryParser.AndContext ctx) {
        final Query q = new Query();
        q.exp = new Expression();
        q.exp.operator = new BerEnum(0);
        q.exp.left = visit(ctx.expr(0));
        q.exp.right = visit(ctx.expr(1));
        return q;
    }

    @Override
    public Query visitOr(QueryParser.OrContext ctx) {
        final Query q = new Query();
        q.exp = new Expression();
        q.exp.operator = new BerEnum(1);
        q.exp.left = visit(ctx.expr(0));
        q.exp.right = visit(ctx.expr(1));
        return q;
    }

    @Override
    public Query visitParenthesis(QueryParser.ParenthesisContext ctx) {
        return super.visit(ctx.expr());
    }
}
