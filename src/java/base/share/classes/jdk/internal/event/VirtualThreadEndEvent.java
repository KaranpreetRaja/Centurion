/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.jdk.internal.event;

/**
 * Event recording that a virtual thread has terminated.
 */
public class VirtualThreadEndEvent extends Event {
    private final static VirtualThreadEndEvent EVENT = new VirtualThreadEndEvent();

    /**
     * Returns {@code true} if event is enabled, {@code false} otherwise.
     */
    public static boolean isTurnedOn() {
        return EVENT.isEnabled();
    }

    public long javaThreadId;
}
