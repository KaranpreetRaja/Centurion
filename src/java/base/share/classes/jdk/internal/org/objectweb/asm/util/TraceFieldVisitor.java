/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.org.objectweb.asm.util;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.TypePath;

/**
 * A {@link FieldVisitor} that prints the fields it visits with a {@link Printer}.
 *
 * @author Eric Bruneton
 */
public final class TraceFieldVisitor extends FieldVisitor {

    /** The printer to convert the visited field into text. */
    // DontCheck(MemberName): can't be renamed (for backward binary compatibility).
    public final Printer p;

    /**
      * Constructs a new {@link TraceFieldVisitor}.
      *
      * @param printer the printer to convert the visited field into text.
      */
    public TraceFieldVisitor(final Printer printer) {
        this(null, printer);
    }

    /**
      * Constructs a new {@link TraceFieldVisitor}.
      *
      * @param fieldVisitor the field visitor to which to delegate calls. May be {@literal null}.
      * @param printer the printer to convert the visited field into text.
      */
    public TraceFieldVisitor(final FieldVisitor fieldVisitor, final Printer printer) {
        super(/* latest api = */ Opcodes.ASM9, fieldVisitor);
        this.p = printer;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        Printer annotationPrinter = p.visitFieldAnnotation(descriptor, visible);
        return new TraceAnnotationVisitor(
                super.visitAnnotation(descriptor, visible), annotationPrinter);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(
            final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        Printer annotationPrinter = p.visitFieldTypeAnnotation(typeRef, typePath, descriptor, visible);
        return new TraceAnnotationVisitor(
                super.visitTypeAnnotation(typeRef, typePath, descriptor, visible), annotationPrinter);
    }

    @Override
    public void visitAttribute(final Attribute attribute) {
        p.visitFieldAttribute(attribute);
        super.visitAttribute(attribute);
    }

    @Override
    public void visitEnd() {
        p.visitFieldEnd();
        super.visitEnd();
    }
}

