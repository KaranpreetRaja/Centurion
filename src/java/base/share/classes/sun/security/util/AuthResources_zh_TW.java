/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.util;

/**
 * <p> This class represents the <code>ResourceBundle</code>
 * for the following packages:
 *
 * <ol>
 * <li> com.sun.security.auth
 * <li> com.sun.security.auth.login
 * </ol>
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
public class AuthResources_zh_TW extends java.util.ListResourceBundle {

    private static final Object[][] contents = {

        // NT principals
        {"invalid.null.input.value", "\u7121\u6548\u7A7A\u503C\u8F38\u5165: {0}"},
        {"NTDomainPrincipal.name", "NTDomainPrincipal: {0}"},
        {"NTNumericCredential.name", "NTNumericCredential: {0}"},
        {"Invalid.NTSid.value", "\u7121\u6548 NTSid \u503C"},
        {"NTSid.name", "NTSid: {0}"},
        {"NTSidDomainPrincipal.name", "NTSidDomainPrincipal: {0}"},
        {"NTSidGroupPrincipal.name", "NTSidGroupPrincipal: {0}"},
        {"NTSidPrimaryGroupPrincipal.name", "NTSidPrimaryGroupPrincipal: {0}"},
        {"NTSidUserPrincipal.name", "NTSidUserPrincipal: {0}"},
        {"NTUserPrincipal.name", "NTUserPrincipal: {0}"},

        // UnixPrincipals
        {"UnixNumericGroupPrincipal.Primary.Group.name",
                "UnixNumericGroupPrincipal [\u4E3B\u7FA4\u7D44]: {0}"},
        {"UnixNumericGroupPrincipal.Supplementary.Group.name",
                "UnixNumericGroupPrincipal [\u9644\u52A0\u7FA4\u7D44]: {0}"},
        {"UnixNumericUserPrincipal.name", "UnixNumericUserPrincipal: {0}"},
        {"UnixPrincipal.name", "UnixPrincipal: {0}"},

        // com.sun.security.auth.login.ConfigFile
        {"Unable.to.properly.expand.config", "\u7121\u6CD5\u9069\u7576\u5730\u64F4\u5145 {0}"},
        {"extra.config.No.such.file.or.directory.",
                "{0} (\u6C92\u6709\u6B64\u6A94\u6848\u6216\u76EE\u9304)"},
        {"Configuration.Error.No.such.file.or.directory",
                "\u7D44\u614B\u932F\u8AA4:\n\t\u7121\u6B64\u6A94\u6848\u6216\u76EE\u9304"},
        {"Configuration.Error.Invalid.control.flag.flag",
                "\u7D44\u614B\u932F\u8AA4:\n\t\u7121\u6548\u7684\u63A7\u5236\u65D7\u6A19\uFF0C{0}"},
        {"Configuration.Error.Can.not.specify.multiple.entries.for.appName",
            "\u7D44\u614B\u932F\u8AA4: \n\t\u7121\u6CD5\u6307\u5B9A\u591A\u91CD\u9805\u76EE {0}"},
        {"Configuration.Error.expected.expect.read.end.of.file.",
                "\u7D44\u614B\u932F\u8AA4: \n\t\u9810\u671F\u7684 [{0}], \u8B80\u53D6 [end of file]"},
        {"Configuration.Error.Line.line.expected.expect.found.value.",
            "\u7D44\u614B\u932F\u8AA4: \n\t\u884C {0}: \u9810\u671F\u7684 [{1}], \u767C\u73FE [{2}]"},
        {"Configuration.Error.Line.line.expected.expect.",
            "\u7D44\u614B\u932F\u8AA4: \n\t\u884C {0}: \u9810\u671F\u7684 [{1}]"},
        {"Configuration.Error.Line.line.system.property.value.expanded.to.empty.value",
            "\u7D44\u614B\u932F\u8AA4: \n\t\u884C {0}: \u7CFB\u7D71\u5C6C\u6027 [{1}] \u64F4\u5145\u81F3\u7A7A\u503C"},

        // com.sun.security.auth.module.JndiLoginModule
        {"username.","\u4F7F\u7528\u8005\u540D\u7A31: "},
        {"password.","\u5BC6\u78BC: "},

        // com.sun.security.auth.module.KeyStoreLoginModule
        {"Please.enter.keystore.information",
                "\u8ACB\u8F38\u5165\u91D1\u9470\u5132\u5B58\u5EAB\u8CC7\u8A0A"},
        {"Keystore.alias.","\u91D1\u9470\u5132\u5B58\u5EAB\u5225\u540D: "},
        {"Keystore.password.","\u91D1\u9470\u5132\u5B58\u5EAB\u5BC6\u78BC: "},
        {"Private.key.password.optional.",
            "\u79C1\u4EBA\u91D1\u9470\u5BC6\u78BC (\u9078\u64C7\u6027\u7684): "},

        // com.sun.security.auth.module.Krb5LoginModule
        {"Kerberos.username.defUsername.",
                "Kerberos \u4F7F\u7528\u8005\u540D\u7A31 [{0}]: "},
        {"Kerberos.password.for.username.",
                "Kerberos \u5BC6\u78BC {0}: "},
    };

    /**
     * Returns the contents of this <code>ResourceBundle</code>.
     *
     * <p>
     *
     * @return the contents of this <code>ResourceBundle</code>.
     */
    public Object[][] getContents() {
        return contents;
    }
}
