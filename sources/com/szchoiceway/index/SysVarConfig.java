package com.szchoiceway.index;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class SysVarConfig {
    public static final String TAG = "SysVarConfig";
    private SysFatPara m_SysFatPara = new SysFatPara();

    public SysVarConfig() {
        Log.i(TAG, "***SysVarConfig***");
        init();
    }

    public void init() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file_cfg_dir = new File("/data/local/");
        if (!file_cfg_dir.exists() && !file_cfg_dir.isDirectory()) {
            System.out.println("配置文件夹Remote_Meeting不存在!");
            if (file_cfg_dir.mkdirs()) {
                System.out.println("创建文件夹成功!");
            } else {
                System.out.println("创建文件夹失败!");
            }
        }
        File file_cfg = new File(file_cfg_dir.getPath(), "cfg.xml");
        if (!file_cfg.exists()) {
            System.out.println("配置文件cfg.xml不存在!");
            try {
                file_cfg.createNewFile();
                System.out.println("创建文件cfg.xml成功!");
                try {
                    FileOutputStream out = new FileOutputStream(file_cfg);
                    this.m_SysFatPara.InitFatPara();
                    out.write(produce_xml_string(this.m_SysFatPara).getBytes());
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        try {
            NodeList node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(file_cfg)).getDocumentElement().getChildNodes();
            SysFatPara sysFatPara = this.m_SysFatPara;
            SysFatPara.dwSize = Integer.parseInt(node.item(0).getFirstChild().getNodeValue());
            SysFatPara sysFatPara2 = this.m_SysFatPara;
            SysFatPara.idwVersion = Integer.parseInt(node.item(1).getFirstChild().getNodeValue());
            SysFatPara sysFatPara3 = this.m_SysFatPara;
            SysFatPara.iCurrLangIdx = Integer.parseInt(node.item(2).getFirstChild().getNodeValue());
            SysFatPara sysFatPara4 = this.m_SysFatPara;
            SysFatPara.dwMainMenuFuncs0 = Integer.parseInt(node.item(3).getFirstChild().getNodeValue());
            SysFatPara sysFatPara5 = this.m_SysFatPara;
            SysFatPara.dwMainMenuFuncs1 = Integer.parseInt(node.item(4).getFirstChild().getNodeValue());
            SysFatPara sysFatPara6 = this.m_SysFatPara;
            SysFatPara.dwMainMenuFuncsReserve = Integer.parseInt(node.item(5).getFirstChild().getNodeValue());
            SysFatPara sysFatPara7 = this.m_SysFatPara;
            SysFatPara.iCanbustype = Integer.parseInt(node.item(6).getFirstChild().getNodeValue());
            SysFatPara sysFatPara8 = this.m_SysFatPara;
            SysFatPara.iCarstype_ID = Integer.parseInt(node.item(7).getFirstChild().getNodeValue());
            SysFatPara sysFatPara9 = this.m_SysFatPara;
            SysFatPara.iCarCanbusName_ID = Integer.parseInt(node.item(8).getFirstChild().getNodeValue());
            SysFatPara sysFatPara10 = this.m_SysFatPara;
            SysFatPara.iTVType = Integer.parseInt(node.item(9).getFirstChild().getNodeValue());
            SysFatPara sysFatPara11 = this.m_SysFatPara;
            SysFatPara.iRadioZone = Integer.parseInt(node.item(10).getFirstChild().getNodeValue());
            SysFatPara sysFatPara12 = this.m_SysFatPara;
            SysFatPara.iFtyLan = Integer.parseInt(node.item(11).getFirstChild().getNodeValue());
            SysFatPara sysFatPara13 = this.m_SysFatPara;
            SysFatPara.iPanelType = Integer.parseInt(node.item(12).getFirstChild().getNodeValue());
            SysFatPara sysFatPara14 = this.m_SysFatPara;
            SysFatPara.iACCVolMemory = Integer.parseInt(node.item(13).getFirstChild().getNodeValue());
            SysFatPara sysFatPara15 = this.m_SysFatPara;
            SysFatPara.iBackcarSnd = Integer.parseInt(node.item(14).getFirstChild().getNodeValue());
            SysFatPara sysFatPara16 = this.m_SysFatPara;
            SysFatPara.iBtnEncoder = Integer.parseInt(node.item(15).getFirstChild().getNodeValue());
            SysFatPara sysFatPara17 = this.m_SysFatPara;
            SysFatPara.iReleaseSysMemory = Integer.parseInt(node.item(16).getFirstChild().getNodeValue());
            SysFatPara sysFatPara18 = this.m_SysFatPara;
            SysFatPara.iLCDInch = Integer.parseInt(node.item(17).getFirstChild().getNodeValue());
            SysFatPara sysFatPara19 = this.m_SysFatPara;
            SysFatPara.iBTModuleType = Integer.parseInt(node.item(18).getFirstChild().getNodeValue());
            SysFatPara sysFatPara20 = this.m_SysFatPara;
            SysFatPara.iTimerZone = Integer.parseInt(node.item(19).getFirstChild().getNodeValue());
            SysFatPara sysFatPara21 = this.m_SysFatPara;
            SysFatPara.iRDSOnOff = Integer.parseInt(node.item(20).getFirstChild().getNodeValue());
            SysFatPara sysFatPara22 = this.m_SysFatPara;
            SysFatPara.iARMVolShow = Integer.parseInt(node.item(21).getFirstChild().getNodeValue());
            SysFatPara sysFatPara23 = this.m_SysFatPara;
            SysFatPara.iSelectDFOS = Integer.parseInt(node.item(22).getFirstChild().getNodeValue());
            SysFatPara sysFatPara24 = this.m_SysFatPara;
            SysFatPara.iBackControl_T113 = Integer.parseInt(node.item(23).getFirstChild().getNodeValue());
            SysFatPara sysFatPara25 = this.m_SysFatPara;
            SysFatPara.iShowTrajectory = Integer.parseInt(node.item(24).getFirstChild().getNodeValue());
            SysFatPara sysFatPara26 = this.m_SysFatPara;
            SysFatPara.iDTVType = Integer.parseInt(node.item(25).getFirstChild().getNodeValue());
            SysFatPara sysFatPara27 = this.m_SysFatPara;
            SysFatPara.iIC_37534 = Integer.parseInt(node.item(26).getFirstChild().getNodeValue());
            SysFatPara sysFatPara28 = this.m_SysFatPara;
            SysFatPara.iBackcarMirror = Integer.parseInt(node.item(27).getFirstChild().getNodeValue());
            SysFatPara sysFatPara29 = this.m_SysFatPara;
            SysFatPara.iKeyDownSound = Integer.parseInt(node.item(28).getFirstChild().getNodeValue());
            SysFatPara sysFatPara30 = this.m_SysFatPara;
            SysFatPara.iCheckBreak = Integer.parseInt(node.item(29).getFirstChild().getNodeValue());
            SysFatPara sysFatPara31 = this.m_SysFatPara;
            SysFatPara.iBTSmall = Integer.parseInt(node.item(30).getFirstChild().getNodeValue());
            SysFatPara sysFatPara32 = this.m_SysFatPara;
            SysFatPara.iNotWifi = Integer.parseInt(node.item(31).getFirstChild().getNodeValue());
            SysFatPara sysFatPara33 = this.m_SysFatPara;
            SysFatPara.iBTModule_ID = Integer.parseInt(node.item(32).getFirstChild().getNodeValue());
            SysFatPara sysFatPara34 = this.m_SysFatPara;
            SysFatPara.iBTType_ID = Integer.parseInt(node.item(33).getFirstChild().getNodeValue());
            SysFatPara sysFatPara35 = this.m_SysFatPara;
            SysFatPara.iDVR2 = Integer.parseInt(node.item(34).getFirstChild().getNodeValue());
            SysFatPara sysFatPara36 = this.m_SysFatPara;
            SysFatPara.iBackcarReduceSnd = Integer.parseInt(node.item(35).getFirstChild().getNodeValue());
            SysFatPara sysFatPara37 = this.m_SysFatPara;
            SysFatPara.iSelFrontCameraMode = Integer.parseInt(node.item(36).getFirstChild().getNodeValue());
            SysFatPara sysFatPara38 = this.m_SysFatPara;
            SysFatPara.iBackcarAutoRunFCAM = Integer.parseInt(node.item(37).getFirstChild().getNodeValue());
            SysFatPara sysFatPara39 = this.m_SysFatPara;
            SysFatPara.iFactoryAutoGPS = Integer.parseInt(node.item(38).getFirstChild().getNodeValue());
            SysFatPara sysFatPara40 = this.m_SysFatPara;
            SysFatPara.iAUSAKDVRMode = Integer.parseInt(node.item(39).getFirstChild().getNodeValue());
            SysFatPara sysFatPara41 = this.m_SysFatPara;
            SysFatPara.iXinXinRongDVDMode = Integer.parseInt(node.item(40).getFirstChild().getNodeValue());
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        try {
            Runtime.getRuntime().exec("chmod 777 /data/local/cfg.xml");
        } catch (IOException e4) {
            e4.printStackTrace();
        }
    }

    private String produce_xml_string(SysFatPara info) {
        StringWriter stringWriter = new StringWriter();
        try {
            XmlSerializer xmlSerializer = XmlPullParserFactory.newInstance().newSerializer();
            xmlSerializer.setOutput(stringWriter);
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag((String) null, "config");
            xmlSerializer.startTag((String) null, "dwSize");
            xmlSerializer.text(Integer.toString(SysFatPara.dwSize));
            xmlSerializer.endTag((String) null, "dwSize");
            xmlSerializer.startTag((String) null, "Version");
            xmlSerializer.text(Integer.toString(SysFatPara.idwVersion));
            xmlSerializer.endTag((String) null, "Version");
            xmlSerializer.startTag((String) null, "CurrLangIdx");
            xmlSerializer.text(Integer.toString(SysFatPara.iCurrLangIdx));
            xmlSerializer.endTag((String) null, "CurrLangIdx");
            xmlSerializer.startTag((String) null, "MainMenuFuncs0");
            xmlSerializer.text(Integer.toString(SysFatPara.dwMainMenuFuncs0));
            xmlSerializer.endTag((String) null, "MainMenuFuncs0");
            xmlSerializer.startTag((String) null, "MainMenuFuncs1");
            xmlSerializer.text(Integer.toString(SysFatPara.dwMainMenuFuncs1));
            xmlSerializer.endTag((String) null, "MainMenuFuncs1");
            xmlSerializer.startTag((String) null, "MainMenuFuncsReserve");
            xmlSerializer.text(Integer.toString(SysFatPara.dwMainMenuFuncsReserve));
            xmlSerializer.endTag((String) null, "MainMenuFuncsReserve");
            xmlSerializer.startTag((String) null, "Canbustype");
            xmlSerializer.text(Integer.toString(SysFatPara.iCanbustype));
            xmlSerializer.endTag((String) null, "Canbustype");
            xmlSerializer.startTag((String) null, "Carstype_ID");
            xmlSerializer.text(Integer.toString(SysFatPara.iCarstype_ID));
            xmlSerializer.endTag((String) null, "Carstype_ID");
            xmlSerializer.startTag((String) null, "CarCanbusName_ID");
            xmlSerializer.text(Integer.toString(SysFatPara.iCarCanbusName_ID));
            xmlSerializer.endTag((String) null, "CarCanbusName_ID");
            xmlSerializer.startTag((String) null, "TVType");
            xmlSerializer.text(Integer.toString(SysFatPara.iTVType));
            xmlSerializer.endTag((String) null, "TVType");
            xmlSerializer.startTag((String) null, "RadioZone");
            xmlSerializer.text(Integer.toString(SysFatPara.iRadioZone));
            xmlSerializer.endTag((String) null, "RadioZone");
            xmlSerializer.startTag((String) null, "FtyLan");
            xmlSerializer.text(Integer.toString(SysFatPara.iFtyLan));
            xmlSerializer.endTag((String) null, "FtyLan");
            xmlSerializer.startTag((String) null, "PanelType");
            xmlSerializer.text(Integer.toString(SysFatPara.iPanelType));
            xmlSerializer.endTag((String) null, "PanelType");
            xmlSerializer.startTag((String) null, "ACCVolMemory");
            xmlSerializer.text(Integer.toString(SysFatPara.iACCVolMemory));
            xmlSerializer.endTag((String) null, "ACCVolMemory");
            xmlSerializer.startTag((String) null, "BackcarSnd");
            xmlSerializer.text(Integer.toString(SysFatPara.iBackcarSnd));
            xmlSerializer.endTag((String) null, "BackcarSnd");
            xmlSerializer.startTag((String) null, "BtnEncoder");
            xmlSerializer.text(Integer.toString(SysFatPara.iBtnEncoder));
            xmlSerializer.endTag((String) null, "BtnEncoder");
            xmlSerializer.startTag((String) null, "ReleaseSysMemory");
            xmlSerializer.text(Integer.toString(SysFatPara.iReleaseSysMemory));
            xmlSerializer.endTag((String) null, "ReleaseSysMemory");
            xmlSerializer.startTag((String) null, "LCDInch");
            xmlSerializer.text(Integer.toString(SysFatPara.iLCDInch));
            xmlSerializer.endTag((String) null, "LCDInch");
            xmlSerializer.startTag((String) null, "BTModuleType");
            xmlSerializer.text(Integer.toString(SysFatPara.iBTModuleType));
            xmlSerializer.endTag((String) null, "BTModuleType");
            xmlSerializer.startTag((String) null, "TimerZone");
            xmlSerializer.text(Integer.toString(SysFatPara.iTimerZone));
            xmlSerializer.endTag((String) null, "TimerZone");
            xmlSerializer.startTag((String) null, "RDSOnOff");
            xmlSerializer.text(Integer.toString(SysFatPara.iRDSOnOff));
            xmlSerializer.endTag((String) null, "RDSOnOff");
            xmlSerializer.startTag((String) null, "ARMVolShow");
            xmlSerializer.text(Integer.toString(SysFatPara.iARMVolShow));
            xmlSerializer.endTag((String) null, "ARMVolShow");
            xmlSerializer.startTag((String) null, "SelectDFOS");
            xmlSerializer.text(Integer.toString(SysFatPara.iSelectDFOS));
            xmlSerializer.endTag((String) null, "SelectDFOS");
            xmlSerializer.startTag((String) null, "BackControl_T113");
            xmlSerializer.text(Integer.toString(SysFatPara.iBackControl_T113));
            xmlSerializer.endTag((String) null, "BackControl_T113");
            xmlSerializer.startTag((String) null, "ShowTrajectory");
            xmlSerializer.text(Integer.toString(SysFatPara.iShowTrajectory));
            xmlSerializer.endTag((String) null, "ShowTrajectory");
            xmlSerializer.startTag((String) null, "DTVType");
            xmlSerializer.text(Integer.toString(SysFatPara.iDTVType));
            xmlSerializer.endTag((String) null, "DTVType");
            xmlSerializer.startTag((String) null, "IC_37534");
            xmlSerializer.text(Integer.toString(SysFatPara.iIC_37534));
            xmlSerializer.endTag((String) null, "IC_37534");
            xmlSerializer.startTag((String) null, "BackcarMirror");
            xmlSerializer.text(Integer.toString(SysFatPara.iBackcarMirror));
            xmlSerializer.endTag((String) null, "BackcarMirror");
            xmlSerializer.startTag((String) null, "KeyDownSound");
            xmlSerializer.text(Integer.toString(SysFatPara.iKeyDownSound));
            xmlSerializer.endTag((String) null, "KeyDownSound");
            xmlSerializer.startTag((String) null, "CheckBreak");
            xmlSerializer.text(Integer.toString(SysFatPara.iCheckBreak));
            xmlSerializer.endTag((String) null, "CheckBreak");
            xmlSerializer.startTag((String) null, "BTSmall");
            xmlSerializer.text(Integer.toString(SysFatPara.iBTSmall));
            xmlSerializer.endTag((String) null, "BTSmall");
            xmlSerializer.startTag((String) null, "NotWifi");
            xmlSerializer.text(Integer.toString(SysFatPara.iNotWifi));
            xmlSerializer.endTag((String) null, "NotWifi");
            xmlSerializer.startTag((String) null, "BTModule_ID");
            xmlSerializer.text(Integer.toString(SysFatPara.iBTModule_ID));
            xmlSerializer.endTag((String) null, "BTModule_ID");
            xmlSerializer.startTag((String) null, "BTType_ID");
            xmlSerializer.text(Integer.toString(SysFatPara.iBTType_ID));
            xmlSerializer.endTag((String) null, "BTType_ID");
            xmlSerializer.startTag((String) null, "DVR2");
            xmlSerializer.text(Integer.toString(SysFatPara.iDVR2));
            xmlSerializer.endTag((String) null, "DVR2");
            xmlSerializer.startTag((String) null, "BackcarReduceSnd");
            xmlSerializer.text(Integer.toString(SysFatPara.iBackcarReduceSnd));
            xmlSerializer.endTag((String) null, "BackcarReduceSnd");
            xmlSerializer.startTag((String) null, "SelFrontCameraMode");
            xmlSerializer.text(Integer.toString(SysFatPara.iSelFrontCameraMode));
            xmlSerializer.endTag((String) null, "SelFrontCameraMode");
            xmlSerializer.startTag((String) null, "BackcarAutoRunFCAM");
            xmlSerializer.text(Integer.toString(SysFatPara.iBackcarAutoRunFCAM));
            xmlSerializer.endTag((String) null, "BackcarAutoRunFCAM");
            xmlSerializer.startTag((String) null, "FactoryAutoGPS");
            xmlSerializer.text(Integer.toString(SysFatPara.iFactoryAutoGPS));
            xmlSerializer.endTag((String) null, "FactoryAutoGPS");
            xmlSerializer.startTag((String) null, "AUSAKDVRMode");
            xmlSerializer.text(Integer.toString(SysFatPara.iAUSAKDVRMode));
            xmlSerializer.endTag((String) null, "AUSAKDVRMode");
            xmlSerializer.startTag((String) null, "XinXinRongDVDMode");
            xmlSerializer.text(Integer.toString(SysFatPara.iXinXinRongDVDMode));
            xmlSerializer.endTag((String) null, "XinXinRongDVDMode");
            xmlSerializer.endTag((String) null, "config");
            xmlSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    public SysFatPara GetFatPara() {
        return this.m_SysFatPara;
    }

    public void SaveFatPara(SysFatPara sysFatPara) {
        Log.i(TAG, "****SaveFatPara****");
        this.m_SysFatPara = sysFatPara;
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file_cfg_dir = new File("/data/local/");
        if (!file_cfg_dir.exists() && !file_cfg_dir.isDirectory()) {
            System.out.println("配置文件夹Remote_Meeting不存在!");
            if (file_cfg_dir.mkdirs()) {
                System.out.println("创建文件夹成功!");
            } else {
                System.out.println("创建文件夹失败!");
            }
        }
        File file_cfg = new File(file_cfg_dir.getPath(), "cfg.xml");
        if (!file_cfg.exists()) {
            System.out.println("配置文件cfg.xml不存在!");
            try {
                file_cfg.createNewFile();
                System.out.println("创建文件cfg.xml成功!");
                try {
                    FileOutputStream out = new FileOutputStream(file_cfg);
                    String str = produce_xml_string(this.m_SysFatPara);
                    StringBuilder append = new StringBuilder().append("m_SysFatPara.iCarCanbusName_ID=== ");
                    SysFatPara sysFatPara2 = this.m_SysFatPara;
                    Log.i(TAG, append.append(SysFatPara.iCarCanbusName_ID).toString());
                    Log.i(TAG, "****SaveFatPara****");
                    out.write(str.getBytes());
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } else {
            try {
                FileOutputStream out2 = new FileOutputStream(file_cfg);
                String str2 = produce_xml_string(this.m_SysFatPara);
                StringBuilder append2 = new StringBuilder().append("m_SysFatPara.iCarCanbusName_ID=== ");
                SysFatPara sysFatPara3 = this.m_SysFatPara;
                Log.i(TAG, append2.append(SysFatPara.iCarCanbusName_ID).toString());
                Log.i(TAG, "2****SaveFatPara****2");
                out2.write(str2.getBytes());
                out2.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
        try {
            Runtime.getRuntime().exec("chmod 777 /data/local/cfg.xml");
        } catch (IOException e4) {
            e4.printStackTrace();
        }
    }
}
