/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.Label;

/**
 * A code generator for switch statements.
 *
 * @author Juozas Baliuka
 * @author Chris Nokleberg
 * @author Eric Bruneton
 */
public interface TableSwitchGenerator {

    /**
      * Generates the code for a switch case.
      *
      * @param key the switch case key.
      * @param end a label that corresponds to the end of the switch statement.
      */
    void generateCase(int key, Label end);

    /** Generates the code for the default switch case. */
    void generateDefault();
}

