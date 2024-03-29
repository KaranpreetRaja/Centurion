/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

#include "jni.h"
#include "jni_util.h"
#include "jlong.h"
#include "jvm.h"

#include "java_lang_Double.h"

/*
 * Find the double float corresponding to a given bit pattern
 */
JNIEXPORT jdouble JNICALL
Java_java_lang_Double_longBitsToDouble(JNIEnv *env, jclass unused, jlong v)
{
    union {
        jlong l;
        double d;
    } u;
    jlong_to_jdouble_bits(&v);
    u.l = v;
    return (jdouble)u.d;
}

/*
 * Find the bit pattern corresponding to a given double float, NOT collapsing NaNs
 */
JNIEXPORT jlong JNICALL
Java_java_lang_Double_doubleToRawLongBits(JNIEnv *env, jclass unused, jdouble v)
{
    union {
        jlong l;
        double d;
    } u;
    jdouble_to_jlong_bits(&v);
    u.d = (double)v;
    return u.l;
}
