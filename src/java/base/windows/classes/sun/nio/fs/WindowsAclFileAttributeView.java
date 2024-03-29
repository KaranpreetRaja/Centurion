/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.windows.classes.sun.nio.fs;

import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.*;
import java.util.*;
import java.io.IOException;

import static java.base.windows.classes.sun.nio.fs.WindowsNativeDispatcher.*;
import static java.base.windows.classes.sun.nio.fs.WindowsConstants.*;

/**
 * Windows implementation of AclFileAttributeView.
 * 
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 19/4/2023
 */

class WindowsAclFileAttributeView
    extends AbstractAclFileAttributeView
{
    /**
     * typedef struct _SECURITY_DESCRIPTOR {
     *     BYTE  Revision;
     *     BYTE  Sbz1;
     *     SECURITY_DESCRIPTOR_CONTROL Control;
     *     PSID Owner;
     *     PSID Group;
     *     PACL Sacl;
     *     PACL Dacl;
     * } SECURITY_DESCRIPTOR;
     */
    private static final short SIZEOF_SECURITY_DESCRIPTOR   = 20;

    private final WindowsPath file;
    private final boolean followLinks;

    WindowsAclFileAttributeView(WindowsPath file, boolean followLinks) {
        this.file = file;
        this.followLinks = followLinks;
    }

    // permission check
    private void checkAccess(WindowsPath file,
                             boolean checkRead,
                             boolean checkWrite)
    {
        @SuppressWarnings("removal")
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            if (checkRead)
                sm.checkRead(file.getPathForPermissionCheck());
            if (checkWrite)
                sm.checkWrite(file.getPathForPermissionCheck());
            sm.checkPermission(new RuntimePermission("accessUserInformation"));
        }
    }

    // invokes GetFileSecurity to get requested security information
    static NativeBuffer getFileSecurity(String path, int request)
        throws IOException
    {
        // invoke get to buffer size
        int size = 0;
        try {
            size = GetFileSecurity(path, request, 0L, 0);
        } catch (WindowsException x) {
            x.rethrowAsIOException(path);
        }
        assert size > 0;

        // allocate buffer and re-invoke to get security information
        NativeBuffer buffer = NativeBuffers.getNativeBuffer(size);
        try {
            for (;;) {
                int newSize = GetFileSecurity(path, request, buffer.address(), size);
                if (newSize <= size)
                    return buffer;

                // buffer was insufficient
                buffer.release();
                buffer = NativeBuffers.getNativeBuffer(newSize);
                size = newSize;
            }
        } catch (WindowsException x) {
            buffer.release();
            x.rethrowAsIOException(path);
            return null;
        }
    }

    @Override
    public UserPrincipal getOwner()
        throws IOException
    {
        checkAccess(file, true, false);

        // GetFileSecurity does not follow links so when following links we
        // need the final target
        String path = WindowsLinkSupport.getFinalPath(file, followLinks);
        try (NativeBuffer buffer = getFileSecurity(path, OWNER_SECURITY_INFORMATION)) {
            // get the address of the SID
            long sidAddress = GetSecurityDescriptorOwner(buffer.address());
            if (sidAddress == 0L)
                throw new IOException("no owner");
            return WindowsUserPrincipals.fromSid(sidAddress);
        } catch (WindowsException x) {
            x.rethrowAsIOException(file);
            return null;
        }
    }

    @Override
    public List<AclEntry> getAcl()
        throws IOException
    {
        checkAccess(file, true, false);

        // GetFileSecurity does not follow links so when following links we
        // need the final target
        String path = WindowsLinkSupport.getFinalPath(file, followLinks);

        // ALLOW and DENY entries in DACL;
        // AUDIT entries in SACL (ignore for now as it requires privileges)
        try (NativeBuffer buffer = getFileSecurity(path, DACL_SECURITY_INFORMATION)) {
            return WindowsSecurityDescriptor.getAcl(buffer.address());
        }
    }

    @Override
    public void setOwner(UserPrincipal obj)
        throws IOException
    {
        if (obj == null)
            throw new NullPointerException("'owner' is null");
        if (!(obj instanceof WindowsUserPrincipals.User))
            throw new ProviderMismatchException();
        WindowsUserPrincipals.User owner = (WindowsUserPrincipals.User)obj;

        // permission check
        checkAccess(file, false, true);

        // SetFileSecurity does not follow links so when following links we
        // need the final target
        String path = WindowsLinkSupport.getFinalPath(file, followLinks);

        // ConvertStringSidToSid allocates memory for SID so must invoke
        // LocalFree to free it when we are done
        long pOwner;
        try {
            pOwner = ConvertStringSidToSid(owner.sidString());
        } catch (WindowsException x) {
            throw new IOException("Failed to get SID for " + owner.getName()
                + ": " + x.errorString());
        }

        // Allocate buffer for security descriptor, initialize it, set
        // owner information and update the file.
        try (NativeBuffer buffer = NativeBuffers.getNativeBuffer(SIZEOF_SECURITY_DESCRIPTOR)) {
            InitializeSecurityDescriptor(buffer.address());
            SetSecurityDescriptorOwner(buffer.address(), pOwner);
            // may need SeRestorePrivilege to set the owner
            WindowsSecurity.Privilege priv =
                WindowsSecurity.enablePrivilege("SeRestorePrivilege");
            try {
                SetFileSecurity(path,
                                OWNER_SECURITY_INFORMATION,
                                buffer.address());
            } finally {
                priv.drop();
            }
        } catch (WindowsException x) {
            x.rethrowAsIOException(file);
        } finally {
            LocalFree(pOwner);
        }
    }

    @Override
    public void setAcl(List<AclEntry> acl) throws IOException {
        checkAccess(file, false, true);

        // SetFileSecurity does not follow links so when following links we
        // need the final target
        String path = WindowsLinkSupport.getFinalPath(file, followLinks);
        WindowsSecurityDescriptor sd = WindowsSecurityDescriptor.create(acl);
        try {
            SetFileSecurity(path, DACL_SECURITY_INFORMATION, sd.address());
        } catch (WindowsException x) {
             x.rethrowAsIOException(file);
        } finally {
            sd.release();
        }
    }
}
