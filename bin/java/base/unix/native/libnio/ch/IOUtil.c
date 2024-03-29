/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

#include <sys/types.h>
#include <string.h>
#include <sys/resource.h>

#include "jni.h"
#include "jni_util.h"
#include "jvm.h"
#include "jlong.h"
#include "sun_nio_ch_IOUtil.h"
#include "java_lang_Integer.h"
#include "java_lang_Long.h"
#include "nio.h"
#include "nio_util.h"

static jfieldID fd_fdID;        /* for jint 'fd' in java.io.FileDescriptor */


JNIEXPORT void JNICALL
Java_sun_nio_ch_IOUtil_initIDs(JNIEnv *env, jclass clazz)
{
    CHECK_NULL(clazz = (*env)->FindClass(env, "java/io/FileDescriptor"));
    CHECK_NULL(fd_fdID = (*env)->GetFieldID(env, clazz, "fd", "I"));
}

JNIEXPORT jboolean JNICALL
Java_sun_nio_ch_IOUtil_randomBytes(JNIEnv *env, jclass clazz,
                                  jbyteArray randArray)
{
    JNU_ThrowByName(env, "java/lang/UnsupportedOperationException", NULL);
    return JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_sun_nio_ch_IOUtil_fdVal(JNIEnv *env, jclass clazz, jobject fdo)
{
    return (*env)->GetIntField(env, fdo, fd_fdID);
}

JNIEXPORT void JNICALL
Java_sun_nio_ch_IOUtil_setfdVal(JNIEnv *env, jclass clazz, jobject fdo, jint val)
{
    setfdval(env, fdo, val);
}

static int
configureBlocking(int fd, jboolean blocking)
{
    int flags = fcntl(fd, F_GETFL);
    int newflags = blocking ? (flags & ~O_NONBLOCK) : (flags | O_NONBLOCK);

    return (flags == newflags) ? 0 : fcntl(fd, F_SETFL, newflags);
}

JNIEXPORT void JNICALL
Java_sun_nio_ch_IOUtil_configureBlocking(JNIEnv *env, jclass clazz,
                                         jobject fdo, jboolean blocking)
{
    if (configureBlocking(fdval(env, fdo), blocking) < 0)
        JNU_ThrowIOExceptionWithLastError(env, "Configure blocking failed");
}

JNIEXPORT jlong JNICALL
Java_sun_nio_ch_IOUtil_makePipe(JNIEnv *env, jobject this, jboolean blocking)
{
    int fd[2];

    if (pipe(fd) < 0) {
        JNU_ThrowIOExceptionWithLastError(env, "Pipe failed");
        return 0;
    }
    if (blocking == JNI_FALSE) {
        if ((configureBlocking(fd[0], JNI_FALSE) < 0)
            || (configureBlocking(fd[1], JNI_FALSE) < 0)) {
            JNU_ThrowIOExceptionWithLastError(env, "Configure blocking failed");
            close(fd[0]);
            close(fd[1]);
            return 0;
        }
    }
    return ((jlong) fd[0] << 32) | (jlong) fd[1];
}

JNIEXPORT jint JNICALL
Java_sun_nio_ch_IOUtil_write1(JNIEnv *env, jclass cl, jint fd, jbyte b)
{
    char c = (char)b;
    return convertReturnVal(env, write(fd, &c, 1), JNI_FALSE);
}

JNIEXPORT jboolean JNICALL
Java_sun_nio_ch_IOUtil_drain(JNIEnv *env, jclass cl, jint fd)
{
    char buf[16];
    int tn = 0;

    for (;;) {
        int n = read(fd, buf, sizeof(buf));
        tn += n;
        if ((n < 0) && (errno != EAGAIN && errno != EWOULDBLOCK))
            JNU_ThrowIOExceptionWithLastError(env, "Drain");
        if (n == (int)sizeof(buf))
            continue;
        return (tn > 0) ? JNI_TRUE : JNI_FALSE;
    }
}

JNIEXPORT jint JNICALL
Java_sun_nio_ch_IOUtil_drain1(JNIEnv *env, jclass cl, jint fd)
{
    int res;
    char buf[1];

    res = read(fd, buf, 1);
    if (res < 0) {
        if (errno == EAGAIN || errno == EWOULDBLOCK) {
            res = 0;
        } else if (errno == EINTR) {
            return IOS_INTERRUPTED;
        } else {
            JNU_ThrowIOExceptionWithLastError(env, "read");
            return IOS_THROWN;
        }
    }
    return res;
}

JNIEXPORT jint JNICALL
Java_sun_nio_ch_IOUtil_fdLimit(JNIEnv *env, jclass this)
{
    struct rlimit rlp;
    if (getrlimit(RLIMIT_NOFILE, &rlp) < 0) {
        JNU_ThrowIOExceptionWithLastError(env, "getrlimit failed");
        return -1;
    }
    if (rlp.rlim_max == RLIM_INFINITY ||
        rlp.rlim_max > (rlim_t)java_lang_Integer_MAX_VALUE) {
        return java_lang_Integer_MAX_VALUE;
    } else {
        return (jint)rlp.rlim_max;
    }
}

JNIEXPORT jint JNICALL
Java_sun_nio_ch_IOUtil_iovMax(JNIEnv *env, jclass this)
{
    jlong iov_max = sysconf(_SC_IOV_MAX);
    if (iov_max == -1)
        iov_max = 16;
    return (jint)iov_max;
}

JNIEXPORT jlong JNICALL
Java_sun_nio_ch_IOUtil_writevMax(JNIEnv *env, jclass this)
{
#if defined(MACOSX) || defined(__linux__)
    //
    // The man pages of writev() on both Linux and macOS specify this
    // constraint on the sum of all byte lengths in the iovec array:
    //
    // [EINVAL] The sum of the iov_len values in the iov array
    //          overflows a 32-bit integer.
    //
    // As of macOS 11 Big Sur, Darwin version 20, writev() started to
    // actually enforce the constraint which had been previously ignored.
    //
    // In practice on Linux writev() has been observed not to write more
    // than 0x7fff0000 (aarch64) or 0x7ffff000 (x64) bytes in one call.
    //
    return java_lang_Integer_MAX_VALUE;
#else
    return java_lang_Long_MAX_VALUE;
#endif
}

/* Declared in nio_util.h for use elsewhere in NIO */

jint
convertReturnVal(JNIEnv *env, jint n, jboolean reading)
{
    if (n > 0) /* Number of bytes written */
        return n;
    else if (n == 0) {
        if (reading) {
            return IOS_EOF; /* EOF is -1 in javaland */
        } else {
            return 0;
        }
    }
    else if (errno == EAGAIN || errno == EWOULDBLOCK)
        return IOS_UNAVAILABLE;
    else if (errno == EINTR)
        return IOS_INTERRUPTED;
    else {
        const char *msg = reading ? "Read failed" : "Write failed";
        JNU_ThrowIOExceptionWithLastError(env, msg);
        return IOS_THROWN;
    }
}

/* Declared in nio_util.h for use elsewhere in NIO */

jlong
convertLongReturnVal(JNIEnv *env, jlong n, jboolean reading)
{
    if (n > 0) /* Number of bytes written */
        return n;
    else if (n == 0) {
        if (reading) {
            return IOS_EOF; /* EOF is -1 in javaland */
        } else {
            return 0;
        }
    }
    else if (errno == EAGAIN || errno == EWOULDBLOCK)
        return IOS_UNAVAILABLE;
    else if (errno == EINTR)
        return IOS_INTERRUPTED;
    else {
        const char *msg = reading ? "Read failed" : "Write failed";
        JNU_ThrowIOExceptionWithLastError(env, msg);
        return IOS_THROWN;
    }
}

jint
fdval(JNIEnv *env, jobject fdo)
{
    return (*env)->GetIntField(env, fdo, fd_fdID);
}

void
setfdval(JNIEnv *env, jobject fdo, jint val) {
    (*env)->SetIntField(env, fdo, fd_fdID, val);
}


