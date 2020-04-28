package com.szchoiceway.eventcenter;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IEventService extends IInterface {
    int GetBTStatus() throws RemoteException;

    int GetCurrDim() throws RemoteException;

    int GetSignalStatus() throws RemoteException;

    boolean IsBackcarConnected() throws RemoteException;

    boolean IsBrakeConneted() throws RemoteException;

    boolean IsDiscConneted() throws RemoteException;

    boolean IsMCUUpgradeWriteErr() throws RemoteException;

    boolean IsMuteOn() throws RemoteException;

    int LoadNLightVal() throws RemoteException;

    void OpenVideo(boolean z) throws RemoteException;

    boolean Send8902McuUpgradeData(byte[] bArr, int i) throws RemoteException;

    void SendAudioSetToMCU(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    void SendBALFADVal(byte b, byte b2) throws RemoteException;

    void SendBLVal(byte b, byte b2) throws RemoteException;

    void SendBlackState(boolean z) throws RemoteException;

    void SendDVRKey(byte b) throws RemoteException;

    void SendFactorySet() throws RemoteException;

    void SendFactorySetToMCU(byte b, byte b2, byte b3, byte b4, byte b5, byte b6) throws RemoteException;

    void SendGPSVolToMCU(byte b) throws RemoteException;

    void SendMainVol(byte b) throws RemoteException;

    void SendSysRTCTimeMCU(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    void SendVol_KSW(boolean z, int i, int i2, int i3) throws RemoteException;

    void SendWheelKey(int i) throws RemoteException;

    void SetCurrDim(int i) throws RemoteException;

    void SetDVDVideoCH(int i) throws RemoteException;

    void SetVideoCH(int i) throws RemoteException;

    void SetVideoSize(int i, int i2, int i3, int i4) throws RemoteException;

    void appySetting() throws RemoteException;

    void beep() throws RemoteException;

    void commitSetting() throws RemoteException;

    void enterUpgradeMode() throws RemoteException;

    void exitCurMode(int i) throws RemoteException;

    void exitUpgradeMode() throws RemoteException;

    byte getBALVal() throws RemoteException;

    byte getBassFre() throws RemoteException;

    byte getBassVal() throws RemoteException;

    int getCameraOwner() throws RemoteException;

    byte getEQMode() throws RemoteException;

    byte getFADVal() throws RemoteException;

    boolean getLoudStatus() throws RemoteException;

    String getMCUVer() throws RemoteException;

    byte getMainVolval() throws RemoteException;

    boolean getMcuInitStatus() throws RemoteException;

    byte getMiddleFre() throws RemoteException;

    byte getMiddleVal() throws RemoteException;

    boolean getMuteStatus() throws RemoteException;

    boolean getRadioAFState() throws RemoteException;

    boolean getRadioAMSState() throws RemoteException;

    boolean getRadioAPSState() throws RemoteException;

    int getRadioBand() throws RemoteException;

    boolean getRadioDXLOCState() throws RemoteException;

    int getRadioFreq() throws RemoteException;

    int[] getRadioFreqList() throws RemoteException;

    int getRadioNum() throws RemoteException;

    String getRadioPTYName() throws RemoteException;

    int getRadioPTYNum() throws RemoteException;

    boolean getRadioPTYState() throws RemoteException;

    boolean getRadioRDSState() throws RemoteException;

    boolean getRadioSTMonoState() throws RemoteException;

    boolean getRadioSteroIconState() throws RemoteException;

    boolean getRadioTAState() throws RemoteException;

    boolean getRadioTPIconState() throws RemoteException;

    boolean getRadioTrafficState() throws RemoteException;

    boolean getSettingBoolean(String str, boolean z) throws RemoteException;

    float getSettingFloat(String str, float f) throws RemoteException;

    int getSettingInt(String str, int i) throws RemoteException;

    long getSettingLong(String str, long j) throws RemoteException;

    String getSettingString(String str, String str2) throws RemoteException;

    String getTFTVer() throws RemoteException;

    byte getTrebleFre() throws RemoteException;

    byte getTrebleVal() throws RemoteException;

    byte getUserBassVal() throws RemoteException;

    byte getUserMiddleVal() throws RemoteException;

    byte getUserTrebleVal() throws RemoteException;

    int getValidCurFolder() throws RemoteException;

    int getValidCurTime() throws RemoteException;

    int getValidCurTrack() throws RemoteException;

    int getValidLoopMode() throws RemoteException;

    int getValidMode() throws RemoteException;

    String getValidModeAblumInfor() throws RemoteException;

    String getValidModeArtistInfor() throws RemoteException;

    String getValidModeTitleInfor() throws RemoteException;

    int getValidPlayStatus() throws RemoteException;

    int getValidRepeatMode() throws RemoteException;

    int getValidTotFolder() throws RemoteException;

    int getValidTotTime() throws RemoteException;

    int getValidTotTrack() throws RemoteException;

    boolean get_kesaiwei_bPark() throws RemoteException;

    boolean get_kesaiwei_chk_Video_Driving_Ban() throws RemoteException;

    int get_m_i_easyconn_state_KSW() throws RemoteException;

    boolean getmIsAddMouseView() throws RemoteException;

    void initRadioZone(byte b) throws RemoteException;

    boolean isUpgradeMode() throws RemoteException;

    void openTVout(int i, boolean z) throws RemoteException;

    void putSettingBoolean(String str, boolean z) throws RemoteException;

    void putSettingFloat(String str, float f) throws RemoteException;

    void putSettingInt(String str, int i) throws RemoteException;

    void putSettingLong(String str, long j) throws RemoteException;

    void putSettingStr(String str, String str2) throws RemoteException;

    void sendBTState(int i) throws RemoteException;

    void sendCanbusData(byte[] bArr) throws RemoteException;

    void sendDvdDataToMcu(byte[] bArr) throws RemoteException;

    void sendEQMode(int i) throws RemoteException;

    void sendKeyDownUpSync(int i) throws RemoteException;

    void sendMcuData_KSW(byte[] bArr) throws RemoteException;

    boolean sendMcuUpgradeData(long j, byte[] bArr, int i, int i2, boolean z) throws RemoteException;

    boolean sendMcuUpgradeMode(boolean z) throws RemoteException;

    void sendMode(int i, boolean z) throws RemoteException;

    void sendMuteState(boolean z) throws RemoteException;

    void sendPlayState(boolean z) throws RemoteException;

    void sendRadioKey(int i) throws RemoteException;

    void sendResetDVD(boolean z) throws RemoteException;

    void sendSetup(byte b, byte b2) throws RemoteException;

    void sendSystemKey(int i) throws RemoteException;

    void sendTVKey(int i) throws RemoteException;

    void sendToOSData() throws RemoteException;

    void sendTouchPos(int i, int i2, boolean z) throws RemoteException;

    void sendUserFreq(int i) throws RemoteException;

    void send_KSW_page2_vol_info(int i, int i2, int i3, int i4) throws RemoteException;

    void setAUXCallback(ICallbackfn iCallbackfn) throws RemoteException;

    void setCameraOwner(int i) throws RemoteException;

    void setCarMediaCallback(ICallbackfn iCallbackfn) throws RemoteException;

    void setCurModeCallback(int i, ICallbackfn iCallbackfn) throws RemoteException;

    void setDashBoardCallback(ICallbackfn iCallbackfn) throws RemoteException;

    void setGpsFocusCallback(ICallbackfn iCallbackfn) throws RemoteException;

    void setMcuInitStatus(boolean z) throws RemoteException;

    void setRadioCallback(ICallbackfn iCallbackfn) throws RemoteException;

    void setTVCallback(ICallbackfn iCallbackfn) throws RemoteException;

    void setValidModeInfor(String str, String str2, String str3, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) throws RemoteException;

    public static abstract class Stub extends Binder implements IEventService {
        private static final String DESCRIPTOR = "com.szchoiceway.eventcenter.IEventService";
        static final int TRANSACTION_GetBTStatus = 108;
        static final int TRANSACTION_GetCurrDim = 112;
        static final int TRANSACTION_GetSignalStatus = 76;
        static final int TRANSACTION_IsBackcarConnected = 109;
        static final int TRANSACTION_IsBrakeConneted = 73;
        static final int TRANSACTION_IsDiscConneted = 97;
        static final int TRANSACTION_IsMCUUpgradeWriteErr = 74;
        static final int TRANSACTION_IsMuteOn = 79;
        static final int TRANSACTION_LoadNLightVal = 110;
        static final int TRANSACTION_OpenVideo = 77;
        static final int TRANSACTION_Send8902McuUpgradeData = 114;
        static final int TRANSACTION_SendAudioSetToMCU = 63;
        static final int TRANSACTION_SendBALFADVal = 71;
        static final int TRANSACTION_SendBLVal = 68;
        static final int TRANSACTION_SendBlackState = 72;
        static final int TRANSACTION_SendDVRKey = 66;
        static final int TRANSACTION_SendFactorySet = 107;
        static final int TRANSACTION_SendFactorySetToMCU = 47;
        static final int TRANSACTION_SendGPSVolToMCU = 106;
        static final int TRANSACTION_SendMainVol = 67;
        static final int TRANSACTION_SendSysRTCTimeMCU = 65;
        static final int TRANSACTION_SendVol_KSW = 115;
        static final int TRANSACTION_SendWheelKey = 64;
        static final int TRANSACTION_SetCurrDim = 111;
        static final int TRANSACTION_SetDVDVideoCH = 95;
        static final int TRANSACTION_SetVideoCH = 75;
        static final int TRANSACTION_SetVideoSize = 78;
        static final int TRANSACTION_appySetting = 39;
        static final int TRANSACTION_beep = 7;
        static final int TRANSACTION_commitSetting = 38;
        static final int TRANSACTION_enterUpgradeMode = 101;
        static final int TRANSACTION_exitCurMode = 31;
        static final int TRANSACTION_exitUpgradeMode = 102;
        static final int TRANSACTION_getBALVal = 55;
        static final int TRANSACTION_getBassFre = 52;
        static final int TRANSACTION_getBassVal = 49;
        static final int TRANSACTION_getCameraOwner = 124;
        static final int TRANSACTION_getEQMode = 57;
        static final int TRANSACTION_getFADVal = 56;
        static final int TRANSACTION_getLoudStatus = 62;
        static final int TRANSACTION_getMCUVer = 32;
        static final int TRANSACTION_getMainVolval = 48;
        static final int TRANSACTION_getMcuInitStatus = 100;
        static final int TRANSACTION_getMiddleFre = 53;
        static final int TRANSACTION_getMiddleVal = 51;
        static final int TRANSACTION_getMuteStatus = 61;
        static final int TRANSACTION_getRadioAFState = 20;
        static final int TRANSACTION_getRadioAMSState = 24;
        static final int TRANSACTION_getRadioAPSState = 25;
        static final int TRANSACTION_getRadioBand = 14;
        static final int TRANSACTION_getRadioDXLOCState = 23;
        static final int TRANSACTION_getRadioFreq = 12;
        static final int TRANSACTION_getRadioFreqList = 13;
        static final int TRANSACTION_getRadioNum = 15;
        static final int TRANSACTION_getRadioPTYName = 19;
        static final int TRANSACTION_getRadioPTYNum = 18;
        static final int TRANSACTION_getRadioPTYState = 17;
        static final int TRANSACTION_getRadioRDSState = 16;
        static final int TRANSACTION_getRadioSTMonoState = 22;
        static final int TRANSACTION_getRadioSteroIconState = 26;
        static final int TRANSACTION_getRadioTAState = 21;
        static final int TRANSACTION_getRadioTPIconState = 27;
        static final int TRANSACTION_getRadioTrafficState = 28;
        static final int TRANSACTION_getSettingBoolean = 40;
        static final int TRANSACTION_getSettingFloat = 41;
        static final int TRANSACTION_getSettingInt = 42;
        static final int TRANSACTION_getSettingLong = 43;
        static final int TRANSACTION_getSettingString = 44;
        static final int TRANSACTION_getTFTVer = 113;
        static final int TRANSACTION_getTrebleFre = 54;
        static final int TRANSACTION_getTrebleVal = 50;
        static final int TRANSACTION_getUserBassVal = 58;
        static final int TRANSACTION_getUserMiddleVal = 60;
        static final int TRANSACTION_getUserTrebleVal = 59;
        static final int TRANSACTION_getValidCurFolder = 90;
        static final int TRANSACTION_getValidCurTime = 88;
        static final int TRANSACTION_getValidCurTrack = 86;
        static final int TRANSACTION_getValidLoopMode = 92;
        static final int TRANSACTION_getValidMode = 46;
        static final int TRANSACTION_getValidModeAblumInfor = 84;
        static final int TRANSACTION_getValidModeArtistInfor = 85;
        static final int TRANSACTION_getValidModeTitleInfor = 83;
        static final int TRANSACTION_getValidPlayStatus = 94;
        static final int TRANSACTION_getValidRepeatMode = 93;
        static final int TRANSACTION_getValidTotFolder = 91;
        static final int TRANSACTION_getValidTotTime = 89;
        static final int TRANSACTION_getValidTotTrack = 87;
        static final int TRANSACTION_get_kesaiwei_bPark = 117;
        static final int TRANSACTION_get_kesaiwei_chk_Video_Driving_Ban = 116;
        static final int TRANSACTION_get_m_i_easyconn_state_KSW = 122;
        static final int TRANSACTION_getmIsAddMouseView = 128;
        static final int TRANSACTION_initRadioZone = 80;
        static final int TRANSACTION_isUpgradeMode = 105;
        static final int TRANSACTION_openTVout = 126;
        static final int TRANSACTION_putSettingBoolean = 37;
        static final int TRANSACTION_putSettingFloat = 36;
        static final int TRANSACTION_putSettingInt = 34;
        static final int TRANSACTION_putSettingLong = 35;
        static final int TRANSACTION_putSettingStr = 33;
        static final int TRANSACTION_sendBTState = 11;
        static final int TRANSACTION_sendCanbusData = 81;
        static final int TRANSACTION_sendDvdDataToMcu = 118;
        static final int TRANSACTION_sendEQMode = 5;
        static final int TRANSACTION_sendKeyDownUpSync = 129;
        static final int TRANSACTION_sendMcuData_KSW = 119;
        static final int TRANSACTION_sendMcuUpgradeData = 104;
        static final int TRANSACTION_sendMcuUpgradeMode = 103;
        static final int TRANSACTION_sendMode = 1;
        static final int TRANSACTION_sendMuteState = 8;
        static final int TRANSACTION_sendPlayState = 9;
        static final int TRANSACTION_sendRadioKey = 2;
        static final int TRANSACTION_sendResetDVD = 96;
        static final int TRANSACTION_sendSetup = 10;
        static final int TRANSACTION_sendSystemKey = 4;
        static final int TRANSACTION_sendTVKey = 3;
        static final int TRANSACTION_sendToOSData = 127;
        static final int TRANSACTION_sendTouchPos = 45;
        static final int TRANSACTION_sendUserFreq = 6;
        static final int TRANSACTION_send_KSW_page2_vol_info = 123;
        static final int TRANSACTION_setAUXCallback = 70;
        static final int TRANSACTION_setCameraOwner = 125;
        static final int TRANSACTION_setCarMediaCallback = 98;
        static final int TRANSACTION_setCurModeCallback = 30;
        static final int TRANSACTION_setDashBoardCallback = 120;
        static final int TRANSACTION_setGpsFocusCallback = 121;
        static final int TRANSACTION_setMcuInitStatus = 99;
        static final int TRANSACTION_setRadioCallback = 29;
        static final int TRANSACTION_setTVCallback = 69;
        static final int TRANSACTION_setValidModeInfor = 82;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IEventService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IEventService)) {
                return new Proxy(obj);
            }
            return (IEventService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
            java.lang.NullPointerException
            	at jadx.core.dex.instructions.args.InsnArg.wrapInstruction(InsnArg.java:118)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.inline(CodeShrinkVisitor.java:146)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:71)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
            */
        public boolean onTransact(int r30, android.os.Parcel r31, android.os.Parcel r32, int r33) throws android.os.RemoteException {
            /*
                r29 = this;
                r13 = r29
                r14 = r30
                r15 = r31
                r12 = r32
                java.lang.String r11 = "com.szchoiceway.eventcenter.IEventService"
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r16 = 1
                if (r14 == r0) goto L_0x0947
                r0 = 0
                switch(r14) {
                    case 1: goto L_0x092f;
                    case 2: goto L_0x091f;
                    case 3: goto L_0x090f;
                    case 4: goto L_0x08ff;
                    case 5: goto L_0x08ef;
                    case 6: goto L_0x08df;
                    case 7: goto L_0x08d3;
                    case 8: goto L_0x08bf;
                    case 9: goto L_0x08ab;
                    case 10: goto L_0x0897;
                    case 11: goto L_0x0887;
                    case 12: goto L_0x0877;
                    case 13: goto L_0x0867;
                    case 14: goto L_0x0857;
                    case 15: goto L_0x0847;
                    case 16: goto L_0x0837;
                    case 17: goto L_0x0827;
                    case 18: goto L_0x0817;
                    case 19: goto L_0x0807;
                    case 20: goto L_0x07f7;
                    case 21: goto L_0x07e7;
                    case 22: goto L_0x07d7;
                    case 23: goto L_0x07c7;
                    case 24: goto L_0x07b7;
                    case 25: goto L_0x07a7;
                    case 26: goto L_0x0797;
                    case 27: goto L_0x0787;
                    case 28: goto L_0x0777;
                    case 29: goto L_0x0763;
                    case 30: goto L_0x074b;
                    case 31: goto L_0x073b;
                    case 32: goto L_0x072b;
                    case 33: goto L_0x0717;
                    case 34: goto L_0x0703;
                    case 35: goto L_0x06ef;
                    case 36: goto L_0x06db;
                    case 37: goto L_0x06c3;
                    case 38: goto L_0x06b7;
                    case 39: goto L_0x06ab;
                    case 40: goto L_0x068f;
                    case 41: goto L_0x0677;
                    case 42: goto L_0x065f;
                    case 43: goto L_0x0647;
                    case 44: goto L_0x062f;
                    case 45: goto L_0x0613;
                    case 46: goto L_0x0603;
                    case 47: goto L_0x05d6;
                    case 48: goto L_0x05c6;
                    case 49: goto L_0x05b6;
                    case 50: goto L_0x05a6;
                    case 51: goto L_0x0596;
                    case 52: goto L_0x0586;
                    case 53: goto L_0x0576;
                    case 54: goto L_0x0566;
                    case 55: goto L_0x0556;
                    case 56: goto L_0x0546;
                    case 57: goto L_0x0536;
                    case 58: goto L_0x0526;
                    case 59: goto L_0x0516;
                    case 60: goto L_0x0506;
                    case 61: goto L_0x04f6;
                    case 62: goto L_0x04e6;
                    case 63: goto L_0x04b9;
                    case 64: goto L_0x04a9;
                    case 65: goto L_0x047c;
                    case 66: goto L_0x046c;
                    case 67: goto L_0x045c;
                    case 68: goto L_0x0448;
                    case 69: goto L_0x0434;
                    case 70: goto L_0x0420;
                    case 71: goto L_0x040c;
                    case 72: goto L_0x03f8;
                    case 73: goto L_0x03e8;
                    case 74: goto L_0x03d8;
                    case 75: goto L_0x03c8;
                    case 76: goto L_0x03b8;
                    case 77: goto L_0x03a4;
                    case 78: goto L_0x0388;
                    case 79: goto L_0x0377;
                    case 80: goto L_0x0368;
                    case 81: goto L_0x0359;
                    case 82: goto L_0x0304;
                    case 83: goto L_0x02f6;
                    case 84: goto L_0x02e8;
                    case 85: goto L_0x02da;
                    case 86: goto L_0x02cc;
                    case 87: goto L_0x02be;
                    case 88: goto L_0x02b0;
                    case 89: goto L_0x02a2;
                    case 90: goto L_0x0294;
                    case 91: goto L_0x0286;
                    case 92: goto L_0x0278;
                    case 93: goto L_0x026a;
                    case 94: goto L_0x025c;
                    case 95: goto L_0x024e;
                    case 96: goto L_0x023c;
                    case 97: goto L_0x022e;
                    case 98: goto L_0x021c;
                    case 99: goto L_0x020a;
                    case 100: goto L_0x01fc;
                    case 101: goto L_0x01f2;
                    case 102: goto L_0x01e8;
                    case 103: goto L_0x01d2;
                    case 104: goto L_0x01a4;
                    case 105: goto L_0x0196;
                    case 106: goto L_0x0188;
                    case 107: goto L_0x017e;
                    case 108: goto L_0x0170;
                    case 109: goto L_0x0162;
                    case 110: goto L_0x0154;
                    case 111: goto L_0x0146;
                    case 112: goto L_0x0138;
                    case 113: goto L_0x012a;
                    case 114: goto L_0x0114;
                    case 115: goto L_0x00f6;
                    case 116: goto L_0x00e8;
                    case 117: goto L_0x00da;
                    case 118: goto L_0x00cc;
                    case 119: goto L_0x00be;
                    case 120: goto L_0x00ac;
                    case 121: goto L_0x009a;
                    case 122: goto L_0x008c;
                    case 123: goto L_0x0072;
                    case 124: goto L_0x0064;
                    case 125: goto L_0x0056;
                    case 126: goto L_0x0040;
                    case 127: goto L_0x0036;
                    case 128: goto L_0x0028;
                    case 129: goto L_0x001a;
                    default: goto L_0x0015;
                }
            L_0x0015:
                boolean r0 = super.onTransact(r30, r31, r32, r33)
                return r0
            L_0x001a:
                r15.enforceInterface(r11)
                int r0 = r31.readInt()
                r13.sendKeyDownUpSync(r0)
                r32.writeNoException()
                return r16
            L_0x0028:
                r15.enforceInterface(r11)
                boolean r0 = r29.getmIsAddMouseView()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x0036:
                r15.enforceInterface(r11)
                r29.sendToOSData()
                r32.writeNoException()
                return r16
            L_0x0040:
                r15.enforceInterface(r11)
                int r1 = r31.readInt()
                int r2 = r31.readInt()
                if (r2 == 0) goto L_0x004f
                r0 = 1
            L_0x004f:
                r13.openTVout(r1, r0)
                r32.writeNoException()
                return r16
            L_0x0056:
                r15.enforceInterface(r11)
                int r0 = r31.readInt()
                r13.setCameraOwner(r0)
                r32.writeNoException()
                return r16
            L_0x0064:
                r15.enforceInterface(r11)
                int r0 = r29.getCameraOwner()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x0072:
                r15.enforceInterface(r11)
                int r0 = r31.readInt()
                int r1 = r31.readInt()
                int r2 = r31.readInt()
                int r3 = r31.readInt()
                r13.send_KSW_page2_vol_info(r0, r1, r2, r3)
                r32.writeNoException()
                return r16
            L_0x008c:
                r15.enforceInterface(r11)
                int r0 = r29.get_m_i_easyconn_state_KSW()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x009a:
                r15.enforceInterface(r11)
                android.os.IBinder r0 = r31.readStrongBinder()
                com.szchoiceway.eventcenter.ICallbackfn r0 = com.szchoiceway.eventcenter.ICallbackfn.Stub.asInterface(r0)
                r13.setGpsFocusCallback(r0)
                r32.writeNoException()
                return r16
            L_0x00ac:
                r15.enforceInterface(r11)
                android.os.IBinder r0 = r31.readStrongBinder()
                com.szchoiceway.eventcenter.ICallbackfn r0 = com.szchoiceway.eventcenter.ICallbackfn.Stub.asInterface(r0)
                r13.setDashBoardCallback(r0)
                r32.writeNoException()
                return r16
            L_0x00be:
                r15.enforceInterface(r11)
                byte[] r0 = r31.createByteArray()
                r13.sendMcuData_KSW(r0)
                r32.writeNoException()
                return r16
            L_0x00cc:
                r15.enforceInterface(r11)
                byte[] r0 = r31.createByteArray()
                r13.sendDvdDataToMcu(r0)
                r32.writeNoException()
                return r16
            L_0x00da:
                r15.enforceInterface(r11)
                boolean r0 = r29.get_kesaiwei_bPark()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x00e8:
                r15.enforceInterface(r11)
                boolean r0 = r29.get_kesaiwei_chk_Video_Driving_Ban()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x00f6:
                r15.enforceInterface(r11)
                int r1 = r31.readInt()
                if (r1 == 0) goto L_0x0101
                r0 = 1
            L_0x0101:
                int r1 = r31.readInt()
                int r2 = r31.readInt()
                int r3 = r31.readInt()
                r13.SendVol_KSW(r0, r1, r2, r3)
                r32.writeNoException()
                return r16
            L_0x0114:
                r15.enforceInterface(r11)
                byte[] r0 = r31.createByteArray()
                int r1 = r31.readInt()
                boolean r2 = r13.Send8902McuUpgradeData(r0, r1)
                r32.writeNoException()
                r12.writeInt(r2)
                return r16
            L_0x012a:
                r15.enforceInterface(r11)
                java.lang.String r0 = r29.getTFTVer()
                r32.writeNoException()
                r12.writeString(r0)
                return r16
            L_0x0138:
                r15.enforceInterface(r11)
                int r0 = r29.GetCurrDim()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x0146:
                r15.enforceInterface(r11)
                int r0 = r31.readInt()
                r13.SetCurrDim(r0)
                r32.writeNoException()
                return r16
            L_0x0154:
                r15.enforceInterface(r11)
                int r0 = r29.LoadNLightVal()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x0162:
                r15.enforceInterface(r11)
                boolean r0 = r29.IsBackcarConnected()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x0170:
                r15.enforceInterface(r11)
                int r0 = r29.GetBTStatus()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x017e:
                r15.enforceInterface(r11)
                r29.SendFactorySet()
                r32.writeNoException()
                return r16
            L_0x0188:
                r15.enforceInterface(r11)
                byte r0 = r31.readByte()
                r13.SendGPSVolToMCU(r0)
                r32.writeNoException()
                return r16
            L_0x0196:
                r15.enforceInterface(r11)
                boolean r0 = r29.isUpgradeMode()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x01a4:
                r15.enforceInterface(r11)
                long r7 = r31.readLong()
                byte[] r9 = r31.createByteArray()
                int r10 = r31.readInt()
                int r17 = r31.readInt()
                int r1 = r31.readInt()
                if (r1 == 0) goto L_0x01bf
                r6 = 1
                goto L_0x01c0
            L_0x01bf:
                r6 = 0
            L_0x01c0:
                r0 = r29
                r1 = r7
                r3 = r9
                r4 = r10
                r5 = r17
                boolean r0 = r0.sendMcuUpgradeData(r1, r3, r4, r5, r6)
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x01d2:
                r15.enforceInterface(r11)
                int r1 = r31.readInt()
                if (r1 == 0) goto L_0x01dd
                r0 = 1
            L_0x01dd:
                boolean r1 = r13.sendMcuUpgradeMode(r0)
                r32.writeNoException()
                r12.writeInt(r1)
                return r16
            L_0x01e8:
                r15.enforceInterface(r11)
                r29.exitUpgradeMode()
                r32.writeNoException()
                return r16
            L_0x01f2:
                r15.enforceInterface(r11)
                r29.enterUpgradeMode()
                r32.writeNoException()
                return r16
            L_0x01fc:
                r15.enforceInterface(r11)
                boolean r0 = r29.getMcuInitStatus()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x020a:
                r15.enforceInterface(r11)
                int r1 = r31.readInt()
                if (r1 == 0) goto L_0x0215
                r0 = 1
            L_0x0215:
                r13.setMcuInitStatus(r0)
                r32.writeNoException()
                return r16
            L_0x021c:
                r15.enforceInterface(r11)
                android.os.IBinder r0 = r31.readStrongBinder()
                com.szchoiceway.eventcenter.ICallbackfn r0 = com.szchoiceway.eventcenter.ICallbackfn.Stub.asInterface(r0)
                r13.setCarMediaCallback(r0)
                r32.writeNoException()
                return r16
            L_0x022e:
                r15.enforceInterface(r11)
                boolean r0 = r29.IsDiscConneted()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x023c:
                r15.enforceInterface(r11)
                int r1 = r31.readInt()
                if (r1 == 0) goto L_0x0247
                r0 = 1
            L_0x0247:
                r13.sendResetDVD(r0)
                r32.writeNoException()
                return r16
            L_0x024e:
                r15.enforceInterface(r11)
                int r0 = r31.readInt()
                r13.SetDVDVideoCH(r0)
                r32.writeNoException()
                return r16
            L_0x025c:
                r15.enforceInterface(r11)
                int r0 = r29.getValidPlayStatus()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x026a:
                r15.enforceInterface(r11)
                int r0 = r29.getValidRepeatMode()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x0278:
                r15.enforceInterface(r11)
                int r0 = r29.getValidLoopMode()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x0286:
                r15.enforceInterface(r11)
                int r0 = r29.getValidTotFolder()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x0294:
                r15.enforceInterface(r11)
                int r0 = r29.getValidCurFolder()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x02a2:
                r15.enforceInterface(r11)
                int r0 = r29.getValidTotTime()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x02b0:
                r15.enforceInterface(r11)
                int r0 = r29.getValidCurTime()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x02be:
                r15.enforceInterface(r11)
                int r0 = r29.getValidTotTrack()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x02cc:
                r15.enforceInterface(r11)
                int r0 = r29.getValidCurTrack()
                r32.writeNoException()
                r12.writeInt(r0)
                return r16
            L_0x02da:
                r15.enforceInterface(r11)
                java.lang.String r0 = r29.getValidModeArtistInfor()
                r32.writeNoException()
                r12.writeString(r0)
                return r16
            L_0x02e8:
                r15.enforceInterface(r11)
                java.lang.String r0 = r29.getValidModeAblumInfor()
                r32.writeNoException()
                r12.writeString(r0)
                return r16
            L_0x02f6:
                r15.enforceInterface(r11)
                java.lang.String r0 = r29.getValidModeTitleInfor()
                r32.writeNoException()
                r12.writeString(r0)
                return r16
            L_0x0304:
                r15.enforceInterface(r11)
                java.lang.String r17 = r31.readString()
                java.lang.String r18 = r31.readString()
                java.lang.String r19 = r31.readString()
                int r20 = r31.readInt()
                int r21 = r31.readInt()
                int r22 = r31.readInt()
                int r23 = r31.readInt()
                int r24 = r31.readInt()
                int r25 = r31.readInt()
                int r26 = r31.readInt()
                int r27 = r31.readInt()
                int r28 = r31.readInt()
                r0 = r29
                r1 = r17
                r2 = r18
                r3 = r19
                r4 = r20
                r5 = r21
                r6 = r22
                r7 = r23
                r8 = r24
                r9 = r25
                r10 = r26
                r14 = r11
                r11 = r27
                r12 = r28
                r0.setValidModeInfor(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
                r32.writeNoException()
                return r16
            L_0x0359:
                r14 = r11
                r15.enforceInterface(r14)
                byte[] r0 = r31.createByteArray()
                r13.sendCanbusData(r0)
                r32.writeNoException()
                return r16
            L_0x0368:
                r14 = r11
                r15.enforceInterface(r14)
                byte r0 = r31.readByte()
                r13.initRadioZone(r0)
                r32.writeNoException()
                return r16
            L_0x0377:
                r14 = r11
                r15.enforceInterface(r14)
                boolean r0 = r29.IsMuteOn()
                r32.writeNoException()
                r7 = r32
                r7.writeInt(r0)
                return r16
            L_0x0388:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r31.readInt()
                int r1 = r31.readInt()
                int r2 = r31.readInt()
                int r3 = r31.readInt()
                r13.SetVideoSize(r0, r1, r2, r3)
                r32.writeNoException()
                return r16
            L_0x03a4:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r1 = r31.readInt()
                if (r1 == 0) goto L_0x03b1
                r0 = 1
            L_0x03b1:
                r13.OpenVideo(r0)
                r32.writeNoException()
                return r16
            L_0x03b8:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r29.GetSignalStatus()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x03c8:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r31.readInt()
                r13.SetVideoCH(r0)
                r32.writeNoException()
                return r16
            L_0x03d8:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.IsMCUUpgradeWriteErr()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x03e8:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.IsBrakeConneted()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x03f8:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r1 = r31.readInt()
                if (r1 == 0) goto L_0x0405
                r0 = 1
            L_0x0405:
                r13.SendBlackState(r0)
                r32.writeNoException()
                return r16
            L_0x040c:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r31.readByte()
                byte r1 = r31.readByte()
                r13.SendBALFADVal(r0, r1)
                r32.writeNoException()
                return r16
            L_0x0420:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                android.os.IBinder r0 = r31.readStrongBinder()
                com.szchoiceway.eventcenter.ICallbackfn r0 = com.szchoiceway.eventcenter.ICallbackfn.Stub.asInterface(r0)
                r13.setAUXCallback(r0)
                r32.writeNoException()
                return r16
            L_0x0434:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                android.os.IBinder r0 = r31.readStrongBinder()
                com.szchoiceway.eventcenter.ICallbackfn r0 = com.szchoiceway.eventcenter.ICallbackfn.Stub.asInterface(r0)
                r13.setTVCallback(r0)
                r32.writeNoException()
                return r16
            L_0x0448:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r31.readByte()
                byte r1 = r31.readByte()
                r13.SendBLVal(r0, r1)
                r32.writeNoException()
                return r16
            L_0x045c:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r31.readByte()
                r13.SendMainVol(r0)
                r32.writeNoException()
                return r16
            L_0x046c:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r31.readByte()
                r13.SendDVRKey(r0)
                r32.writeNoException()
                return r16
            L_0x047c:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r8 = r31.readInt()
                int r9 = r31.readInt()
                int r10 = r31.readInt()
                int r11 = r31.readInt()
                int r12 = r31.readInt()
                int r17 = r31.readInt()
                r0 = r29
                r1 = r8
                r2 = r9
                r3 = r10
                r4 = r11
                r5 = r12
                r6 = r17
                r0.SendSysRTCTimeMCU(r1, r2, r3, r4, r5, r6)
                r32.writeNoException()
                return r16
            L_0x04a9:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r31.readInt()
                r13.SendWheelKey(r0)
                r32.writeNoException()
                return r16
            L_0x04b9:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r8 = r31.readInt()
                int r9 = r31.readInt()
                int r10 = r31.readInt()
                int r11 = r31.readInt()
                int r12 = r31.readInt()
                int r17 = r31.readInt()
                r0 = r29
                r1 = r8
                r2 = r9
                r3 = r10
                r4 = r11
                r5 = r12
                r6 = r17
                r0.SendAudioSetToMCU(r1, r2, r3, r4, r5, r6)
                r32.writeNoException()
                return r16
            L_0x04e6:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getLoudStatus()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x04f6:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getMuteStatus()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x0506:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getUserMiddleVal()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x0516:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getUserTrebleVal()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x0526:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getUserBassVal()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x0536:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getEQMode()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x0546:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getFADVal()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x0556:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getBALVal()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x0566:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getTrebleFre()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x0576:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getMiddleFre()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x0586:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getBassFre()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x0596:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getMiddleVal()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x05a6:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getTrebleVal()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x05b6:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getBassVal()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x05c6:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r29.getMainVolval()
                r32.writeNoException()
                r7.writeByte(r0)
                return r16
            L_0x05d6:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r8 = r31.readByte()
                byte r9 = r31.readByte()
                byte r10 = r31.readByte()
                byte r11 = r31.readByte()
                byte r12 = r31.readByte()
                byte r17 = r31.readByte()
                r0 = r29
                r1 = r8
                r2 = r9
                r3 = r10
                r4 = r11
                r5 = r12
                r6 = r17
                r0.SendFactorySetToMCU(r1, r2, r3, r4, r5, r6)
                r32.writeNoException()
                return r16
            L_0x0603:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r29.getValidMode()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x0613:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r1 = r31.readInt()
                int r2 = r31.readInt()
                int r3 = r31.readInt()
                if (r3 == 0) goto L_0x0628
                r0 = 1
            L_0x0628:
                r13.sendTouchPos(r1, r2, r0)
                r32.writeNoException()
                return r16
            L_0x062f:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                java.lang.String r0 = r31.readString()
                java.lang.String r1 = r31.readString()
                java.lang.String r2 = r13.getSettingString(r0, r1)
                r32.writeNoException()
                r7.writeString(r2)
                return r16
            L_0x0647:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                java.lang.String r0 = r31.readString()
                long r1 = r31.readLong()
                long r3 = r13.getSettingLong(r0, r1)
                r32.writeNoException()
                r7.writeLong(r3)
                return r16
            L_0x065f:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                java.lang.String r0 = r31.readString()
                int r1 = r31.readInt()
                int r2 = r13.getSettingInt(r0, r1)
                r32.writeNoException()
                r7.writeInt(r2)
                return r16
            L_0x0677:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                java.lang.String r0 = r31.readString()
                float r1 = r31.readFloat()
                float r2 = r13.getSettingFloat(r0, r1)
                r32.writeNoException()
                r7.writeFloat(r2)
                return r16
            L_0x068f:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                java.lang.String r1 = r31.readString()
                int r2 = r31.readInt()
                if (r2 == 0) goto L_0x06a0
                r0 = 1
            L_0x06a0:
                boolean r2 = r13.getSettingBoolean(r1, r0)
                r32.writeNoException()
                r7.writeInt(r2)
                return r16
            L_0x06ab:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                r29.appySetting()
                r32.writeNoException()
                return r16
            L_0x06b7:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                r29.commitSetting()
                r32.writeNoException()
                return r16
            L_0x06c3:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                java.lang.String r1 = r31.readString()
                int r2 = r31.readInt()
                if (r2 == 0) goto L_0x06d4
                r0 = 1
            L_0x06d4:
                r13.putSettingBoolean(r1, r0)
                r32.writeNoException()
                return r16
            L_0x06db:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                java.lang.String r0 = r31.readString()
                float r1 = r31.readFloat()
                r13.putSettingFloat(r0, r1)
                r32.writeNoException()
                return r16
            L_0x06ef:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                java.lang.String r0 = r31.readString()
                long r1 = r31.readLong()
                r13.putSettingLong(r0, r1)
                r32.writeNoException()
                return r16
            L_0x0703:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                java.lang.String r0 = r31.readString()
                int r1 = r31.readInt()
                r13.putSettingInt(r0, r1)
                r32.writeNoException()
                return r16
            L_0x0717:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                java.lang.String r0 = r31.readString()
                java.lang.String r1 = r31.readString()
                r13.putSettingStr(r0, r1)
                r32.writeNoException()
                return r16
            L_0x072b:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                java.lang.String r0 = r29.getMCUVer()
                r32.writeNoException()
                r7.writeString(r0)
                return r16
            L_0x073b:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r31.readInt()
                r13.exitCurMode(r0)
                r32.writeNoException()
                return r16
            L_0x074b:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r31.readInt()
                android.os.IBinder r1 = r31.readStrongBinder()
                com.szchoiceway.eventcenter.ICallbackfn r1 = com.szchoiceway.eventcenter.ICallbackfn.Stub.asInterface(r1)
                r13.setCurModeCallback(r0, r1)
                r32.writeNoException()
                return r16
            L_0x0763:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                android.os.IBinder r0 = r31.readStrongBinder()
                com.szchoiceway.eventcenter.ICallbackfn r0 = com.szchoiceway.eventcenter.ICallbackfn.Stub.asInterface(r0)
                r13.setRadioCallback(r0)
                r32.writeNoException()
                return r16
            L_0x0777:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getRadioTrafficState()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x0787:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getRadioTPIconState()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x0797:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getRadioSteroIconState()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x07a7:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getRadioAPSState()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x07b7:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getRadioAMSState()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x07c7:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getRadioDXLOCState()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x07d7:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getRadioSTMonoState()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x07e7:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getRadioTAState()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x07f7:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getRadioAFState()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x0807:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                java.lang.String r0 = r29.getRadioPTYName()
                r32.writeNoException()
                r7.writeString(r0)
                return r16
            L_0x0817:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r29.getRadioPTYNum()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x0827:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getRadioPTYState()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x0837:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                boolean r0 = r29.getRadioRDSState()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x0847:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r29.getRadioNum()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x0857:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r29.getRadioBand()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x0867:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int[] r0 = r29.getRadioFreqList()
                r32.writeNoException()
                r7.writeIntArray(r0)
                return r16
            L_0x0877:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r29.getRadioFreq()
                r32.writeNoException()
                r7.writeInt(r0)
                return r16
            L_0x0887:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r31.readInt()
                r13.sendBTState(r0)
                r32.writeNoException()
                return r16
            L_0x0897:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                byte r0 = r31.readByte()
                byte r1 = r31.readByte()
                r13.sendSetup(r0, r1)
                r32.writeNoException()
                return r16
            L_0x08ab:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r1 = r31.readInt()
                if (r1 == 0) goto L_0x08b8
                r0 = 1
            L_0x08b8:
                r13.sendPlayState(r0)
                r32.writeNoException()
                return r16
            L_0x08bf:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r1 = r31.readInt()
                if (r1 == 0) goto L_0x08cc
                r0 = 1
            L_0x08cc:
                r13.sendMuteState(r0)
                r32.writeNoException()
                return r16
            L_0x08d3:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                r29.beep()
                r32.writeNoException()
                return r16
            L_0x08df:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r31.readInt()
                r13.sendUserFreq(r0)
                r32.writeNoException()
                return r16
            L_0x08ef:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r31.readInt()
                r13.sendEQMode(r0)
                r32.writeNoException()
                return r16
            L_0x08ff:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r31.readInt()
                r13.sendSystemKey(r0)
                r32.writeNoException()
                return r16
            L_0x090f:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r31.readInt()
                r13.sendTVKey(r0)
                r32.writeNoException()
                return r16
            L_0x091f:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r0 = r31.readInt()
                r13.sendRadioKey(r0)
                r32.writeNoException()
                return r16
            L_0x092f:
                r14 = r11
                r7 = r12
                r15.enforceInterface(r14)
                int r1 = r31.readInt()
                int r2 = r31.readInt()
                if (r2 == 0) goto L_0x0940
                r0 = 1
            L_0x0940:
                r13.sendMode(r1, r0)
                r32.writeNoException()
                return r16
            L_0x0947:
                r14 = r11
                r7 = r12
                r7.writeString(r14)
                return r16
            */
            throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.eventcenter.IEventService.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements IEventService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void sendMode(int mode, boolean waitAck) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeInt(waitAck);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendRadioKey(int key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(key);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendTVKey(int key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(key);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendSystemKey(int key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(key);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendEQMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendUserFreq(int freq) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(freq);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void beep() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendMuteState(boolean mute) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mute);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendPlayState(boolean play) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(play);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendSetup(byte item, byte val) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByte(item);
                    _data.writeByte(val);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendBTState(int state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getRadioFreq() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int[] getRadioFreqList() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    return _reply.createIntArray();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getRadioBand() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getRadioNum() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getRadioRDSState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getRadioPTYState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getRadioPTYNum() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getRadioPTYName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getRadioAFState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getRadioTAState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getRadioSTMonoState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getRadioDXLOCState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getRadioAMSState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getRadioAPSState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getRadioSteroIconState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getRadioTPIconState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getRadioTrafficState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setRadioCallback(ICallbackfn cbfnRadio) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cbfnRadio != null ? cbfnRadio.asBinder() : null);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setCurModeCallback(int mode, ICallbackfn cbfnCurMode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeStrongBinder(cbfnCurMode != null ? cbfnCurMode.asBinder() : null);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void exitCurMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getMCUVer() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void putSettingStr(String key, String value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(value);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void putSettingInt(String key, int value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeInt(value);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void putSettingLong(String key, long value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeLong(value);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void putSettingFloat(String key, float value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeFloat(value);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void putSettingBoolean(String key, boolean value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeInt(value);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void commitSetting() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void appySetting() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getSettingBoolean(String key, boolean defValue) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeInt(defValue);
                    boolean _result = false;
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public float getSettingFloat(String key, float defValue) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeFloat(defValue);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readFloat();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getSettingInt(String key, int defValue) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeInt(defValue);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public long getSettingLong(String key, long defValue) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeLong(defValue);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readLong();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getSettingString(String key, String defValue) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(defValue);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendTouchPos(int x, int y, boolean down) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    _data.writeInt(down);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getValidMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendFactorySetToMCU(byte byCarTyte, byte byOtherSet, byte byDTVType, byte byDVDYUV, byte byScreen, byte byLEDSelect) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByte(byCarTyte);
                    _data.writeByte(byOtherSet);
                    _data.writeByte(byDTVType);
                    _data.writeByte(byDVDYUV);
                    _data.writeByte(byScreen);
                    _data.writeByte(byLEDSelect);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getMainVolval() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getBassVal() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getTrebleVal() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getMiddleVal() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getBassFre() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getMiddleFre() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getTrebleFre() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getBALVal() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getFADVal() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getEQMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getUserBassVal() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getUserTrebleVal() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte getUserMiddleVal() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getMuteStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getLoudStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendAudioSetToMCU(int iBassVal, int iMiddleVal, int iTrebleVal, int iBassFre, int iMiddleFre, int iTrebleFre) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(iBassVal);
                    _data.writeInt(iMiddleVal);
                    _data.writeInt(iTrebleVal);
                    _data.writeInt(iBassFre);
                    _data.writeInt(iMiddleFre);
                    _data.writeInt(iTrebleFre);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendWheelKey(int iWheelKey) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(iWheelKey);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendSysRTCTimeMCU(int iYear, int iMon, int iDay, int iHour, int iMin, int iSec) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(iYear);
                    _data.writeInt(iMon);
                    _data.writeInt(iDay);
                    _data.writeInt(iHour);
                    _data.writeInt(iMin);
                    _data.writeInt(iSec);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendDVRKey(byte iDVRKey) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByte(iDVRKey);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendMainVol(byte iMainVol) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByte(iMainVol);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendBLVal(byte iBLVal, byte iNBLVal) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByte(iBLVal);
                    _data.writeByte(iNBLVal);
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setTVCallback(ICallbackfn cbfnTV) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cbfnTV != null ? cbfnTV.asBinder() : null);
                    this.mRemote.transact(69, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setAUXCallback(ICallbackfn cbfnAUX) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cbfnAUX != null ? cbfnAUX.asBinder() : null);
                    this.mRemote.transact(70, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendBALFADVal(byte byBAL, byte byFAD) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByte(byBAL);
                    _data.writeByte(byFAD);
                    this.mRemote.transact(71, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendBlackState(boolean bBlack) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(bBlack);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean IsBrakeConneted() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean IsMCUUpgradeWriteErr() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SetVideoCH(int iChID) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(iChID);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int GetSignalStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void OpenVideo(boolean bOpen) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(bOpen);
                    this.mRemote.transact(77, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SetVideoSize(int x, int y, int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    this.mRemote.transact(78, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean IsMuteOn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(79, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void initRadioZone(byte radioZoneType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByte(radioZoneType);
                    this.mRemote.transact(80, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendCanbusData(byte[] byData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(byData);
                    this.mRemote.transact(81, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setValidModeInfor(String titleInfor, String abluminfor, String artistinfor, int iCurTrack, int iTotTrack, int iCurFolder, int iTotFolder, int iCurTime, int iTotTime, int iLoopMode, int iRepeatMode, int iPlayStatus) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(titleInfor);
                    _data.writeString(abluminfor);
                    try {
                        _data.writeString(artistinfor);
                        try {
                            _data.writeInt(iCurTrack);
                        } catch (Throwable th) {
                            th = th;
                            int i = iTotTrack;
                            int i2 = iCurFolder;
                            int i3 = iTotFolder;
                            int i4 = iCurTime;
                            int i5 = iTotTime;
                            int i6 = iLoopMode;
                            int i7 = iRepeatMode;
                            int i8 = iPlayStatus;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        int i9 = iCurTrack;
                        int i10 = iTotTrack;
                        int i22 = iCurFolder;
                        int i32 = iTotFolder;
                        int i42 = iCurTime;
                        int i52 = iTotTime;
                        int i62 = iLoopMode;
                        int i72 = iRepeatMode;
                        int i82 = iPlayStatus;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(iTotTrack);
                        try {
                            _data.writeInt(iCurFolder);
                            try {
                                _data.writeInt(iTotFolder);
                            } catch (Throwable th3) {
                                th = th3;
                                int i422 = iCurTime;
                                int i522 = iTotTime;
                                int i622 = iLoopMode;
                                int i722 = iRepeatMode;
                                int i822 = iPlayStatus;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            int i322 = iTotFolder;
                            int i4222 = iCurTime;
                            int i5222 = iTotTime;
                            int i6222 = iLoopMode;
                            int i7222 = iRepeatMode;
                            int i8222 = iPlayStatus;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        int i222 = iCurFolder;
                        int i3222 = iTotFolder;
                        int i42222 = iCurTime;
                        int i52222 = iTotTime;
                        int i62222 = iLoopMode;
                        int i72222 = iRepeatMode;
                        int i82222 = iPlayStatus;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(iCurTime);
                        try {
                            _data.writeInt(iTotTime);
                            try {
                                _data.writeInt(iLoopMode);
                            } catch (Throwable th6) {
                                th = th6;
                                int i722222 = iRepeatMode;
                                int i822222 = iPlayStatus;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            int i622222 = iLoopMode;
                            int i7222222 = iRepeatMode;
                            int i8222222 = iPlayStatus;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th8) {
                        th = th8;
                        int i522222 = iTotTime;
                        int i6222222 = iLoopMode;
                        int i72222222 = iRepeatMode;
                        int i82222222 = iPlayStatus;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(iRepeatMode);
                        try {
                            _data.writeInt(iPlayStatus);
                            try {
                                this.mRemote.transact(82, _data, _reply, 0);
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                            } catch (Throwable th9) {
                                th = th9;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        int i822222222 = iPlayStatus;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                    String str = artistinfor;
                    int i92 = iCurTrack;
                    int i102 = iTotTrack;
                    int i2222 = iCurFolder;
                    int i32222 = iTotFolder;
                    int i422222 = iCurTime;
                    int i5222222 = iTotTime;
                    int i62222222 = iLoopMode;
                    int i722222222 = iRepeatMode;
                    int i8222222222 = iPlayStatus;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            public String getValidModeTitleInfor() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(83, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getValidModeAblumInfor() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(84, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getValidModeArtistInfor() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(85, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getValidCurTrack() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(86, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getValidTotTrack() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(87, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getValidCurTime() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(88, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getValidTotTime() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(89, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getValidCurFolder() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(90, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getValidTotFolder() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(91, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getValidLoopMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(92, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getValidRepeatMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(93, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getValidPlayStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(94, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SetDVDVideoCH(int iChID) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(iChID);
                    this.mRemote.transact(95, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendResetDVD(boolean bReset) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(bReset);
                    this.mRemote.transact(96, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean IsDiscConneted() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(97, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setCarMediaCallback(ICallbackfn cbfnCarMedia) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cbfnCarMedia != null ? cbfnCarMedia.asBinder() : null);
                    this.mRemote.transact(98, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setMcuInitStatus(boolean bInitMcu) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(bInitMcu);
                    this.mRemote.transact(99, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getMcuInitStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(100, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void enterUpgradeMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(101, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void exitUpgradeMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(102, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean sendMcuUpgradeMode(boolean rl78McuMode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rl78McuMode);
                    boolean _result = false;
                    this.mRemote.transact(103, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean sendMcuUpgradeData(long dataStartAddr, byte[] bydata, int startPos, int len, boolean lastFrame) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(dataStartAddr);
                    _data.writeByteArray(bydata);
                    _data.writeInt(startPos);
                    _data.writeInt(len);
                    _data.writeInt(lastFrame);
                    boolean _result = false;
                    this.mRemote.transact(104, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isUpgradeMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(105, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendGPSVolToMCU(byte iGPSVol) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByte(iGPSVol);
                    this.mRemote.transact(106, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendFactorySet() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(107, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int GetBTStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(108, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean IsBackcarConnected() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(109, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int LoadNLightVal() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(110, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SetCurrDim(int iCurrDim) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(iCurrDim);
                    this.mRemote.transact(111, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int GetCurrDim() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(112, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getTFTVer() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(113, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean Send8902McuUpgradeData(byte[] bydata, int len) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(bydata);
                    _data.writeInt(len);
                    boolean _result = false;
                    this.mRemote.transact(114, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendVol_KSW(boolean bMute, int iMode, int iVolType, int iVolVal) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(bMute);
                    _data.writeInt(iMode);
                    _data.writeInt(iVolType);
                    _data.writeInt(iVolVal);
                    this.mRemote.transact(115, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean get_kesaiwei_chk_Video_Driving_Ban() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(116, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean get_kesaiwei_bPark() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(117, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendDvdDataToMcu(byte[] byData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(byData);
                    this.mRemote.transact(118, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendMcuData_KSW(byte[] byData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(byData);
                    this.mRemote.transact(119, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setDashBoardCallback(ICallbackfn cbfnDashBoard) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cbfnDashBoard != null ? cbfnDashBoard.asBinder() : null);
                    this.mRemote.transact(120, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setGpsFocusCallback(ICallbackfn cbfnGpsFocus) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cbfnGpsFocus != null ? cbfnGpsFocus.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_setGpsFocusCallback, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int get_m_i_easyconn_state_KSW() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_get_m_i_easyconn_state_KSW, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void send_KSW_page2_vol_info(int i_eq_mode, int i_low, int i_mid, int i_hight) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(i_eq_mode);
                    _data.writeInt(i_low);
                    _data.writeInt(i_mid);
                    _data.writeInt(i_hight);
                    this.mRemote.transact(Stub.TRANSACTION_send_KSW_page2_vol_info, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getCameraOwner() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getCameraOwner, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setCameraOwner(int owner) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(owner);
                    this.mRemote.transact(Stub.TRANSACTION_setCameraOwner, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void openTVout(int controller, boolean open) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(controller);
                    _data.writeInt(open);
                    this.mRemote.transact(Stub.TRANSACTION_openTVout, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendToOSData() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_sendToOSData, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getmIsAddMouseView() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(128, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendKeyDownUpSync(int key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(key);
                    this.mRemote.transact(Stub.TRANSACTION_sendKeyDownUpSync, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
