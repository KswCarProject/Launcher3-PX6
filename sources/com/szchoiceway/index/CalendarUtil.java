package com.szchoiceway.index;

import org.apache.http.HttpStatus;
import org.apache.http.message.TokenParser;

public class CalendarUtil {
    private static String[] animalNames = {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
    private static int baseChineseDate = 11;
    private static int baseChineseMonth = 11;
    private static int baseChineseYear = 4597;
    private static int baseDate = 1;
    private static int baseIndex = 0;
    private static int baseMonth = 1;
    private static int baseYear = 1901;
    private static int[] bigLeapMonthYears = {6, 14, 19, 25, 33, 36, 38, 41, 44, 52, 55, 79, 117, 136, 147, 150, 155, 158, 185, 193};
    private static String[] branchNames = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    private static String[] chineseMonthNames = {"正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊"};
    private static char[] chineseMonths;
    private static char[] daysInGregorianMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static final String[] daysOfMonth = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    private static String[] monthNames = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    private static char[][] principleTermMap = {new char[]{21, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 20, 20, 20, 20, 20, 19, 20, 20, 20, 19, 19, 20}, new char[]{20, 19, 19, 20, 20, 19, 19, 19, 19, 19, 19, 19, 19, 18, 19, 19, 19, 18, 18, 19, 19, 18, 18, 18, 18, 18, 18, 18}, new char[]{21, 21, 21, 22, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 20, 20, 20, 20, 19, 20, 20, 20, 20}, new char[]{20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 20, 20, 20, 20, 19, 20, 20, 20, 19, 19, 20, 20, 19, 19, 19, 20, 20}, new char[]{21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 21}, new char[]{22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 21}, new char[]{23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 23}, new char[]{23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23}, new char[]{23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23}, new char[]{24, 24, 24, 24, 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 23}, new char[]{23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 22}, new char[]{22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21, 22}};
    private static String[] principleTermNames = {"雨水", "春分", "谷雨", "夏满", "夏至", "大暑", "处暑", "秋分", "霜降", "小雪", "冬至", "大寒"};
    private static char[][] principleTermYear = {new char[]{TokenParser.CR, '-', 'Q', 'q', 149, 185, 201}, new char[]{21, '9', ']', '}', 161, 193, 201}, new char[]{21, '8', 'X', 'x', 152, 188, 200, 201}, new char[]{21, '1', 'Q', 't', 144, 176, 200, 201}, new char[]{17, '1', 'M', 'p', 140, 168, 200, 201}, new char[]{28, '<', 'X', 't', 148, 180, 200, 201}, new char[]{25, '5', 'T', 'p', 144, 172, 200, 201}, new char[]{29, '9', 'Y', 'x', 148, 180, 200, 201}, new char[]{17, '-', 'I', 'l', 140, 168, 200, 201}, new char[]{28, '<', TokenParser.ESCAPE, '|', 160, 192, 200, 201}, new char[]{16, ',', 'P', 'p', 148, 180, 200, 201}, new char[]{17, '5', 'X', 'x', 156, 188, 200, 201}};
    private static char[][] sectionalTermMap = {new char[]{7, 6, 6, 6, 6, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 5, 5, 5, 5, 5, 4, 5, 5}, new char[]{5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 4, 4, 3, 3, 4, 4, 3, 3, 3}, new char[]{6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 5}, new char[]{5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4, 4, 5, 4, 4, 4, 4, 5}, new char[]{6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 5}, new char[]{6, 6, 7, 7, 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 5}, new char[]{7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6, 7, 7, 6, 6, 6, 7, 7}, new char[]{8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6, 7, 7, 7}, new char[]{8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 7}, new char[]{9, 9, 9, 9, 8, 9, 9, 9, 8, 8, 9, 9, 8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 8}, new char[]{8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6, 7, 7, 7}, new char[]{7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6, 7, 7, 6, 6, 6, 7, 7}};
    private static String[] sectionalTermNames = {"立春", "惊蛰", "清明", "立夏", "芒种", "小暑", "立秋", "白露", "寒露", "立冬", "大雪", "小寒"};
    private static char[][] sectionalTermYear = {new char[]{TokenParser.CR, '1', 'U', 'u', 149, 185, 201, 250, 250}, new char[]{TokenParser.CR, '-', 'Q', 'u', 149, 185, 201, 250, 250}, new char[]{TokenParser.CR, '0', 'T', 'p', 148, 184, 200, 201, 250}, new char[]{TokenParser.CR, '-', 'L', 'l', 140, 172, 200, 201, 250}, new char[]{TokenParser.CR, ',', 'H', 'h', 132, 168, 200, 201, 250}, new char[]{5, '!', 'D', '`', '|', 152, 188, 200, 201}, new char[]{29, '9', 'U', 'x', 148, 176, 200, 201, 250}, new char[]{TokenParser.CR, '0', 'L', 'h', 132, 168, 196, 200, 201}, new char[]{25, '<', 'X', 'x', 148, 184, 200, 201, 250}, new char[]{16, ',', 'L', 'l', 144, 172, 200, 201, 250}, new char[]{28, '<', TokenParser.ESCAPE, '|', 160, 192, 200, 201, 250}, new char[]{17, '5', 'U', '|', 156, 188, 200, 201, 250}};
    private static String[] stemNames = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private int chineseDate;
    private int chineseMonth;
    private int chineseYear;
    private int dayOfWeek;
    private int dayOfYear;
    private String[] daysOfAlmanac = {"初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"};
    private int gregorianDate;
    private int gregorianMonth;
    private int gregorianYear;
    private boolean isGregorianLeap;
    private String[] monthOfAlmanac = {"正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "冬月", "腊月"};
    private int principleTerm;
    private int sectionalTerm;

    static {
        char[] cArr = new char[HttpStatus.SC_PAYMENT_REQUIRED];
        // fill-array-data instruction
        cArr[0] = 0;
        cArr[1] = 4;
        cArr[2] = 173;
        cArr[3] = 8;
        cArr[4] = 90;
        cArr[5] = 1;
        cArr[6] = 213;
        cArr[7] = 84;
        cArr[8] = 180;
        cArr[9] = 9;
        cArr[10] = 100;
        cArr[11] = 5;
        cArr[12] = 89;
        cArr[13] = 69;
        cArr[14] = 149;
        cArr[15] = 10;
        cArr[16] = 166;
        cArr[17] = 4;
        cArr[18] = 85;
        cArr[19] = 36;
        cArr[20] = 173;
        cArr[21] = 8;
        cArr[22] = 90;
        cArr[23] = 98;
        cArr[24] = 218;
        cArr[25] = 4;
        cArr[26] = 180;
        cArr[27] = 5;
        cArr[28] = 180;
        cArr[29] = 85;
        cArr[30] = 82;
        cArr[31] = 13;
        cArr[32] = 148;
        cArr[33] = 10;
        cArr[34] = 74;
        cArr[35] = 42;
        cArr[36] = 86;
        cArr[37] = 2;
        cArr[38] = 109;
        cArr[39] = 113;
        cArr[40] = 109;
        cArr[41] = 1;
        cArr[42] = 218;
        cArr[43] = 2;
        cArr[44] = 210;
        cArr[45] = 82;
        cArr[46] = 169;
        cArr[47] = 5;
        cArr[48] = 73;
        cArr[49] = 13;
        cArr[50] = 42;
        cArr[51] = 69;
        cArr[52] = 43;
        cArr[53] = 9;
        cArr[54] = 86;
        cArr[55] = 1;
        cArr[56] = 181;
        cArr[57] = 32;
        cArr[58] = 109;
        cArr[59] = 1;
        cArr[60] = 89;
        cArr[61] = 105;
        cArr[62] = 212;
        cArr[63] = 10;
        cArr[64] = 168;
        cArr[65] = 5;
        cArr[66] = 169;
        cArr[67] = 86;
        cArr[68] = 165;
        cArr[69] = 4;
        cArr[70] = 43;
        cArr[71] = 9;
        cArr[72] = 158;
        cArr[73] = 56;
        cArr[74] = 182;
        cArr[75] = 8;
        cArr[76] = 236;
        cArr[77] = 116;
        cArr[78] = 108;
        cArr[79] = 5;
        cArr[80] = 212;
        cArr[81] = 10;
        cArr[82] = 228;
        cArr[83] = 106;
        cArr[84] = 82;
        cArr[85] = 5;
        cArr[86] = 149;
        cArr[87] = 10;
        cArr[88] = 90;
        cArr[89] = 66;
        cArr[90] = 91;
        cArr[91] = 4;
        cArr[92] = 182;
        cArr[93] = 4;
        cArr[94] = 180;
        cArr[95] = 34;
        cArr[96] = 106;
        cArr[97] = 5;
        cArr[98] = 82;
        cArr[99] = 117;
        cArr[100] = 201;
        cArr[101] = 10;
        cArr[102] = 82;
        cArr[103] = 5;
        cArr[104] = 53;
        cArr[105] = 85;
        cArr[106] = 77;
        cArr[107] = 10;
        cArr[108] = 90;
        cArr[109] = 2;
        cArr[110] = 93;
        cArr[111] = 49;
        cArr[112] = 181;
        cArr[113] = 2;
        cArr[114] = 106;
        cArr[115] = 138;
        cArr[116] = 104;
        cArr[117] = 5;
        cArr[118] = 169;
        cArr[119] = 10;
        cArr[120] = 138;
        cArr[121] = 106;
        cArr[122] = 42;
        cArr[123] = 5;
        cArr[124] = 45;
        cArr[125] = 9;
        cArr[126] = 170;
        cArr[127] = 72;
        cArr[128] = 90;
        cArr[129] = 1;
        cArr[130] = 181;
        cArr[131] = 9;
        cArr[132] = 176;
        cArr[133] = 57;
        cArr[134] = 100;
        cArr[135] = 5;
        cArr[136] = 37;
        cArr[137] = 117;
        cArr[138] = 149;
        cArr[139] = 10;
        cArr[140] = 150;
        cArr[141] = 4;
        cArr[142] = 77;
        cArr[143] = 84;
        cArr[144] = 173;
        cArr[145] = 4;
        cArr[146] = 218;
        cArr[147] = 4;
        cArr[148] = 212;
        cArr[149] = 68;
        cArr[150] = 180;
        cArr[151] = 5;
        cArr[152] = 84;
        cArr[153] = 133;
        cArr[154] = 82;
        cArr[155] = 13;
        cArr[156] = 146;
        cArr[157] = 10;
        cArr[158] = 86;
        cArr[159] = 106;
        cArr[160] = 86;
        cArr[161] = 2;
        cArr[162] = 109;
        cArr[163] = 2;
        cArr[164] = 106;
        cArr[165] = 65;
        cArr[166] = 218;
        cArr[167] = 2;
        cArr[168] = 178;
        cArr[169] = 161;
        cArr[170] = 169;
        cArr[171] = 5;
        cArr[172] = 73;
        cArr[173] = 13;
        cArr[174] = 10;
        cArr[175] = 109;
        cArr[176] = 42;
        cArr[177] = 9;
        cArr[178] = 86;
        cArr[179] = 1;
        cArr[180] = 173;
        cArr[181] = 80;
        cArr[182] = 109;
        cArr[183] = 1;
        cArr[184] = 217;
        cArr[185] = 2;
        cArr[186] = 209;
        cArr[187] = 58;
        cArr[188] = 168;
        cArr[189] = 5;
        cArr[190] = 41;
        cArr[191] = 133;
        cArr[192] = 165;
        cArr[193] = 12;
        cArr[194] = 42;
        cArr[195] = 9;
        cArr[196] = 150;
        cArr[197] = 84;
        cArr[198] = 182;
        cArr[199] = 8;
        cArr[200] = 108;
        cArr[201] = 9;
        cArr[202] = 100;
        cArr[203] = 69;
        cArr[204] = 212;
        cArr[205] = 10;
        cArr[206] = 164;
        cArr[207] = 5;
        cArr[208] = 81;
        cArr[209] = 37;
        cArr[210] = 149;
        cArr[211] = 10;
        cArr[212] = 42;
        cArr[213] = 114;
        cArr[214] = 91;
        cArr[215] = 4;
        cArr[216] = 182;
        cArr[217] = 4;
        cArr[218] = 172;
        cArr[219] = 82;
        cArr[220] = 106;
        cArr[221] = 5;
        cArr[222] = 210;
        cArr[223] = 10;
        cArr[224] = 162;
        cArr[225] = 74;
        cArr[226] = 74;
        cArr[227] = 5;
        cArr[228] = 85;
        cArr[229] = 148;
        cArr[230] = 45;
        cArr[231] = 10;
        cArr[232] = 90;
        cArr[233] = 2;
        cArr[234] = 117;
        cArr[235] = 97;
        cArr[236] = 181;
        cArr[237] = 2;
        cArr[238] = 106;
        cArr[239] = 3;
        cArr[240] = 97;
        cArr[241] = 69;
        cArr[242] = 169;
        cArr[243] = 10;
        cArr[244] = 74;
        cArr[245] = 5;
        cArr[246] = 37;
        cArr[247] = 37;
        cArr[248] = 45;
        cArr[249] = 9;
        cArr[250] = 154;
        cArr[251] = 104;
        cArr[252] = 218;
        cArr[253] = 8;
        cArr[254] = 180;
        cArr[255] = 9;
        cArr[256] = 168;
        cArr[257] = 89;
        cArr[258] = 84;
        cArr[259] = 3;
        cArr[260] = 165;
        cArr[261] = 10;
        cArr[262] = 145;
        cArr[263] = 58;
        cArr[264] = 150;
        cArr[265] = 4;
        cArr[266] = 173;
        cArr[267] = 176;
        cArr[268] = 173;
        cArr[269] = 4;
        cArr[270] = 218;
        cArr[271] = 4;
        cArr[272] = 244;
        cArr[273] = 98;
        cArr[274] = 180;
        cArr[275] = 5;
        cArr[276] = 84;
        cArr[277] = 11;
        cArr[278] = 68;
        cArr[279] = 93;
        cArr[280] = 82;
        cArr[281] = 10;
        cArr[282] = 149;
        cArr[283] = 4;
        cArr[284] = 85;
        cArr[285] = 34;
        cArr[286] = 109;
        cArr[287] = 2;
        cArr[288] = 90;
        cArr[289] = 113;
        cArr[290] = 218;
        cArr[291] = 2;
        cArr[292] = 170;
        cArr[293] = 5;
        cArr[294] = 178;
        cArr[295] = 85;
        cArr[296] = 73;
        cArr[297] = 11;
        cArr[298] = 74;
        cArr[299] = 10;
        cArr[300] = 45;
        cArr[301] = 57;
        cArr[302] = 54;
        cArr[303] = 1;
        cArr[304] = 109;
        cArr[305] = 128;
        cArr[306] = 109;
        cArr[307] = 1;
        cArr[308] = 217;
        cArr[309] = 2;
        cArr[310] = 233;
        cArr[311] = 106;
        cArr[312] = 168;
        cArr[313] = 5;
        cArr[314] = 41;
        cArr[315] = 11;
        cArr[316] = 154;
        cArr[317] = 76;
        cArr[318] = 170;
        cArr[319] = 8;
        cArr[320] = 182;
        cArr[321] = 8;
        cArr[322] = 180;
        cArr[323] = 56;
        cArr[324] = 108;
        cArr[325] = 9;
        cArr[326] = 84;
        cArr[327] = 117;
        cArr[328] = 212;
        cArr[329] = 10;
        cArr[330] = 164;
        cArr[331] = 5;
        cArr[332] = 69;
        cArr[333] = 85;
        cArr[334] = 149;
        cArr[335] = 10;
        cArr[336] = 154;
        cArr[337] = 4;
        cArr[338] = 85;
        cArr[339] = 68;
        cArr[340] = 181;
        cArr[341] = 4;
        cArr[342] = 106;
        cArr[343] = 130;
        cArr[344] = 106;
        cArr[345] = 5;
        cArr[346] = 210;
        cArr[347] = 10;
        cArr[348] = 146;
        cArr[349] = 106;
        cArr[350] = 74;
        cArr[351] = 5;
        cArr[352] = 85;
        cArr[353] = 10;
        cArr[354] = 42;
        cArr[355] = 74;
        cArr[356] = 90;
        cArr[357] = 2;
        cArr[358] = 181;
        cArr[359] = 2;
        cArr[360] = 178;
        cArr[361] = 49;
        cArr[362] = 105;
        cArr[363] = 3;
        cArr[364] = 49;
        cArr[365] = 115;
        cArr[366] = 169;
        cArr[367] = 10;
        cArr[368] = 74;
        cArr[369] = 5;
        cArr[370] = 45;
        cArr[371] = 85;
        cArr[372] = 45;
        cArr[373] = 9;
        cArr[374] = 90;
        cArr[375] = 1;
        cArr[376] = 213;
        cArr[377] = 72;
        cArr[378] = 180;
        cArr[379] = 9;
        cArr[380] = 104;
        cArr[381] = 137;
        cArr[382] = 84;
        cArr[383] = 11;
        cArr[384] = 164;
        cArr[385] = 10;
        cArr[386] = 165;
        cArr[387] = 106;
        cArr[388] = 149;
        cArr[389] = 4;
        cArr[390] = 173;
        cArr[391] = 8;
        cArr[392] = 106;
        cArr[393] = 68;
        cArr[394] = 218;
        cArr[395] = 4;
        cArr[396] = 116;
        cArr[397] = 5;
        cArr[398] = 176;
        cArr[399] = 37;
        cArr[400] = 84;
        cArr[401] = 3;
        chineseMonths = cArr;
    }

    public CalendarUtil() {
        setGregorian(1901, 1, 1);
    }

    public String getChineseDay(int y, int m, int d) {
        CalendarUtil c = new CalendarUtil();
        c.setGregorian(y, m, d);
        c.computeChineseFields();
        c.computeSolarTerms();
        return this.daysOfAlmanac[c.getChineseDate() - 1];
    }

    public String getChineseMonth(int y, int m, int d) {
        setGregorian(y, m, d);
        computeChineseFields();
        computeSolarTerms();
        int cd = getChineseMonth();
        if (cd < 1 || cd > 29) {
            cd = 1;
        }
        return this.monthOfAlmanac[cd - 1];
    }

    public void setGregorian(int y, int m, int d) {
        this.gregorianYear = y;
        this.gregorianMonth = m;
        this.gregorianDate = d;
        this.isGregorianLeap = isGregorianLeapYear(y);
        this.dayOfYear = dayOfYear(y, m, d);
        this.dayOfWeek = dayOfWeek(y, m, d);
        this.chineseYear = 0;
        this.chineseMonth = 0;
        this.chineseDate = 0;
        this.sectionalTerm = 0;
        this.principleTerm = 0;
    }

    public static boolean isGregorianLeapYear(int year) {
        boolean isLeap = false;
        if (year % 4 == 0) {
            isLeap = true;
        }
        if (year % 100 == 0) {
            isLeap = false;
        }
        if (year % HttpStatus.SC_BAD_REQUEST == 0) {
            return true;
        }
        return isLeap;
    }

    public static int daysInGregorianMonth(int y, int m) {
        char d = daysInGregorianMonth[m - 1];
        if (m != 2 || !isGregorianLeapYear(y)) {
            return d;
        }
        return d + 1;
    }

    public static int dayOfYear(int y, int m, int d) {
        int c = 0;
        for (int i = 1; i < m; i++) {
            c += daysInGregorianMonth(y, i);
        }
        return c + d;
    }

    public static int dayOfWeek(int y, int m, int d) {
        int y2 = ((y - 1) % HttpStatus.SC_BAD_REQUEST) + 1;
        int ly = (((y2 - 1) / 4) - ((y2 - 1) / 100)) + ((y2 - 1) / HttpStatus.SC_BAD_REQUEST);
        return (((((1 + ((y2 - 1) - ly)) + (ly * 2)) + dayOfYear(y2, m, d)) - 1) % 7) + 1;
    }

    public int computeChineseFields() {
        if (this.gregorianYear < 1901 || this.gregorianYear > 2100) {
            return 1;
        }
        int startYear = baseYear;
        int startMonth = baseMonth;
        int startDate = baseDate;
        this.chineseYear = baseChineseYear;
        this.chineseMonth = baseChineseMonth;
        this.chineseDate = baseChineseDate;
        if (this.gregorianYear >= 2000) {
            startYear = baseYear + 99;
            startMonth = 1;
            startDate = 1;
            this.chineseYear = baseChineseYear + 99;
            this.chineseMonth = 11;
            this.chineseDate = 25;
        }
        int daysDiff = 0;
        for (int i = startYear; i < this.gregorianYear; i++) {
            daysDiff += 365;
            if (isGregorianLeapYear(i)) {
                daysDiff++;
            }
        }
        for (int i2 = startMonth; i2 < this.gregorianMonth; i2++) {
            daysDiff += daysInGregorianMonth(this.gregorianYear, i2);
        }
        this.chineseDate += daysDiff + (this.gregorianDate - startDate);
        int lastDate = daysInChineseMonth(this.chineseYear, this.chineseMonth);
        int nextMonth = nextChineseMonth(this.chineseYear, this.chineseMonth);
        while (this.chineseDate > lastDate) {
            if (Math.abs(nextMonth) < Math.abs(this.chineseMonth)) {
                this.chineseYear++;
            }
            this.chineseMonth = nextMonth;
            this.chineseDate -= lastDate;
            lastDate = daysInChineseMonth(this.chineseYear, this.chineseMonth);
            nextMonth = nextChineseMonth(this.chineseYear, this.chineseMonth);
        }
        return 0;
    }

    public static int daysInChineseMonth(int y, int m) {
        int index = (y - baseChineseYear) + baseIndex;
        if (1 > m || m > 8) {
            if (9 > m || m > 12) {
                if (((chineseMonths[(index * 2) + 1] >> 4) & 15) != Math.abs(m)) {
                    return 0;
                }
                for (int i : bigLeapMonthYears) {
                    if (i == index) {
                        return 30;
                    }
                }
                return 29;
            } else if (((chineseMonths[(index * 2) + 1] >> (m - 9)) & 1) == 1) {
                return 29;
            } else {
                return 30;
            }
        } else if (((chineseMonths[index * 2] >> (m - 1)) & 1) == 1) {
            return 29;
        } else {
            return 30;
        }
    }

    public static int nextChineseMonth(int y, int m) {
        int n = Math.abs(m) + 1;
        if (m > 0) {
            if (((chineseMonths[(((y - baseChineseYear) + baseIndex) * 2) + 1] >> 4) & 15) == m) {
                n = -m;
            }
        }
        if (n == 13) {
            return 1;
        }
        return n;
    }

    public int computeSolarTerms() {
        if (this.gregorianYear < 1901 || this.gregorianYear > 2100) {
            return 1;
        }
        this.sectionalTerm = sectionalTerm(this.gregorianYear, this.gregorianMonth);
        this.principleTerm = principleTerm(this.gregorianYear, this.gregorianMonth);
        return 0;
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=char, code=int, for r2v1, types: [char] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int sectionalTerm(int r7, int r8) {
        /*
            r6 = 4
            r3 = 1901(0x76d, float:2.664E-42)
            if (r7 < r3) goto L_0x0009
            r3 = 2100(0x834, float:2.943E-42)
            if (r7 <= r3) goto L_0x000b
        L_0x0009:
            r2 = 0
        L_0x000a:
            return r2
        L_0x000b:
            r0 = 0
            int r3 = baseYear
            int r3 = r7 - r3
            int r1 = r3 + 1
        L_0x0012:
            char[][] r3 = sectionalTermYear
            int r4 = r8 + -1
            r3 = r3[r4]
            char r3 = r3[r0]
            if (r1 < r3) goto L_0x001f
            int r0 = r0 + 1
            goto L_0x0012
        L_0x001f:
            char[][] r3 = sectionalTermMap
            int r4 = r8 + -1
            r3 = r3[r4]
            int r4 = r0 * 4
            int r5 = r1 % 4
            int r4 = r4 + r5
            char r2 = r3[r4]
            r3 = 121(0x79, float:1.7E-43)
            if (r1 != r3) goto L_0x0033
            if (r8 != r6) goto L_0x0033
            r2 = 5
        L_0x0033:
            r3 = 132(0x84, float:1.85E-43)
            if (r1 != r3) goto L_0x003a
            if (r8 != r6) goto L_0x003a
            r2 = 5
        L_0x003a:
            r3 = 194(0xc2, float:2.72E-43)
            if (r1 != r3) goto L_0x000a
            r3 = 6
            if (r8 != r3) goto L_0x000a
            r2 = 6
            goto L_0x000a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.CalendarUtil.sectionalTerm(int, int):int");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=char, code=int, for r2v1, types: [char] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int principleTerm(int r6, int r7) {
        /*
            r3 = 1901(0x76d, float:2.664E-42)
            if (r6 < r3) goto L_0x0008
            r3 = 2100(0x834, float:2.943E-42)
            if (r6 <= r3) goto L_0x000a
        L_0x0008:
            r2 = 0
        L_0x0009:
            return r2
        L_0x000a:
            r0 = 0
            int r3 = baseYear
            int r3 = r6 - r3
            int r1 = r3 + 1
        L_0x0011:
            char[][] r3 = principleTermYear
            int r4 = r7 + -1
            r3 = r3[r4]
            char r3 = r3[r0]
            if (r1 < r3) goto L_0x001e
            int r0 = r0 + 1
            goto L_0x0011
        L_0x001e:
            char[][] r3 = principleTermMap
            int r4 = r7 + -1
            r3 = r3[r4]
            int r4 = r0 * 4
            int r5 = r1 % 4
            int r4 = r4 + r5
            char r2 = r3[r4]
            r3 = 171(0xab, float:2.4E-43)
            if (r1 != r3) goto L_0x0034
            r3 = 3
            if (r7 != r3) goto L_0x0034
            r2 = 21
        L_0x0034:
            r3 = 181(0xb5, float:2.54E-43)
            if (r1 != r3) goto L_0x0009
            r3 = 5
            if (r7 != r3) goto L_0x0009
            r2 = 21
            goto L_0x0009
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.CalendarUtil.principleTerm(int, int):int");
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("Gregorian Year: " + this.gregorianYear + "\n");
        buf.append("Gregorian Month: " + this.gregorianMonth + "\n");
        buf.append("Gregorian Date: " + this.gregorianDate + "\n");
        buf.append("Is Leap Year: " + this.isGregorianLeap + "\n");
        buf.append("Day of Year: " + this.dayOfYear + "\n");
        buf.append("Day of Week: " + this.dayOfWeek + "\n");
        buf.append("Chinese Year: " + this.chineseYear + "\n");
        buf.append("Heavenly Stem: " + ((this.chineseYear - 1) % 10) + "\n");
        buf.append("Earthly Branch: " + ((this.chineseYear - 1) % 12) + "\n");
        buf.append("Chinese Month: " + this.chineseMonth + "\n");
        buf.append("Chinese Date: " + this.chineseDate + "\n");
        buf.append("Sectional Term: " + this.sectionalTerm + "\n");
        buf.append("Principle Term: " + this.principleTerm + "\n");
        return buf.toString();
    }

    public String[] getYearTable() {
        setGregorian(this.gregorianYear, 1, 1);
        computeChineseFields();
        computeSolarTerms();
        String[] table = new String[58];
        table[0] = getTextLine(27, "公历年历：" + this.gregorianYear);
        table[1] = getTextLine(27, "农历年历：" + (this.chineseYear + 1) + " (" + stemNames[((this.chineseYear + 1) - 1) % 10] + branchNames[((this.chineseYear + 1) - 1) % 12] + " - " + animalNames[((this.chineseYear + 1) - 1) % 12] + "年)");
        int ln = 2;
        for (int i = 1; i <= 6; i++) {
            table[ln] = "                                                                                    ";
            ln++;
            String[] mLeft = getMonthTable();
            String[] mRight = getMonthTable();
            for (int j = 0; j < mLeft.length; j++) {
                table[ln] = mLeft[j] + "  " + mRight[j];
                ln++;
            }
        }
        table[ln] = "                                                                                    ";
        int ln2 = ln + 1;
        table[ln2] = getTextLine(0, "##/## - 公历日期/农历日期，(*)#月 - (闰)农历月第一天");
        int ln3 = ln2 + 1;
        return table;
    }

    public static String getTextLine(int s, String t) {
        if (t == null || s >= "                                                                                    ".length() || t.length() + s >= "                                                                                    ".length()) {
            return "                                                                                    ";
        }
        return "                                                                                    ".substring(0, s) + t + "                                                                                    ".substring(t.length() + s);
    }

    public String[] getMonthTable() {
        String title;
        String line;
        setGregorian(this.gregorianYear, this.gregorianMonth, 1);
        computeChineseFields();
        computeSolarTerms();
        String[] table = new String[8];
        if (this.gregorianMonth < 11) {
            title = "                   ";
        } else {
            title = "                 ";
        }
        table[0] = title + monthNames[this.gregorianMonth - 1] + "月" + "                   ";
        table[1] = "   日    一    二    三    四    五    六 ";
        int wk = 2;
        String line2 = "";
        for (int i = 1; i < this.dayOfWeek; i++) {
            line2 = line + "      ";
        }
        int days = daysInGregorianMonth(this.gregorianYear, this.gregorianMonth);
        for (int i2 = this.gregorianDate; i2 <= days; i2++) {
            line = line + getDateString() + TokenParser.SP;
            rollUpOneDay();
            if (this.dayOfWeek == 1) {
                table[wk] = line;
                line = "";
                wk++;
            }
        }
        for (int i3 = this.dayOfWeek; i3 <= 7; i3++) {
            line = line + "      ";
        }
        table[wk] = line;
        for (int i4 = wk + 1; i4 < table.length; i4++) {
            table[i4] = "                                          ";
        }
        for (int i5 = 0; i5 < table.length; i5++) {
            table[i5] = table[i5].substring(0, table[i5].length() - 1);
        }
        return table;
    }

    public String getDateString() {
        String gm = String.valueOf(this.gregorianMonth);
        if (gm.length() == 1) {
            String gm2 = TokenParser.SP + gm;
        }
        String cm = String.valueOf(Math.abs(this.chineseMonth));
        if (cm.length() == 1) {
            String cm2 = TokenParser.SP + cm;
        }
        String gd = String.valueOf(this.gregorianDate);
        if (gd.length() == 1) {
            gd = TokenParser.SP + gd;
        }
        String cd = String.valueOf(this.chineseDate);
        if (cd.length() == 1) {
            cd = TokenParser.SP + cd;
        }
        if (this.gregorianDate == this.sectionalTerm) {
            return " " + sectionalTermNames[this.gregorianMonth - 1];
        }
        if (this.gregorianDate == this.principleTerm) {
            return " " + principleTermNames[this.gregorianMonth - 1];
        }
        if (this.chineseDate == 1 && this.chineseMonth > 0) {
            return " " + chineseMonthNames[this.chineseMonth - 1] + "月";
        }
        if (this.chineseDate != 1 || this.chineseMonth >= 0) {
            return gd + '/' + cd;
        }
        return "*" + chineseMonthNames[(-this.chineseMonth) - 1] + "月";
    }

    public int rollUpOneDay() {
        this.dayOfWeek = (this.dayOfWeek % 7) + 1;
        this.dayOfYear++;
        this.gregorianDate++;
        if (this.gregorianDate > daysInGregorianMonth(this.gregorianYear, this.gregorianMonth)) {
            this.gregorianDate = 1;
            this.gregorianMonth++;
            if (this.gregorianMonth > 12) {
                this.gregorianMonth = 1;
                this.gregorianYear++;
                this.dayOfYear = 1;
                this.isGregorianLeap = isGregorianLeapYear(this.gregorianYear);
            }
            this.sectionalTerm = sectionalTerm(this.gregorianYear, this.gregorianMonth);
            this.principleTerm = principleTerm(this.gregorianYear, this.gregorianMonth);
        }
        this.chineseDate++;
        if (this.chineseDate <= daysInChineseMonth(this.chineseYear, this.chineseMonth)) {
            return 0;
        }
        this.chineseDate = 1;
        this.chineseMonth = nextChineseMonth(this.chineseYear, this.chineseMonth);
        if (this.chineseMonth != 1) {
            return 0;
        }
        this.chineseYear++;
        return 0;
    }

    public int getGregorianYear() {
        return this.gregorianYear;
    }

    public void setGregorianYear(int gregorianYear2) {
        this.gregorianYear = gregorianYear2;
    }

    public int getGregorianMonth() {
        return this.gregorianMonth;
    }

    public void setGregorianMonth(int gregorianMonth2) {
        this.gregorianMonth = gregorianMonth2;
    }

    public int getGregorianDate() {
        return this.gregorianDate;
    }

    public void setGregorianDate(int gregorianDate2) {
        this.gregorianDate = gregorianDate2;
    }

    public boolean isGregorianLeap() {
        return this.isGregorianLeap;
    }

    public void setGregorianLeap(boolean isGregorianLeap2) {
        this.isGregorianLeap = isGregorianLeap2;
    }

    public int getDayOfYear() {
        return this.dayOfYear;
    }

    public void setDayOfYear(int dayOfYear2) {
        this.dayOfYear = dayOfYear2;
    }

    public int getDayOfWeek() {
        return this.dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek2) {
        this.dayOfWeek = dayOfWeek2;
    }

    public int getChineseYear() {
        return this.chineseYear;
    }

    public String getChinesYearStr() {
        return stemNames[(this.chineseYear - 1) % 10] + branchNames[(this.chineseYear - 1) % 12] + animalNames[(this.chineseYear - 1) % 12] + "年";
    }

    public String getChinesDayStr() {
        int cd = getChineseDate();
        if (cd < 1) {
            cd = 1;
        } else if (cd > 31) {
            cd = 31;
        }
        return this.daysOfAlmanac[cd - 1];
    }

    public String getChinesMonthStr() {
        int cd = getChineseMonth();
        if (cd < 1) {
            cd = 1;
        }
        if (cd > 12) {
            cd = 12;
        }
        return this.monthOfAlmanac[cd - 1];
    }

    public void setChineseYear(int chineseYear2) {
        this.chineseYear = chineseYear2;
    }

    public int getChineseMonth() {
        return this.chineseMonth;
    }

    public void setChineseMonth(int chineseMonth2) {
        this.chineseMonth = chineseMonth2;
    }

    public int getChineseDate() {
        return this.chineseDate;
    }

    public void setChineseDate(int chineseDate2) {
        this.chineseDate = chineseDate2;
    }

    public int getSectionalTerm() {
        return this.sectionalTerm;
    }

    public void setSectionalTerm(int sectionalTerm2) {
        this.sectionalTerm = sectionalTerm2;
    }

    public int getPrincipleTerm() {
        return this.principleTerm;
    }

    public void setPrincipleTerm(int principleTerm2) {
        this.principleTerm = principleTerm2;
    }

    public static char[] getDaysInGregorianMonth() {
        return daysInGregorianMonth;
    }

    public static void setDaysInGregorianMonth(char[] daysInGregorianMonth2) {
        daysInGregorianMonth = daysInGregorianMonth2;
    }

    public static String[] getStemNames() {
        return stemNames;
    }

    public static void setStemNames(String[] stemNames2) {
        stemNames = stemNames2;
    }

    public static String[] getBranchNames() {
        return branchNames;
    }

    public static void setBranchNames(String[] branchNames2) {
        branchNames = branchNames2;
    }

    public static String[] getAnimalNames() {
        return animalNames;
    }

    public static void setAnimalNames(String[] animalNames2) {
        animalNames = animalNames2;
    }

    public static char[] getChineseMonths() {
        return chineseMonths;
    }

    public static void setChineseMonths(char[] chineseMonths2) {
        chineseMonths = chineseMonths2;
    }

    public static int getBaseYear() {
        return baseYear;
    }

    public static void setBaseYear(int baseYear2) {
        baseYear = baseYear2;
    }

    public static int getBaseMonth() {
        return baseMonth;
    }

    public static void setBaseMonth(int baseMonth2) {
        baseMonth = baseMonth2;
    }

    public static int getBaseDate() {
        return baseDate;
    }

    public static void setBaseDate(int baseDate2) {
        baseDate = baseDate2;
    }

    public static int getBaseIndex() {
        return baseIndex;
    }

    public static void setBaseIndex(int baseIndex2) {
        baseIndex = baseIndex2;
    }

    public static int getBaseChineseYear() {
        return baseChineseYear;
    }

    public static void setBaseChineseYear(int baseChineseYear2) {
        baseChineseYear = baseChineseYear2;
    }

    public static int getBaseChineseMonth() {
        return baseChineseMonth;
    }

    public static void setBaseChineseMonth(int baseChineseMonth2) {
        baseChineseMonth = baseChineseMonth2;
    }

    public static int getBaseChineseDate() {
        return baseChineseDate;
    }

    public static void setBaseChineseDate(int baseChineseDate2) {
        baseChineseDate = baseChineseDate2;
    }

    public static int[] getBigLeapMonthYears() {
        return bigLeapMonthYears;
    }

    public static void setBigLeapMonthYears(int[] bigLeapMonthYears2) {
        bigLeapMonthYears = bigLeapMonthYears2;
    }

    public static char[][] getSectionalTermMap() {
        return sectionalTermMap;
    }

    public static void setSectionalTermMap(char[][] sectionalTermMap2) {
        sectionalTermMap = sectionalTermMap2;
    }

    public static char[][] getSectionalTermYear() {
        return sectionalTermYear;
    }

    public static void setSectionalTermYear(char[][] sectionalTermYear2) {
        sectionalTermYear = sectionalTermYear2;
    }

    public static char[][] getPrincipleTermMap() {
        return principleTermMap;
    }

    public static void setPrincipleTermMap(char[][] principleTermMap2) {
        principleTermMap = principleTermMap2;
    }

    public static char[][] getPrincipleTermYear() {
        return principleTermYear;
    }

    public static void setPrincipleTermYear(char[][] principleTermYear2) {
        principleTermYear = principleTermYear2;
    }

    public static String[] getMonthNames() {
        return monthNames;
    }

    public static void setMonthNames(String[] monthNames2) {
        monthNames = monthNames2;
    }

    public static String[] getChineseMonthNames() {
        return chineseMonthNames;
    }

    public static void setChineseMonthNames(String[] chineseMonthNames2) {
        chineseMonthNames = chineseMonthNames2;
    }

    public static String[] getPrincipleTermNames() {
        return principleTermNames;
    }

    public static void setPrincipleTermNames(String[] principleTermNames2) {
        principleTermNames = principleTermNames2;
    }

    public static String[] getSectionalTermNames() {
        return sectionalTermNames;
    }

    public static void setSectionalTermNames(String[] sectionalTermNames2) {
        sectionalTermNames = sectionalTermNames2;
    }
}
