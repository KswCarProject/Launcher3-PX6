package com.szchoiceway.eventcenter;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.szchoiceway.eventcenter.ICallbackfn;

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

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    sendMode(data.readInt(), data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    sendRadioKey(data.readInt());
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    sendTVKey(data.readInt());
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    sendSystemKey(data.readInt());
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    sendEQMode(data.readInt());
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    sendUserFreq(data.readInt());
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    beep();
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    sendMuteState(data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    sendPlayState(data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    sendSetup(data.readByte(), data.readByte());
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    sendBTState(data.readInt());
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _result = getRadioFreq();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _result2 = getRadioFreqList();
                    reply.writeNoException();
                    reply.writeIntArray(_result2);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int _result3 = getRadioBand();
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _result4 = getRadioNum();
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result5 = getRadioRDSState();
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result6 = getRadioPTYState();
                    reply.writeNoException();
                    reply.writeInt(_result6 ? 1 : 0);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    int _result7 = getRadioPTYNum();
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    String _result8 = getRadioPTYName();
                    reply.writeNoException();
                    reply.writeString(_result8);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result9 = getRadioAFState();
                    reply.writeNoException();
                    reply.writeInt(_result9 ? 1 : 0);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result10 = getRadioTAState();
                    reply.writeNoException();
                    reply.writeInt(_result10 ? 1 : 0);
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result11 = getRadioSTMonoState();
                    reply.writeNoException();
                    reply.writeInt(_result11 ? 1 : 0);
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result12 = getRadioDXLOCState();
                    reply.writeNoException();
                    reply.writeInt(_result12 ? 1 : 0);
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result13 = getRadioAMSState();
                    reply.writeNoException();
                    reply.writeInt(_result13 ? 1 : 0);
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result14 = getRadioAPSState();
                    reply.writeNoException();
                    reply.writeInt(_result14 ? 1 : 0);
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result15 = getRadioSteroIconState();
                    reply.writeNoException();
                    reply.writeInt(_result15 ? 1 : 0);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result16 = getRadioTPIconState();
                    reply.writeNoException();
                    reply.writeInt(_result16 ? 1 : 0);
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result17 = getRadioTrafficState();
                    reply.writeNoException();
                    reply.writeInt(_result17 ? 1 : 0);
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    setRadioCallback(ICallbackfn.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    setCurModeCallback(data.readInt(), ICallbackfn.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    exitCurMode(data.readInt());
                    reply.writeNoException();
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    String _result18 = getMCUVer();
                    reply.writeNoException();
                    reply.writeString(_result18);
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    putSettingStr(data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    putSettingInt(data.readString(), data.readInt());
                    reply.writeNoException();
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    putSettingLong(data.readString(), data.readLong());
                    reply.writeNoException();
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    putSettingFloat(data.readString(), data.readFloat());
                    reply.writeNoException();
                    return true;
                case 37:
                    data.enforceInterface(DESCRIPTOR);
                    putSettingBoolean(data.readString(), data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case 38:
                    data.enforceInterface(DESCRIPTOR);
                    commitSetting();
                    reply.writeNoException();
                    return true;
                case 39:
                    data.enforceInterface(DESCRIPTOR);
                    appySetting();
                    reply.writeNoException();
                    return true;
                case 40:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result19 = getSettingBoolean(data.readString(), data.readInt() != 0);
                    reply.writeNoException();
                    reply.writeInt(_result19 ? 1 : 0);
                    return true;
                case 41:
                    data.enforceInterface(DESCRIPTOR);
                    float _result20 = getSettingFloat(data.readString(), data.readFloat());
                    reply.writeNoException();
                    reply.writeFloat(_result20);
                    return true;
                case 42:
                    data.enforceInterface(DESCRIPTOR);
                    int _result21 = getSettingInt(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result21);
                    return true;
                case 43:
                    data.enforceInterface(DESCRIPTOR);
                    long _result22 = getSettingLong(data.readString(), data.readLong());
                    reply.writeNoException();
                    reply.writeLong(_result22);
                    return true;
                case 44:
                    data.enforceInterface(DESCRIPTOR);
                    String _result23 = getSettingString(data.readString(), data.readString());
                    reply.writeNoException();
                    reply.writeString(_result23);
                    return true;
                case 45:
                    data.enforceInterface(DESCRIPTOR);
                    sendTouchPos(data.readInt(), data.readInt(), data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case 46:
                    data.enforceInterface(DESCRIPTOR);
                    int _result24 = getValidMode();
                    reply.writeNoException();
                    reply.writeInt(_result24);
                    return true;
                case 47:
                    data.enforceInterface(DESCRIPTOR);
                    SendFactorySetToMCU(data.readByte(), data.readByte(), data.readByte(), data.readByte(), data.readByte(), data.readByte());
                    reply.writeNoException();
                    return true;
                case 48:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result25 = getMainVolval();
                    reply.writeNoException();
                    reply.writeByte(_result25);
                    return true;
                case 49:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result26 = getBassVal();
                    reply.writeNoException();
                    reply.writeByte(_result26);
                    return true;
                case 50:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result27 = getTrebleVal();
                    reply.writeNoException();
                    reply.writeByte(_result27);
                    return true;
                case 51:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result28 = getMiddleVal();
                    reply.writeNoException();
                    reply.writeByte(_result28);
                    return true;
                case 52:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result29 = getBassFre();
                    reply.writeNoException();
                    reply.writeByte(_result29);
                    return true;
                case 53:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result30 = getMiddleFre();
                    reply.writeNoException();
                    reply.writeByte(_result30);
                    return true;
                case 54:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result31 = getTrebleFre();
                    reply.writeNoException();
                    reply.writeByte(_result31);
                    return true;
                case 55:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result32 = getBALVal();
                    reply.writeNoException();
                    reply.writeByte(_result32);
                    return true;
                case 56:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result33 = getFADVal();
                    reply.writeNoException();
                    reply.writeByte(_result33);
                    return true;
                case TRANSACTION_getEQMode /*57*/:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result34 = getEQMode();
                    reply.writeNoException();
                    reply.writeByte(_result34);
                    return true;
                case TRANSACTION_getUserBassVal /*58*/:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result35 = getUserBassVal();
                    reply.writeNoException();
                    reply.writeByte(_result35);
                    return true;
                case TRANSACTION_getUserTrebleVal /*59*/:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result36 = getUserTrebleVal();
                    reply.writeNoException();
                    reply.writeByte(_result36);
                    return true;
                case TRANSACTION_getUserMiddleVal /*60*/:
                    data.enforceInterface(DESCRIPTOR);
                    byte _result37 = getUserMiddleVal();
                    reply.writeNoException();
                    reply.writeByte(_result37);
                    return true;
                case 61:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result38 = getMuteStatus();
                    reply.writeNoException();
                    reply.writeInt(_result38 ? 1 : 0);
                    return true;
                case 62:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result39 = getLoudStatus();
                    reply.writeNoException();
                    reply.writeInt(_result39 ? 1 : 0);
                    return true;
                case 63:
                    data.enforceInterface(DESCRIPTOR);
                    SendAudioSetToMCU(data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case 64:
                    data.enforceInterface(DESCRIPTOR);
                    SendWheelKey(data.readInt());
                    reply.writeNoException();
                    return true;
                case 65:
                    data.enforceInterface(DESCRIPTOR);
                    SendSysRTCTimeMCU(data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case 66:
                    data.enforceInterface(DESCRIPTOR);
                    SendDVRKey(data.readByte());
                    reply.writeNoException();
                    return true;
                case 67:
                    data.enforceInterface(DESCRIPTOR);
                    SendMainVol(data.readByte());
                    reply.writeNoException();
                    return true;
                case 68:
                    data.enforceInterface(DESCRIPTOR);
                    SendBLVal(data.readByte(), data.readByte());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setTVCallback /*69*/:
                    data.enforceInterface(DESCRIPTOR);
                    setTVCallback(ICallbackfn.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setAUXCallback /*70*/:
                    data.enforceInterface(DESCRIPTOR);
                    setAUXCallback(ICallbackfn.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_SendBALFADVal /*71*/:
                    data.enforceInterface(DESCRIPTOR);
                    SendBALFADVal(data.readByte(), data.readByte());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_SendBlackState /*72*/:
                    data.enforceInterface(DESCRIPTOR);
                    SendBlackState(data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_IsBrakeConneted /*73*/:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result40 = IsBrakeConneted();
                    reply.writeNoException();
                    reply.writeInt(_result40 ? 1 : 0);
                    return true;
                case TRANSACTION_IsMCUUpgradeWriteErr /*74*/:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result41 = IsMCUUpgradeWriteErr();
                    reply.writeNoException();
                    reply.writeInt(_result41 ? 1 : 0);
                    return true;
                case 75:
                    data.enforceInterface(DESCRIPTOR);
                    SetVideoCH(data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_GetSignalStatus /*76*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result42 = GetSignalStatus();
                    reply.writeNoException();
                    reply.writeInt(_result42);
                    return true;
                case TRANSACTION_OpenVideo /*77*/:
                    data.enforceInterface(DESCRIPTOR);
                    OpenVideo(data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_SetVideoSize /*78*/:
                    data.enforceInterface(DESCRIPTOR);
                    SetVideoSize(data.readInt(), data.readInt(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_IsMuteOn /*79*/:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result43 = IsMuteOn();
                    reply.writeNoException();
                    reply.writeInt(_result43 ? 1 : 0);
                    return true;
                case 80:
                    data.enforceInterface(DESCRIPTOR);
                    initRadioZone(data.readByte());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_sendCanbusData /*81*/:
                    data.enforceInterface(DESCRIPTOR);
                    sendCanbusData(data.createByteArray());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setValidModeInfor /*82*/:
                    data.enforceInterface(DESCRIPTOR);
                    setValidModeInfor(data.readString(), data.readString(), data.readString(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getValidModeTitleInfor /*83*/:
                    data.enforceInterface(DESCRIPTOR);
                    String _result44 = getValidModeTitleInfor();
                    reply.writeNoException();
                    reply.writeString(_result44);
                    return true;
                case TRANSACTION_getValidModeAblumInfor /*84*/:
                    data.enforceInterface(DESCRIPTOR);
                    String _result45 = getValidModeAblumInfor();
                    reply.writeNoException();
                    reply.writeString(_result45);
                    return true;
                case TRANSACTION_getValidModeArtistInfor /*85*/:
                    data.enforceInterface(DESCRIPTOR);
                    String _result46 = getValidModeArtistInfor();
                    reply.writeNoException();
                    reply.writeString(_result46);
                    return true;
                case TRANSACTION_getValidCurTrack /*86*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result47 = getValidCurTrack();
                    reply.writeNoException();
                    reply.writeInt(_result47);
                    return true;
                case TRANSACTION_getValidTotTrack /*87*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result48 = getValidTotTrack();
                    reply.writeNoException();
                    reply.writeInt(_result48);
                    return true;
                case TRANSACTION_getValidCurTime /*88*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result49 = getValidCurTime();
                    reply.writeNoException();
                    reply.writeInt(_result49);
                    return true;
                case TRANSACTION_getValidTotTime /*89*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result50 = getValidTotTime();
                    reply.writeNoException();
                    reply.writeInt(_result50);
                    return true;
                case TRANSACTION_getValidCurFolder /*90*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result51 = getValidCurFolder();
                    reply.writeNoException();
                    reply.writeInt(_result51);
                    return true;
                case TRANSACTION_getValidTotFolder /*91*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result52 = getValidTotFolder();
                    reply.writeNoException();
                    reply.writeInt(_result52);
                    return true;
                case TRANSACTION_getValidLoopMode /*92*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result53 = getValidLoopMode();
                    reply.writeNoException();
                    reply.writeInt(_result53);
                    return true;
                case TRANSACTION_getValidRepeatMode /*93*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result54 = getValidRepeatMode();
                    reply.writeNoException();
                    reply.writeInt(_result54);
                    return true;
                case TRANSACTION_getValidPlayStatus /*94*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result55 = getValidPlayStatus();
                    reply.writeNoException();
                    reply.writeInt(_result55);
                    return true;
                case TRANSACTION_SetDVDVideoCH /*95*/:
                    data.enforceInterface(DESCRIPTOR);
                    SetDVDVideoCH(data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_sendResetDVD /*96*/:
                    data.enforceInterface(DESCRIPTOR);
                    sendResetDVD(data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_IsDiscConneted /*97*/:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result56 = IsDiscConneted();
                    reply.writeNoException();
                    reply.writeInt(_result56 ? 1 : 0);
                    return true;
                case TRANSACTION_setCarMediaCallback /*98*/:
                    data.enforceInterface(DESCRIPTOR);
                    setCarMediaCallback(ICallbackfn.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case 99:
                    data.enforceInterface(DESCRIPTOR);
                    setMcuInitStatus(data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case 100:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result57 = getMcuInitStatus();
                    reply.writeNoException();
                    reply.writeInt(_result57 ? 1 : 0);
                    return true;
                case 101:
                    data.enforceInterface(DESCRIPTOR);
                    enterUpgradeMode();
                    reply.writeNoException();
                    return true;
                case 102:
                    data.enforceInterface(DESCRIPTOR);
                    exitUpgradeMode();
                    reply.writeNoException();
                    return true;
                case 103:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result58 = sendMcuUpgradeMode(data.readInt() != 0);
                    reply.writeNoException();
                    reply.writeInt(_result58 ? 1 : 0);
                    return true;
                case 104:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result59 = sendMcuUpgradeData(data.readLong(), data.createByteArray(), data.readInt(), data.readInt(), data.readInt() != 0);
                    reply.writeNoException();
                    reply.writeInt(_result59 ? 1 : 0);
                    return true;
                case TRANSACTION_isUpgradeMode /*105*/:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result60 = isUpgradeMode();
                    reply.writeNoException();
                    reply.writeInt(_result60 ? 1 : 0);
                    return true;
                case TRANSACTION_SendGPSVolToMCU /*106*/:
                    data.enforceInterface(DESCRIPTOR);
                    SendGPSVolToMCU(data.readByte());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_SendFactorySet /*107*/:
                    data.enforceInterface(DESCRIPTOR);
                    SendFactorySet();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_GetBTStatus /*108*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result61 = GetBTStatus();
                    reply.writeNoException();
                    reply.writeInt(_result61);
                    return true;
                case TRANSACTION_IsBackcarConnected /*109*/:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result62 = IsBackcarConnected();
                    reply.writeNoException();
                    reply.writeInt(_result62 ? 1 : 0);
                    return true;
                case TRANSACTION_LoadNLightVal /*110*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result63 = LoadNLightVal();
                    reply.writeNoException();
                    reply.writeInt(_result63);
                    return true;
                case TRANSACTION_SetCurrDim /*111*/:
                    data.enforceInterface(DESCRIPTOR);
                    SetCurrDim(data.readInt());
                    reply.writeNoException();
                    return true;
                case 112:
                    data.enforceInterface(DESCRIPTOR);
                    int _result64 = GetCurrDim();
                    reply.writeNoException();
                    reply.writeInt(_result64);
                    return true;
                case 113:
                    data.enforceInterface(DESCRIPTOR);
                    String _result65 = getTFTVer();
                    reply.writeNoException();
                    reply.writeString(_result65);
                    return true;
                case 114:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result66 = Send8902McuUpgradeData(data.createByteArray(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result66 ? 1 : 0);
                    return true;
                case 115:
                    data.enforceInterface(DESCRIPTOR);
                    SendVol_KSW(data.readInt() != 0, data.readInt(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_get_kesaiwei_chk_Video_Driving_Ban /*116*/:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result67 = get_kesaiwei_chk_Video_Driving_Ban();
                    reply.writeNoException();
                    reply.writeInt(_result67 ? 1 : 0);
                    return true;
                case TRANSACTION_get_kesaiwei_bPark /*117*/:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result68 = get_kesaiwei_bPark();
                    reply.writeNoException();
                    reply.writeInt(_result68 ? 1 : 0);
                    return true;
                case TRANSACTION_sendDvdDataToMcu /*118*/:
                    data.enforceInterface(DESCRIPTOR);
                    sendDvdDataToMcu(data.createByteArray());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_sendMcuData_KSW /*119*/:
                    data.enforceInterface(DESCRIPTOR);
                    sendMcuData_KSW(data.createByteArray());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setDashBoardCallback /*120*/:
                    data.enforceInterface(DESCRIPTOR);
                    setDashBoardCallback(ICallbackfn.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setGpsFocusCallback /*121*/:
                    data.enforceInterface(DESCRIPTOR);
                    setGpsFocusCallback(ICallbackfn.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_get_m_i_easyconn_state_KSW /*122*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result69 = get_m_i_easyconn_state_KSW();
                    reply.writeNoException();
                    reply.writeInt(_result69);
                    return true;
                case TRANSACTION_send_KSW_page2_vol_info /*123*/:
                    data.enforceInterface(DESCRIPTOR);
                    send_KSW_page2_vol_info(data.readInt(), data.readInt(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getCameraOwner /*124*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result70 = getCameraOwner();
                    reply.writeNoException();
                    reply.writeInt(_result70);
                    return true;
                case TRANSACTION_setCameraOwner /*125*/:
                    data.enforceInterface(DESCRIPTOR);
                    setCameraOwner(data.readInt());
                    reply.writeNoException();
                    return true;
                case 126:
                    data.enforceInterface(DESCRIPTOR);
                    openTVout(data.readInt(), data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case 127:
                    data.enforceInterface(DESCRIPTOR);
                    sendToOSData();
                    reply.writeNoException();
                    return true;
                case 128:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result71 = getmIsAddMouseView();
                    reply.writeNoException();
                    reply.writeInt(_result71 ? 1 : 0);
                    return true;
                case TRANSACTION_sendKeyDownUpSync /*129*/:
                    data.enforceInterface(DESCRIPTOR);
                    sendKeyDownUpSync(data.readInt());
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
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
                int i = 1;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    if (!waitAck) {
                        i = 0;
                    }
                    _data.writeInt(i);
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
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mute) {
                        i = 1;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendPlayState(boolean play) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (play) {
                        i = 1;
                    }
                    _data.writeInt(i);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    if (value) {
                        i = 1;
                    }
                    _data.writeInt(i);
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
                int i;
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    if (defValue) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
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
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    if (down) {
                        i = 1;
                    }
                    _data.writeInt(i);
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
                    this.mRemote.transact(Stub.TRANSACTION_getEQMode, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getUserBassVal, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getUserTrebleVal, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getUserMiddleVal, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readByte();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getMuteStatus() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                    this.mRemote.transact(Stub.TRANSACTION_setTVCallback, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_setAUXCallback, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_SendBALFADVal, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void SendBlackState(boolean bBlack) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bBlack) {
                        i = 1;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_SendBlackState, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean IsBrakeConneted() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_IsBrakeConneted, _data, _reply, 0);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_IsMCUUpgradeWriteErr, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_GetSignalStatus, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void OpenVideo(boolean bOpen) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bOpen) {
                        i = 1;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_OpenVideo, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_SetVideoSize, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean IsMuteOn() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_IsMuteOn, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_sendCanbusData, _data, _reply, 0);
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
                    _data.writeString(artistinfor);
                    _data.writeInt(iCurTrack);
                    _data.writeInt(iTotTrack);
                    _data.writeInt(iCurFolder);
                    _data.writeInt(iTotFolder);
                    _data.writeInt(iCurTime);
                    _data.writeInt(iTotTime);
                    _data.writeInt(iLoopMode);
                    _data.writeInt(iRepeatMode);
                    _data.writeInt(iPlayStatus);
                    this.mRemote.transact(Stub.TRANSACTION_setValidModeInfor, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getValidModeTitleInfor() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getValidModeTitleInfor, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getValidModeAblumInfor, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getValidModeArtistInfor, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getValidCurTrack, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getValidTotTrack, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getValidCurTime, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getValidTotTime, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getValidCurFolder, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getValidTotFolder, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getValidLoopMode, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getValidRepeatMode, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_getValidPlayStatus, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_SetDVDVideoCH, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendResetDVD(boolean bReset) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bReset) {
                        i = 1;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_sendResetDVD, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean IsDiscConneted() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_IsDiscConneted, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_setCarMediaCallback, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setMcuInitStatus(boolean bInitMcu) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bInitMcu) {
                        i = 1;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(99, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getMcuInitStatus() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
                int i;
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (rl78McuMode) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(103, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean sendMcuUpgradeData(long dataStartAddr, byte[] bydata, int startPos, int len, boolean lastFrame) throws RemoteException {
                int i;
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(dataStartAddr);
                    _data.writeByteArray(bydata);
                    _data.writeInt(startPos);
                    _data.writeInt(len);
                    if (lastFrame) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(104, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isUpgradeMode() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isUpgradeMode, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_SendGPSVolToMCU, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_SendFactorySet, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_GetBTStatus, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean IsBackcarConnected() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_IsBackcarConnected, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_LoadNLightVal, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_SetCurrDim, _data, _reply, 0);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(bydata);
                    _data.writeInt(len);
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
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bMute) {
                        i = 1;
                    }
                    _data.writeInt(i);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_get_kesaiwei_chk_Video_Driving_Ban, _data, _reply, 0);
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
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_get_kesaiwei_bPark, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_sendDvdDataToMcu, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_sendMcuData_KSW, _data, _reply, 0);
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
                    this.mRemote.transact(Stub.TRANSACTION_setDashBoardCallback, _data, _reply, 0);
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
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(controller);
                    if (open) {
                        i = 1;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(126, _data, _reply, 0);
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
                    this.mRemote.transact(127, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getmIsAddMouseView() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
