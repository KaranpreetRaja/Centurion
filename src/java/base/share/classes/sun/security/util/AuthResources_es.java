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
public class AuthResources_es extends java.util.ListResourceBundle {

    private static final Object[][] contents = {

        // NT principals
        {"invalid.null.input.value", "entrada nula no v\u00E1lida: {0}"},
        {"NTDomainPrincipal.name", "NTDomainPrincipal: {0}"},
        {"NTNumericCredential.name", "NTNumericCredential: {0}"},
        {"Invalid.NTSid.value", "Valor de NTSid no v\u00E1lido"},
        {"NTSid.name", "NTSid: {0}"},
        {"NTSidDomainPrincipal.name", "NTSidDomainPrincipal: {0}"},
        {"NTSidGroupPrincipal.name", "NTSidGroupPrincipal: {0}"},
        {"NTSidPrimaryGroupPrincipal.name", "NTSidPrimaryGroupPrincipal: {0}"},
        {"NTSidUserPrincipal.name", "NTSidUserPrincipal: {0}"},
        {"NTUserPrincipal.name", "NTUserPrincipal: {0}"},

        // UnixPrincipals
        {"UnixNumericGroupPrincipal.Primary.Group.name",
                "UnixNumericGroupPrincipal [Grupo Principal] {0}"},
        {"UnixNumericGroupPrincipal.Supplementary.Group.name",
                "UnixNumericGroupPrincipal [Grupo Adicional] {0}"},
        {"UnixNumericUserPrincipal.name", "UnixNumericUserPrincipal: {0}"},
        {"UnixPrincipal.name", "UnixPrincipal: {0}"},

        // com.sun.security.auth.login.ConfigFile
        {"Unable.to.properly.expand.config", "No se ha podido ampliar correctamente {0}"},
        {"extra.config.No.such.file.or.directory.",
                "{0} (No existe tal archivo o directorio)"},
        {"Configuration.Error.No.such.file.or.directory",
                "Error de Configuraci\u00F3n:\n\tNo existe tal archivo o directorio"},
        {"Configuration.Error.Invalid.control.flag.flag",
                "Error de Configuraci\u00F3n:\n\tIndicador de control no v\u00E1lido, {0}"},
        {"Configuration.Error.Can.not.specify.multiple.entries.for.appName",
            "Error de Configuraci\u00F3n:\n\tNo se pueden especificar varias entradas para {0}"},
        {"Configuration.Error.expected.expect.read.end.of.file.",
                "Error de configuraci\u00F3n:\n\tse esperaba [{0}], se ha le\u00EDdo [final de archivo]"},
        {"Configuration.Error.Line.line.expected.expect.found.value.",
            "Error de configuraci\u00F3n:\n\tL\u00EDnea {0}: se esperaba [{1}], se ha encontrado [{2}]"},
        {"Configuration.Error.Line.line.expected.expect.",
            "Error de configuraci\u00F3n:\n\tL\u00EDnea {0}: se esperaba [{1}]"},
        {"Configuration.Error.Line.line.system.property.value.expanded.to.empty.value",
            "Error de configuraci\u00F3n:\n\tL\u00EDnea {0}: propiedad de sistema [{1}] ampliada a valor vac\u00EDo"},

        // com.sun.security.auth.module.JndiLoginModule
        {"username.","nombre de usuario: "},
        {"password.","contrase\u00F1a: "},

        // com.sun.security.auth.module.KeyStoreLoginModule
        {"Please.enter.keystore.information",
                "Introduzca la informaci\u00F3n del almac\u00E9n de claves"},
        {"Keystore.alias.","Alias de Almac\u00E9n de Claves: "},
        {"Keystore.password.","Contrase\u00F1a de Almac\u00E9n de Claves: "},
        {"Private.key.password.optional.",
            "Contrase\u00F1a de Clave Privada (opcional): "},

        // com.sun.security.auth.module.Krb5LoginModule
        {"Kerberos.username.defUsername.",
                "Nombre de usuario de Kerberos [{0}]: "},
        {"Kerberos.password.for.username.",
                "Contrase\u00F1a de Kerberos de {0}: "},
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
