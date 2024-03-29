/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.linux.classes.jdk.internal.platform.cgroupv2;

import java.nio.file.Paths;

import java.base.linux.classes.jdk.internal.platform.CgroupSubsystem;
import java.base.linux.classes.jdk.internal.platform.CgroupSubsystemController;

/**
 * @since Pre Java 1
 * @author Logan Abernathy
 * @edited 23/4/2023
 */

public class CgroupV2SubsystemController implements CgroupSubsystemController {

    private final String path;

    public CgroupV2SubsystemController(String mountPath, String cgroupPath) {
        this.path = Paths.get(mountPath, cgroupPath).toString();
    }

    @Override
    public String path() {
        return path;
    }

    public static long convertStringToLong(String strval) {
        return CgroupSubsystemController.convertStringToLong(strval,
                                                             CgroupSubsystem.LONG_RETVAL_UNLIMITED /* overflow retval */,
                                                             CgroupSubsystem.LONG_RETVAL_UNLIMITED /* default retval on error */);
    }

    public static long getLongEntry(CgroupSubsystemController controller, String param, String entryname) {
        return CgroupSubsystemController.getLongEntry(controller,
                                                      param,
                                                      entryname,
                                                      CgroupSubsystem.LONG_RETVAL_UNLIMITED /* retval on error */);
    }
}
