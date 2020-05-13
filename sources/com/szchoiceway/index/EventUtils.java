package com.szchoiceway.index;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventUtils {
    public static final String ACTION_SHOW_TASK_LIST = "com.szchoiceway.eventcenter.EventUtils.ACTION_SHOW_TASK_LIST";
    public static final String ACTION_SWITCH_ORIGINACAR = "com.szchoiceway.eventcenter.EventUtils.ACTION_SWITCH_ORIGINACAR";
    public static final String ACTION_SWITCH_USBDVRMODE = "com.szchoiceway.ACTION_SWITCH_USBDVRMODE";
    public static final String ACTION_SWITCH_USBDVRMODE_EXTRA = "com.szchoiceway.ACTION_SWITCH_USBDVRMODE_EXTRA";
    public static final byte ADDR_CMD_DVR = 29;
    public static final String AUX_MODE_CLASS_NAME = "com.szchoiceway.auxplayer.MainActivity";
    public static final String AUX_MODE_PACKAGE_NAME = "com.szchoiceway.auxplayer";
    public static final String BACKCAR_SERVICE_NAME = "com.szchoiceway.backcarevent.BackcarService";
    public static final String BROADCAST_VALID_MODE_EVT = "com.szchoiceway.eventcenter.EventUtils.BROADCAST_VALID_MODE_EVT";
    public static final String BTMUSIC_MODE_CLASS_NAME = "com.szchoiceway.btsuite.BTMusicActivity";
    public static final String BT_MODE_CLASS_NAME = "com.szchoiceway.btsuite.BTMainActivity";
    public static final String BT_MODE_PACKAGE_NAME = "com.szchoiceway.btsuite";
    public static final String CANBUS_MODE_CLASS_NAME = "com.szchoiceway.VScanbus.CanMainActivity";
    public static final String CANBUS_MODE_PACKAGE_NAME = "com.szchoiceway.VScanbus";
    public static final String CARRECORDER_MODE_CLASS_NAME = "com.rk.carrecorder.CameraActivity";
    public static final String CARRECORDER_MODE_PACKAGE_NAME = "com.rk.carrecorder";
    public static final String CAR_AIR_DATA = "EventUtils.CAR_AIR_DATA";
    public static final String CHEKU_BOTTOM_KEY = "com.szchoiceway.eventcenter.EventUtils.CHEKU_BOTTOM_KEY";
    public static final int CHEKU_BOTTOM_KEY_1 = 1;
    public static final int CHEKU_BOTTOM_KEY_10 = 10;
    public static final int CHEKU_BOTTOM_KEY_2 = 2;
    public static final int CHEKU_BOTTOM_KEY_3 = 3;
    public static final int CHEKU_BOTTOM_KEY_4 = 4;
    public static final int CHEKU_BOTTOM_KEY_5 = 5;
    public static final int CHEKU_BOTTOM_KEY_6 = 6;
    public static final int CHEKU_BOTTOM_KEY_7 = 7;
    public static final int CHEKU_BOTTOM_KEY_8 = 8;
    public static final int CHEKU_BOTTOM_KEY_9 = 9;
    public static final String CHEKU_BOTTOM_KEY_DATA = "com.szchoiceway.eventcenter.EventUtils.CHEKU_BOTTOM_KEY_DATA";
    public static final String CHEKU_BOTTOM_KEY_DATA_PARKDOWN = "com.szchoiceway.eventcenter.EventUtils.CHEKU_BOTTOM_KEY_DATA_PARKDOWN";
    public static final String CHEKU_BOTTOM_KEY_PARK = "com.szchoiceway.eventcenter.EventUtils.CHEKU_BOTTOM_KEY_PARK";
    public static final String CHEKU_BOTTOM_KEY_PARK_DATA = "com.szchoiceway.eventcenter.EventUtils.CHEKU_BOTTOM_KEY_PARK_DATA";
    public static final String CHEKU_WEATHER_INIT_REFRESH_WEATHER = "com.choiceway.eventcenter.EventUtils.CHEKU_WEATHER_INIT_REFRESH_WEATHER";
    public static final String CHEKU_WEATHER_IPC = "com.choiceway.eventcenter.EventUtils.CHEKU_WEATHER_IPC";
    public static final String CHEKU_WEATHER_IPC_CityName = "com.choiceway.eventcenter.EventUtils.CHEKU_WEATHER_IPC_CityName";
    public static final String CHEKU_WEATHER_IPC_UpdateInfor = "com.choiceway.eventcenter.EventUtils.CHEKU_WEATHER_IPC_UpdateInfor";
    public static final String CHEKU_WEATHER_IPC_WeatherDay = "com.choiceway.eventcenter.EventUtils.CHEKU_WEATHER_IPC_WeatherDay";
    public static final String CHEKU_WEATHER_IPC_WeatherIcon = "com.choiceway.eventcenter.EventUtils.CHEKU_WEATHER_IPC_WeatherIcon";
    public static final String CHEKU_WEATHER_IPC_WeatherInfor = "com.choiceway.eventcenter.EventUtils.CHEKU_WEATHER_IPC_WeatherInfor";
    public static final String CHEKU_WEATHER_IPC_WeatherTemp = "com.choiceway.eventcenter.EventUtils.CHEKU_WEATHER_IPC_WeatherTemp";
    public static final String CHEKU_WEATHER_IPC_WeatherTitle = "com.choiceway.eventcenter.EventUtils.CHEKU_WEATHER_IPC_WeatherTitle";
    public static final String CHEKU_WEATHER_SETCITYCODE = "com.choiceway.eventcenter.EventUtils.CHEKU_WEATHER_SETCITYCODE";
    public static final byte CMD_ARM_SEND_PASSWORD = 30;
    public static final byte CMD_ARM_SYS_RTC_TIME = 19;
    public static final byte CMD_BACK_RADAR = 32;
    public static final byte CMD_BAL_FAD_VAL = 122;
    public static final byte CMD_BEEP = 6;
    public static final byte CMD_BL_PW = 25;
    public static final byte CMD_BMT_VAL = 118;
    public static final byte CMD_BREAK_STATE = 28;
    public static final byte CMD_BT_POWER = 24;
    public static final byte CMD_BT_STATE = 11;
    public static final byte CMD_CAMRY_CAN_BAIC_INFO = 36;
    public static final byte CMD_CAMRY_CAN_CURRENT_TRIP = 34;
    public static final byte CMD_CAMRY_CAN_HISTORY_TRIP = 35;
    public static final byte CMD_CAMRY_CAN_MIN_TRIP = 39;
    public static final byte CMD_CAMRY_CAN_TPMS_STATUS = 37;
    public static final byte CMD_CAMRY_CAN_TRIP_INFO = 33;
    public static final byte CMD_CAMRY_CAN_VEHICLE_SETTINGS = 38;
    public static final byte CMD_CANBUS_ADDRESS = 13;
    public static final byte CMD_CANBUS_ALL_ADD = -91;
    public static final byte CMD_CAN_AIR = -96;
    public static final byte CMD_CAR_AIR = -95;
    public static final byte CMD_CAR_INFO = -76;
    public static final byte CMD_CMMB_POWER = 22;
    public static final byte CMD_CRV_SIYU_COMPASS_STATUS = -46;
    public static final byte CMD_CRV_SIYU_MEDIA_STATUS = 33;
    public static final byte CMD_CRV_SIYU_TIME_STATUS = -47;
    public static final byte CMD_DISC_AUTO_IN = -126;
    public static final byte CMD_DSP_TYTE = 119;
    public static final byte CMD_DVD_FLIE_TYPE = 18;
    public static final byte CMD_DVD_TYPE = 33;
    public static final byte CMD_FACTORY_SET = 15;
    public static final byte CMD_FM_EVENT = 115;
    public static final byte CMD_FM_KEY = 2;
    public static final byte CMD_FREQ_SEL = Byte.MIN_VALUE;
    public static final byte CMD_GPS_SOUND_TYPE = 20;
    public static final byte CMD_HEAD_RADAR = 35;
    public static final byte CMD_KEY_AD = 126;
    public static final byte CMD_KEY_EVENT = 114;
    public static final byte CMD_LOUND = 123;
    public static final byte CMD_MAIN_VOL = 121;
    public static final byte CMD_MCU_CONTROL_BLACK_SCREEN = -124;
    public static final byte CMD_MCU_INIT = 124;
    public static final byte CMD_MCU_SEND_PASSWORD = -123;
    public static final byte CMD_MCU_UPGRADE = 14;
    public static final byte CMD_MENU_MOVE_DIR = 52;
    public static final byte CMD_MENU_TOUCH_POS = 51;
    public static final byte CMD_MODE = 1;
    public static final byte CMD_MODE_ASK = 112;
    public static final byte CMD_MODE_POWERON = -127;
    public static final byte CMD_MUTE = 120;
    public static final byte CMD_NOTIFY_MCU_GPSMODE = 39;
    public static final byte CMD_PLAY_STATE = 21;
    public static final byte CMD_RADIO_ONOFF = 125;
    public static final byte CMD_SEND_8825_VALUE = 35;
    public static final byte CMD_SEND_AROUND_VALUE = 47;
    public static final byte CMD_SEND_AUDIO_VALUE = 34;
    public static final byte CMD_SEND_BLACK_SCREEN = 31;
    public static final byte CMD_SEND_DIM = 36;
    public static final byte CMD_SEND_GPS_VOL = 38;
    public static final byte CMD_SEND_KEY_MUTE = 17;
    public static final byte CMD_SEND_NOTIFY_HAS_RADAR_SIGNAL = 41;
    public static final byte CMD_SEND_PWM_VALUE = 46;
    public static final byte CMD_SEND_RADA = 40;
    public static final byte CMD_SEND_STUDY_KEY_FLAG = -120;
    public static final byte CMD_SEND_TPMS_DATA = 37;
    public static final byte CMD_SET_BMT_VAL = 23;
    public static final byte CMD_SET_FM_FREQ = 12;
    public static final byte CMD_SET_MUTE = 10;
    public static final byte CMD_SRC_ARMUI = 49;
    public static final byte CMD_SRC_SWITCH = 48;
    public static final byte CMD_SYS_EQ = 9;
    public static final byte CMD_SYS_EVENT = 113;
    public static final byte CMD_SYS_KEY = 8;
    public static final byte CMD_SYS_RTC_TIME = -125;
    public static final byte CMD_SYS_SET = 5;
    public static final byte CMD_TOUCH_BTN_KEY = 27;
    public static final byte CMD_TOUCH_POS = 4;
    public static final byte CMD_TV_EVENT = 117;
    public static final byte CMD_TV_KEY = 3;
    public static final byte CMD_UPGRADE_ACK = Byte.MAX_VALUE;
    public static final byte CMD_VIDEO_FORMAT = 26;
    public static final byte CMD_WHEEL = 7;
    public static final byte CMD_WHEEL_STATE = 116;
    public static final String CMMB_MODE_CLASS_NAME = "com.szchoiceway.navigation.CMMBActivity";
    public static final String CMMB_MODE_PACKAGE_NAME = "com.szchoiceway.navigation";
    public static final String DDBOX_KEY_NEXT = "com.glsx.ddbox.audiofocus.playnext";
    public static final String DDBOX_KEY_PREV = "com.glsx.ddbox.audiofocus.playpre";
    public static final String DSP_MODE_CLASS_NAME = "com.szchoiceway.dsp.MainActivity";
    public static final String DSP_MODE_PACKAGE_NAME = "com.szchoiceway.dsp";
    public static final String DVD_MODE_CLASS_NAME = "com.szchoiceway.navigation.DVDActivity";
    public static final String DVD_MODE_PACKAGE_NAME = "com.szchoiceway.navigation";
    public static final String DVR_MODE_CLASS_NAME = "com.szchoiceway.dvrplayer.DvrmainActivity";
    public static final String DVR_MODE_PACKAGE_NAME = "com.szchoiceway.dvrplayer";
    public static final int EVENT_BACKCAR_END = 4100;
    public static final int EVENT_BACKCAR_START = 4099;
    public static final int EVENT_KEY_EVENT = 4098;
    public static final int EVENT_MODE_CHANGE = 4097;
    public static final int EVENT_POWER = 4101;
    public static final String EVENT_SERVICE_NAME = "com.szchoiceway.eventcenter.EventService";
    public static final int EVENT_START = 4096;
    public static final String EVT_BACKCAR_NAME = "BACKCAR_STATE";
    public static final int EVT_END_BACKCAR = 502;
    public static final int EVT_START_BACKCAR = 501;
    public static final String EXPLORER_MODE_CLASS_NAME = "com.android.browser.BrowserActivity";
    public static final String EXPLORER_MODE_CLASS_NAME2 = "com.google.android.apps.chrome.Main";
    public static final String EXPLORER_MODE_PACKAGE_NAME = "com.android.browser";
    public static final String EXPLORER_MODE_PACKAGE_NAME2 = "com.android.chrome";
    public static final byte FM_BAND_NUM = 1;
    public static final byte FM_CURR_FREQ = 3;
    public static final byte FM_FREQ_LIST = 4;
    public static final byte FM_PS_NAME = 6;
    public static final byte FM_PTY_TYPE = 5;
    public static final byte FM_STATE = 0;
    public static final byte FM_TUNER_NUM = 2;
    public static final int HANDLER_ACC_DISCONNECTED = 255;
    public static final int HANDLER_BACKCAR_END = 251;
    public static final int HANDLER_BACKCAR_START = 250;
    public static final int HANDLER_BREAK_EVT = 254;
    public static final int HANDLER_EXECUTE_LAST_MDOE = 256;
    public static final int HANDLER_LAMP_EVT = 253;
    public static final int HANDLER_MENU_TOUCH_POS = 252;
    public static final int HANDLER_QUIT = 256;
    public static final String HBCP_EVT_HSHF_GET_STATUS = "com.szchoiceway.btsuite.HBCP_EVT_HSHF_GET_STATUS";
    public static final String HBCP_EVT_HSHF_STATUS = "com.szchoiceway.btsuite.HBCP_EVT_HSHF_STATUS";
    public static final int HBCP_STATUS_HSHF_ACTIVE_CALL = 6;
    public static final int HBCP_STATUS_HSHF_CONNECTED = 3;
    public static final int HBCP_STATUS_HSHF_CONNECTING = 2;
    public static final int HBCP_STATUS_HSHF_INCOMING_CALL = 5;
    public static final int HBCP_STATUS_HSHF_INITIALISING = 0;
    public static final int HBCP_STATUS_HSHF_OUTGOING_CALL = 4;
    public static final int HBCP_STATUS_HSHF_READY = 1;
    public static final int HIDE_VOL_DIALOG = 8;
    public static final String INSTRUCT_MODE_CLASS_NAME = "com.szchoiceway.instructions.MainActivity";
    public static final String INSTRUCT_MODE_PACKAGE_NAME = "com.szchoiceway.instructions";
    public static final String INTENT_EXTRA_INT_KEYNAME = "com.szchoiceway.btsuite.DATA_INT";
    public static final String INTENT_EXTRA_STR_KEYNAME = "com.szchoiceway.btsuite.DATA_STR";
    public static final byte KESAIWEI_1280X480_BELT_BRAKE = 91;
    public static final byte KESAIWEI_1280X480_CALLBACK_STATE = 90;
    public static final String KEY_BAL_SETTINGS = "COM.SZCHOICEWAY_BAL_SETTINGS";
    public static final String KEY_BASS_FREQ_SETTINGS = "COM.SZCHOICEWAY_BASS_FREQ_SETTINGS";
    public static final String KEY_BASS_SETTINGS = "COM.SZCHOICEWAY_BASS_SETTINGS";
    public static final String KEY_BRIGHTNESS_SETTINGS = "COM.SZCHOICEWAY_BRIGHTNESS_SETTINGS";
    public static final String KEY_BackcarFullview = "Set_BackcarFullview";
    public static final String KEY_BackcarRadar = "Set_BackcarRadar";
    public static final String KEY_BackcarTrack = "Set_BackcarTrack";
    public static final String KEY_EQ_MODE_SETTINGS = "COM.SZCHOICEWAY_EQ_MODE_SETTINGS";
    public static final String KEY_FAD_SETTINGS = "COM.SZCHOICEWAY_FAD_SETTINGS";
    public static final String KEY_KEYDOWNSND_SETTINGS = "COM.SZCHOICEWAY_KEYDOWNSND_SETTINGS";
    public static final String KEY_LOGO_SETTINGS = "COM.SZCHOICEWAY_LOGO_SETTINGS";
    public static final String KEY_LiShengDVD = "Set_LiShengDVD";
    public static final String KEY_LiShengType = "Set_LiShengType";
    public static final String KEY_MID_FREQ_SETTINGS = "COM.SZCHOICEWAY_MID_FREQ_SETTINGS";
    public static final String KEY_MID_SETTINGS = "COM.SZCHOICEWAY_MID_SETTINGS";
    public static final String KEY_NBRIGHTNESS_SETTINGS = "COM.SZCHOICEWAY_NBRIGHTNESS_SETTINGS";
    public static final String KEY_NaiAoShiDVD = "Set_NaiAoShiDVD";
    public static final String KEY_NaiAoShiType = "Set_NaiAoShiType";
    public static final String KEY_OrgBackcar = "Set_OrgBackcar";
    public static final String KEY_RADIO_ZONE_SETTINGS = "COM.SZCHOICEWAY_RADIO_ZONE_SETTINGS";
    public static final String KEY_STANDBY_SETTINGS = "COM.SZCHOICEWAY_STANDBY_SETTINGS";
    public static final String KEY_SYS_LAST_MODE = "KEY_SYS_LAST_MODE_SAVE";
    public static final String KEY_TRE_FREQ_SETTINGS = "COM.SZCHOICEWAY_TRE_FREQ_SETTINGS";
    public static final String KEY_TRE_SETTINGS = "COM.SZCHOICEWAY_TRE_SETTINGS";
    public static final String KSW_OLD_BMW_X1_ORIGINAL_CLASS = "com.szchoiceway.ksw_old_bmw_x1_original.MainActivity";
    public static final String KSW_OLD_BMW_X1_ORIGINAL_PACKAGE = "com.szchoiceway.ksw_old_bmw_x1_original";
    public static final String KSW_ZXW_BT_CONNECED_SHOW_VIEW = "com.choiceway.eventcenter.EventUtils.KSW_ZXW_BT_CONNECED_SHOW_VIEW";
    public static final String KSW_ZXW_BT_CONNECED_SHOW_VIEW_DATA = "com.choiceway.eventcenter.EventUtils.KSW_ZXW_BT_CONNECED_SHOW_VIEW_DATA";
    public static final String MAISILUO_ZXW_SHOW_ALL_APPS = "com.choiceway.eventcenter.EventUtils.MAISILUO_ZXW_SHOW_ALL_APPS";
    public static final byte MCU_KEY1_12 = 109;
    public static final byte MCU_KEY1_14 = 110;
    public static final byte MCU_KEY1_2 = 103;
    public static final byte MCU_KEY1_3 = 104;
    public static final byte MCU_KEY1_4 = 105;
    public static final byte MCU_KEY1_5 = 108;
    public static final byte MCU_KEY2_1 = 106;
    public static final byte MCU_KEY2_10 = 107;
    public static final byte MCU_KEY_AB = 86;
    public static final byte MCU_KEY_AC = 91;
    public static final byte MCU_KEY_AIR_SW = 95;
    public static final byte MCU_KEY_AMS = 12;
    public static final byte MCU_KEY_ANGLE = 31;
    public static final byte MCU_KEY_APPLIST = -7;
    public static final byte MCU_KEY_APS = 13;
    public static final byte MCU_KEY_AUTO = 94;
    public static final byte MCU_KEY_BEEP = -15;
    public static final byte MCU_KEY_BLACK = 21;
    public static final byte MCU_KEY_BLOW_FONT = 92;
    public static final byte MCU_KEY_BLOW_REVERSE = 93;
    public static final byte MCU_KEY_BND = 14;
    public static final byte MCU_KEY_BT = 59;
    public static final byte MCU_KEY_CLEAR = 83;
    public static final byte MCU_KEY_CLOCK = 73;
    public static final byte MCU_KEY_CMMB = 62;
    public static final byte MCU_KEY_DIM = -10;
    public static final byte MCU_KEY_DISP = 10;
    public static final byte MCU_KEY_DISPLAY = 32;
    public static final byte MCU_KEY_DOWN = 26;
    public static final byte MCU_KEY_DUAL = 88;
    public static final byte MCU_KEY_DVD = 57;
    public static final byte MCU_KEY_DVD_MENU = 84;
    public static final byte MCU_KEY_EJECT = 15;
    public static final byte MCU_KEY_ENTER = 28;
    public static final byte MCU_KEY_EQ = 51;
    public static final byte MCU_KEY_FAN_ADD = 99;
    public static final byte MCU_KEY_FAN_SUB = 98;
    public static final byte MCU_KEY_FF = 7;
    public static final byte MCU_KEY_FORCE_EJECT = 56;
    public static final byte MCU_KEY_F_CAM = 102;
    public static final byte MCU_KEY_GOTO = 47;
    public static final byte MCU_KEY_HANGUP = 22;
    public static final byte MCU_KEY_IDLE = -8;
    public static final byte MCU_KEY_LEFT = 24;
    public static final byte MCU_KEY_LEFT_TEMP_ADD = 100;
    public static final byte MCU_KEY_LEFT_TEMP_SUB = 101;
    public static final byte MCU_KEY_LOCDX = 48;
    public static final byte MCU_KEY_LOUDNESS = 82;
    public static final byte MCU_KEY_L_R = 50;
    public static final byte MCU_KEY_MAX_AC = 90;
    public static final byte MCU_KEY_MENU = 9;
    public static final byte MCU_KEY_MODE = 16;
    public static final byte MCU_KEY_MOVIE = -13;
    public static final byte MCU_KEY_MP5 = 60;
    public static final byte MCU_KEY_MUSIC = -14;
    public static final byte MCU_KEY_MUTE = 17;
    public static final byte MCU_KEY_NAV = 55;
    public static final byte MCU_KEY_NEXT = 2;
    public static final byte MCU_KEY_NONE = 0;
    public static final byte MCU_KEY_NP = -12;
    public static final byte MCU_KEY_NUM0 = 36;
    public static final byte MCU_KEY_NUM1 = 37;
    public static final byte MCU_KEY_NUM10 = 46;
    public static final byte MCU_KEY_NUM1_LONG = 64;
    public static final byte MCU_KEY_NUM2 = 38;
    public static final byte MCU_KEY_NUM2_LONG = 65;
    public static final byte MCU_KEY_NUM3 = 39;
    public static final byte MCU_KEY_NUM3_LONG = 66;
    public static final byte MCU_KEY_NUM4 = 40;
    public static final byte MCU_KEY_NUM4_LONG = 67;
    public static final byte MCU_KEY_NUM5 = 41;
    public static final byte MCU_KEY_NUM5_LONG = 68;
    public static final byte MCU_KEY_NUM6 = 42;
    public static final byte MCU_KEY_NUM6_LONG = 69;
    public static final byte MCU_KEY_NUM7 = 43;
    public static final byte MCU_KEY_NUM8 = 44;
    public static final byte MCU_KEY_NUM9 = 45;
    public static final byte MCU_KEY_PHONELINK = -6;
    public static final byte MCU_KEY_PIP = -16;
    public static final byte MCU_KEY_PLAY = 4;
    public static final byte MCU_KEY_PLAYPAUSE = 6;
    public static final byte MCU_KEY_POWER = 1;
    public static final byte MCU_KEY_PREV = 3;
    public static final byte MCU_KEY_PROG = 72;
    public static final byte MCU_KEY_RADIO = 54;
    public static final byte MCU_KEY_RADIO_NEXT = 70;
    public static final byte MCU_KEY_RADIO_PREV = 71;
    public static final byte MCU_KEY_RANDOM = 30;
    public static final byte MCU_KEY_REP = 11;
    public static final byte MCU_KEY_REPEAT = 29;
    public static final byte MCU_KEY_RETURN = 85;
    public static final byte MCU_KEY_RF = 8;
    public static final byte MCU_KEY_RIGHT = 27;
    public static final byte MCU_KEY_RIGHT_TEMP_ADD = 96;
    public static final byte MCU_KEY_RIGHT_TEMP_SUB = 97;
    public static final byte MCU_KEY_SEARCH = 87;
    public static final byte MCU_KEY_SEL = 58;
    public static final byte MCU_KEY_SETUP = 20;
    public static final byte MCU_KEY_SLOW = 53;
    public static final byte MCU_KEY_SOUNDLANG = 34;
    public static final byte MCU_KEY_STANDBY = -9;
    public static final byte MCU_KEY_STMONO = 49;
    public static final byte MCU_KEY_STOP = 5;
    public static final byte MCU_KEY_SUBT = 33;
    public static final byte MCU_KEY_SYS_ESC = 78;
    public static final byte MCU_KEY_SYS_HOME = 76;
    public static final byte MCU_KEY_SYS_MENU = 77;
    public static final byte MCU_KEY_SYS_WINCE = 79;
    public static final byte MCU_KEY_TAB = 89;
    public static final byte MCU_KEY_TALK = 23;
    public static final byte MCU_KEY_TFT_CLOSE = 75;
    public static final byte MCU_KEY_TFT_LONG_CLOSE = 81;
    public static final byte MCU_KEY_TFT_LONG_OPEN = 80;
    public static final byte MCU_KEY_TFT_OPEN = 74;
    public static final byte MCU_KEY_TITLE = 52;
    public static final byte MCU_KEY_TOUCH = 63;
    public static final byte MCU_KEY_TOUCH_CAL = -11;
    public static final byte MCU_KEY_TV = 61;
    public static final byte MCU_KEY_UP = 25;
    public static final byte MCU_KEY_VOL_ADD = 18;
    public static final byte MCU_KEY_VOL_SUB = 19;
    public static final byte MCU_KEY_ZOOM = 35;
    public static final String MCU_MSG_ACC_DISCONNECTED_EVT = "com.choiceway.eventcenter.EventUtils.MCU_MSG_ACC_DISCONNECTED_EVT";
    public static final String MCU_MSG_BACKCAR_END_EVT = "com.choiceway.eventcenter.EventUtils.MCU_MSG_BACKCAR_END";
    public static final String MCU_MSG_BACKCAR_START_EVT = "com.choiceway.eventcenter.EventUtils.MCU_MSG_BACKCAR_START";
    public static final String MCU_MSG_BAL_FAD = "com.choiceway.eventcenter.EventUtils.MCU_MSG_BAL_FAD";
    public static final String MCU_MSG_BRAKE_EVT = "com.choiceway.eventcenter.EventUtils.MCU_MSG_BRAKE_EVT";
    public static final String MCU_MSG_CAN_ALL_INFO = "com.choiceway.eventcenter.EventUtils.MCU_MSG_CAN_ALL_INFO";
    public static final String MCU_MSG_CAR_AIR = "com.choiceway.eventcenter.EventUtils.CMD_CAR_AIR";
    public static final String MCU_MSG_CAR_INFO = "com.choiceway.eventcenter.EventUtils.MCU_MSG_CAR_INFO";
    public static final String MCU_MSG_EQ_MODE = "com.choiceway.eventcenter.EventUtils.MCU_MSG_EQ_MODE";
    public static final String MCU_MSG_INIT_EVT = "com.choiceway.eventcenter.EventUtils.MCU_MSG_INIT_EVT";
    public static final String MCU_MSG_LOUD_EVT = "com.choiceway.eventcenter.EventUtils.MCU_MSG_LOUD_EVT";
    public static final String MCU_MSG_MAIL_VOL = "com.choiceway.eventcenter.EventUtils.MCU_MSG_MAIL_VOL";
    public static final String MCU_MSG_MAIL_VOL_VAL = "com.choiceway.eventcenter.EventUtils.MCU_MSG_MAIL_VOL_VAL";
    public static final String MCU_MSG_MUTE_STATE = "com.choiceway.eventcenter.EventUtils.MCU_MSG_MUTE_STATE";
    public static final String MCU_MSG_WHEEL_INFO = "com.choiceway.eventcenter.EventUtils.MCU_MSG_WHEEL_INFO";
    public static final byte MCU_SEND_8825_VALUE = -122;
    public static final String MOVIE_MODE_CLASS_NAME = "com.szchoiceway.videoplayer.MainActivity";
    public static final String MOVIE_MODE_PACKAGE_NAME = "com.szchoiceway.videoplayer";
    public static final String MSG_EVENT_BACKCAR = "com.szchoiceway.backcarevent.BACKCAR";
    public static final String MUSIC_MODE_CLASS_NAME = "com.szchoiceway.musicplayer.MainActivity";
    public static final String MUSIC_MODE_PACKAGE_NAME = "com.szchoiceway.musicplayer";
    public static final String MUSIC_NAVI_MODE_CLASS_NAME = "com.szchoiceway.navigation.MusicSetActivity";
    public static final String MUSIC_NAVI_MODE_PACKAGE_NAME = "com.szchoiceway.navigation";
    public static final String NAV_MODE_CLASS_NAME = "com.szchoiceway.navigation.MainActivity";
    public static final String NAV_MODE_PACKAGE_NAME = "com.szchoiceway.navigation";
    public static final String NAV_SET_GOTO_PAGE = "com.szchoiceway.NaviSettings.GotoPage";
    public static final String NOTIFY_WORKSPACE_PLAY_STATUS = "com.choiceway.eventcenter.EventUtils.NOTIFY_WORKSPACE_PLAY_STATUS";
    public static final String NOTIFY_WORKSPACE_PLAY_STRACK = "com.choiceway.eventcenter.EventUtils.NOTIFY_WORKSPACE_PLAY_STRACK";
    public static final String PHONEAPP_MODE_CLASS_NAME = "net.easyconn.WelcomeActivity";
    public static final String PHONEAPP_MODE_PACKAGE_NAME = "net.easyconn";
    public static final String PHONELINK_MODE_CLASS_NAME = "com.szchoiceway.phonelink.MainActivity";
    public static final String PHONELINK_MODE_PACKAGE_NAME = "com.szchoiceway.phonelink";
    public static final String PLAY_STATUS_DATA = "EventUtils.PLAY_STATUS_DATA";
    public static final String PLAY_STRACK_DATA = "EventUtils.PLAY_STRACK_DATA";
    public static final String RADIO_DSP_MODE_CLASS_NAME = "com.szchoiceway.dsp.radio.MainActivity";
    public static final String RADIO_DSP_MODE_PACKAGE_NAME = "com.szchoiceway.dsp.radio";
    public static final String RADIO_MODE_CLASS_NAME = "com.szchoiceway.radio.MainActivity";
    public static final String RADIO_MODE_PACKAGE_NAME = "com.szchoiceway.radio";
    public static final String REC_AUTONAVI_STANDARD = "AUTONAVI_STANDARD_BROADCAST_SEND";
    public static final String SET_MODE_CLASS_NAME = "com.szchoiceway.settings.MainActivity";
    public static final String SET_MODE_PACKAGE_NAME = "com.szchoiceway.settings";
    public static final int SHOW_VOL_DIALOG = 7;
    public static final String STEER_WHEEL_INFOR = "com.choiceway.eventcenter.EventUtils.STEER_WHEEL_INFOR";
    public static final String STEER_WHEEL_INFOR_LPARAM = "EventUtils.STEER_WHEEL_INFOR_LPARAM";
    public static final String STEER_WHEEL_INFOR_WPARAM = "EventUtils.STEER_WHEEL_INFOR_WPARAM";
    public static final String STEER_WHEEL_STATUS = "com.choiceway.eventcenter.EventUtils.STEER_WHEEL_STATUS";
    public static final String STEER_WHEEL_STUDY_STATUS = "EventUtils.STEER_WHEEL_STUDY_STATUS";
    public static final int SYS_BAL_ADD = 8;
    public static final int SYS_BAL_SUB = 9;
    public static final int SYS_BASS_ADD = 2;
    public static final int SYS_BASS_FREQ_ADD = 17;
    public static final int SYS_BASS_FREQ_SUB = 18;
    public static final int SYS_BASS_SUB = 3;
    public static final byte SYS_BEEP_ONOFF = 2;
    public static final int SYS_EJECT = 15;
    public static final int SYS_FAD_ADD = 10;
    public static final int SYS_FAD_SUB = 11;
    public static final byte SYS_FM_ZONE = 1;
    public static final int SYS_FORCE_EJECT = 16;
    public static final byte SYS_GPS_SND = 4;
    public static final int SYS_INIT_AUDIO = 14;
    public static final int SYS_LOUD = 13;
    public static final int SYS_MID_ADD = 4;
    public static final int SYS_MID_FREQ_ADD = 19;
    public static final int SYS_MID_FREQ_SUB = 20;
    public static final int SYS_MID_SUB = 5;
    public static final int SYS_MUTE = 12;
    public static final byte SYS_PTY_NUM = 3;
    public static final byte SYS_RDS_ONFF = 0;
    public static final byte SYS_SYS_VOL = 5;
    public static final int SYS_TREB_FREQ_ADD = 21;
    public static final int SYS_TREB_FREQ_SUB = 22;
    public static final int SYS_TRE_ADD = 6;
    public static final int SYS_TRE_SUB = 7;
    public static final int SYS_VOL_ADD = 0;
    public static final int SYS_VOL_SUB = 1;
    public static final byte TV_AMS = 9;
    public static final byte TV_BAND = 0;
    public static final byte TV_CHANNEL = 1;
    public static final byte TV_CHECK_VALID = 3;
    public static final byte TV_FORMAT = 6;
    public static final byte TV_FREQ = 2;
    public static final byte TV_FREQ_ADD = 2;
    public static final byte TV_FREQ_DEC = 3;
    public static final byte TV_FREQ_STEP = 1;
    public static final String TV_MODE_CLASS_NAME = "com.szchoiceway.tvplayer.MainActivity";
    public static final String TV_MODE_PACKAGE_NAME = "com.szchoiceway.tvplayer";
    public static final byte TV_NEXT = 7;
    public static final byte TV_NONE = 0;
    public static final byte TV_NOTIFY_OPENCAMERA = 4;
    public static final byte TV_PREV = 8;
    public static final byte TV_SEEK_DOWN = 5;
    public static final byte TV_SEEK_UP = 4;
    public static final byte TV_SIGNAL_INVALID = 10;
    public static final byte TV_SIGNAL_VALID = 11;
    public static final byte TV_SUB_FORMAT = 12;
    public static final String TXZ_TRIGGERRECORD_BUTTON_KEYWORDS = "com.txznet.triggerrecordbuttonKeywords";
    public static final String UART_DEV_PATH = "/dev/ttyS0";
    public static final String UART_DEV_PATH_RK3188 = "/dev/ttyS0";
    public static final String UART_DEV_PATH_RKPX3 = "/dev/ttyS0";
    public static final int UART_DEV_SPEED = 115200;
    public static final String USB_DVR_MODE_CLASS_NAME = "com.szchoiceway.usbdvrplayer.UsbDvrActivity";
    public static final String USB_DVR_MODE_PACKAGE_NAME = "com.szchoiceway.usbdvrplayer";
    public static final String VALID_MODE_INFOR_CHANGE = "com.szchoiceway.eventcenter.EventUtils.VALID_MODE_INFOR_CHANGE";
    public static final String WEATHER_AREA_CHANGE = "com.szchoiceway.eventcenter.EventUtils.WEATHER_AREA_CHANGE";
    public static final int WSK_BANK = 4;
    public static final int WSK_CLEAR = 115;
    public static final int WSK_ENTER = 112;
    public static final int WSK_HANDUP = 6;
    public static final int WSK_MODE = 0;
    public static final int WSK_MUTE = 5;
    public static final int WSK_NEXT = 1;
    public static final int WSK_OK = 114;
    public static final int WSK_PREV = 2;
    public static final int WSK_PWOFF = 3;
    public static final int WSK_QUIT = 113;
    public static final int WSK_TURNON = 7;
    public static final int WSK_VOLDEC = 9;
    public static final int WSK_VOLINC = 8;
    public static final String ZHTY_SHOW_OR_HIDE_TXZ_WIN = "com.choiceway.eventcenter.ZHTY_SHOW_OR_HIDE_TXZ_WIN";
    public static final String ZHTY_SHOW_OR_HIDE_TXZ_WIN_DATA = "com.choiceway.eventcenter.ZHTY_SHOW_OR_HIDE_TXZ_WIN_DATA";
    public static final String ZXW_ACTION_NOTIIFY_MEDIA_TYPE = "com.szchoiceway.eventcenter.EventUtils.ZXW_ACTION_NOTIIFY_MEDIA_TYPE";
    public static final String ZXW_ACTION_NOTIIFY_MEDIA_TYPE_EXTRA = "com.szchoiceway.eventcenter.EventUtils.ZXW_ACTION_NOTIIFY_MEDIA_TYPE_EXTRA";
    public static final String ZXW_ARRAY_BYTE_DATA_EVT = "com.choiceway.eventcenter.EventUtils.ARRAY_BYTE_DATA";
    public static final String ZXW_ARRAY_BYTE_EXTRA_DATA_EVT = "com.choiceway.eventcenter.EventUtils.ZXW_ARRAY_BYTE_EXTRA_DATA_EVT";
    public static final String ZXW_AUX_MODE_CLASS_NAME = "com.szchoiceway.auxplayer.MainActivity";
    public static final String ZXW_AUX_MODE_PACKAGE_NAME = "com.szchoiceway.auxplayer";
    public static final String ZXW_AVM_LEFT_RIGHT_BACK = "com.szchoiceway.eventcenter.ZXW_AVM_LEFT_RIGHT_BACK";
    public static final String ZXW_CAN_KEY_EVT = "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT";
    public static final String ZXW_CAN_KEY_EVT_EXTRA = "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT_EXTRA";
    public static final String ZXW_CAN_START_UP_APPS = "com.szchoiceway.eventcenter.EventUtils.ZXW_CAN_START_UP_APPS";
    public static final String ZXW_MCU_UPGRADE_ACK_EVT = "com.choiceway.eventcenter.EventUtils.ZXW_MCU_UPGRADE_ACK_EVT";
    public static final String ZXW_MCU_UPGRADE_ACK_EVT_EXTRA = "com.choiceway.eventcenter.EventUtils.ZXW_MCU_UPGRADE_ACK_EVT_EXTRA";
    public static final int ZXW_ORIGINAL_MCU_KEY_DOWN = 4;
    public static final int ZXW_ORIGINAL_MCU_KEY_ENTER = 5;
    public static final String ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_DATA = "com.choiceway.eventcenter.EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_DATA";
    public static final String ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_EVT = "com.choiceway.eventcenter.EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_EVT";
    public static final int ZXW_ORIGINAL_MCU_KEY_LEFT = 1;
    public static final int ZXW_ORIGINAL_MCU_KEY_LEFT_HANDED = 7;
    public static final int ZXW_ORIGINAL_MCU_KEY_RIGHT = 2;
    public static final int ZXW_ORIGINAL_MCU_KEY_RIGHT_HANDED = 8;
    public static final int ZXW_ORIGINAL_MCU_KEY_UP = 3;
    public static final String ZXW_RADIO_INFO_EVT = "com.choiceway.eventcenter.EventUtils.ZXW_RADIO_INFO_EVT";
    public static final String ZXW_REFRESH_WALL_LOGO = "com.choiceway.FatUtils.ZXW_REFRESH_WALL_LOGO";
    public static final String ZXW_RESET_WALLPAGER_CHANGE = "com.choiceway.ZXW_RESET_WALLPAGER_CHANGE";
    public static final String ZXW_SENDBROADCAST8902MOD = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD";
    public static final String ZXW_SENDBROADCAST8902MOD_EXTRA = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_EXTRA";
    public static final String ZXW_SENDBROADCAST8902MOD_ShunShiSuDu = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_ShunShiSuDu";
    public static final String ZXW_SENDBROADCAST8902MOD_anquandai = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_anquandai";
    public static final String ZXW_SENDBROADCAST8902MOD_fadongjizhuansu = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_fadongjizhuansu";
    public static final String ZXW_SENDBROADCAST8902MOD_huanjinwendu = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_huanjinwendu";
    public static final String ZXW_SENDBROADCAST8902MOD_shousha = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_shousha";
    public static final String ZXW_SENDBROADCAST8902MOD_xushilicheng = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_xushilicheng";
    public static final String ZXW_SENDBROADCAST8902MOD_youLiang = "com.szchoiceway.eventcenter.EventUtils.ZXW_SENDBROADCAST8902MOD_youLiang";
    public static final String ZXW_SYS_APPWIDGET_CityName = "com.choiceway.EventUtils.ZXW_SYS_APPWIDGET_CityName";
    public static final String ZXW_SYS_APPWIDGET_CurWeather = "com.choiceway.EventUtils.ZXW_SYS_APPWIDGET_CurWeather";
    public static final String ZXW_SYS_APPWIDGET_CurWeatherInfor = "com.choiceway.EventUtils.ZXW_SYS_APPWIDGET_CurWeatherInfor";
    public static final String ZXW_SYS_APPWIDGET_DayWeather = "com.choiceway.EventUtils.ZXW_SYS_APPWIDGET_DayWeather";
    public static final String ZXW_SYS_APPWIDGET_EXTRA = "com.choiceway.eventcenter.EventUtils.ZXW_SYS_APPWIDGET_EXTRA";
    public static final String ZXW_SYS_APPWIDGET_UpdateTimer = "com.choiceway.EventUtils.ZXW_SYS_APPWIDGET_UpdateTimer";
    public static final String ZXW_SYS_APPWIDGET_WeatherIconStr = "com.choiceway.EventUtils.ZXW_SYS_APPWIDGET_WeatherIconStr";
    public static final String ZXW_SYS_APPWIDGET_WeatherInfor = "com.choiceway.EventUtils.ZXW_SYS_APPWIDGET_WeatherInfor";
    public static final String ZXW_SYS_EXTRA = "com.choiceway.eventcenter.EventUtils.ZXW_SYS_EXTRA";
    public static final String ZXW_SYS_KEY_EVT = "com.choiceway.eventcenter.EventUtils.ZXW_SYS_KEY";
    public static final int eRZY_AUSTRALIA = 5;
    public static final int eRZY_BRAZIL = 6;
    public static final int eRZY_CHINA = 0;
    public static final int eRZY_JAPAN = 4;
    public static final int eRZY_MAX = 7;
    public static final int eRZY_RUSSIA = 3;
    public static final int eRZY_USA1 = 1;
    public static final int eRZY_USA2 = 2;
    public static final int eSWSS_NONE = 0;
    public static final int eSWSS_PRESS_KEY = 2;
    public static final int eSWSS_SELECT_KEY = 1;
    public static final int eSWSS_STUDY_ERROR = 4;
    public static final int eSWSS_STUDY_OK = 3;
    public static final byte eTK_DVR_DOWN = 1;
    public static final byte eTK_DVR_FOMAT = 3;
    public static final byte eTK_DVR_MENU = 4;
    public static final byte eTK_DVR_PLAY = 2;
    public static final byte eTK_DVR_UP = 0;
    /* access modifiers changed from: private */
    public static final Map<Integer, eSrcMode> mValueList = new HashMap();

    public enum eSrcMode {
        SRC_NONE(0),
        SRC_RADIO(1),
        SRC_DVD(2),
        SRC_USB(3),
        SRC_CARD(4),
        SRC_IPOD(5),
        SRC_BT(6),
        SRC_BTMUSIC(7),
        SRC_CMMB(8),
        SRC_TV(9),
        SRC_MOVIE(10),
        SRC_MUSIC(11),
        SRC_EBOOK(12),
        SRC_IMAGE(13),
        SRC_ANDROID(14),
        SRC_VMCD(15),
        SRC_NETWORK(16),
        SRC_CARMEDIA(17),
        SRC_CAR_BT(18),
        SRC_DVR(19),
        SRC_MOBILE_APP(20),
        SRC_ATSL_AIRCONSOLE(30),
        SRC_TXZ_WEBCHAT(37),
        SRC_TXZ_MUSIC(38),
        SRC_PHONELINK(39),
        SRC_AUX(40),
        SRC_BACKCAR(41),
        SRC_GPS(42),
        SRC_HOME(43),
        SRC_REHOME(44),
        SRC_COMPASS(45),
        SRC_STANDBY(46),
        SRC_EQ(47),
        SRC_BACKLIGHT_SET(48),
        SRC_SETUP(49),
        SRC_FRONT_CAMERA(50),
        SRC_BCAM(51),
        SRC_LEFT_CAMERA(52),
        SRC_RIGHT_CAMERA(54),
        SRC_MCU_VERSION(80),
        SRC_TFT_VERSION(81),
        SRC_NULL(99),
        SRC_POWERON(100),
        SRC_POWEROFF(101),
        SRC_IDLE_MODE(CanUtils.SRC_IDLE_MODE),
        SRC_IDLE_REST(CanUtils.SRC_IDLE_REST),
        SRC_MUSIC_NAVI(105),
        SRC_PHONE_APP(106),
        SRC_QUICK_ACCESS_1(107),
        SRC_QUICK_ACCESS_2(108),
        SRC_Original_TO_ARM(109);
        
        private int value;

        private eSrcMode(int val) {
            this.value = val;
            EventUtils.mValueList.put(Integer.valueOf(val), this);
        }

        public byte getValue() {
            return (byte) (this.value & 255);
        }

        public int getIntValue() {
            return this.value;
        }

        public static eSrcMode valueOf(int val) {
            eSrcMode ret = (eSrcMode) EventUtils.mValueList.get(Integer.valueOf(val));
            if (ret == null) {
                return SRC_NONE;
            }
            return ret;
        }

        public String toString() {
            return Integer.toString(this.value);
        }
    }

    public static String getMcuComDevicePath() {
        if (Build.MODEL.equals("rk3188")) {
            return "/dev/ttyS0";
        }
        return "/dev/ttyS0";
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            String hv = Integer.toHexString(b & 255);
            stringBuilder.append("0x");
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + " ");
        }
        return stringBuilder.toString();
    }

    public static void sendBroadcast(Context context, String action, byte[] bydata) {
        if (context != null && bydata != null) {
            Intent intt = new Intent(action);
            intt.addFlags(32);
            intt.putExtra(CAR_AIR_DATA, bydata);
            context.sendBroadcast(intt);
        }
    }

    public static void sendSysBroadcast(Context context, String action, int iKeyExtra) {
        if (context != null) {
            Intent intt = new Intent(action);
            intt.putExtra(ZXW_SYS_EXTRA, iKeyExtra);
            context.sendBroadcast(intt);
        }
    }

    public static void sendBroadcastExtra(Context context, String action, int iExtraData) {
        if (context != null) {
            Intent intt = new Intent(action);
            intt.putExtra(ZXW_ARRAY_BYTE_EXTRA_DATA_EVT, iExtraData);
            context.sendBroadcast(intt);
        }
    }

    public static boolean startActivityIfNotRuning(Context context, Class<?> cls) {
        if (cls.getName().equals(((ActivityManager) context.getSystemService("activity")).getRunningTasks(1).get(0).topActivity.getClassName())) {
            return true;
        }
        Intent intent = new Intent(context, cls);
        intent.addFlags(268435456);
        context.startActivity(intent);
        return false;
    }

    public static void sendBroadcastCanKeyExtra(Context context, String action, int iExtraData) {
        if (context != null) {
            Intent intt = new Intent(action);
            intt.putExtra("com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT_EXTRA", iExtraData);
            context.sendBroadcast(intt);
        }
    }

    public static void startActivityIfNotRuning(Context context, String packageName, String className) {
        if (context != null && packageName != null && className != null) {
            ComponentName topAct = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1).get(0).topActivity;
            if (!topAct.getClassName().equals(className) || !topAct.getPackageName().equals(packageName)) {
                Intent intent = new Intent();
                intent.addCategory("android.intent.category.LAUNCHER");
                intent.setComponent(new ComponentName(packageName, className));
                intent.setFlags(270532608);
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isActivityRuning(Context context, Class<?> cls) {
        if (!cls.getName().equals(((ActivityManager) context.getSystemService("activity")).getRunningTasks(1).get(0).topActivity.getClassName())) {
            return false;
        }
        return true;
    }

    public static boolean getInstallStatus(Context context, String strPackage) {
        boolean bIsInstall = false;
        PackageManager packageManager = context.getPackageManager();
        new ArrayList();
        List<PackageInfo> mAllPackages = packageManager.getInstalledPackages(0);
        for (int i = 0; i < mAllPackages.size(); i++) {
            if (strPackage.equals(mAllPackages.get(i).packageName)) {
                bIsInstall = true;
            }
        }
        return bIsInstall;
    }

    public static String getProgressFromPosition(int position) {
        int time = position;
        if (((time / 60) / 60) / 1000 > 0) {
            long m = (long) ((time / 60000) % 60);
            return String.format("%02d:%02d:%02d", new Object[]{Long.valueOf((long) ((time / 3600000) % 24)), Long.valueOf(m), Long.valueOf((long) ((time / 1000) % 60))});
        } else if ((time / 60) / 1000 > 0) {
            return String.format("00:%02d:%02d", new Object[]{Long.valueOf((long) ((time / 60000) % 60)), Long.valueOf((long) ((time / 1000) % 60))});
        } else {
            return String.format("00:00:%02d", new Object[]{Long.valueOf((long) ((time / 1000) % 60))});
        }
    }

    public static int getSecFromPosition(int position) {
        int time = position;
        if (((time / 60) / 60) / 1000 > 0) {
            int i = (time / 60000) % 60;
            int i2 = (time / 3600000) % 24;
            return (time / 1000) % 60;
        } else if ((time / 60) / 1000 <= 0) {
            return (time / 1000) % 60;
        } else {
            int i3 = (time / 60000) % 60;
            return (time / 1000) % 60;
        }
    }

    public static int getMinFromPosition(int position) {
        int time = position;
        if (((time / 60) / 60) / 1000 > 0) {
            int i = (time / 1000) % 60;
            int i2 = (time / 3600000) % 24;
            return (time / 60000) % 60;
        } else if ((time / 60) / 1000 > 0) {
            int i3 = (time / 1000) % 60;
            return (time / 60000) % 60;
        } else {
            int iMinute = (time / 1000) % 60;
            return 0;
        }
    }

    public static int getHourFromPosition(int position) {
        int time = position;
        if (((time / 60) / 60) / 1000 > 0) {
            int i = (time / 1000) % 60;
            int i2 = (time / 60000) % 60;
            return (time / 3600000) % 24;
        } else if ((time / 60) / 1000 > 0) {
            int i3 = (time / 1000) % 60;
            int i4 = (time / 60000) % 60;
            return 0;
        } else {
            int iHour = (time / 1000) % 60;
            return 0;
        }
    }
}
