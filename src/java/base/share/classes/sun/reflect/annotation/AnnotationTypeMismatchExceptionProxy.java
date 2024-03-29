/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.reflect.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * ExceptionProxy for AnnotationTypeMismatchException.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 22/4/2023 
 */
class AnnotationTypeMismatchExceptionProxy extends ExceptionProxy {
    @java.io.Serial
    private static final long serialVersionUID = 7844069490309503934L;
    @SuppressWarnings("serial") // Not statically typed as Serializable
    private Method member; // Would be more robust to null-out in a writeObject method.
    private final String foundType;

    /**
     * It turns out to be convenient to construct these proxies in
     * two stages.  Since this is a private implementation class, we
     * permit ourselves this liberty even though it's normally a very
     * bad idea.
     */
    AnnotationTypeMismatchExceptionProxy(String foundType) {
        this.foundType = foundType;
    }

    AnnotationTypeMismatchExceptionProxy setMember(Method member) {
        this.member = member;
        return this;
    }

    protected RuntimeException generateException() {
        return new AnnotationTypeMismatchException(member, foundType);
    }

    @Override
    public String toString() {
        return "/* Warning type mismatch! \"" + foundType + "\" */" ;
    }
}
