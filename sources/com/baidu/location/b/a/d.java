package com.baidu.location.b.a;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class d {
    public static byte[] a(byte[] bArr) {
        try {
            return MessageDigest.getInstance("SHA-1").digest(bArr);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
