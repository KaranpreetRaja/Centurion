/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.jdk.internal.access;

import java.util.concurrent.ForkJoinPool;

public interface JavaUtilConcurrentFJPAccess {
    long beginCompensatedBlock(ForkJoinPool pool);
    void endCompensatedBlock(ForkJoinPool pool, long post);
}
