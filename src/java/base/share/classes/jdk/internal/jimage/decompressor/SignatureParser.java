/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */
package java.base.share.classes.jdk.internal.jimage.decompressor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * A descriptor parser used to extract java type strings from
 * UTF_8 descriptors.
 *
 * @implNote This class needs to maintain JDK 8 source compatibility.
 *
 * It is used internally in the JDK to implement jimage/jrtfs access,
 * but also compiled and delivered as part of the jrtfs.jar to support access
 * to the jimage file provided by the shipped JDK by tools running on JDK 8.
 */
public class SignatureParser {

   public static class ParseResult {

        public final List<String> types = new ArrayList<>();
        public String formatted;
        private ParseResult() {}
    }

    private SignatureParser() {}

    public static String reconstruct(String formatted, List<String> arguments) {
        int arg_index = 0;
        char[] chars = formatted.toCharArray();
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            out.append(c);
            switch (c) {
                case 'L': {
                    String pkg = arguments.get(arg_index);
                    if(!pkg.isEmpty()) {
                        out.append(pkg).append("/");
                    }
                    arg_index+=1;
                    out.append(arguments.get(arg_index));
                    arg_index+=1;
                    break;
                }
                default: {
                    break;
                }
            }
        }
        return out.toString();
    }

    public static ParseResult parseSignatureDescriptor(String str) {
        ParseResult res = new ParseResult();
        char[] chars = str.toCharArray();
        StringBuilder type = null;
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            switch (c) {
                case ';':
                case ':':
                case '<': {
                    if(type != null) {
                        String fullName = type.toString();
                        int endIndex = fullName.lastIndexOf("/");
                        String clazz = fullName;
                        String pkg = "";
                        if(endIndex != -1) {
                            pkg = fullName.substring(0, endIndex);
                            clazz = fullName.substring(endIndex+1);
                        }
                        res.types.add(pkg);
                        res.types.add(clazz);
                    }
                    formatted.append(c);

                    type = null;
                    break;
                }
                case 'L': {
                    if(type == null) {
                        type = new StringBuilder();
                        formatted.append(c);
                    } else {
                        type.append(c);
                    }
                    break;
                }
                default: {
                    if(type == null) {
                        formatted.append(c);
                    } else {
                        type.append(c);
                    }
                    break;
                }
            }
        }
        res.formatted = formatted.toString();
        return res;
    }
}
