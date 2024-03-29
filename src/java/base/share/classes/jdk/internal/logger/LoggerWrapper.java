/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */


package java.base.share.classes.jdk.internal.logger;

import java.util.Objects;
import java.lang.System.Logger;
import sun.util.logging.PlatformLogger;

/**
 * An implementation of {@link Logger} that redirects all calls to a wrapped
 * instance of Logger.
 *
 * @param <L> Type of the wrapped Logger: {@code Logger} or an
 *            extension of that interface.
 */
public class LoggerWrapper<L extends Logger> extends AbstractLoggerWrapper<L> {

    final L wrapped;
    final PlatformLogger.Bridge platformProxy;

    public LoggerWrapper(L wrapped) {
        this(Objects.requireNonNull(wrapped), (Void)null);
    }

    LoggerWrapper(L wrapped, Void unused) {
        this.wrapped = wrapped;
        this.platformProxy = (wrapped instanceof PlatformLogger.Bridge) ?
            (PlatformLogger.Bridge) wrapped : null;
    }

    @Override
    public final L wrapped() {
        return wrapped;
    }

    @Override
    public final PlatformLogger.Bridge platformProxy() {
        return platformProxy;
    }

}
