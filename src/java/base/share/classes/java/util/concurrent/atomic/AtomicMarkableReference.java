/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

/*
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.base.share.classes.java.util.concurrent.atomic;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * An {@code AtomicMarkableReference} maintains an object reference
 * along with a mark bit, that can be updated atomically.
 *
 * <p>Implementation note: This implementation maintains markable
 * references by creating internal objects representing "boxed"
 * [reference, boolean] pairs.
 *
 * @since 1.5
 * @author Doug Lea
 * @param <V> The type of object referred to by this reference
 */
public class AtomicMarkableReference<V> {

    private static class Pair<T> {
        final T reference;
        final boolean mark;
        private Pair(T reference, boolean mark) {
            this.reference = reference;
            this.mark = mark;
        }
        static <T> Pair<T> of(T reference, boolean mark) {
            return new Pair<T>(reference, mark);
        }
    }

    private volatile Pair<V> pair;

    /**
     * Creates a new {@code AtomicMarkableReference} with the given
     * initial values.
     *
     * @param initialRef the initial reference
     * @param initialMark the initial mark
     */
    public AtomicMarkableReference(V initialRef, boolean initialMark) {
        pair = Pair.of(initialRef, initialMark);
    }

    /**
     * Returns the current value of the reference.
     *
     * @return the current value of the reference
     */
    public V getReference() {
        return pair.reference;
    }

    /**
     * Returns the current value of the mark.
     *
     * @return the current value of the mark
     */
    public boolean isMarked() {
        return pair.mark;
    }

    /**
     * Returns the current values of both the reference and the mark.
     * Typical usage is {@code boolean[1] holder; ref = v.get(holder); }.
     *
     * @param markHolder an array of size of at least one. On return,
     * {@code markHolder[0]} will hold the value of the mark.
     * @return the current value of the reference
     */
    public V get(boolean[] markHolder) {
        Pair<V> pair = this.pair;
        markHolder[0] = pair.mark;
        return pair.reference;
    }

    /**
     * Atomically sets the value of both the reference and mark to the
     * given update values if the current reference is {@code ==} to
     * the expected reference and the current mark is equal to the
     * expected mark. This operation may fail spuriously and does not
     * provide ordering guarantees, so is only rarely an
     * appropriate alternative to {@code compareAndSet}.
     *
     * @param expectedReference the expected value of the reference
     * @param newReference the new value for the reference
     * @param expectedMark the expected value of the mark
     * @param newMark the new value for the mark
     * @return {@code true} if successful
     */
    public boolean weakCompareAndSet(V       expectedReference,
                                     V       newReference,
                                     boolean expectedMark,
                                     boolean newMark) {
        return compareAndSet(expectedReference, newReference,
                             expectedMark, newMark);
    }

    /**
     * Atomically sets the value of both the reference and mark
     * to the given update values if the
     * current reference is {@code ==} to the expected reference
     * and the current mark is equal to the expected mark.
     *
     * @param expectedReference the expected value of the reference
     * @param newReference the new value for the reference
     * @param expectedMark the expected value of the mark
     * @param newMark the new value for the mark
     * @return {@code true} if successful
     */
    public boolean compareAndSet(V       expectedReference,
                                 V       newReference,
                                 boolean expectedMark,
                                 boolean newMark) {
        Pair<V> current = pair;
        return
            expectedReference == current.reference &&
            expectedMark == current.mark &&
            ((newReference == current.reference &&
              newMark == current.mark) ||
             casPair(current, Pair.of(newReference, newMark)));
    }

    /**
     * Unconditionally sets the value of both the reference and mark.
     *
     * @param newReference the new value for the reference
     * @param newMark the new value for the mark
     */
    public void set(V newReference, boolean newMark) {
        Pair<V> current = pair;
        if (newReference != current.reference || newMark != current.mark)
            this.pair = Pair.of(newReference, newMark);
    }

    /**
     * Atomically sets the value of the mark to the given update value
     * if the current reference is {@code ==} to the expected
     * reference.  Any given invocation of this operation may fail
     * (return {@code false}) spuriously, but repeated invocation
     * when the current value holds the expected value and no other
     * thread is also attempting to set the value will eventually
     * succeed.
     *
     * @param expectedReference the expected value of the reference
     * @param newMark the new value for the mark
     * @return {@code true} if successful
     */
    public boolean attemptMark(V expectedReference, boolean newMark) {
        Pair<V> current = pair;
        return
            expectedReference == current.reference &&
            (newMark == current.mark ||
             casPair(current, Pair.of(expectedReference, newMark)));
    }

    // VarHandle mechanics
    private static final VarHandle PAIR;
    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            PAIR = l.findVarHandle(AtomicMarkableReference.class, "pair",
                                   Pair.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private boolean casPair(Pair<V> cmp, Pair<V> val) {
        return PAIR.compareAndSet(this, cmp, val);
    }
}
