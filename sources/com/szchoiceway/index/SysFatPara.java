package com.szchoiceway.index;

import android.util.Log;

public class SysFatPara {
    public static final String TAG = "SysFatPara";
    public static int dwMainMenuFuncs0 = 0;
    public static int dwMainMenuFuncs1 = 0;
    public static int dwMainMenuFuncsReserve = 0;
    public static int dwSize = 0;
    public static int iACCVolMemory = 0;
    public static int iARMVolShow = 0;
    public static int iAUSAKDVRMode = 0;
    public static int iBTModuleType = 0;
    public static int iBTModule_ID = 0;
    public static int iBTSmall = 0;
    public static int iBTType_ID = 0;
    public static int iBackControl_T113 = 0;
    public static int iBackcarAutoRunFCAM = 0;
    public static int iBackcarMirror = 0;
    public static int iBackcarReduceSnd = 0;
    public static int iBackcarSnd = 0;
    public static int iBtnEncoder = 0;
    public static int iCanbustype = 0;
    public static int iCarCanbusName_ID = 0;
    public static int iCarstype_ID = 0;
    public static int iCheckBreak = 0;
    public static int iCurrLangIdx = 0;
    public static int iDTVType = 0;
    public static int iDVR2 = 0;
    public static int iFactoryAutoGPS = 0;
    public static int iFtyLan = 0;
    public static int iIC_37534 = 0;
    public static int iKeyDownSound = 0;
    public static int iLCDInch = 0;
    public static int iNotWifi = 0;
    public static int iPanelType = 0;
    public static int iRDSOnOff = 0;
    public static int iRadioZone = 4;
    public static int iReleaseSysMemory = 0;
    public static int iSelFrontCameraMode = 0;
    public static int iSelectDFOS = 0;
    public static int iShowTrajectory = 0;
    public static int iTVType = 1;
    public static int iTimerZone = 0;
    public static int iXinXinRongDVDMode = 0;
    public static int idwVersion = 20150603;

    public SysFatPara() {
        Log.i(TAG, "***SysFatPara***");
    }

    /* access modifiers changed from: package-private */
    public void InitFatPara() {
        dwSize = 0;
        idwVersion = 20150603;
        iCurrLangIdx = 0;
        dwMainMenuFuncs0 = 0;
        dwMainMenuFuncs1 = 0;
        dwMainMenuFuncsReserve = 0;
        iCanbustype = 0;
        iCarstype_ID = 0;
        iCarCanbusName_ID = 0;
        iTVType = 1;
        iRadioZone = 4;
        iFtyLan = 0;
        iPanelType = 0;
        iACCVolMemory = 0;
        iBackcarSnd = 0;
        iBtnEncoder = 0;
        iReleaseSysMemory = 0;
        iLCDInch = 0;
        iBTModuleType = 0;
        iTimerZone = 0;
        iRDSOnOff = 0;
        iARMVolShow = 0;
        iSelectDFOS = 0;
        iBackControl_T113 = 0;
        iShowTrajectory = 0;
        iDTVType = 0;
        iIC_37534 = 0;
        iBackcarMirror = 0;
        iKeyDownSound = 0;
        iCheckBreak = 0;
        iBTSmall = 0;
        iNotWifi = 0;
        iBTModule_ID = 0;
        iBTType_ID = 0;
        iDVR2 = 0;
        iBackcarReduceSnd = 0;
        iSelFrontCameraMode = 0;
        iBackcarAutoRunFCAM = 0;
        iFactoryAutoGPS = 0;
        iAUSAKDVRMode = 0;
        iXinXinRongDVDMode = 0;
    }
}
