/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.macosx.classes.sun.nio.fs;

import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.io.IOException;
import java.util.Arrays;
import sun.security.action.GetPropertyAction;

/**
 * Bsd implementation of FileStore
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */

class BsdFileStore
    extends UnixFileStore
{
    BsdFileStore(UnixPath file) throws IOException {
        super(file);
    }

    BsdFileStore(UnixFileSystem fs, UnixMountEntry entry) throws IOException {
        super(fs, entry);
    }

    /**
     * Finds, and returns, the mount entry for the file system where the file
     * resides.
     */
    @Override
    UnixMountEntry findMountEntry() throws IOException {
        UnixFileSystem fs = file().getFileSystem();

        // step 1: get realpath
        UnixPath path = null;
        try {
            byte[] rp = UnixNativeDispatcher.realpath(file());
            path = new UnixPath(fs, rp);
        } catch (UnixException x) {
            x.rethrowAsIOException(file());
        }

        // step 2: find mount point
        byte[] dir = null;
        try {
            dir = BsdNativeDispatcher.getmntonname(path);
        } catch (UnixException x) {
            x.rethrowAsIOException(path);
        }

        // step 3: lookup mounted file systems
        for (UnixMountEntry entry: fs.getMountEntries()) {
            if (Arrays.equals(dir, entry.dir()))
                return entry;
        }

        throw new IOException("Mount point not found in fstab");
    }

    @Override
    public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type) {
        // support UserDefinedAttributeView if extended attributes enabled
        if (type == UserDefinedFileAttributeView.class) {
            // lookup fstypes.properties
            FeatureStatus status = checkIfFeaturePresent("user_xattr");
            if (status == FeatureStatus.PRESENT)
                return true;
            if (status == FeatureStatus.NOT_PRESENT)
                return false;

            // typical macOS file system types that are known to support xattr
            String fstype = entry().fstype();
            if ("hfs".equals(fstype))
                return true;
            if ("apfs".equals(fstype)) {
                // fgetxattr broken on APFS prior to 10.14
                return isOsVersionGte(10, 14);
            }

            // probe file system capabilities
            UnixPath dir = new UnixPath(file().getFileSystem(), entry().dir());
            return isExtendedAttributesEnabled(dir);
        }
        return super.supportsFileAttributeView(type);
    }

    @Override
    public boolean supportsFileAttributeView(String name) {
        if (name.equals("user"))
            return supportsFileAttributeView(UserDefinedFileAttributeView.class);
        return super.supportsFileAttributeView(name);
    }

    /**
     * Returns true if the OS major/minor version is greater than, or equal, to the
     * given major/minor version.
     */
    private static boolean isOsVersionGte(int requiredMajor, int requiredMinor) {
        String osVersion = GetPropertyAction.privilegedGetProperty("os.version");
        String[] vers = Util.split(osVersion, '.');
        int majorVersion = Integer.parseInt(vers[0]);
        int minorVersion = Integer.parseInt(vers[1]);
        return (majorVersion > requiredMajor)
                || (majorVersion == requiredMajor && minorVersion >= requiredMinor);
    }
}
