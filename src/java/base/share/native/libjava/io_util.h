/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

#include "jni.h"
#include "jni_util.h"

extern jfieldID IO_fd_fdID;
extern jfieldID IO_handle_fdID;
extern jfieldID IO_append_fdID;

#ifdef _ALLBSD_SOURCE
#include <fcntl.h>
#ifndef O_SYNC
#define O_SYNC  O_FSYNC
#endif
#ifndef O_DSYNC
#define O_DSYNC O_FSYNC
#endif
#elif !defined(O_DSYNC) || !defined(O_SYNC)
#define O_SYNC  (0x0800)
#define O_DSYNC (0x2000)
#endif

/*
 * IO helper functions
 */

jint readSingle(JNIEnv *env, jobject this, jfieldID fid);
jint readBytes(JNIEnv *env, jobject this, jbyteArray bytes, jint off,
               jint len, jfieldID fid);
void writeSingle(JNIEnv *env, jobject this, jint byte, jboolean append, jfieldID fid);
void writeBytes(JNIEnv *env, jobject this, jbyteArray bytes, jint off,
                jint len, jboolean append, jfieldID fid);
void fileOpen(JNIEnv *env, jobject this, jstring path, jfieldID fid, int flags);
void throwFileNotFoundException(JNIEnv *env, jstring path);

/*
 * Macros for managing platform strings.  The typical usage pattern is:
 *
 *     WITH_PLATFORM_STRING(env, string, var) {
 *         doSomethingWith(var);
 *     } END_PLATFORM_STRING(env, var);
 *
 *  where  env      is the prevailing JNIEnv,
 *         string   is a JNI reference to a java.lang.String object, and
 *         var      is the char * variable that will point to the string,
 *                  after being converted into the platform encoding.
 *
 * The related macro WITH_FIELD_PLATFORM_STRING first extracts the string from
 * a given field of a given object:
 *
 *     WITH_FIELD_PLATFORM_STRING(env, object, id, var) {
 *         doSomethingWith(var);
 *     } END_PLATFORM_STRING(env, var);
 *
 *  where  env      is the prevailing JNIEnv,
 *         object   is a jobject,
 *         id       is the field ID of the String field to be extracted, and
 *         var      is the char * variable that will point to the string.
 *
 * Uses of these macros may be nested as long as each WITH_.._STRING macro
 * declares a unique variable.
 */

#define WITH_PLATFORM_STRING(env, strexp, var)                                \
    if (1) {                                                                  \
        const char *var;                                                      \
        jstring _##var##str = (strexp);                                       \
        if (_##var##str == NULL) {                                            \
            JNU_ThrowNullPointerException((env), NULL);                       \
            goto _##var##end;                                                 \
        }                                                                     \
        var = JNU_GetStringPlatformChars((env), _##var##str, NULL);           \
        if (var == NULL) goto _##var##end;

#define WITH_FIELD_PLATFORM_STRING(env, object, id, var)                      \
    WITH_PLATFORM_STRING(env,                                                 \
                         ((object == NULL)                                    \
                          ? NULL                                              \
                          : (*(env))->GetObjectField((env), (object), (id))), \
                         var)

#define END_PLATFORM_STRING(env, var)                                         \
        JNU_ReleaseStringPlatformChars(env, _##var##str, var);                \
    _##var##end: ;                                                            \
    } else ((void)NULL)


/* Macros for transforming Java Strings into native Unicode strings.
 * Works analogously to WITH_PLATFORM_STRING.
 */

#define WITH_UNICODE_STRING(env, strexp, var)                                 \
    if (1) {                                                                  \
        const jchar *var;                                                     \
        jstring _##var##str = (strexp);                                       \
        if (_##var##str == NULL) {                                            \
            JNU_ThrowNullPointerException((env), NULL);                       \
            goto _##var##end;                                                 \
        }                                                                     \
        var = (*(env))->GetStringChars((env), _##var##str, NULL);             \
        if (var == NULL) goto _##var##end;

#define END_UNICODE_STRING(env, var)                                          \
        (*(env))->ReleaseStringChars(env, _##var##str, var);                  \
    _##var##end: ;                                                            \
    } else ((void)NULL)
