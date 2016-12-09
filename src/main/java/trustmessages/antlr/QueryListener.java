package trustmessages.antlr;// Generated from /home/david/Development/java/jasn1/src/main/resources/Query.g4 by ANTLR 4.5.3

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link QueryParser}.
 */
public interface QueryListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by the {@code comparison}
     * labeled alternative in {@link QueryParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterComparison(QueryParser.ComparisonContext ctx);

    /**
     * Exit a parse tree produced by the {@code comparison}
     * labeled alternative in {@link QueryParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitComparison(QueryParser.ComparisonContext ctx);

    /**
     * Enter a parse tree produced by the {@code or}
     * labeled alternative in {@link QueryParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterOr(QueryParser.OrContext ctx);

    /**
     * Exit a parse tree produced by the {@code or}
     * labeled alternative in {@link QueryParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitOr(QueryParser.OrContext ctx);

    /**
     * Enter a parse tree produced by the {@code and}
     * labeled alternative in {@link QueryParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterAnd(QueryParser.AndContext ctx);

    /**
     * Exit a parse tree produced by the {@code and}
     * labeled alternative in {@link QueryParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitAnd(QueryParser.AndContext ctx);

    /**
     * Enter a parse tree produced by the {@code parenthesis}
     * labeled alternative in {@link QueryParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterParenthesis(QueryParser.ParenthesisContext ctx);

    /**
     * Exit a parse tree produced by the {@code parenthesis}
     * labeled alternative in {@link QueryParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitParenthesis(QueryParser.ParenthesisContext ctx);
}