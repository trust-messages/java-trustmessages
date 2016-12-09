package trustmessages.antlr;// Generated from /home/david/Development/java/jasn1/src/main/resources/Query.g4 by ANTLR 4.5.3

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QueryLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, FIELD = 5, OP = 6, VALUE = 7, WS = 8;
    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    public static final String[] ruleNames = {
            "T__0", "T__1", "T__2", "T__3", "FIELD", "OP", "VALUE", "WS"
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


    public QueryLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "trustmessages/antlr/Query.g4";
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
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\nL\b\1\4\2\t\2\4" +
                    "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\3\3\3" +
                    "\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3" +
                    "\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\66\n\6\3\7\3\7" +
                    "\3\7\3\7\3\7\3\7\3\7\5\7?\n\7\3\b\6\bB\n\b\r\b\16\bC\3\t\6\tG\n\t\r\t" +
                    "\16\tH\3\t\3\t\2\2\n\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\3\2\4\7\2\60\60" +
                    "\62;B\\^^c|\5\2\13\f\17\17\"\"S\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2" +
                    "\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\3\23\3\2" +
                    "\2\2\5\25\3\2\2\2\7\27\3\2\2\2\t\33\3\2\2\2\13\65\3\2\2\2\r>\3\2\2\2\17" +
                    "A\3\2\2\2\21F\3\2\2\2\23\24\7*\2\2\24\4\3\2\2\2\25\26\7+\2\2\26\6\3\2" +
                    "\2\2\27\30\7C\2\2\30\31\7P\2\2\31\32\7F\2\2\32\b\3\2\2\2\33\34\7Q\2\2" +
                    "\34\35\7T\2\2\35\n\3\2\2\2\36\37\7u\2\2\37 \7q\2\2 !\7w\2\2!\"\7t\2\2" +
                    "\"#\7e\2\2#\66\7g\2\2$%\7v\2\2%&\7c\2\2&\'\7t\2\2\'(\7i\2\2()\7g\2\2)" +
                    "\66\7v\2\2*+\7u\2\2+,\7g\2\2,-\7t\2\2-.\7x\2\2./\7k\2\2/\60\7e\2\2\60" +
                    "\66\7g\2\2\61\62\7f\2\2\62\63\7c\2\2\63\64\7v\2\2\64\66\7g\2\2\65\36\3" +
                    "\2\2\2\65$\3\2\2\2\65*\3\2\2\2\65\61\3\2\2\2\66\f\3\2\2\2\678\7#\2\28" +
                    "?\7?\2\29:\7>\2\2:?\7?\2\2;<\7@\2\2<?\7?\2\2=?\4>@\2>\67\3\2\2\2>9\3\2" +
                    "\2\2>;\3\2\2\2>=\3\2\2\2?\16\3\2\2\2@B\t\2\2\2A@\3\2\2\2BC\3\2\2\2CA\3" +
                    "\2\2\2CD\3\2\2\2D\20\3\2\2\2EG\t\3\2\2FE\3\2\2\2GH\3\2\2\2HF\3\2\2\2H" +
                    "I\3\2\2\2IJ\3\2\2\2JK\b\t\2\2K\22\3\2\2\2\7\2\65>CH\3\b\2\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}