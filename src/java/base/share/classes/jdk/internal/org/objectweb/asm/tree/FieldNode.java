/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.org.objectweb.asm.tree;

import java.util.List;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.TypePath;

/**
 * A node that represents a field.
 *
 * @author Eric Bruneton
 */
public class FieldNode extends FieldVisitor {

    /**
      * The field's access flags (see {@link jdk.internal.org.objectweb.asm.Opcodes}). This field also indicates if
      * the field is synthetic and/or deprecated.
      */
    public int access;

    /** The field's name. */
    public String name;

    /** The field's descriptor (see {@link jdk.internal.org.objectweb.asm.Type}). */
    public String desc;

    /** The field's signature. May be {@literal null}. */
    public String signature;

    /**
      * The field's initial value. This field, which may be {@literal null} if the field does not have
      * an initial value, must be an {@link Integer}, a {@link Float}, a {@link Long}, a {@link Double}
      * or a {@link String}.
      */
    public Object value;

    /** The runtime visible annotations of this field. May be {@literal null}. */
    public List<AnnotationNode> visibleAnnotations;

    /** The runtime invisible annotations of this field. May be {@literal null}. */
    public List<AnnotationNode> invisibleAnnotations;

    /** The runtime visible type annotations of this field. May be {@literal null}. */
    public List<TypeAnnotationNode> visibleTypeAnnotations;

    /** The runtime invisible type annotations of this field. May be {@literal null}. */
    public List<TypeAnnotationNode> invisibleTypeAnnotations;

    /** The non standard attributes of this field. * May be {@literal null}. */
    public List<Attribute> attrs;

    /**
      * Constructs a new {@link FieldNode}. <i>Subclasses must not use this constructor</i>. Instead,
      * they must use the {@link #FieldNode(int, int, String, String, String, Object)} version.
      *
      * @param access the field's access flags (see {@link jdk.internal.org.objectweb.asm.Opcodes}). This parameter
      *     also indicates if the field is synthetic and/or deprecated.
      * @param name the field's name.
      * @param descriptor the field's descriptor (see {@link jdk.internal.org.objectweb.asm.Type}).
      * @param signature the field's signature.
      * @param value the field's initial value. This parameter, which may be {@literal null} if the
      *     field does not have an initial value, must be an {@link Integer}, a {@link Float}, a {@link
      *     Long}, a {@link Double} or a {@link String}.
      * @throws IllegalStateException If a subclass calls this constructor.
      */
    public FieldNode(
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final Object value) {
        this(/* latest api = */ Opcodes.ASM9, access, name, descriptor, signature, value);
        if (getClass() != FieldNode.class) {
            throw new IllegalStateException();
        }
    }

    /**
      * Constructs a new {@link FieldNode}.
      *
      * @param api the ASM API version implemented by this visitor. Must be one of the {@code
      *     ASM}<i>x</i> values in {@link Opcodes}.
      * @param access the field's access flags (see {@link jdk.internal.org.objectweb.asm.Opcodes}). This parameter
      *     also indicates if the field is synthetic and/or deprecated.
      * @param name the field's name.
      * @param descriptor the field's descriptor (see {@link jdk.internal.org.objectweb.asm.Type}).
      * @param signature the field's signature.
      * @param value the field's initial value. This parameter, which may be {@literal null} if the
      *     field does not have an initial value, must be an {@link Integer}, a {@link Float}, a {@link
      *     Long}, a {@link Double} or a {@link String}.
      */
    public FieldNode(
            final int api,
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final Object value) {
        super(api);
        this.access = access;
        this.name = name;
        this.desc = descriptor;
        this.signature = signature;
        this.value = value;
    }

    // -----------------------------------------------------------------------------------------------
    // Implementation of the FieldVisitor abstract class
    // -----------------------------------------------------------------------------------------------

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        AnnotationNode annotation = new AnnotationNode(descriptor);
        if (visible) {
            visibleAnnotations = Util.add(visibleAnnotations, annotation);
        } else {
            invisibleAnnotations = Util.add(invisibleAnnotations, annotation);
        }
        return annotation;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(
            final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            visibleTypeAnnotations = Util.add(visibleTypeAnnotations, typeAnnotation);
        } else {
            invisibleTypeAnnotations = Util.add(invisibleTypeAnnotations, typeAnnotation);
        }
        return typeAnnotation;
    }

    @Override
    public void visitAttribute(final Attribute attribute) {
        attrs = Util.add(attrs, attribute);
    }

    @Override
    public void visitEnd() {
        // Nothing to do.
    }

    // -----------------------------------------------------------------------------------------------
    // Accept methods
    // -----------------------------------------------------------------------------------------------

    /**
      * Checks that this field node is compatible with the given ASM API version. This method checks
      * that this node, and all its children recursively, do not contain elements that were introduced
      * in more recent versions of the ASM API than the given version.
      *
      * @param api an ASM API version. Must be one of the {@code ASM}<i>x</i> values in {@link
      *     Opcodes}.
      */
    public void check(final int api) {
        if (api == Opcodes.ASM4) {
            if (visibleTypeAnnotations != null && !visibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (invisibleTypeAnnotations != null && !invisibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
        }
    }

    /**
      * Makes the given class visitor visit this field.
      *
      * @param classVisitor a class visitor.
      */
    public void accept(final ClassVisitor classVisitor) {
        FieldVisitor fieldVisitor = classVisitor.visitField(access, name, desc, signature, value);
        if (fieldVisitor == null) {
            return;
        }
        // Visit the annotations.
        if (visibleAnnotations != null) {
            for (int i = 0, n = visibleAnnotations.size(); i < n; ++i) {
                AnnotationNode annotation = visibleAnnotations.get(i);
                annotation.accept(fieldVisitor.visitAnnotation(annotation.desc, true));
            }
        }
        if (invisibleAnnotations != null) {
            for (int i = 0, n = invisibleAnnotations.size(); i < n; ++i) {
                AnnotationNode annotation = invisibleAnnotations.get(i);
                annotation.accept(fieldVisitor.visitAnnotation(annotation.desc, false));
            }
        }
        if (visibleTypeAnnotations != null) {
            for (int i = 0, n = visibleTypeAnnotations.size(); i < n; ++i) {
                TypeAnnotationNode typeAnnotation = visibleTypeAnnotations.get(i);
                typeAnnotation.accept(
                        fieldVisitor.visitTypeAnnotation(
                                typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, true));
            }
        }
        if (invisibleTypeAnnotations != null) {
            for (int i = 0, n = invisibleTypeAnnotations.size(); i < n; ++i) {
                TypeAnnotationNode typeAnnotation = invisibleTypeAnnotations.get(i);
                typeAnnotation.accept(
                        fieldVisitor.visitTypeAnnotation(
                                typeAnnotation.typeRef, typeAnnotation.typePath, typeAnnotation.desc, false));
            }
        }
        // Visit the non standard attributes.
        if (attrs != null) {
            for (int i = 0, n = attrs.size(); i < n; ++i) {
                fieldVisitor.visitAttribute(attrs.get(i));
            }
        }
        fieldVisitor.visitEnd();
    }
}

