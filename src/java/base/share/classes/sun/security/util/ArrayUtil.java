/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.util;

import java.base.share.classes.jdk.internal.util.Preconditions;

import java.security.ProviderException;


/**
 * This class holds the various utility methods for array range checks.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */

public final class ArrayUtil {

    public static void blockSizeCheck(int len, int blockSize) {
        if ((len % blockSize) != 0) {
            throw new ProviderException("Internal error in input buffering");
        }
    }

    public static void nullAndBoundsCheck(byte[] array, int offset, int len) {
        // NPE is thrown when array is null
        Preconditions.checkFromIndexSize(offset, len, array.length, Preconditions.AIOOBE_FORMATTER);
    }

    private static void swap(byte[] arr, int i, int j) {
        byte tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static void reverse(byte [] arr) {
        int i = 0;
        int j = arr.length - 1;

        while (i < j) {
            swap(arr, i, j);
            i++;
            j--;
        }
    }
}
