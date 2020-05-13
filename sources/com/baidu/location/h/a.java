package com.baidu.location.h;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.szchoiceway.index.EventUtils;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.regex.Pattern;

public class a {

    /* renamed from: com.baidu.location.h.a$a  reason: collision with other inner class name */
    static class C0007a {
        public static String a(byte[] bArr) {
            char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            StringBuilder sb = new StringBuilder(bArr.length * 2);
            for (int i = 0; i < bArr.length; i++) {
                sb.append(cArr[(bArr[i] & EventUtils.MCU_KEY_PIP) >> 4]);
                sb.append(cArr[bArr[i] & 15]);
            }
            return sb.toString();
        }
    }

    public static String a(Context context) {
        String packageName = context.getPackageName();
        return a(context, packageName) + ";" + packageName;
    }

    private static String a(Context context, String str) {
        String str2;
        try {
            str2 = a((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(context.getPackageManager().getPackageInfo(str, 64).signatures[0].toByteArray())));
        } catch (PackageManager.NameNotFoundException e) {
            str2 = "";
        } catch (CertificateException e2) {
            str2 = "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str2.length(); i++) {
            stringBuffer.append(str2.charAt(i));
            if (i > 0 && i % 2 == 1 && i < str2.length() - 1) {
                stringBuffer.append(":");
            }
        }
        return stringBuffer.toString();
    }

    static String a(X509Certificate x509Certificate) {
        try {
            return C0007a.a(a(x509Certificate.getEncoded()));
        } catch (CertificateEncodingException e) {
            return null;
        }
    }

    static byte[] a(byte[] bArr) {
        try {
            return MessageDigest.getInstance("SHA1").digest(bArr);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String b(Context context) {
        try {
            String string = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.getString("com.baidu.lbsapi.API_KEY");
            return !TextUtils.isEmpty(string) ? Pattern.compile("[&=]").matcher(string).replaceAll("") : string;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
