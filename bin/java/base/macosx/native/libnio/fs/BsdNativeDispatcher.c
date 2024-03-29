/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

#include "jni.h"
#include "jni_util.h"
#include "jvm.h"
#include "jlong.h"

#include <sys/param.h>
#include <sys/mount.h>
#ifdef ST_RDONLY
#define statfs statvfs
#define getfsstat getvfsstat
#define f_flags f_flag
#define ISREADONLY ST_RDONLY
#else
#define ISREADONLY MNT_RDONLY
#endif
#include <sys/attr.h>
#include <unistd.h>

#include <stdlib.h>
#include <string.h>
#include <sys/attr.h>
#include <sys/clonefile.h>

/**
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */

static jfieldID entry_name;
static jfieldID entry_dir;
static jfieldID entry_fstype;
static jfieldID entry_options;

struct fsstat_iter {
    struct statfs *buf;
    int pos;
    int nentries;
};

#include "sun_nio_fs_BsdNativeDispatcher.h"

static void throwUnixException(JNIEnv* env, int errnum) {
    jobject x = JNU_NewObjectByName(env, "sun/nio/fs/UnixException",
        "(I)V", errnum);
    if (x != NULL) {
        (*env)->Throw(env, x);
    }
}

/**
 * Initialize jfieldIDs
 */
JNIEXPORT void JNICALL
Java_sun_nio_fs_BsdNativeDispatcher_initIDs(JNIEnv* env, jclass this)
{
    jclass clazz;

    clazz = (*env)->FindClass(env, "sun/nio/fs/UnixMountEntry");
    CHECK_NULL(clazz);
    entry_name = (*env)->GetFieldID(env, clazz, "name", "[B");
    CHECK_NULL(entry_name);
    entry_dir = (*env)->GetFieldID(env, clazz, "dir", "[B");
    CHECK_NULL(entry_dir);
    entry_fstype = (*env)->GetFieldID(env, clazz, "fstype", "[B");
    CHECK_NULL(entry_fstype);
    entry_options = (*env)->GetFieldID(env, clazz, "opts", "[B");
    CHECK_NULL(entry_options);
}

JNIEXPORT jlong JNICALL
Java_sun_nio_fs_BsdNativeDispatcher_getfsstat(JNIEnv* env, jclass this)
{
    int nentries;
    size_t bufsize;
    struct fsstat_iter *iter = malloc(sizeof(*iter));

    if (iter == NULL) {
        JNU_ThrowOutOfMemoryError(env, "native heap");
        return 0;
    }

    iter->pos = 0;
    iter->nentries = 0;
    iter->buf = NULL;

    nentries = getfsstat(NULL, 0, MNT_NOWAIT);

    if (nentries <= 0) {
        free(iter);
        throwUnixException(env, errno);
        return 0;
    }

    // It's possible that a new filesystem gets mounted between
    // the first getfsstat and the second so loop until consistent

    while (nentries != iter->nentries) {
        if (iter->buf != NULL)
            free(iter->buf);

        bufsize = nentries * sizeof(struct statfs);
        iter->nentries = nentries;

        iter->buf = malloc(bufsize);
        if (iter->buf == NULL) {
            free(iter);
            JNU_ThrowOutOfMemoryError(env, "native heap");
            return 0;
        }

        nentries = getfsstat(iter->buf, bufsize, MNT_WAIT);
        if (nentries <= 0) {
            free(iter->buf);
            free(iter);
            throwUnixException(env, errno);
            return 0;
        }
    }

    return (jlong)iter;
}

JNIEXPORT jint JNICALL
Java_sun_nio_fs_BsdNativeDispatcher_fsstatEntry(JNIEnv* env, jclass this,
    jlong value, jobject entry)
{
    struct fsstat_iter *iter = jlong_to_ptr(value);
    jsize len;
    jbyteArray bytes;
    char* name;
    char* dir;
    char* fstype;
    char* options;

    if (iter == NULL || iter->pos >= iter->nentries)
       return -1;

    name = iter->buf[iter->pos].f_mntfromname;
    dir = iter->buf[iter->pos].f_mntonname;
    fstype = iter->buf[iter->pos].f_fstypename;
    if (iter->buf[iter->pos].f_flags & ISREADONLY)
        options="ro";
    else
        options="";

    iter->pos++;

    len = strlen(name);
    bytes = (*env)->NewByteArray(env, len);
    if (bytes == NULL)
        return -1;
    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)name);
    (*env)->SetObjectField(env, entry, entry_name, bytes);

    len = strlen(dir);
    bytes = (*env)->NewByteArray(env, len);
    if (bytes == NULL)
        return -1;
    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)dir);
    (*env)->SetObjectField(env, entry, entry_dir, bytes);

    len = strlen(fstype);
    bytes = (*env)->NewByteArray(env, len);
    if (bytes == NULL)
        return -1;
    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)fstype);
    (*env)->SetObjectField(env, entry, entry_fstype, bytes);

    len = strlen(options);
    bytes = (*env)->NewByteArray(env, len);
    if (bytes == NULL)
        return -1;
    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte*)options);
    (*env)->SetObjectField(env, entry, entry_options, bytes);

    return 0;
}

JNIEXPORT void JNICALL
Java_sun_nio_fs_BsdNativeDispatcher_endfsstat(JNIEnv* env, jclass this, jlong value)
{
    struct fsstat_iter *iter = jlong_to_ptr(value);

    if (iter != NULL) {
        free(iter->buf);
        free(iter);
    }
}

JNIEXPORT jbyteArray JNICALL
Java_sun_nio_fs_BsdNativeDispatcher_getmntonname0(JNIEnv *env, jclass this,
    jlong pathAddress)
{
    struct statfs buf;
    const char* path = (const char*)jlong_to_ptr(pathAddress);

    if (statfs(path, &buf) != 0) {
        throwUnixException(env, errno);
    }

    jsize len = strlen(buf.f_mntonname);
    jbyteArray mntonname = (*env)->NewByteArray(env, len);
    if (mntonname != NULL) {
        (*env)->SetByteArrayRegion(env, mntonname, 0, len,
            (jbyte*)buf.f_mntonname);
    }

    return mntonname;
}

JNIEXPORT jint JNICALL
Java_sun_nio_fs_BsdNativeDispatcher_clonefile0(JNIEnv* env, jclass this,
    jlong srcAddress, jlong dstAddress, jint flags)
{
    const char* src = (const char*)jlong_to_ptr(srcAddress);
    const char* dst = (const char*)jlong_to_ptr(dstAddress);

    int ret = clonefile(src, dst, flags);
    if (ret != 0) {
        throwUnixException(env, errno);
        return ret;
    }

    return 0;
}

size_t initattrlist(jint commonattr, jlong modTime, jlong accTime,
    jlong createTime, const int attrsize, char* buf, struct attrlist *attrList)
{
    int count = 0;

    // attributes are ordered per the getattrlist(2) spec
    if ((commonattr & ATTR_CMN_CRTIME) != 0) {
        struct timespec* t = (struct timespec*)buf;
        t->tv_sec   = createTime / 1000000000;
        t->tv_nsec  = createTime % 1000000000;
        count++;
    }
    if ((commonattr & ATTR_CMN_MODTIME) != 0) {
        struct timespec* t = (struct timespec*)(buf + count*attrsize);
        t->tv_sec   = modTime / 1000000000;
        t->tv_nsec  = modTime % 1000000000;
        count++;
    }
    if ((commonattr & ATTR_CMN_ACCTIME) != 0) {
        struct timespec* t = (struct timespec*)(buf + count*attrsize);
        t->tv_sec   = accTime / 1000000000;
        t->tv_nsec  = accTime % 1000000000;
        count++;
    }

    memset(attrList, 0, sizeof(struct attrlist));
    attrList->bitmapcount = ATTR_BIT_MAP_COUNT;
    attrList->commonattr = commonattr;

    return count*attrsize;
}

JNIEXPORT void JNICALL
Java_sun_nio_fs_BsdNativeDispatcher_setattrlist0(JNIEnv* env, jclass this,
    jlong pathAddress, int commonattr, jlong modTime, jlong accTime,
    jlong createTime, jlong options)
{
    const char* path = (const char*)jlong_to_ptr(pathAddress);
    // attributes must align on 4-byte boundaries per the getattrlist(2) spec
    const int attrsize = ((sizeof(struct timespec) + 3)/4)*4;
    char buf[3*attrsize];

    struct attrlist attrList;
    size_t attrBufSize = initattrlist(commonattr, modTime, accTime, createTime,
                                      attrsize, buf, &attrList);

    if (setattrlist(path, &attrList, (void*)buf, attrBufSize, options) != 0) {
        throwUnixException(env, errno);
    }
}

JNIEXPORT void JNICALL
Java_sun_nio_fs_BsdNativeDispatcher_fsetattrlist0(JNIEnv* env, jclass this,
    jint fd, int commonattr, jlong modTime, jlong accTime,
    jlong createTime, jlong options)
{
    // attributes must align on 4-byte boundaries per the getattrlist(2) spec
    const int attrsize = ((sizeof(struct timespec) + 3)/4)*4;
    char buf[3*attrsize];

    struct attrlist attrList;
    size_t attrBufSize = initattrlist(commonattr, modTime, accTime, createTime,
                                      attrsize, buf, &attrList);

    if (fsetattrlist(fd, &attrList, (void*)buf, attrBufSize, options) != 0) {
        throwUnixException(env, errno);
    }
}
