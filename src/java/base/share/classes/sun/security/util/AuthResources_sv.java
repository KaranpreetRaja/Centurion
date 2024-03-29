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
public class AuthResources_sv extends java.util.ListResourceBundle {

    private static final Object[][] contents = {

        // NT principals
        {"invalid.null.input.value", "ogiltiga null-indata: {0}"},
        {"NTDomainPrincipal.name", "NTDomainPrincipal: {0}"},
        {"NTNumericCredential.name", "NTNumericCredential: {0}"},
        {"Invalid.NTSid.value", "Ogiltigt NTSid-v\u00E4rde"},
        {"NTSid.name", "NTSid: {0}"},
        {"NTSidDomainPrincipal.name", "NTSidDomainPrincipal: {0}"},
        {"NTSidGroupPrincipal.name", "NTSidGroupPrincipal: {0}"},
        {"NTSidPrimaryGroupPrincipal.name", "NTSidPrimaryGroupPrincipal: {0}"},
        {"NTSidUserPrincipal.name", "NTSidUserPrincipal: {0}"},
        {"NTUserPrincipal.name", "NTUserPrincipal: {0}"},

        // UnixPrincipals
        {"UnixNumericGroupPrincipal.Primary.Group.name",
                "UnixNumericGroupPrincipal [prim\u00E4r grupp]: {0}"},
        {"UnixNumericGroupPrincipal.Supplementary.Group.name",
                "UnixNumericGroupPrincipal [till\u00E4ggsgrupp]: {0}"},
        {"UnixNumericUserPrincipal.name", "UnixNumericUserPrincipal: {0}"},
        {"UnixPrincipal.name", "UnixPrincipal: {0}"},

        // com.sun.security.auth.login.ConfigFile
        {"Unable.to.properly.expand.config", "Kan inte ut\u00F6ka korrekt {0}"},
        {"extra.config.No.such.file.or.directory.",
                "{0} (det finns ingen s\u00E5dan fil eller katalog)"},
        {"Configuration.Error.No.such.file.or.directory",
                "Konfigurationsfel:\n\tFilen eller katalogen finns inte"},
        {"Configuration.Error.Invalid.control.flag.flag",
                "Konfigurationsfel:\n\tOgiltig kontrollflagga, {0}"},
        {"Configuration.Error.Can.not.specify.multiple.entries.for.appName",
            "Konfigurationsfel:\n\tKan inte ange flera poster f\u00F6r {0}"},
        {"Configuration.Error.expected.expect.read.end.of.file.",
                "Konfigurationsfel:\n\tf\u00F6rv\u00E4ntade [{0}], l\u00E4ste [filslut]"},
        {"Configuration.Error.Line.line.expected.expect.found.value.",
            "Konfigurationsfel:\n\tRad {0}: f\u00F6rv\u00E4ntade [{1}], hittade [{2}]"},
        {"Configuration.Error.Line.line.expected.expect.",
            "Konfigurationsfel:\n\tRad {0}: f\u00F6rv\u00E4ntade [{1}]"},
        {"Configuration.Error.Line.line.system.property.value.expanded.to.empty.value",
            "Konfigurationsfel:\n\tRad {0}: systemegenskapen [{1}] ut\u00F6kad till tomt v\u00E4rde"},

        // com.sun.security.auth.module.JndiLoginModule
        {"username.","anv\u00E4ndarnamn: "},
        {"password.","l\u00F6senord: "},

        // com.sun.security.auth.module.KeyStoreLoginModule
        {"Please.enter.keystore.information",
                "Ange nyckellagerinformation"},
        {"Keystore.alias.","Nyckellageralias: "},
        {"Keystore.password.","Nyckellagerl\u00F6senord: "},
        {"Private.key.password.optional.",
            "L\u00F6senord f\u00F6r personlig nyckel (valfritt): "},

        // com.sun.security.auth.module.Krb5LoginModule
        {"Kerberos.username.defUsername.",
                "Kerberos-anv\u00E4ndarnamn [{0}]: "},
        {"Kerberos.password.for.username.",
                "Kerberos-l\u00F6senord f\u00F6r {0}: "},
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
