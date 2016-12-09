package trustmessages.tms;


import trustmessages.asn.*;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

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

    public abstract SystemIdentity getId();

    public abstract List<Assessment> getAssessments(Query query);

    public abstract List<Trust> getTrust(Query query);

    public abstract Map<String, String> getFormat();

    public final Predicate<Trust> createTrustPredicate(Query query) {
        if (query.log != null) {
            final Predicate<Trust> left = createTrustPredicate(query.log.l);
            final Predicate<Trust> right = createTrustPredicate(query.log.r);

            if (query.log.op.value == 0) { // and
                return p -> left.and(right).test(p);
            } else { // or
                return p -> left.or(right).test(p);
            }
        } else {
            final Value value = query.cmp.value;
            final BiPredicate<? super Comparable, ? super Comparable> comparator = COMPARATORS.get(query.cmp.op.value);

            if (value.date != null) {
                return p -> comparator.test(p.date.value, value.date.value);
            } else if (value.target != null) {
                return p -> comparator.test(p.target.toString(), value.target.toString());
            } else if (value.service != null) {
                return p -> comparator.test(p.service.toString(), value.service.toString());
            } else {
                throw new IllegalArgumentException("Invalid value object: " + value.toString());
            }
        }
    }

    public final Predicate<Assessment> createAssessmentPredicate(Query query) {
        if (query.log != null) {
            final Predicate<Assessment> left = createAssessmentPredicate(query.log.l);
            final Predicate<Assessment> right = createAssessmentPredicate(query.log.r);

            if (query.log.op.value == 0) { // and
                return p -> left.and(right).test(p);
            } else { // or
                return p -> left.or(right).test(p);
            }
        } else {
            final Value value = query.cmp.value;
            final BiPredicate<? super Comparable, ? super Comparable> comparator = COMPARATORS.get(query.cmp.op.value);

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
