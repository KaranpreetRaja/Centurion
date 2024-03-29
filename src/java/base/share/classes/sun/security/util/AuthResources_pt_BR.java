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
public class AuthResources_pt_BR extends java.util.ListResourceBundle {

    private static final Object[][] contents = {

        // NT principals
        {"invalid.null.input.value", "entrada nula inv\u00E1lida: {0}"},
        {"NTDomainPrincipal.name", "NTDomainPrincipal: {0}"},
        {"NTNumericCredential.name", "NTNumericCredential: {0}"},
        {"Invalid.NTSid.value", "Valor de NTSid inv\u00E1lido"},
        {"NTSid.name", "NTSid: {0}"},
        {"NTSidDomainPrincipal.name", "NTSidDomainPrincipal: {0}"},
        {"NTSidGroupPrincipal.name", "NTSidGroupPrincipal: {0}"},
        {"NTSidPrimaryGroupPrincipal.name", "NTSidPrimaryGroupPrincipal: {0}"},
        {"NTSidUserPrincipal.name", "NTSidUserPrincipal: {0}"},
        {"NTUserPrincipal.name", "NTUserPrincipal: {0}"},

        // UnixPrincipals
        {"UnixNumericGroupPrincipal.Primary.Group.name",
                "UnixNumericGroupPrincipal [Grupo Principal]: {0}"},
        {"UnixNumericGroupPrincipal.Supplementary.Group.name",
                "UnixNumericGroupPrincipal [Grupo Complementar]: {0}"},
        {"UnixNumericUserPrincipal.name", "UnixNumericUserPrincipal: {0}"},
        {"UnixPrincipal.name", "UnixPrincipal: {0}"},

        // com.sun.security.auth.login.ConfigFile
        {"Unable.to.properly.expand.config", "N\u00E3o \u00E9 poss\u00EDvel expandir corretamente {0}"},
        {"extra.config.No.such.file.or.directory.",
                "{0} (tal arquivo ou diret\u00F3rio n\u00E3o existe)"},
        {"Configuration.Error.No.such.file.or.directory",
                "Erro de Configura\u00E7\u00E3o:\n\tN\u00E3o h\u00E1 tal arquivo ou diret\u00F3rio"},
        {"Configuration.Error.Invalid.control.flag.flag",
                "Erro de Configura\u00E7\u00E3o:\n\tFlag de controle inv\u00E1lido, {0}"},
        {"Configuration.Error.Can.not.specify.multiple.entries.for.appName",
            "Erro de Configura\u00E7\u00E3o:\n\tN\u00E3o \u00E9 poss\u00EDvel especificar v\u00E1rias entradas para {0}"},
        {"Configuration.Error.expected.expect.read.end.of.file.",
                "Erro de Configura\u00E7\u00E3o:\n\tesperado [{0}], lido [fim do arquivo]"},
        {"Configuration.Error.Line.line.expected.expect.found.value.",
            "Erro de Configura\u00E7\u00E3o:\n\tLinha {0}: esperada [{1}], encontrada [{2}]"},
        {"Configuration.Error.Line.line.expected.expect.",
            "Erro de Configura\u00E7\u00E3o:\n\tLinha {0}: esperada [{1}]"},
        {"Configuration.Error.Line.line.system.property.value.expanded.to.empty.value",
            "Erro de Configura\u00E7\u00E3o:\n\tLinha {0}: propriedade do sistema [{1}] expandida para valor vazio"},

        // com.sun.security.auth.module.JndiLoginModule
        {"username.","nome do usu\u00E1rio: "},
        {"password.","senha: "},

        // com.sun.security.auth.module.KeyStoreLoginModule
        {"Please.enter.keystore.information",
                "Especifique as informa\u00E7\u00F5es do armazenamento de chaves"},
        {"Keystore.alias.","Alias do armazenamento de chaves: "},
        {"Keystore.password.","Senha do armazenamento de chaves: "},
        {"Private.key.password.optional.",
            "Senha da chave privada (opcional): "},

        // com.sun.security.auth.module.Krb5LoginModule
        {"Kerberos.username.defUsername.",
                "Nome do usu\u00E1rio de Kerberos [{0}]: "},
        {"Kerberos.password.for.username.",
                "Senha de Kerberos de {0}: "},
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
