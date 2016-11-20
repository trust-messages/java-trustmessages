package com.david;


import com.david.messages.Assessment;
import com.david.messages.Query;
import com.david.messages.Trust;
import com.david.messages.Value;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public abstract class InMemoryTrustDb {
    private static class ValueComparator<T extends Comparable<? super T>> implements Comparator<T> {
        public int compare(T a, T b) {
            return a.compareTo(b);
        }
    }

    private static final ValueComparator<String> SC = new ValueComparator<>();
    private static final ValueComparator<Long> IC = new ValueComparator<>();

    static final List<String> USERS = Arrays.asList("alice", "bob", "charlie", "david", "eve");
    static final List<String> SERVICES = Arrays.asList("seller", "letter", "renter", "buyer");

    static final PrimitiveIterator.OfInt TIME = IntStream.iterate(0, i -> i + 1).iterator();

    private final static Map<Long, BiPredicate<String, String>> STRING_COMPARATORS = new HashMap<>();
    private final static Map<Long, BiPredicate<Long, Long>> INTEGER_COMPARATORS = new HashMap<>();

    static {
        STRING_COMPARATORS.put(0L, (a, b) -> SC.compare(a, b) == 0);
        STRING_COMPARATORS.put(1L, (a, b) -> SC.compare(a, b) != 0);
        STRING_COMPARATORS.put(2L, (a, b) -> SC.compare(a, b) < 0);
        STRING_COMPARATORS.put(3L, (a, b) -> SC.compare(a, b) <= 0);
        STRING_COMPARATORS.put(4L, (a, b) -> SC.compare(a, b) > 0);
        STRING_COMPARATORS.put(5L, (a, b) -> SC.compare(a, b) >= 0);
        INTEGER_COMPARATORS.put(0L, (a, b) -> IC.compare(a, b) == 0);
        INTEGER_COMPARATORS.put(1L, (a, b) -> IC.compare(a, b) != 0);
        INTEGER_COMPARATORS.put(2L, (a, b) -> IC.compare(a, b) < 0);
        INTEGER_COMPARATORS.put(3L, (a, b) -> IC.compare(a, b) <= 0);
        INTEGER_COMPARATORS.put(4L, (a, b) -> IC.compare(a, b) > 0);
        INTEGER_COMPARATORS.put(5L, (a, b) -> IC.compare(a, b) >= 0);
    }

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

            if (value.date != null) {
                final BiPredicate<Long, Long> comparator = INTEGER_COMPARATORS.get(query.cmp.op.value);
                return p -> comparator.test(p.date.value, value.date.value);
            } else if (value.target != null) {
                final BiPredicate<String, String> comparator = STRING_COMPARATORS.get(query.cmp.op.value);
                return p -> comparator.test(p.target.toString(), value.target.toString());
            } else if (value.service != null) {
                final BiPredicate<String, String> comparator = STRING_COMPARATORS.get(query.cmp.op.value);
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

            if (value.date != null) {
                final BiPredicate<Long, Long> comparator = INTEGER_COMPARATORS.get(query.cmp.op.value);
                return p -> comparator.test(p.date.value, value.date.value);
            } else if (value.source != null) {
                final BiPredicate<String, String> comparator = STRING_COMPARATORS.get(query.cmp.op.value);
                return p -> comparator.test(p.source.toString(), value.source.toString());
            } else if (value.target != null) {
                final BiPredicate<String, String> comparator = STRING_COMPARATORS.get(query.cmp.op.value);
                return p -> comparator.test(p.target.toString(), value.target.toString());
            } else if (value.service != null) {
                final BiPredicate<String, String> comparator = STRING_COMPARATORS.get(query.cmp.op.value);
                return p -> comparator.test(p.service.toString(), value.service.toString());
            } else {
                throw new IllegalArgumentException("Invalid value object: " + value.toString());
            }

        }
    }
}
