/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.windows.classes.sun.nio.fs;

import java.io.IOException;

import static java.base.windows.classes.sun.nio.fs.WindowsConstants.*;
import static java.base.windows.classes.sun.nio.fs.WindowsNativeDispatcher.*;

/*
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 19/4/2023
 */

class WindowsUserPrincipals {
    private WindowsUserPrincipals() { }

    static class User implements UserPrincipal {
        // String representation of SID
        private final String sidString;

        // SID type
        private final int sidType;

        // Account name (if available) or SID
        private final String accountName;

        User(String sidString, int sidType, String accountName) {
            this.sidString = sidString;
            this.sidType = sidType;
            this.accountName = accountName;
        }

        // package-private
        String sidString() {
            return sidString;
        }

        @Override
        public String getName() {
            return accountName;
        }

        @Override
        public String toString() {
            String type;
            switch (sidType) {
                case SidTypeUser : type = "User"; break;
                case SidTypeGroup : type = "Group"; break;
                case SidTypeDomain : type = "Domain"; break;
                case SidTypeAlias : type = "Alias"; break;
                case SidTypeWellKnownGroup : type = "Well-known group"; break;
                case SidTypeDeletedAccount : type = "Deleted"; break;
                case SidTypeInvalid : type = "Invalid"; break;
                case SidTypeComputer : type = "Computer"; break;
                default: type = "Unknown";
            }
            return accountName + " (" + type + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!(obj instanceof WindowsUserPrincipals.User))
                return false;
            WindowsUserPrincipals.User other = (WindowsUserPrincipals.User)obj;
            return this.sidString.equals(other.sidString);
        }

        @Override
        public int hashCode() {
            return sidString.hashCode();
        }
    }

    static class Group extends User implements GroupPrincipal {
        Group(String sidString, int sidType, String accountName) {
            super(sidString, sidType, accountName);
        }
    }

    static UserPrincipal fromSid(long sidAddress) throws IOException {
        String sidString;
        try {
            sidString = ConvertSidToStringSid(sidAddress);
            if (sidString == null) {
                // pre-Windows XP system?
                throw new AssertionError();
            }
        } catch (WindowsException x) {
            throw new IOException("Unable to convert SID to String: " +
                x.errorString());
        }

        // lookup account; if not available then use the SID as the name
        Account account = null;
        String name;
        try {
            account = LookupAccountSid(sidAddress);
            name = account.domain() + "\\" + account.name();
        } catch (WindowsException x) {
            name = sidString;
        }

        int sidType = (account == null) ? SidTypeUnknown : account.use();
        if ((sidType == SidTypeGroup) ||
            (sidType == SidTypeWellKnownGroup) ||
            (sidType == SidTypeAlias)) // alias for local group
        {
            return new Group(sidString, sidType, name);
        } else {
            return new User(sidString, sidType, name);
        }
    }

    static UserPrincipal lookup(String name) throws IOException {
        @SuppressWarnings("removal")
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("lookupUserInformation"));
        }

        // invoke LookupAccountName to get buffer size needed for SID
        int size;
        try {
            size = LookupAccountName(name, 0L, 0);
        } catch (WindowsException x) {
            if (x.lastError() == ERROR_NONE_MAPPED)
                throw new UserPrincipalNotFoundException(name);
            throw new IOException(name + ": " + x.errorString());
        }
        assert size > 0;

        // allocate buffer and re-invoke LookupAccountName get SID
        try (NativeBuffer sidBuffer = NativeBuffers.getNativeBuffer(size)) {
            int newSize = LookupAccountName(name, sidBuffer.address(), size);
            if (newSize != size) {
                // can this happen?
                throw new AssertionError("SID change during lookup");
            }

            // return user principal
            return fromSid(sidBuffer.address());
        } catch (WindowsException x) {
            throw new IOException(name + ": " + x.errorString());
        }
    }
}
