/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.jdk.internal.foreign.abi.x64.windows;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

enum TypeClass {
    STRUCT_REGISTER,
    STRUCT_REFERENCE,
    POINTER,
    INTEGER,
    FLOAT,
    VARARG_FLOAT;

    private static TypeClass classifyValueType(ValueLayout type, boolean isVararg) {
        // No 128-bit integers in the Windows C ABI. There are __m128(i|d) intrinsic types but they act just
        // like a struct when passing as an argument (passed by pointer).
        // https://docs.microsoft.com/en-us/cpp/cpp/m128?view=vs-2019

        // x87 is ignored on Windows:
        // "The x87 register stack is unused, and may be used by the callee,
        // but must be considered volatile across function calls."
        // https://docs.microsoft.com/en-us/cpp/build/x64-calling-convention?view=vs-2019

        Class<?> carrier = type.carrier();
        if (carrier == boolean.class || carrier == byte.class || carrier == char.class ||
                carrier == short.class || carrier == int.class || carrier == long.class) {
            return INTEGER;
        } else if (carrier == float.class || carrier == double.class) {
            if (isVararg) {
                return VARARG_FLOAT;
            } else {
                return FLOAT;
            }
        } else if (carrier == MemorySegment.class) {
            return POINTER;
        } else {
            throw new IllegalStateException("Cannot get here: " + carrier.getName());
        }
    }

    static boolean isRegisterAggregate(MemoryLayout type) {
        long size = type.byteSize();
        return size == 1
            || size == 2
            || size == 4
            || size == 8;
    }

    private static TypeClass classifyStructType(MemoryLayout layout) {
        if (isRegisterAggregate(layout)) {
            return STRUCT_REGISTER;
        }
        return STRUCT_REFERENCE;
    }

    static TypeClass typeClassFor(MemoryLayout type, boolean isVararg) {
        if (type instanceof ValueLayout) {
            return classifyValueType((ValueLayout) type, isVararg);
        } else if (type instanceof GroupLayout) {
            return classifyStructType(type);
        } else {
            throw new IllegalArgumentException("Unsupported layout: " + type);
        }
    }
}
