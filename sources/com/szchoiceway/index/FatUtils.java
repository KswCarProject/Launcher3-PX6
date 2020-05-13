package com.szchoiceway.index;

public class FatUtils {
    public static final int CarAudi_A3 = 42;
    public static final int CarBMWX1_WC = 41;
    public static final int CarBZ408 = 8;
    public static final int CarBZB200 = 24;
    public static final int CarBaojun730_RZC = 39;
    public static final int CarBeiqiWeiWangM20 = 25;
    public static final int CarBiaozhi2008 = 23;
    public static final int CarCHRYSLER = 44;
    public static final int CarCRUZE = 11;
    public static final int CarCRV = 5;
    public static final int CarCS75_RZC = 38;
    public static final int CarCamary = 4;
    public static final int CarCamary_RZC = 32;
    public static final int CarCherokee = 47;
    public static final int CarChery = 52;
    public static final int CarChuanqiGA3 = 26;
    public static final int CarCrider = 21;
    public static final int CarCrider_WC = 31;
    public static final int CarEC7_RZC = 37;
    public static final int CarFEISI = 14;
    public static final int CarFIAT = 12;
    public static final int CarFOCUS = 9;
    public static final int CarGC7_RZC = 36;
    public static final int CarGM = 16;
    public static final int CarGM_RZC = 35;
    public static final int CarGolf7 = 27;
    public static final int CarHaimaS7 = 18;
    public static final int CarHaimam5 = 40;
    public static final int CarIX35 = 6;
    public static final int CarIX45 = 13;
    public static final int CarJAC_Refine = 48;
    public static final int CarJeepRubion = 22;
    public static final int CarKX5 = 49;
    public static final int CarKeSaiWei = 202;
    public static final int CarLOVA = 51;
    public static final int CarMG_GS = 43;
    public static final int CarMaiRuiWei = 203;
    public static final int CarMaiTeng_cc = 1;
    public static final int CarMazida = 15;
    public static final int CarMondeo = 34;
    public static final int CarOPEL = 7;
    public static final int CarQijun_RZ = 28;
    public static final int CarQoros = 54;
    public static final int CarRenault = 50;
    public static final int CarRongWei360 = 45;
    public static final int CarRuiHu3X = 53;
    public static final int CarRuiyi = 2;
    public static final int CarSiyu = 3;
    public static final int CarTIANLAI = 17;
    public static final int CarVolvoS6_WC = 30;
    public static final int CarX80_RZ = 29;
    public static final int CarXingShuo = 201;
    public static final int CarXinpuFOCUS = 19;
    public static final int CarYAGE = 20;
    public static final int CarYAZUN = 10;
    public static final int CarZhongHangTianYi = 200;
    public static final int CarZoyteT600 = 46;
    public static final int Car_CAN_IR = 33;
    public static final int Car_Max = 255;
    public static final int Car_Normal = 0;
    public static final String ZXW_RELOAD_TOUCH_KEY_CFG = "com.choiceway.FatUtils.ZXW_RELOAD_TOUCH_KEY_CFG";
    public static final String ZXW_SAVE_CANBUS_SET = "com.choiceway.FatUtils.ZXW_SAVE_CANBUS_SET";
    public static final String ZXW_SAVE_CANBUS_SET_EXTRA = "com.choiceway.FatUtils.ZXW_SAVE_CANBUS_SET_EXTRA";
    public static final String ZXW_TOUCH_LEARN_ID = "com.choiceway.FatUtils.ZXW_TOUCH_LEARN_ID";
    public static final String ZXW_TOUCH_LEARN_ID_EXTRA = "com.choiceway.FatUtils.ZXW_TOUCH_LEARN_ID_EXTRA";
    public static final String ZXW_TOUCH_LEARN_INFOR = "com.choiceway.FatUtils.ZXW_TOUCH_LEARN_INFOR";
    public static final String ZXW_TOUCH_LEARN_INFOR_EXTRA = "com.choiceway.FatUtils.ZXW_TOUCH_LEARN_INFOR_EXTRA";
    public static final String ZXW_TOUCH_LEARN_STATUS = "com.choiceway.FatUtils.ZXW_TOUCH_LEARN_STATUS";
    public static final String ZXW_TOUCH_LEARN_STATUS_EXTRA = "com.choiceway.FatUtils.ZXW_TOUCH_LEARN_STATUS_EXTRA";

    public static int read_bit(int Data, int pos) {
        int b_mask = 1 << pos;
        if ((Data & b_mask) == b_mask) {
            return 1;
        }
        return 0;
    }

    public static int BIT_ON(int val, int pos) {
        return val | (1 << pos);
    }

    public static int BIT_OFF(int val, int pos) {
        return val & ((1 << pos) ^ -1);
    }
}
