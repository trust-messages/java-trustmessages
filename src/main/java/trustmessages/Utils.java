package trustmessages;

import trustmessages.antlr.QueryLexer;
import trustmessages.antlr.QueryParser;
import trustmessages.antlr.Visitor;
import trustmessages.asn.AssessmentRequest;
import trustmessages.asn.Message;
import trustmessages.asn.Query;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerInteger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class Utils {
    public static byte[] decode(String in) {
        return Base64.getDecoder().decode(in);
    }

    public static String encode(byte[] in) {
        return Base64.getEncoder().encodeToString(in);
    }

    public static void main(String[] args) throws IOException {
        final AssessmentRequest ar = new AssessmentRequest(new BerInteger(1),
                getQuery("source = david AND (service = seller OR service = letter) AND (target = balu OR target = aleks)"));
        final Message m = new Message(ar, null, null, null, null, null, null);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        m.encode(baos);

        System.out.println(Base64.getEncoder().encodeToString(baos.getArray()));
        System.out.println(m);
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

    public static byte[] encode(Message message) {
        try {
            final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
            message.encode(baos);
            return baos.getArray();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static Message decode(byte[] bytes) throws IOException {
        final Message message = new Message();
        message.decode(new ByteArrayInputStream(bytes), null);
        return message;
    }
}
