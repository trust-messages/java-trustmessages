// Generated from Query.g4 by ANTLR 4.6
package trustmessages.antlr;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QueryParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, FIELD = 5, OP = 6, VALUE = 7, WS = 8;
    public static final int
            RULE_expr = 0;
    public static final String[] ruleNames = {
            "expr"
    };

    private static final String[] _LITERAL_NAMES = {
            null, "'('", "')'", "'AND'", "'OR'"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, null, null, null, null, "FIELD", "OP", "VALUE", "WS"
    };
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;

    static {
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }

            if (tokenNames[i] == null) {
                tokenNames[i] = "<INVALID>";
            }
        }
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    @Override
    public String getGrammarFileName() {
        return "Query.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public QueryParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class ExprContext extends ParserRuleContext {
        public ExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expr;
        }

        public ExprContext() {
        }

        public void copyFrom(ExprContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class ComparisonContext extends ExprContext {
        public TerminalNode FIELD() {
            return getToken(QueryParser.FIELD, 0);
        }

        public TerminalNode OP() {
            return getToken(QueryParser.OP, 0);
        }

        public TerminalNode VALUE() {
            return getToken(QueryParser.VALUE, 0);
        }

        public ComparisonContext(ExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof QueryVisitor) return ((QueryVisitor<? extends T>) visitor).visitComparison(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class OrContext extends ExprContext {
        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public OrContext(ExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof QueryVisitor) return ((QueryVisitor<? extends T>) visitor).visitOr(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class AndContext extends ExprContext {
        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public AndContext(ExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof QueryVisitor) return ((QueryVisitor<? extends T>) visitor).visitAnd(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class ParenthesisContext extends ExprContext {
        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public ParenthesisContext(ExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof QueryVisitor) return ((QueryVisitor<? extends T>) visitor).visitParenthesis(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        return expr(0);
    }

    private ExprContext expr(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        ExprContext _localctx = new ExprContext(_ctx, _parentState);
        ExprContext _prevctx = _localctx;
        int _startState = 0;
        enterRecursionRule(_localctx, 0, RULE_expr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(10);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case FIELD: {
                        _localctx = new ComparisonContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;

                        setState(3);
                        match(FIELD);
                        setState(4);
                        match(OP);
                        setState(5);
                        match(VALUE);
                    }
                    break;
                    case T__0: {
                        _localctx = new ParenthesisContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(6);
                        match(T__0);
                        setState(7);
                        expr(0);
                        setState(8);
                        match(T__1);
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                _ctx.stop = _input.LT(-1);
                setState(20);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(18);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 1, _ctx)) {
                                case 1: {
                                    _localctx = new AndContext(new ExprContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(12);
                                    if (!(precpred(_ctx, 2)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                    setState(13);
                                    match(T__2);
                                    setState(14);
                                    expr(3);
                                }
                                break;
                                case 2: {
                                    _localctx = new OrContext(new ExprContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                    setState(15);
                                    if (!(precpred(_ctx, 1)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                    setState(16);
                                    match(T__3);
                                    setState(17);
                                    expr(2);
                                }
                                break;
                            }
                        }
                    }
                    setState(22);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 0:
                return expr_sempred((ExprContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean expr_sempred(ExprContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(_ctx, 2);
            case 1:
                return precpred(_ctx, 1);
        }
        return true;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\n\32\4\2\t\2\3\2" +
                    "\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2\r\n\2\3\2\3\2\3\2\3\2\3\2\3\2\7\2\25" +
                    "\n\2\f\2\16\2\30\13\2\3\2\2\3\2\3\2\2\2\33\2\f\3\2\2\2\4\5\b\2\1\2\5\6" +
                    "\7\7\2\2\6\7\7\b\2\2\7\r\7\t\2\2\b\t\7\3\2\2\t\n\5\2\2\2\n\13\7\4\2\2" +
                    "\13\r\3\2\2\2\f\4\3\2\2\2\f\b\3\2\2\2\r\26\3\2\2\2\16\17\f\4\2\2\17\20" +
                    "\7\5\2\2\20\25\5\2\2\5\21\22\f\3\2\2\22\23\7\6\2\2\23\25\5\2\2\4\24\16" +
                    "\3\2\2\2\24\21\3\2\2\2\25\30\3\2\2\2\26\24\3\2\2\2\26\27\3\2\2\2\27\3" +
                    "\3\2\2\2\30\26\3\2\2\2\5\f\24\26";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}