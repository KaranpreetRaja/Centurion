/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.java.io;

import java.security.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * This class is for Serializable permissions. A SerializablePermission
 * contains a name (also referred to as a "target name") but
 * no actions list; you either have the named permission
 * or you don't.
 *
 * <P>
 * The target name is the name of the Serializable permission (see below).
 *
 * <P>
 * The following table lists the standard {@code SerializablePermission} target names,
 * and for each provides a description of what the permission allows
 * and a discussion of the risks of granting code the permission.
 *
 * <table class="striped">
 * <caption style="display:none">Permission target name, what the permission allows, and associated risks</caption>
 * <thead>
 * <tr>
 * <th scope="col">Permission Target Name</th>
 * <th scope="col">What the Permission Allows</th>
 * <th scope="col">Risks of Allowing this Permission</th>
 * </tr>
 * </thead>
 * <tbody>
 *
 * <tr>
 *   <th scope="row">enableSubclassImplementation</th>
 *   <td>Subclass implementation of ObjectOutputStream or ObjectInputStream
 * to override the default serialization or deserialization, respectively,
 * of objects</td>
 *   <td>Code can use this to serialize or
 * deserialize classes in a purposefully malfeasant manner. For example,
 * during serialization, malicious code can use this to
 * purposefully store confidential private field data in a way easily accessible
 * to attackers. Or, during deserialization it could, for example, deserialize
 * a class with all its private fields zeroed out.</td>
 * </tr>
 *
 * <tr>
 *   <th scope="row">enableSubstitution</th>
 *   <td>Substitution of one object for another during
 * serialization or deserialization</td>
 *   <td>This is dangerous because malicious code
 * can replace the actual object with one which has incorrect or
 * malignant data.</td>
 * </tr>
 *
 * <tr>
 *   <th scope="row">serialFilter</th>
 *   <td>Setting a filter for ObjectInputStreams.</td>
 *   <td>Code could remove a configured filter and remove protections
 *       already established.</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @see java.security.BasicPermission
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 * @see java.lang.SecurityManager
 *
 *
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 24/4/2023
 */

/* code was borrowed originally from java.lang.RuntimePermission. */

public final class SerializablePermission extends BasicPermission {

    @java.base.share.classes.java.io.Serial
    private static final long serialVersionUID = 8537212141160296410L;

    /**
     * @serial
     */
    private String actions;

    /**
     * Creates a new SerializablePermission with the specified name.
     * The name is the symbolic name of the SerializablePermission, such as
     * "enableSubstitution", etc.
     *
     * @param name the name of the SerializablePermission.
     *
     * @throws NullPointerException if {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code name} is empty.
     */
    public SerializablePermission(String name)
    {
        super(name);
    }

    /**
     * Creates a new SerializablePermission object with the specified name.
     * The name is the symbolic name of the SerializablePermission, and the
     * actions String is currently unused and should be null.
     *
     * @param name the name of the SerializablePermission.
     * @param actions currently unused and must be set to null
     *
     * @throws NullPointerException if {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code name} is empty.
     */

    public SerializablePermission(String name, String actions)
    {
        super(name, actions);
    }
}
