/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.nio.ch;

/**
 * Implemented by asynchronous channels that require notification when an I/O
 * operation is cancelled.
 */

interface Cancellable {
    /**
     * Invoked to notify channel that cancel has been invoked while holding
     * the Future's lock.
     */
    void onCancel(PendingFuture<?,?> task);
}
