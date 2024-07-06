//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.bayesserver;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

public final class License {
    private static final Object a = new Object();
    private static boolean b;
    private static boolean c;
    private static fa[] d = new fa[]{new fa("177E5E4AABEBAB01AB9E7333C297A6A2", 1239, false), new fa("D2791B9B4FB9A24E4653A0B864BAE8C5", 1197, true)};

    private License() {
    }

    public static boolean getIsValid() {
        return true;
    }

    public static void invalidate() {
        c = true;
    }

    private static boolean a(String var0) {
        int var3 = 0;
        char[] var4;
        int var5 = (var4 = var0.toCharArray()).length;

        for(int var6 = 0; var6 < var5; ++var6) {
            char var7;
            if (Character.isDigit(var7 = var4[var6])) {
                var3 += Character.getNumericValue(var7);
            }
        }

        if (var3 == 0) {
            return false;
        } else if (var3 % 7 == 0) {
            return true;
        } else {
            return false;
        }
    }

    private static fa a(int var0) {
        fa[] var1;
        int var2 = (var1 = d).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            fa var4 = var1[var3];
            if (var0 == var4.b()) {
                return var4;
            }
        }

        return null;
    }

    private static fa b(String var0) {
        fa[] var1;
        int var2 = (var1 = d).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            fa var4 = var1[var3];
            if (var0.toUpperCase(Locale.US).equals(var4.a())) {
                return var4;
            }
        }

        return null;
    }

    public static void validate(String key) {
        if (key == null) {
            throw new NullPointerException("key");
        } else if (!c) {
            if (b) {
                throw new IllegalStateException("Validation failed in a previous attempt.  Please restart the application to retry.");
            } else {
                synchronized(a) {
                    fa var2;
                    try {
                        var2 = b(d(key));
                    } catch (Exception var6) {
                        var2 = a(c(key));
                    }

                    if (var2 != null) {
                        if (a(key)) {
                            c = true;
                        } else {
                            b = true;
                            throw new IllegalStateException("License validation failed.  Invalid license key.");
                        }
                    } else {
                        b = true;
                        throw new IllegalStateException("License validation failed.  Invalid license key.");
                    }
                }
            }
        }
    }

    private static int c(String var0) {
        int var1 = 0;

        for(int var2 = 0; var2 < var0.length() / 2; ++var2) {
            char var3 = var0.charAt(var2);
            var1 += var3;
        }

        return var1;
    }

    private static String d(String var0) {
        if (var0 == null) {
            throw new NullPointerException("password");
        } else {
            byte[] var1;
            try {
                var1 = var0.getBytes("UTF-8");
            } catch (UnsupportedEncodingException var5) {
                throw new IllegalStateException("License validation encoding failed");
            }

            MessageDigest var2;
            try {
                var2 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var4) {
                throw new IllegalStateException("License validation algorithm failed");
            }

            byte[] var3 = var2.digest(Arrays.copyOf(var1, var1.length / 2));
            return (new BigInteger(1, var3)).toString(16);
        }
    }
}
