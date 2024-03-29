/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.java.net;
import java.io.IOException;
import java.base.share.classes.java.net.spi.InetAddressResolver.LookupPolicy;

import static java.base.share.classes.java.net.spi.InetAddressResolver.LookupPolicy.IPV4;

/*
 * Package private implementation of InetAddressImpl for IPv4.
 *
 * @since 1.4
 */
final class Inet4AddressImpl implements InetAddressImpl {
    public native String getLocalHostName() throws UnknownHostException;
    public InetAddress[] lookupAllHostAddr(String hostname, LookupPolicy lookupPolicy)
            throws UnknownHostException {
        if ((lookupPolicy.characteristics() & IPV4) == 0) {
            throw new UnknownHostException(hostname);
        }
        return lookupAllHostAddr(hostname);
    }
    private native InetAddress[] lookupAllHostAddr(String hostname) throws UnknownHostException;
    public native String getHostByAddr(byte[] addr) throws UnknownHostException;
    private native boolean isReachable0(byte[] addr, int timeout, byte[] ifaddr, int ttl) throws IOException;

    public synchronized InetAddress anyLocalAddress() {
        if (anyLocalAddress == null) {
            anyLocalAddress = new Inet4Address(); // {0x00,0x00,0x00,0x00}
            anyLocalAddress.holder().hostName = "0.0.0.0";
        }
        return anyLocalAddress;
    }

    public synchronized InetAddress loopbackAddress() {
        if (loopbackAddress == null) {
            byte[] loopback = {0x7f,0x00,0x00,0x01};
            loopbackAddress = new Inet4Address("localhost", loopback);
        }
        return loopbackAddress;
    }

  public boolean isReachable(InetAddress addr, int timeout, NetworkInterface netif, int ttl) throws IOException {
      byte[] ifaddr = null;
      if (netif != null) {
          /*
           * Let's make sure we use an address of the proper family
           */
          java.util.Enumeration<InetAddress> it = netif.getInetAddresses();
          InetAddress inetaddr = null;
          while (!(inetaddr instanceof Inet4Address) &&
                 it.hasMoreElements())
              inetaddr = it.nextElement();
          if (inetaddr instanceof Inet4Address)
              ifaddr = inetaddr.getAddress();
      }
      return isReachable0(addr.getAddress(), timeout, ifaddr, ttl);
  }
    private InetAddress      anyLocalAddress;
    private InetAddress      loopbackAddress;
}
