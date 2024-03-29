/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.sun.net.ftp.impl;

/**
 * Default FtpClientProvider.
 * Uses sun.net.ftp.FtpCLient.
 */
public class DefaultFtpClientProvider extends sun.net.ftp.FtpClientProvider {

    @Override
    public sun.net.ftp.FtpClient createFtpClient() {
        return java.base.share.classes.sun.net.ftp.impl.FtpClient.create();
    }

}
