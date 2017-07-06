package trustmessages.tms;


import trustmessages.asn.Format;
import trustmessages.asn.Query;
import trustmessages.asn.Rating;
import trustmessages.asn.Value;

import java.math.BigInteger;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class InMemoryTrustDb {
    static final List<String> USERS = Arrays.asList("alice", "bob", "charlie", "david", "eve");
    static final List<String> SERVICES = Arrays.asList("seller", "letter", "renter", "buyer");

    static final PrimitiveIterator.OfInt TIME = IntStream.iterate(0, i -> i + 1).iterator();

    private final static Map<Long, BiPredicate<? super Comparable, ? super Comparable>> COMPARATORS = new HashMap<>();

    static {
        COMPARATORS.put(0L, (a, b) -> a.compareTo(b) == 0);
        COMPARATORS.put(1L, (a, b) -> a.compareTo(b) != 0);
        COMPARATORS.put(2L, (a, b) -> a.compareTo(b) < 0);
        COMPARATORS.put(3L, (a, b) -> a.compareTo(b) <= 0);
        COMPARATORS.put(4L, (a, b) -> a.compareTo(b) > 0);
        COMPARATORS.put(5L, (a, b) -> a.compareTo(b) >= 0);
    }

    public abstract Map<Type, Format> getId();

    public abstract Map<Type, String> getFormat();

    protected abstract Stream<Rating> allAssessments();

    protected abstract Stream<Rating> allTrust();

    public List<Rating> getAssessments(Query query) {
        return allAssessments().filter(createPredicate(query)).collect(Collectors.toList());
    }

    public List<Rating> getTrust(Query query) {
        return allTrust().filter(createPredicate(query)).collect(Collectors.toList());
    }


    public final Predicate<Rating> createPredicate(Query query) {
        if (query.exp != null) {
            final Predicate<Rating> left = createPredicate(query.exp.left);
            final Predicate<Rating> right = createPredicate(query.exp.right);

            if (query.exp.operator.value.equals(BigInteger.ZERO)) { // and
                return p -> left.and(right).test(p);
            } else { // or
                return p -> left.or(right).test(p);
            }
        } else {
            final Value value = query.con.value;
            final BiPredicate<? super Comparable, ? super Comparable> comparator =
                    COMPARATORS.get(query.con.operator.value.longValue());

            if (value.date != null) {
                return p -> comparator.test(p.date.value, value.date.value);
            } else if (value.source != null) {
                return p -> comparator.test(p.source.toString(), value.source.toString());
            } else if (value.target != null) {
                return p -> comparator.test(p.target.toString(), value.target.toString());
            } else if (value.service != null) {
                return p -> comparator.test(p.service.toString(), value.service.toString());
            } else {
                throw new IllegalArgumentException("Invalid value object: " + value.toString());
            }
        }
    }
}
