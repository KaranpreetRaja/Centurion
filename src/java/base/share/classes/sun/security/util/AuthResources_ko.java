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
public class AuthResources_ko extends java.util.ListResourceBundle {

    private static final Object[][] contents = {

        // NT principals
        {"invalid.null.input.value", "\uBD80\uC801\uD569\uD55C \uB110 \uC785\uB825: {0}"},
        {"NTDomainPrincipal.name", "NTDomainPrincipal: {0}"},
        {"NTNumericCredential.name", "NTNumericCredential: {0}"},
        {"Invalid.NTSid.value", "NTSid \uAC12\uC774 \uBD80\uC801\uD569\uD569\uB2C8\uB2E4."},
        {"NTSid.name", "NTSid: {0}"},
        {"NTSidDomainPrincipal.name", "NTSidDomainPrincipal: {0}"},
        {"NTSidGroupPrincipal.name", "NTSidGroupPrincipal: {0}"},
        {"NTSidPrimaryGroupPrincipal.name", "NTSidPrimaryGroupPrincipal: {0}"},
        {"NTSidUserPrincipal.name", "NTSidUserPrincipal: {0}"},
        {"NTUserPrincipal.name", "NTUserPrincipal: {0}"},

        // UnixPrincipals
        {"UnixNumericGroupPrincipal.Primary.Group.name",
                "UnixNumericGroupPrincipal [\uAE30\uBCF8 \uADF8\uB8F9]: {0}"},
        {"UnixNumericGroupPrincipal.Supplementary.Group.name",
                "UnixNumericGroupPrincipal [\uBCF4\uC870 \uADF8\uB8F9]: {0}"},
        {"UnixNumericUserPrincipal.name", "UnixNumericUserPrincipal: {0}"},
        {"UnixPrincipal.name", "UnixPrincipal: {0}"},

        // com.sun.security.auth.login.ConfigFile
        {"Unable.to.properly.expand.config", "{0}\uC744(\uB97C) \uC81C\uB300\uB85C \uD655\uC7A5\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},
        {"extra.config.No.such.file.or.directory.",
                "{0}(\uD574\uB2F9 \uD30C\uC77C \uB610\uB294 \uB514\uB809\uD1A0\uB9AC\uAC00 \uC5C6\uC2B5\uB2C8\uB2E4.)"},
        {"Configuration.Error.No.such.file.or.directory",
                "\uAD6C\uC131 \uC624\uB958:\n\t\uD574\uB2F9 \uD30C\uC77C \uB610\uB294 \uB514\uB809\uD1A0\uB9AC\uAC00 \uC5C6\uC2B5\uB2C8\uB2E4."},
        {"Configuration.Error.Invalid.control.flag.flag",
                "\uAD6C\uC131 \uC624\uB958:\n\t\uC81C\uC5B4 \uD50C\uB798\uADF8\uAC00 \uBD80\uC801\uD569\uD568, {0}"},
        {"Configuration.Error.Can.not.specify.multiple.entries.for.appName",
            "\uAD6C\uC131 \uC624\uB958:\n\t{0}\uC5D0 \uB300\uD574 \uC5EC\uB7EC \uD56D\uBAA9\uC744 \uC9C0\uC815\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},
        {"Configuration.Error.expected.expect.read.end.of.file.",
                "\uAD6C\uC131 \uC624\uB958:\n\t[{0}]\uC774(\uAC00) \uD544\uC694\uD558\uC9C0\uB9CC [\uD30C\uC77C\uC758 \uB05D]\uC5D0 \uB3C4\uB2EC\uD588\uC2B5\uB2C8\uB2E4."},
        {"Configuration.Error.Line.line.expected.expect.found.value.",
            "\uAD6C\uC131 \uC624\uB958:\n\t{0} \uD589: [{1}]\uC774(\uAC00) \uD544\uC694\uD558\uC9C0\uB9CC [{2}]\uC774(\uAC00) \uBC1C\uACAC\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},
        {"Configuration.Error.Line.line.expected.expect.",
            "\uAD6C\uC131 \uC624\uB958:\n\t{0} \uD589: [{1}]\uC774(\uAC00) \uD544\uC694\uD569\uB2C8\uB2E4."},
        {"Configuration.Error.Line.line.system.property.value.expanded.to.empty.value",
            "\uAD6C\uC131 \uC624\uB958:\n\t{0} \uD589: \uC2DC\uC2A4\uD15C \uC18D\uC131 [{1}]\uC774(\uAC00) \uBE48 \uAC12\uC73C\uB85C \uD655\uC7A5\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},

        // com.sun.security.auth.module.JndiLoginModule
        {"username.","\uC0AC\uC6A9\uC790 \uC774\uB984: "},
        {"password.","\uBE44\uBC00\uBC88\uD638: "},

        // com.sun.security.auth.module.KeyStoreLoginModule
        {"Please.enter.keystore.information",
                "\uD0A4 \uC800\uC7A5\uC18C \uC815\uBCF4\uB97C \uC785\uB825\uD558\uC2ED\uC2DC\uC624."},
        {"Keystore.alias.","\uD0A4 \uC800\uC7A5\uC18C \uBCC4\uCE6D: "},
        {"Keystore.password.","\uD0A4 \uC800\uC7A5\uC18C \uBE44\uBC00\uBC88\uD638: "},
        {"Private.key.password.optional.",
            "\uC804\uC6A9 \uD0A4 \uBE44\uBC00\uBC88\uD638(\uC120\uD0DD\uC0AC\uD56D): "},

        // com.sun.security.auth.module.Krb5LoginModule
        {"Kerberos.username.defUsername.",
                "Kerberos \uC0AC\uC6A9\uC790 \uC774\uB984 [{0}]: "},
        {"Kerberos.password.for.username.",
                "{0}\uC758 Kerberos \uBE44\uBC00\uBC88\uD638: "},
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
