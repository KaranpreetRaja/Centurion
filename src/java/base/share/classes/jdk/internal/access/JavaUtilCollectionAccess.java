/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.access;

import java.util.List;

public interface JavaUtilCollectionAccess {
    <E> List<E> listFromTrustedArray(Object[] array);
    <E> List<E> listFromTrustedArrayNullsAllowed(Object[] array);
}
