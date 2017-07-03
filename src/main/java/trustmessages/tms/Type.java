package trustmessages.tms;

import org.openmuc.jasn1.ber.types.BerEnum;

import java.math.BigInteger;

public enum Type {
    TRUST(BigInteger.ZERO), ASSESSMENT(BigInteger.ONE);

    public final BigInteger value;

    Type(BigInteger v) {
        value = v;
    }

    public static Type fromEnum(final BerEnum enum_) {
        for (Type t : values()) {
            if (t.value.equals(enum_.value)) {
                return t;
            }
        }

        return null;
    }
}
