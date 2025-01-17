// Generated from Query.g4 by ANTLR 4.6
package trustmessages.antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link QueryParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface QueryVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by the {@code or}
     * labeled alternative in {@link QueryParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOr(QueryParser.OrContext ctx);

    /**
     * Visit a parse tree produced by the {@code and}
     * labeled alternative in {@link QueryParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAnd(QueryParser.AndContext ctx);

    /**
     * Visit a parse tree produced by the {@code constraint}
     * labeled alternative in {@link QueryParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitConstraint(QueryParser.ConstraintContext ctx);

    /**
     * Visit a parse tree produced by the {@code parenthesis}
     * labeled alternative in {@link QueryParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParenthesis(QueryParser.ParenthesisContext ctx);
}