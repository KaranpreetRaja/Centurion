/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.sun.net.www.protocol.http;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.HashMap;

/**
 * @author Michael McMahon
 */
public class AuthCacheImpl implements AuthCache {
    // No blocking IO is performed within the synchronized code blocks
    // in this class, so there is no need to convert this class to using
    // java.util.concurrent.locks
    HashMap<String,LinkedList<AuthCacheValue>> hashtable;

    public AuthCacheImpl () {
        hashtable = new HashMap<String,LinkedList<AuthCacheValue>>();
    }

    public void setMap (HashMap<String,LinkedList<AuthCacheValue>> map) {
        hashtable = map;
    }

    // put a value in map according to primary key + secondary key which
    // is the path field of AuthenticationInfo
    public synchronized void put (String pkey, AuthCacheValue value) {
        LinkedList<AuthCacheValue> list = hashtable.get (pkey);
        String skey = value.getPath();
        if (list == null) {
            list = new LinkedList<AuthCacheValue>();
            hashtable.put(pkey, list);
        }
        // Check if the path already exists or a super-set of it exists
        ListIterator<AuthCacheValue> iter = list.listIterator();
        while (iter.hasNext()) {
            AuthenticationInfo inf = (AuthenticationInfo)iter.next();
            if (inf.path == null || inf.path.startsWith (skey)) {
                iter.remove ();
            }
        }
        iter.add(value);
    }

    // get a value from map checking both primary
    // and secondary (urlpath) key

    public synchronized AuthCacheValue get (String pkey, String skey) {
        AuthenticationInfo result = null;
        LinkedList<AuthCacheValue> list = hashtable.get (pkey);
        if (list == null || list.size() == 0) {
            return null;
        }
        if (skey == null) {
            // list should contain only one element
            return list.get(0);
        }
        for (AuthCacheValue authCacheValue : list) {
            AuthenticationInfo inf = (AuthenticationInfo) authCacheValue;
            if (skey.startsWith (inf.path)) {
                return inf;
            }
        }
        return null;
    }

    public synchronized void remove (String pkey, AuthCacheValue entry) {
        LinkedList<AuthCacheValue> list = hashtable.get (pkey);
        if (list == null) {
            return;
        }
        if (entry == null) {
            list.clear();
            return;
        }
        ListIterator<AuthCacheValue> iter = list.listIterator ();
        while (iter.hasNext()) {
            AuthenticationInfo inf = (AuthenticationInfo)iter.next();
            if (entry.equals(inf)) {
                iter.remove ();
            }
        }
    }
}
