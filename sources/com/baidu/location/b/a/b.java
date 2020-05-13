package com.baidu.location.b.a;

import com.szchoiceway.index.EventUtils;
import java.io.UnsupportedEncodingException;

public final class b {
    private static final byte[] a = {EventUtils.MCU_KEY_NUM2_LONG, EventUtils.MCU_KEY_NUM3_LONG, EventUtils.MCU_KEY_NUM4_LONG, EventUtils.MCU_KEY_NUM5_LONG, EventUtils.MCU_KEY_NUM6_LONG, EventUtils.MCU_KEY_RADIO_NEXT, EventUtils.MCU_KEY_RADIO_PREV, EventUtils.MCU_KEY_PROG, EventUtils.MCU_KEY_CLOCK, EventUtils.MCU_KEY_TFT_OPEN, EventUtils.MCU_KEY_TFT_CLOSE, EventUtils.MCU_KEY_SYS_HOME, EventUtils.MCU_KEY_SYS_MENU, EventUtils.MCU_KEY_SYS_ESC, EventUtils.MCU_KEY_SYS_WINCE, EventUtils.MCU_KEY_TFT_LONG_OPEN, EventUtils.MCU_KEY_TFT_LONG_CLOSE, EventUtils.MCU_KEY_LOUDNESS, EventUtils.MCU_KEY_CLEAR, EventUtils.MCU_KEY_DVD_MENU, EventUtils.MCU_KEY_RETURN, EventUtils.MCU_KEY_AB, EventUtils.MCU_KEY_SEARCH, EventUtils.MCU_KEY_DUAL, EventUtils.MCU_KEY_TAB, 90, EventUtils.MCU_KEY_RIGHT_TEMP_SUB, EventUtils.MCU_KEY_FAN_SUB, EventUtils.MCU_KEY_FAN_ADD, EventUtils.MCU_KEY_LEFT_TEMP_ADD, EventUtils.MCU_KEY_LEFT_TEMP_SUB, EventUtils.MCU_KEY_F_CAM, EventUtils.MCU_KEY1_2, EventUtils.MCU_KEY1_3, EventUtils.MCU_KEY1_4, EventUtils.MCU_KEY2_1, EventUtils.MCU_KEY2_10, EventUtils.MCU_KEY1_5, EventUtils.MCU_KEY1_12, EventUtils.MCU_KEY1_14, 111, EventUtils.CMD_MODE_ASK, EventUtils.CMD_SYS_EVENT, EventUtils.CMD_KEY_EVENT, EventUtils.CMD_FM_EVENT, EventUtils.CMD_WHEEL_STATE, EventUtils.CMD_TV_EVENT, EventUtils.CMD_BMT_VAL, EventUtils.CMD_DSP_TYTE, EventUtils.CMD_MUTE, EventUtils.CMD_MAIN_VOL, EventUtils.CMD_BAL_FAD_VAL, 48, 49, 50, 51, 52, EventUtils.MCU_KEY_SLOW, EventUtils.MCU_KEY_RADIO, EventUtils.MCU_KEY_NAV, EventUtils.MCU_KEY_FORCE_EJECT, EventUtils.MCU_KEY_DVD, EventUtils.MCU_KEY_NUM7, 47};

    public static String a(byte[] bArr, String str) throws UnsupportedEncodingException {
        int i;
        int length = (bArr.length * 4) / 3;
        byte[] bArr2 = new byte[(length + (length / 76) + 3)];
        int length2 = bArr.length - (bArr.length % 3);
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < length2; i4 += 3) {
            int i5 = i3 + 1;
            bArr2[i3] = a[(bArr[i4] & 255) >> 2];
            int i6 = i5 + 1;
            bArr2[i5] = a[((bArr[i4] & 3) << 4) | ((bArr[i4 + 1] & 255) >> 4)];
            int i7 = i6 + 1;
            bArr2[i6] = a[((bArr[i4 + 1] & 15) << 2) | ((bArr[i4 + 2] & 255) >> 6)];
            int i8 = i7 + 1;
            bArr2[i7] = a[bArr[i4 + 2] & EventUtils.MCU_KEY_TOUCH];
            if ((i8 - i2) % 76 != 0 || i8 == 0) {
                i3 = i8;
            } else {
                i3 = i8 + 1;
                bArr2[i8] = 10;
                i2++;
            }
        }
        switch (bArr.length % 3) {
            case 1:
                int i9 = i3 + 1;
                bArr2[i3] = a[(bArr[length2] & 255) >> 2];
                int i10 = i9 + 1;
                bArr2[i9] = a[(bArr[length2] & 3) << 4];
                int i11 = i10 + 1;
                bArr2[i10] = EventUtils.MCU_KEY_TV;
                i = i11 + 1;
                bArr2[i11] = EventUtils.MCU_KEY_TV;
                break;
            case 2:
                int i12 = i3 + 1;
                bArr2[i3] = a[(bArr[length2] & 255) >> 2];
                int i13 = i12 + 1;
                bArr2[i12] = a[((bArr[length2] & 3) << 4) | ((bArr[length2 + 1] & 255) >> 4)];
                int i14 = i13 + 1;
                bArr2[i13] = a[(bArr[length2 + 1] & 15) << 2];
                i = i14 + 1;
                bArr2[i14] = EventUtils.MCU_KEY_TV;
                break;
            default:
                i = i3;
                break;
        }
        return new String(bArr2, 0, i, str);
    }

    public static byte[] a(byte[] bArr) {
        return a(bArr, bArr.length);
    }

    public static byte[] a(byte[] bArr, int i) {
        byte b;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6 = (i / 4) * 3;
        if (i6 == 0) {
            return new byte[0];
        }
        byte[] bArr2 = new byte[i6];
        int i7 = 0;
        while (true) {
            byte b2 = bArr[i - 1];
            if (!(b2 == 10 || b2 == 13 || b2 == 32 || b2 == 9)) {
                if (b2 != 61) {
                    break;
                }
                i7++;
            }
            i--;
        }
        int i8 = 0;
        byte b3 = 0;
        int i9 = 0;
        int i10 = 0;
        while (i8 < i) {
            byte b4 = bArr[i8];
            if (b4 == 10 || b4 == 13 || b4 == 32) {
                b = b3;
                i2 = i10;
                i3 = i9;
            } else if (b4 == 9) {
                b = b3;
                i2 = i10;
                i3 = i9;
            } else {
                if (b4 >= 65 && b4 <= 90) {
                    i4 = b4 - 65;
                } else if (b4 >= 97 && b4 <= 122) {
                    i4 = b4 - 71;
                } else if (b4 >= 48 && b4 <= 57) {
                    i4 = b4 + 4;
                } else if (b4 == 43) {
                    i4 = 62;
                } else if (b4 != 47) {
                    return null;
                } else {
                    i4 = 63;
                }
                byte b5 = (b3 << 6) | ((byte) i4);
                if (i9 % 4 == 3) {
                    int i11 = i10 + 1;
                    bArr2[i10] = (byte) ((16711680 & b5) >> 16);
                    int i12 = i11 + 1;
                    bArr2[i11] = (byte) ((65280 & b5) >> 8);
                    i5 = i12 + 1;
                    bArr2[i12] = (byte) (b5 & 255);
                } else {
                    i5 = i10;
                }
                i3 = i9 + 1;
                byte b6 = b5;
                i2 = i5;
                b = b6;
            }
            i8++;
            i9 = i3;
            i10 = i2;
            b3 = b;
        }
        if (i7 > 0) {
            int i13 = b3 << (i7 * 6);
            int i14 = i10 + 1;
            bArr2[i10] = (byte) ((16711680 & i13) >> 16);
            if (i7 == 1) {
                i10 = i14 + 1;
                bArr2[i14] = (byte) ((65280 & i13) >> 8);
            } else {
                i10 = i14;
            }
        }
        byte[] bArr3 = new byte[i10];
        System.arraycopy(bArr2, 0, bArr3, 0, i10);
        return bArr3;
    }
}
