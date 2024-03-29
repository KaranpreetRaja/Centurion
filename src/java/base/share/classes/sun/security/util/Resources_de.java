/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.security.util;

/**
 * This class represents the <code>ResourceBundle</code>
 * for javax.security.auth and sun.security.
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 21/4/2023 
 */
public class Resources_de extends java.util.ListResourceBundle {

    private static final Object[][] contents = {

        // javax.security.auth.PrivateCredentialPermission
        {"invalid.null.input.s.", "Ung\u00FCltige Nulleingabe(n)"},
        {"actions.can.only.be.read.", "Aktionen k\u00F6nnen nur \"lesen\" sein"},
        {"permission.name.name.syntax.invalid.",
                "Syntax f\u00FCr Berechtigungsnamen [{0}] ung\u00FCltig: "},
        {"Credential.Class.not.followed.by.a.Principal.Class.and.Name",
                "Nach Zugangsdatenklasse folgt keine Principal-Klasse und kein Name"},
        {"Principal.Class.not.followed.by.a.Principal.Name",
                "Nach Principal-Klasse folgt kein Principal-Name"},
        {"Principal.Name.must.be.surrounded.by.quotes",
                "Principal-Name muss in Anf\u00FChrungszeichen stehen"},
        {"Principal.Name.missing.end.quote",
                "Abschlie\u00DFendes Anf\u00FChrungszeichen f\u00FCr Principal-Name fehlt"},
        {"PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value",
                "Principal-Klasse PrivateCredentialPermission kann kein Platzhalterwert (*) sein, wenn der Principal-Name kein Platzhalterwert (*) ist"},
        {"CredOwner.Principal.Class.class.Principal.Name.name",
                "CredOwner:\n\tPrincipal-Klasse = {0}\n\tPrincipal-Name = {1}"},

        // javax.security.auth.x500
        {"provided.null.name", "Nullname angegeben"},
        {"provided.null.keyword.map", "Null-Schl\u00FCsselwortzuordnung angegeben"},
        {"provided.null.OID.map", "Null-OID-Zuordnung angegeben"},

        // javax.security.auth.Subject
        {"NEWLINE", "\n"},
        {"invalid.null.AccessControlContext.provided",
                "Ung\u00FCltiger Nullwert f\u00FCr AccessControlContext angegeben"},
        {"invalid.null.action.provided", "Ung\u00FCltige Nullaktion angegeben"},
        {"invalid.null.Class.provided", "Ung\u00FCltige Nullklasse angegeben"},
        {"Subject.", "Subjekt:\n"},
        {".Principal.", "\tPrincipal: "},
        {".Public.Credential.", "\t\u00D6ffentliche Zugangsdaten: "},
        {".Private.Credential.", "\tPrivate Zugangsdaten: "},
        {".Private.Credential.inaccessible.",
                "\tKein Zugriff auf private Zugangsdaten\n"},
        {"Subject.is.read.only", "Subjekt ist schreibgesch\u00FCtzt"},
        {"attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set",
                "Es wird versucht, ein Objekt hinzuzuf\u00FCgen, das keine Instanz von java.security.Principal f\u00FCr eine Principal-Gruppe eines Subjekts ist"},
        {"attempting.to.add.an.object.which.is.not.an.instance.of.class",
                "Es wird versucht, ein Objekt hinzuzuf\u00FCgen, das keine Instanz von {0} ist"},

        // javax.security.auth.login.AppConfigurationEntry
        {"LoginModuleControlFlag.", "LoginModuleControlFlag: "},

        // javax.security.auth.login.LoginContext
        {"Invalid.null.input.name", "Ung\u00FCltige Nulleingabe: Name"},
        {"No.LoginModules.configured.for.name",
         "F\u00FCr {0} sind keine LoginModules konfiguriert"},
        {"invalid.null.Subject.provided", "Ung\u00FCltiges Nullsubjekt angegeben"},
        {"invalid.null.CallbackHandler.provided",
                "Ung\u00FCltiger Nullwert f\u00FCr CallbackHandler angegeben"},
        {"null.subject.logout.called.before.login",
                "Nullsubjekt - Abmeldung vor Anmeldung aufgerufen"},
        {"Login.Failure.all.modules.ignored",
                "Anmeldefehler: Alle Module werden ignoriert"},

        // sun.security.provider.PolicyFile

        {"java.security.policy.error.parsing.policy.message",
                "java.security.policy: Fehler beim Parsen von {0}:\n\t{1}"},
        {"java.security.policy.error.adding.Permission.perm.message",
                "java.security.policy: Fehler beim Hinzuf\u00FCgen von Berechtigung, {0}:\n\t{1}"},
        {"java.security.policy.error.adding.Entry.message",
                "java.security.policy: Fehler beim Hinzuf\u00FCgen von Eintrag:\n\t{0}"},
        {"alias.name.not.provided.pe.name.", "Aliasname nicht angegeben ({0})"},
        {"unable.to.perform.substitution.on.alias.suffix",
                "Substitution f\u00FCr Alias {0} kann nicht ausgef\u00FChrt werden"},
        {"substitution.value.prefix.unsupported",
                "Substitutionswert {0} nicht unterst\u00FCtzt"},
        {"SPACE", " "},
        {"LPARAM", "("},
        {"RPARAM", ")"},
        {"type.can.t.be.null","Typ kann nicht null sein"},

        // sun.security.provider.PolicyParser
        {"keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore",
                "keystorePasswordURL kann nicht ohne Keystore angegeben werden"},
        {"expected.keystore.type", "Keystore-Typ erwartet"},
        {"expected.keystore.provider", "Keystore-Provider erwartet"},
        {"multiple.Codebase.expressions",
                "mehrere Codebase-Ausdr\u00FCcke"},
        {"multiple.SignedBy.expressions","mehrere SignedBy-Ausdr\u00FCcke"},
        {"duplicate.keystore.domain.name","Keystore-Domainname doppelt vorhanden: {0}"},
        {"duplicate.keystore.name","Keystore-Name doppelt vorhanden: {0}"},
        {"SignedBy.has.empty.alias","Leerer Alias in SignedBy"},
        {"can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name",
                "Principal kann nicht mit einer Platzhalterklasse ohne Platzhalternamen angegeben werden"},
        {"expected.codeBase.or.SignedBy.or.Principal",
                "codeBase oder SignedBy oder Principal erwartet"},
        {"expected.permission.entry", "Berechtigungseintrag erwartet"},
        {"number.", "Nummer "},
        {"expected.expect.read.end.of.file.",
                "[{0}] erwartet, [Dateiende] gelesen"},
        {"expected.read.end.of.file.",
                "[;] erwartet, [Dateiende] gelesen"},
        {"line.number.msg", "Zeile {0}: {1}"},
        {"line.number.expected.expect.found.actual.",
                "Zeile {0}: [{1}] erwartet, [{2}] gefunden"},
        {"null.principalClass.or.principalName",
                "principalClass oder principalName null"},

        // sun.security.pkcs11.SunPKCS11
        {"PKCS11.Token.providerName.Password.",
                "Kennwort f\u00FCr PKCS11-Token [{0}]: "},
    };


    /**
     * Returns the contents of this <code>ResourceBundle</code>.
     *
     * @return the contents of this <code>ResourceBundle</code>.
     */
    @Override
    public Object[][] getContents() {
        return contents;
    }
}

