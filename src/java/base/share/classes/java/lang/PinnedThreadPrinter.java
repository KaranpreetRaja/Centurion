/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.java.lang;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import static java.base.share.classes.java.lang.StackWalker.Option.*;

/**
 * Helper class to print the virtual thread stack trace when pinned.
 *
 * The class maintains a ClassValue with the hashes of stack traces that are pinned by
 * code in that Class. This is used to avoid printing the same stack trace many times.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */
class PinnedThreadPrinter {
    static final StackWalker STACK_WALKER;
    static {
        var options = Set.of(SHOW_REFLECT_FRAMES, RETAIN_CLASS_REFERENCE);
        PrivilegedAction<StackWalker> pa = () ->
            LiveStackFrame.getStackWalker(options, VirtualThread.continuationScope());
        @SuppressWarnings("removal")
        var stackWalker = AccessController.doPrivileged(pa);
        STACK_WALKER = stackWalker;
    }

    private static final ClassValue<Hashes> HASHES = new ClassValue<>() {
        @Override
        protected Hashes computeValue(Class<?> type) {
            return new Hashes();
        }
    };

    @SuppressWarnings("serial")
    private static class Hashes extends LinkedHashMap<Integer, Boolean> {
        boolean add(int hash) {
            return (putIfAbsent(hash, Boolean.TRUE) == null);
        }
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Boolean> oldest) {
            // limit number of hashes
            return size() > 8;
        }
    }

    /**
     * Returns a hash of the given stack trace. The hash is based on the class,
     * method and bytecode index.
     */
    private static int hash(List<LiveStackFrame> stack) {
        int hash = 0;
        for (LiveStackFrame frame : stack) {
            hash = (31 * hash) + Objects.hash(frame.getDeclaringClass(),
                    frame.getMethodName(),
                    frame.getByteCodeIndex());
        }
        return hash;
    }

    /**
     * Prints the continuation stack trace.
     *
     * @param printAll true to print all stack frames, false to only print the
     *        frames that are native or holding a monitor
     */
    static void printStackTrace(PrintStream out, boolean printAll) {
        List<LiveStackFrame> stack = STACK_WALKER.walk(s ->
            s.map(f -> (LiveStackFrame) f)
                    .filter(f -> f.getDeclaringClass() != PinnedThreadPrinter.class)
                    .collect(Collectors.toList())
        );

        // find the closest frame that is causing the thread to be pinned
        stack.stream()
            .filter(f -> (f.isNativeMethod() || f.getMonitors().length > 0))
            .map(LiveStackFrame::getDeclaringClass)
            .findFirst()
            .ifPresentOrElse(klass -> {
                int hash = hash(stack);
                Hashes hashes = HASHES.get(klass);
                synchronized (hashes) {
                    // print the stack trace if not already seen
                    if (hashes.add(hash)) {
                        printStackTrace(stack, out, printAll);
                    }
                }
            }, () -> printStackTrace(stack, out, true));  // not found
    }

    private static void printStackTrace(List<LiveStackFrame> stack,
                                        PrintStream out,
                                        boolean printAll) {
        out.println(Thread.currentThread());
        for (LiveStackFrame frame : stack) {
            var ste = frame.toStackTraceElement();
            int monitorCount = frame.getMonitors().length;
            if (monitorCount > 0) {
                out.format("    %s <== monitors:%d%n", ste, monitorCount);
            } else if (frame.isNativeMethod() || printAll) {
                out.format("    %s%n", ste);
            }
        }
    }
}
