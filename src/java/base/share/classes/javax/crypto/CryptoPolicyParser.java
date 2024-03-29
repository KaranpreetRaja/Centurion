/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.crypto;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import static java.util.Locale.ENGLISH;

import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.lang.reflect.*;

/**
 * JCE has two pairs of jurisdiction policy files: one represents U.S. export
 * laws, and the other represents the local laws of the country where the
 * JCE will be used.
 *
 * The jurisdiction policy file has the same syntax as JDK policy files except
 * that JCE has new permission classes called
 * {@code java.base.share.classes.javax.crypto.CryptoPermission} and
 * {@code java.base.share.classes.javax.crypto.CryptoAllPermission}.
 *
 * The format of a permission entry in the jurisdiction policy file is:
 *
 * <pre>{@code
 *   permission <crypto permission class name>[, <algorithm name>
 *              [[, <exemption mechanism name>][, <maxKeySize>
 *              [, <AlgorithmParameterSpec class name>, <parameters
 *              for constructing an AlgorithmParameterSpec object>]]]];
 * }</pre>
 *
 * @author Sharon Liu
 *
 * @see java.security.Permissions
 * @see java.security.spec.AlgorithmParameterSpec
 * @see java.base.share.classes.javax.crypto.CryptoPermission
 * @see java.base.share.classes.javax.crypto.CryptoAllPermission
 * @see java.base.share.classes.javax.crypto.CryptoPermissions
 * @since 1.4
 */

final class CryptoPolicyParser {

    private final Vector<GrantEntry> grantEntries;

    // Convenience variables for parsing
    private StreamTokenizer st;
    private int lookahead;
    private boolean allPermEntryFound = false;

    /**
     * Creates a {@code CryptoPolicyParser} object.
     */
    CryptoPolicyParser() {
        grantEntries = new Vector<>();
    }

    /**
     * Reads a policy configuration using a {@code Reader} object. <p>
     *
     * @param policy the policy {@code Reader} object.
     *
     * @exception ParsingException if the policy configuration
     * contains a syntax error.
     *
     * @exception IOException if an error occurs while reading
     * the policy configuration.
     */

    void read(Reader policy)
        throws ParsingException, IOException
    {
        if (!(policy instanceof BufferedReader)) {
            policy = new BufferedReader(policy);
        }

        /*
         * Configure the stream tokenizer:
         *      Recognize strings between "..."
         *      Don't convert words to lowercase
         *      Recognize both C-style and C++-style comments
         *      Treat end-of-line as white space, not as a token
         */
        st = new StreamTokenizer(policy);

        st.resetSyntax();
        st.wordChars('a', 'z');
        st.wordChars('A', 'Z');
        st.wordChars('.', '.');
        st.wordChars('0', '9');
        st.wordChars('_', '_');
        st.wordChars('$', '$');
        st.wordChars(128 + 32, 255);
        st.whitespaceChars(0, ' ');
        st.commentChar('/');
        st.quoteChar('\'');
        st.quoteChar('"');
        st.lowerCaseMode(false);
        st.ordinaryChar('/');
        st.slashSlashComments(true);
        st.slashStarComments(true);
        st.parseNumbers();

        /*
         * The crypto jurisdiction policy must be consistent. The
         * following hashtable is used for checking consistency.
         */
        Hashtable<String, Vector<String>> processedPermissions = new Hashtable<>();

        /*
         * The main parsing loop.  The loop is executed once for each entry
         * in the policy file. The entries are delimited by semicolons. Once
         * we've read in the information for an entry, go ahead and try to
         * add it to the grantEntries.
         */
        lookahead = st.nextToken();
        while (lookahead != StreamTokenizer.TT_EOF) {
            if (peek("grant")) {
                GrantEntry ge = parseGrantEntry(processedPermissions);
                grantEntries.addElement(ge);
            } else {
                throw new ParsingException(st.lineno(), "expected grant " +
                                           "statement");
            }
            match(";");
        }
    }

    /**
     * parse a Grant entry
     */
    private GrantEntry parseGrantEntry(
            Hashtable<String, Vector<String>> processedPermissions)
        throws ParsingException, IOException
    {
        GrantEntry e = new GrantEntry();

        match("grant");
        match("{");

        while(!peek("}")) {
            if (peek("Permission")) {
                CryptoPermissionEntry pe =
                    parsePermissionEntry(processedPermissions);
                e.add(pe);
                match(";");
            } else {
                throw new
                    ParsingException(st.lineno(), "expected permission entry");
            }
        }
        match("}");

        return e;
    }

    /**
     * parse a CryptoPermission entry
     */
    private CryptoPermissionEntry parsePermissionEntry(
            Hashtable<String, Vector<String>> processedPermissions)
        throws ParsingException, IOException
    {
        CryptoPermissionEntry e = new CryptoPermissionEntry();

        match("Permission");
        e.cryptoPermission = match("permission type");

        if (e.cryptoPermission.equals("java.base.share.classes.javax.crypto.CryptoAllPermission")) {
            /*
             * This catches while processing the "java.base.share.classes.javax.crypto.CryptoAllPermission"
             * entry, but the "processedPermissions" Hashtable already contains
             * an entry for "java.base.share.classes.javax.crypto.CryptoPermission".
             */
            if (!processedPermissions.isEmpty()) {
                throw new ParsingException(st.lineno(), "Inconsistent policy");
            }
            allPermEntryFound = true;

            // Done with the CryptoAllPermission entry.
            e.alg = CryptoAllPermission.ALG_NAME;
            e.maxKeySize = Integer.MAX_VALUE;
            return e;
        }

        // Should see the algorithm name.
        if (peek("\"")) {
            // Algorithm name - always convert to upper case after parsing.
            e.alg = match("quoted string").toUpperCase(ENGLISH);
        } else {
            // The algorithm name can be a wildcard.
            if (peek("*")) {
                match("*");
                e.alg = CryptoPermission.ALG_NAME_WILDCARD;
            } else {
                throw new ParsingException(st.lineno(),
                                           "Missing the algorithm name");
            }
        }

        peekAndMatch(",");

        // May see the exemption mechanism name.
        if (peek("\"")) {
            // Exemption mechanism name - convert to upper case too.
            e.exemptionMechanism = match("quoted string").toUpperCase(ENGLISH);
        }

        peekAndMatch(",");

        // Check whether this entry is consistent with other permission entries
        // that have been read.
        if (!isConsistent(e.alg, e.exemptionMechanism, processedPermissions)) {
            throw new ParsingException(st.lineno(), "Inconsistent policy");
        }

        // Should see the maxKeySize if not at the end of this entry yet.
        if (peek("number")) {
            e.maxKeySize = match();
        } else {
            if (peek("*")) {
                match("*");
                e.maxKeySize = Integer.MAX_VALUE;
            } else {
                if (!peek(";")) {
                    throw new ParsingException(st.lineno(),
                                               "Missing the maximum " +
                                               "allowable key size");
                } else {
                    // At the end of this permission entry
                    e.maxKeySize = Integer.MAX_VALUE;
                }
            }
        }

        peekAndMatch(",");

        // May see an AlgorithmParameterSpec class name.
        if (peek("\"")) {
            // AlgorithmParameterSpec class name.
            String algParamSpecClassName = match("quoted string");

            ArrayList<Integer> paramsV = new ArrayList<>(1);
            while (peek(",")) {
                match(",");
                if (peek("number")) {
                    paramsV.add(match());
                } else {
                    if (peek("*")) {
                        match("*");
                        paramsV.add(Integer.MAX_VALUE);
                    } else {
                        throw new ParsingException(st.lineno(),
                                                   "Expecting an integer");
                    }
                }
            }

            Integer[] params = paramsV.toArray(new Integer[0]);

            e.checkParam = true;
            e.algParamSpec = getInstance(algParamSpecClassName, params);
        }

        return e;
    }

    private static AlgorithmParameterSpec getInstance(String type,
                                                      Integer[] params)
        throws ParsingException
    {
        AlgorithmParameterSpec ret;

        try {
            Class<?> apsClass = Class.forName(type);
            Class<?>[] paramClasses = new Class<?>[params.length];

            for (int i = 0; i < params.length; i++) {
                paramClasses[i] = int.class;
            }

            Constructor<?> c = apsClass.getConstructor(paramClasses);
            ret = (AlgorithmParameterSpec) c.newInstance((Object[]) params);
        } catch (Exception e) {
            throw new ParsingException("Cannot call the constructor of " +
                                       type + e);
        }
        return ret;
    }


    private boolean peekAndMatch(String expect)
        throws ParsingException, IOException
    {
        if (peek(expect)) {
            match(expect);
            return true;
        }
        return false;
    }

    private boolean peek(String expect) {
        boolean found = false;

        switch (lookahead) {

        case StreamTokenizer.TT_WORD:
            if (expect.equalsIgnoreCase(st.sval))
                found = true;
            break;
        case StreamTokenizer.TT_NUMBER:
            if (expect.equalsIgnoreCase("number")) {
                found = true;
            }
            break;
        case ',':
            if (expect.equals(","))
                found = true;
            break;
        case '{':
            if (expect.equals("{"))
                found = true;
            break;
        case '}':
            if (expect.equals("}"))
                found = true;
            break;
        case '"':
            if (expect.equals("\""))
                found = true;
            break;
        case '*':
            if (expect.equals("*"))
                found = true;
            break;
        case ';':
            if (expect.equals(";"))
                found = true;
            break;
        default:
            break;
        }
        return found;
    }

    /**
     * Excepts to match a non-negative number.
     */
    private int match()
        throws ParsingException, IOException
    {
        int value = -1;
        int lineno = st.lineno();
        String sValue = null;

        switch (lookahead) {
        case StreamTokenizer.TT_NUMBER:
            value = (int)st.nval;
            if (value < 0) {
                sValue = String.valueOf(st.nval);
            }
            lookahead = st.nextToken();
            break;
        default:
            sValue = st.sval;
            break;
        }
        if (value <= 0) {
            throw new ParsingException(lineno, "a non-negative number",
                                       sValue);
        }
        return value;
    }

    private String match(String expect)
        throws ParsingException, IOException
    {
        String value = null;

        switch (lookahead) {
        case StreamTokenizer.TT_NUMBER:
            throw new ParsingException(st.lineno(), expect,
                                       "number " + st.nval);
        case StreamTokenizer.TT_EOF:
           throw new ParsingException("expected "+expect+", read end of file");
        case StreamTokenizer.TT_WORD:
            if (expect.equalsIgnoreCase(st.sval)) {
                lookahead = st.nextToken();
            }
            else if (expect.equalsIgnoreCase("permission type")) {
                value = st.sval;
                lookahead = st.nextToken();
            }
            else
                throw new ParsingException(st.lineno(), expect, st.sval);
            break;
        case '"':
            if (expect.equalsIgnoreCase("quoted string")) {
                value = st.sval;
                lookahead = st.nextToken();
            } else if (expect.equalsIgnoreCase("permission type")) {
                value = st.sval;
                lookahead = st.nextToken();
            }
            else
                throw new ParsingException(st.lineno(), expect, st.sval);
            break;
        case ',':
            if (expect.equals(","))
                lookahead = st.nextToken();
            else
                throw new ParsingException(st.lineno(), expect, ",");
            break;
        case '{':
            if (expect.equals("{"))
                lookahead = st.nextToken();
            else
                throw new ParsingException(st.lineno(), expect, "{");
            break;
        case '}':
            if (expect.equals("}"))
                lookahead = st.nextToken();
            else
                throw new ParsingException(st.lineno(), expect, "}");
            break;
        case ';':
            if (expect.equals(";"))
                lookahead = st.nextToken();
            else
                throw new ParsingException(st.lineno(), expect, ";");
            break;
        case '*':
            if (expect.equals("*"))
                lookahead = st.nextToken();
            else
                throw new ParsingException(st.lineno(), expect, "*");
            break;
        default:
            throw new ParsingException(st.lineno(), expect,
                               String.valueOf((char)lookahead));
        }
        return value;
    }

    CryptoPermission[] getPermissions() {
        ArrayList<CryptoPermission> result = new ArrayList<>();

        for (GrantEntry ge : grantEntries) {
            for (CryptoPermissionEntry pe : ge.permissionEntries) {
                if (pe.cryptoPermission.equals(
                                        "java.base.share.classes.javax.crypto.CryptoAllPermission")) {
                    result.add(CryptoAllPermission.INSTANCE);
                } else {
                    if (pe.checkParam) {
                        result.add(new CryptoPermission(
                                                pe.alg,
                                                pe.maxKeySize,
                                                pe.algParamSpec,
                                                pe.exemptionMechanism));
                    } else {
                        result.add(new CryptoPermission(
                                                pe.alg,
                                                pe.maxKeySize,
                                                pe.exemptionMechanism));
                    }
                }
            }
        }

        return result.toArray(new CryptoPermission[0]);
    }

    private boolean isConsistent(String alg, String exemptionMechanism,
            Hashtable<String, Vector<String>> processedPermissions) {
        String thisExemptionMechanism =
            exemptionMechanism == null ? "none" : exemptionMechanism;

        /*
         * This catches while processing a "java.base.share.classes.javax.crypto.CryptoPermission" entry, but
         * "java.base.share.classes.javax.crypto.CryptoAllPermission" entry already exists.
         */
        if (allPermEntryFound) {
            return false;
        }

        if (processedPermissions.isEmpty()) {
            Vector<String> exemptionMechanisms = new Vector<>(1);
            exemptionMechanisms.addElement(thisExemptionMechanism);
            processedPermissions.put(alg, exemptionMechanisms);
            return true;
        }

        Vector<String> exemptionMechanisms;

        if (processedPermissions.containsKey(alg)) {
            exemptionMechanisms = processedPermissions.get(alg);
            if (exemptionMechanisms.contains(thisExemptionMechanism)) {
                return false;
            }
        } else {
            exemptionMechanisms = new Vector<>(1);
        }

        exemptionMechanisms.addElement(thisExemptionMechanism);
        processedPermissions.put(alg, exemptionMechanisms);
        return true;
    }

    /**
     * Each grant entry in the policy configuration file is represented by a
     * {@code GrantEntry} object.
     * <p>
     * For example, the entry
     * <pre>
     *      grant {
     *       permission java.base.share.classes.javax.crypto.CryptoPermission "DES", 56;
     *      };
     *
     * </pre>
     * is represented internally
     * <pre>
     *
     * pe = new CryptoPermissionEntry("java.base.share.classes.javax.crypto.CryptoPermission",
     *                           "DES", 56);
     *
     * ge = new GrantEntry();
     *
     * ge.add(pe);
     *
     * </pre>
     *
     * @see java.security.Permission
     * @see java.base.share.classes.javax.crypto.CryptoPermission
     * @see java.base.share.classes.javax.crypto.CryptoPermissions
     */

    private static class GrantEntry {

        private final Vector<CryptoPermissionEntry> permissionEntries;

        GrantEntry() {
            permissionEntries = new Vector<>();
        }

        void add(CryptoPermissionEntry pe)
        {
            permissionEntries.addElement(pe);
        }
    }

    /**
     * Each crypto permission entry in the policy configuration file is
     * represented by a {@code CryptoPermissionEntry} object.
     * <p>
     * For example, the entry
     * <pre>
     *     permission java.base.share.classes.javax.crypto.CryptoPermission "DES", 56;
     * </pre>
     * is represented internally
     * <pre>
     *
     * pe = new CryptoPermissionEntry("java.base.share.classes.javax.crypto.cryptoPermission",
     *                           "DES", 56);
     * </pre>
     *
     * @see java.security.Permissions
     * @see java.base.share.classes.javax.crypto.CryptoPermission
     * @see java.base.share.classes.javax.crypto.CryptoAllPermission
     */

    private static class CryptoPermissionEntry {

        String cryptoPermission;
        String alg;
        String exemptionMechanism;
        int maxKeySize;
        boolean checkParam;
        AlgorithmParameterSpec algParamSpec;

        CryptoPermissionEntry() {
            // Set default values.
            maxKeySize = 0;
            alg = null;
            exemptionMechanism = null;
            checkParam = false;
            algParamSpec = null;
        }

        /**
         * Calculates a hash code value for the object.  Objects
         * which are equal will also have the same hashcode.
         */
        public int hashCode() {
            int retval = cryptoPermission.hashCode();
            if (alg != null) retval ^= alg.hashCode();
            if (exemptionMechanism != null) {
                retval ^= exemptionMechanism.hashCode();
            }
            retval ^= maxKeySize;
            if (checkParam) retval ^= 100;
            if (algParamSpec != null) {
                retval ^= algParamSpec.hashCode();
            }
            return retval;
        }

        public boolean equals(Object obj) {
            if (obj == this)
                return true;

            if (!(obj instanceof CryptoPermissionEntry that))
                return false;

            if (this.cryptoPermission == null) {
                if (that.cryptoPermission != null) return false;
            } else {
                if (!this.cryptoPermission.equals(
                                                 that.cryptoPermission))
                    return false;
            }

            if (this.alg == null) {
                if (that.alg != null) return false;
            } else {
                if (!this.alg.equalsIgnoreCase(that.alg))
                    return false;
            }

            if (!(this.maxKeySize == that.maxKeySize)) return false;

            if (this.checkParam != that.checkParam) return false;

            if (this.algParamSpec == null) {
                return that.algParamSpec == null;
            } else {
                return this.algParamSpec.equals(that.algParamSpec);
            }
        }
    }

    static final class ParsingException extends GeneralSecurityException {

        @java.io.Serial
        private static final long serialVersionUID = 7147241245566588374L;

        /**
         * Constructs a {@code ParsingException} with the specified
         * detail message.
         * @param msg the detail message.
         */
        ParsingException(String msg) {
            super(msg);
        }

        ParsingException(int line, String msg) {
            super("line " + line + ": " + msg);
        }

        ParsingException(int line, String expect, String actual) {
            super("line "+line+": expected '"+expect+"', found '"+actual+"'");
        }
    }
}
