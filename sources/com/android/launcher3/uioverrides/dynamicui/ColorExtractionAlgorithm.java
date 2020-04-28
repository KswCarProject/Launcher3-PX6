package com.android.launcher3.uioverrides.dynamicui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Range;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ColorExtractionAlgorithm {
    static final ColorRange[] BLACKLISTED_COLORS = {new ColorRange(new Range(Float.valueOf(0.0f), Float.valueOf(20.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.21f), Float.valueOf(0.79f))), new ColorRange(new Range(Float.valueOf(0.0f), Float.valueOf(20.0f)), new Range(Float.valueOf(0.3f), Float.valueOf(0.7f)), new Range(Float.valueOf(0.355f), Float.valueOf(0.653f))), new ColorRange(new Range(Float.valueOf(20.0f), Float.valueOf(40.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.28f), Float.valueOf(0.643f))), new ColorRange(new Range(Float.valueOf(20.0f), Float.valueOf(40.0f)), new Range(Float.valueOf(0.3f), Float.valueOf(0.7f)), new Range(Float.valueOf(0.414f), Float.valueOf(0.561f))), new ColorRange(new Range(Float.valueOf(20.0f), Float.valueOf(40.0f)), new Range(Float.valueOf(0.0f), Float.valueOf(3.0f)), new Range(Float.valueOf(0.343f), Float.valueOf(0.584f))), new ColorRange(new Range(Float.valueOf(40.0f), Float.valueOf(60.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.173f), Float.valueOf(0.349f))), new ColorRange(new Range(Float.valueOf(40.0f), Float.valueOf(60.0f)), new Range(Float.valueOf(0.3f), Float.valueOf(0.7f)), new Range(Float.valueOf(0.233f), Float.valueOf(0.427f))), new ColorRange(new Range(Float.valueOf(40.0f), Float.valueOf(60.0f)), new Range(Float.valueOf(0.0f), Float.valueOf(0.3f)), new Range(Float.valueOf(0.231f), Float.valueOf(0.484f))), new ColorRange(new Range(Float.valueOf(60.0f), Float.valueOf(80.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.488f), Float.valueOf(0.737f))), new ColorRange(new Range(Float.valueOf(60.0f), Float.valueOf(80.0f)), new Range(Float.valueOf(0.3f), Float.valueOf(0.7f)), new Range(Float.valueOf(0.673f), Float.valueOf(0.837f))), new ColorRange(new Range(Float.valueOf(80.0f), Float.valueOf(100.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.469f), Float.valueOf(0.61f))), new ColorRange(new Range(Float.valueOf(100.0f), Float.valueOf(120.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.388f), Float.valueOf(0.612f))), new ColorRange(new Range(Float.valueOf(100.0f), Float.valueOf(120.0f)), new Range(Float.valueOf(0.3f), Float.valueOf(0.7f)), new Range(Float.valueOf(0.424f), Float.valueOf(0.541f))), new ColorRange(new Range(Float.valueOf(120.0f), Float.valueOf(140.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.375f), Float.valueOf(0.52f))), new ColorRange(new Range(Float.valueOf(120.0f), Float.valueOf(140.0f)), new Range(Float.valueOf(0.3f), Float.valueOf(0.7f)), new Range(Float.valueOf(0.435f), Float.valueOf(0.524f))), new ColorRange(new Range(Float.valueOf(140.0f), Float.valueOf(160.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.496f), Float.valueOf(0.641f))), new ColorRange(new Range(Float.valueOf(160.0f), Float.valueOf(180.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.496f), Float.valueOf(0.567f))), new ColorRange(new Range(Float.valueOf(180.0f), Float.valueOf(200.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.52f), Float.valueOf(0.729f))), new ColorRange(new Range(Float.valueOf(220.0f), Float.valueOf(240.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.396f), Float.valueOf(0.571f))), new ColorRange(new Range(Float.valueOf(220.0f), Float.valueOf(240.0f)), new Range(Float.valueOf(0.3f), Float.valueOf(0.7f)), new Range(Float.valueOf(0.425f), Float.valueOf(0.551f))), new ColorRange(new Range(Float.valueOf(240.0f), Float.valueOf(260.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.418f), Float.valueOf(0.639f))), new ColorRange(new Range(Float.valueOf(220.0f), Float.valueOf(240.0f)), new Range(Float.valueOf(0.3f), Float.valueOf(0.7f)), new Range(Float.valueOf(0.441f), Float.valueOf(0.576f))), new ColorRange(new Range(Float.valueOf(260.0f), Float.valueOf(280.0f)), new Range(Float.valueOf(0.3f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.461f), Float.valueOf(0.553f))), new ColorRange(new Range(Float.valueOf(300.0f), Float.valueOf(320.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.484f), Float.valueOf(0.588f))), new ColorRange(new Range(Float.valueOf(300.0f), Float.valueOf(320.0f)), new Range(Float.valueOf(0.3f), Float.valueOf(0.7f)), new Range(Float.valueOf(0.48f), Float.valueOf(0.592f))), new ColorRange(new Range(Float.valueOf(320.0f), Float.valueOf(340.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.466f), Float.valueOf(0.629f))), new ColorRange(new Range(Float.valueOf(340.0f), Float.valueOf(360.0f)), new Range(Float.valueOf(0.7f), Float.valueOf(1.0f)), new Range(Float.valueOf(0.437f), Float.valueOf(0.596f)))};
    private static final float FIT_WEIGHT_H = 1.0f;
    private static final float FIT_WEIGHT_L = 10.0f;
    private static final float FIT_WEIGHT_S = 1.0f;
    private static final TonalPalette GREY_PALETTE = new TonalPalette(new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.08f, 0.11f, 0.14901961f, 0.2f, 0.29803923f, 0.4f, 0.49803922f, 0.61960787f, 0.7176471f, 0.81960785f, 0.91764706f, 0.9490196f});
    public static final int MAIN_COLOR_DARK = -14606047;
    public static final int MAIN_COLOR_LIGHT = -5197648;
    public static final int SECONDARY_COLOR_DARK = -16777216;
    public static final int SECONDARY_COLOR_LIGHT = -6381922;
    private static final String TAG = "Tonal";
    private static final TonalPalette[] TONAL_PALETTES = {new TonalPalette(new float[]{1.0f, 1.0f, 0.991f, 0.991f, 0.98333335f, 0.0f, 0.0f, 0.0f, 0.011343804f, 0.015625f, 0.024193548f, 0.02739726f, 0.01754386f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.84347826f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.04f, 0.09f, 0.14f, 0.2f, 0.27450982f, 0.34901962f, 0.42352942f, 0.54901963f, 0.6254902f, 0.6862745f, 0.75686276f, 0.8568627f, 0.9254902f}), new TonalPalette(new float[]{0.638f, 0.638f, 0.6385768f, 0.63011694f, 0.6223958f, 0.6151079f, 0.6065401f, 0.5986965f, 0.5910747f, 0.5833333f, 0.5748032f, 0.5582011f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 0.90140843f, 0.8128655f, 0.7979798f, 0.78165936f, 0.7787234f, 1.0f, 1.0f, 1.0f}, new float[]{0.05f, 0.12f, 0.17450981f, 0.22352941f, 0.2784314f, 0.33529413f, 0.3882353f, 0.4490196f, 0.5392157f, 0.6509804f, 0.7509804f, 0.87647057f}), new TonalPalette(new float[]{0.563f, 0.569f, 0.5666f, 0.5669935f, 0.5748032f, 0.5595238f, 0.54731184f, 0.53932583f, 0.5315956f, 0.524031f, 0.51547116f, 0.5080808f, 0.5f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.8847737f, 1.0f, 1.0f, 1.0f}, new float[]{0.07f, 0.12f, 0.16f, 0.2f, 0.24901961f, 0.27450982f, 0.30392158f, 0.34901962f, 0.4137255f, 0.4764706f, 0.5352941f, 0.6764706f, 0.8f}), new TonalPalette(new float[]{0.508f, 0.511f, 0.508f, 0.508f, 0.50823045f, 0.5069444f, 0.5f, 0.5f, 0.5f, 0.48724955f, 0.4800347f, 0.47551343f, 0.47244096f, 0.46710527f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.8888889f, 0.92424244f, 1.0f, 1.0f, 0.81333333f, 0.78688526f, 1.0f, 1.0f, 1.0f}, new float[]{0.04f, 0.06f, 0.08f, 0.12f, 0.15882353f, 0.21176471f, 0.25882354f, 0.3f, 0.34901962f, 0.44117647f, 0.52156866f, 0.5862745f, 0.7509804f, 0.8509804f}), new TonalPalette(new float[]{0.333f, 0.333f, 0.333f, 0.33333334f, 0.33333334f, 0.34006733f, 0.34006733f, 0.34006733f, 0.3425926f, 0.34757835f, 0.34767026f, 0.3467742f, 0.37037036f}, new float[]{0.7f, 0.72f, 0.69f, 0.6703297f, 0.7288136f, 0.5657143f, 0.50769234f, 0.39442232f, 0.62068963f, 0.89312977f, 1.0f, 1.0f, 1.0f}, new float[]{0.05f, 0.08f, 0.14f, 0.17843138f, 0.23137255f, 0.34313726f, 0.38235295f, 0.49215686f, 0.65882355f, 0.74313724f, 0.81764704f, 0.8784314f, 0.92941177f}), new TonalPalette(new float[]{0.161f, 0.163f, 0.163f, 0.16228071f, 0.1503268f, 0.15879264f, 0.16236559f, 0.17443869f, 0.17824075f, 0.18674698f, 0.18692449f, 0.19467787f, 0.18604651f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.05f, 0.08f, 0.11f, 0.14901961f, 0.2f, 0.24901961f, 0.30392158f, 0.37843138f, 0.42352942f, 0.4882353f, 0.64509803f, 0.76666665f, 0.83137256f}), new TonalPalette(new float[]{0.108f, 0.105f, 0.105f, 0.105f, 0.10619469f, 0.11924686f, 0.13046448f, 0.14248367f, 0.15060242f, 0.16220239f, 0.16666667f, 0.16666667f, 0.16228071f, 0.15686275f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.17f, 0.22f, 0.28f, 0.35f, 0.44313726f, 0.46862745f, 0.47843137f, 0.5f, 0.5117647f, 0.56078434f, 0.6509804f, 0.7509804f, 0.8509804f, 0.9f}), new TonalPalette(new float[]{0.036f, 0.036f, 0.036f, 0.036f, 0.035612535f, 0.050980393f, 0.0751634f, 0.09477124f, 0.11503268f, 0.13464053f, 0.14640523f, 0.1582397f, 0.15773809f, 0.15359478f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.19f, 0.26f, 0.34f, 0.39f, 0.45882353f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.6509804f, 0.78039217f, 0.9f}), new TonalPalette(new float[]{0.955f, 0.961f, 0.958f, 0.95964915f, 0.9593837f, 0.9514768f, 0.94385964f, 0.93968254f, 0.9395425f, 0.93939394f, 0.9362745f, 0.97540987f, 0.98245615f}, new float[]{0.87f, 0.85f, 0.85f, 0.84070796f, 0.8206897f, 0.7979798f, 0.766129f, 0.9051724f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.06f, 0.11f, 0.16f, 0.22156863f, 0.28431374f, 0.3882353f, 0.4862745f, 0.54509807f, 0.6f, 0.6764706f, 0.8f, 0.88039213f, 0.9254902f}), new TonalPalette(new float[]{0.866f, 0.855f, 0.84102565f, 0.8333333f, 0.82852566f, 0.8215223f, 0.80833334f, 0.8046595f, 0.80058223f, 0.78423774f, 0.77710843f, 0.7747748f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.73714286f, 0.64341086f, 0.46835443f}, new float[]{0.05f, 0.08f, 0.12745099f, 0.15490197f, 0.20392157f, 0.24901961f, 0.3137255f, 0.3647059f, 0.4490196f, 0.65686274f, 0.7470588f, 0.845098f}), new TonalPalette(new float[]{0.925f, 0.93f, 0.938f, 0.947f, 0.9559524f, 0.968107f, 0.97604793f, 0.9873563f, 0.0f, 0.0f, 0.009057971f, 0.026748972f, 0.041666668f, 0.053030305f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.83505154f, 0.6929461f, 0.6387665f, 0.69148934f, 0.75838923f, 0.80701756f, 0.9310345f, 1.0f, 1.0f}, new float[]{0.1f, 0.13f, 0.17f, 0.2f, 0.27450982f, 0.38039216f, 0.47254902f, 0.55490196f, 0.6313726f, 0.7078431f, 0.7764706f, 0.82941175f, 0.90588236f, 0.95686275f}), new TonalPalette(new float[]{0.733f, 0.736f, 0.744f, 0.751462f, 0.76797384f, 0.78020835f, 0.78443116f, 0.796875f, 0.8165618f, 0.8487179f, 0.85823756f, 0.85620916f, 0.8666667f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.81632656f, 0.66533864f, 0.754717f, 0.9298246f, 0.9558824f, 0.95604396f, 1.0f, 1.0f}, new float[]{0.07f, 0.12f, 0.17f, 0.22352941f, 0.3f, 0.38431373f, 0.49215686f, 0.58431375f, 0.6647059f, 0.73333335f, 0.8215686f, 0.9f, 0.9411765f}), new TonalPalette(new float[]{0.6666667f, 0.6666667f, 0.6666667f, 0.6666667f, 0.6666667f, 0.6666667f, 0.6666667f, 0.6666667f, 0.6666667f, 0.6666667f, 0.6666667f}, new float[]{0.25f, 0.24590164f, 0.17880794f, 0.14606741f, 0.13761468f, 0.14893617f, 0.16756757f, 0.203125f, 0.26086956f, 0.3f, 0.5f}, new float[]{0.18f, 0.23921569f, 0.29607844f, 0.34901962f, 0.42745098f, 0.5392157f, 0.6372549f, 0.7490196f, 0.81960785f, 0.88235295f, 0.9372549f}), new TonalPalette(new float[]{0.938f, 0.944f, 0.952f, 0.961f, 0.9678571f, 0.99448127f, 0.0f, 0.0f, 0.0047348486f, 0.003164557f, 0.0f, 0.9980392f, 0.9814815f, 0.9722222f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.7023256f, 0.66386557f, 0.65217394f, 0.7719298f, 0.83157897f, 0.686747f, 0.72649574f, 0.8181818f, 0.8181818f}, new float[]{0.08f, 0.13f, 0.18f, 0.23f, 0.27450982f, 0.42156863f, 0.46666667f, 0.50392157f, 0.5529412f, 0.627451f, 0.6745098f, 0.7705882f, 0.89215684f, 0.95686275f}), new TonalPalette(new float[]{0.88f, 0.888f, 0.897f, 0.90522873f, 0.9112022f, 0.92701524f, 0.9343137f, 0.93915343f, 0.9437984f, 0.943662f, 0.9438944f, 0.94262296f, 0.9444444f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 0.81333333f, 0.7927461f, 0.7798165f, 0.7777778f, 0.81904763f, 0.8255814f, 0.8211382f, 0.81333333f, 0.8f}, new float[]{0.08f, 0.12f, 0.16f, 0.2f, 0.29411766f, 0.37843138f, 0.42745098f, 0.4764706f, 0.5882353f, 0.6627451f, 0.7588235f, 0.85294116f, 0.9411765f}), new TonalPalette(new float[]{0.669f, 0.68f, 0.6884058f, 0.697479f, 0.707989f, 0.7154471f, 0.7217742f, 0.7274143f, 0.72727275f, 0.7258065f, 0.7252252f, 0.73333335f}, new float[]{0.81f, 0.81f, 0.8214286f, 0.68786126f, 0.6080402f, 0.57746476f, 0.53913045f, 0.46724892f, 0.4680851f, 0.46268657f, 0.45679012f, 0.45454547f}, new float[]{0.12f, 0.16f, 0.21960784f, 0.3392157f, 0.39019608f, 0.41764706f, 0.4509804f, 0.5509804f, 0.6313726f, 0.7372549f, 0.84117645f, 0.9352941f}), new TonalPalette(new float[]{0.64705884f, 0.65166664f, 0.64641744f, 0.6441441f, 0.64327484f, 0.64166665f, 0.6402439f, 0.6412429f, 0.6435185f, 0.64285713f}, new float[]{0.8095238f, 0.65789473f, 0.5721925f, 0.5362319f, 0.5f, 0.44247788f, 0.4408602f, 0.44360903f, 0.45f, 0.4375f}, new float[]{0.16470589f, 0.29803923f, 0.36666667f, 0.40588236f, 0.44705883f, 0.5568628f, 0.63529414f, 0.7392157f, 0.84313726f, 0.9372549f}), new TonalPalette(new float[]{0.469f, 0.46732026f, 0.47186148f, 0.47936508f, 0.48071626f, 0.48296836f, 0.484375f, 0.48412699f, 0.48444444f, 0.48518518f, 0.49074075f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.627451f, 0.4183267f, 0.41899443f, 0.41284403f, 0.4090909f}, new float[]{0.07f, 0.1f, 0.1509804f, 0.20588236f, 0.2372549f, 0.26862746f, 0.4f, 0.50784314f, 0.6490196f, 0.7862745f, 0.9137255f}), new TonalPalette(new float[]{0.542f, 0.54444444f, 0.5555556f, 0.5555556f, 0.55376345f, 0.55263156f, 0.5555556f, 0.5555556f, 0.5555556f, 0.55128205f, 0.56666666f}, new float[]{0.25f, 0.24590164f, 0.19148937f, 0.17910448f, 0.18343195f, 0.18446602f, 0.15384616f, 0.15625f, 0.15328467f, 0.15662651f, 0.15151516f}, new float[]{0.05f, 0.11960784f, 0.18431373f, 0.2627451f, 0.33137256f, 0.40392157f, 0.5411765f, 0.62352943f, 0.73137254f, 0.8372549f, 0.9352941f}), new TonalPalette(new float[]{0.022222223f, 0.024691358f, 0.03125f, 0.039473683f, 0.041666668f, 0.043650795f, 0.04411765f, 0.041666668f, 0.044444446f, 0.055555556f}, new float[]{0.33333334f, 0.2783505f, 0.2580645f, 0.25675675f, 0.25287357f, 0.175f, 0.15315315f, 0.15189873f, 0.15789473f, 0.15789473f}, new float[]{0.0882353f, 0.19019608f, 0.24313726f, 0.2901961f, 0.34117648f, 0.47058824f, 0.5647059f, 0.6901961f, 0.8137255f, 0.9254902f}), new TonalPalette(new float[]{0.027f, 0.03f, 0.038f, 0.044f, 0.050884955f, 0.07254902f, 0.093464054f, 0.104575165f, 0.116993465f, 0.1255814f, 0.12689394f, 0.12533334f, 0.125f, 0.12777779f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.25f, 0.3f, 0.35f, 0.4f, 0.44313726f, 0.5f, 0.5f, 0.5f, 0.5f, 0.57843137f, 0.654902f, 0.75490195f, 0.8509804f, 0.9411765f})};
    private float[] mTmpHSL = new float[3];

    public static ColorExtractionAlgorithm newInstance(Context context) {
        return (ColorExtractionAlgorithm) Utilities.getOverrideObject(ColorExtractionAlgorithm.class, context.getApplicationContext(), R.string.color_extraction_impl_class);
    }

    public Pair<Integer, Integer> extractInto(WallpaperColorsCompat inWallpaperColors) {
        int primaryIndex;
        if (inWallpaperColors == null) {
            return applyFallback(inWallpaperColors);
        }
        List<Integer> mainColors = getMainColors(inWallpaperColors);
        int mainColorsSize = mainColors.size();
        boolean supportsDarkText = (inWallpaperColors.getColorHints() & 1) != 0;
        if (mainColorsSize == 0) {
            return applyFallback(inWallpaperColors);
        }
        Integer bestColor = 0;
        float[] hsl = new float[3];
        int i = 0;
        while (true) {
            if (i >= mainColorsSize) {
                break;
            }
            int colorValue = mainColors.get(i).intValue();
            ColorUtils.RGBToHSL(Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue), hsl);
            if (!isBlacklisted(hsl)) {
                bestColor = Integer.valueOf(colorValue);
                break;
            }
            i++;
        }
        if (bestColor == null) {
            return applyFallback(inWallpaperColors);
        }
        int colorValue2 = bestColor.intValue();
        ColorUtils.RGBToHSL(Color.red(colorValue2), Color.green(colorValue2), Color.blue(colorValue2), hsl);
        hsl[0] = hsl[0] / 360.0f;
        TonalPalette palette = findTonalPalette(hsl[0], hsl[1]);
        if (palette == null) {
            Log.w(TAG, "Could not find a tonal palette!");
            return applyFallback(inWallpaperColors);
        }
        int fitIndex = bestFit(palette, hsl[0], hsl[1], hsl[2]);
        if (fitIndex == -1) {
            Log.w(TAG, "Could not find best fit!");
            return applyFallback(inWallpaperColors);
        }
        float[] h = fit(palette.h, hsl[0], fitIndex, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        float[] s = fit(palette.s, hsl[1], fitIndex, 0.0f, 1.0f);
        float[] l = fit(palette.l, hsl[2], fitIndex, 0.0f, 1.0f);
        int mainColor = getColorInt(fitIndex, h, s, l);
        ColorUtils.colorToHSL(mainColor, this.mTmpHSL);
        float mainLuminosity = this.mTmpHSL[2];
        List<Integer> list = mainColors;
        ColorUtils.colorToHSL(MAIN_COLOR_LIGHT, this.mTmpHSL);
        float lightLuminosity = this.mTmpHSL[2];
        if (mainLuminosity > lightLuminosity) {
            return applyFallback(inWallpaperColors);
        }
        float f = lightLuminosity;
        ColorUtils.colorToHSL(MAIN_COLOR_DARK, this.mTmpHSL);
        float darkLuminosity = this.mTmpHSL[2];
        if (mainLuminosity < darkLuminosity) {
            return applyFallback(inWallpaperColors);
        }
        if (supportsDarkText) {
            primaryIndex = h.length - 1;
        } else if (fitIndex < 2) {
            primaryIndex = 0;
        } else {
            primaryIndex = Math.min(fitIndex, 3);
        }
        int primaryIndex2 = 2;
        if (primaryIndex >= 2) {
            primaryIndex2 = -2;
        }
        float[] fArr = l;
        float f2 = darkLuminosity;
        return new Pair<>(Integer.valueOf(mainColor), Integer.valueOf(getColorInt(primaryIndex2 + primaryIndex, h, s, l)));
    }

    public static Pair<Integer, Integer> applyFallback(WallpaperColorsCompat inWallpaperColors) {
        boolean light = true;
        if (inWallpaperColors == null || (inWallpaperColors.getColorHints() & 1) == 0) {
            light = false;
        }
        return new Pair<>(Integer.valueOf(light ? MAIN_COLOR_LIGHT : MAIN_COLOR_DARK), Integer.valueOf(light ? SECONDARY_COLOR_LIGHT : -16777216));
    }

    private int getColorInt(int fitIndex, float[] h, float[] s, float[] l) {
        this.mTmpHSL[0] = fract(h[fitIndex]) * 360.0f;
        this.mTmpHSL[1] = s[fitIndex];
        this.mTmpHSL[2] = l[fitIndex];
        return ColorUtils.HSLToColor(this.mTmpHSL);
    }

    private boolean isBlacklisted(float[] hsl) {
        for (ColorRange badRange : BLACKLISTED_COLORS) {
            if (badRange.containsColor(hsl[0], hsl[1], hsl[2])) {
                return true;
            }
        }
        return false;
    }

    private static float[] fit(float[] data, float v, int index, float min, float max) {
        float[] fitData = new float[data.length];
        float delta = v - data[index];
        for (int i = 0; i < data.length; i++) {
            fitData[i] = Utilities.boundToRange(data[i] + delta, min, max);
        }
        return fitData;
    }

    private static int bestFit(@NonNull TonalPalette palette, float h, float s, float l) {
        int minErrorIndex = -1;
        float minError = Float.POSITIVE_INFINITY;
        for (int i = 0; i < palette.h.length; i++) {
            float error = (Math.abs(h - palette.h[i]) * 1.0f) + (Math.abs(s - palette.s[i]) * 1.0f) + (Math.abs(l - palette.l[i]) * FIT_WEIGHT_L);
            if (error < minError) {
                minError = error;
                minErrorIndex = i;
            }
        }
        return minErrorIndex;
    }

    @Nullable
    private static TonalPalette findTonalPalette(float h, float s) {
        float fract;
        if (s < 0.05f) {
            return GREY_PALETTE;
        }
        TonalPalette best = null;
        float error = Float.POSITIVE_INFINITY;
        for (TonalPalette candidate : TONAL_PALETTES) {
            if (h >= candidate.minHue && h <= candidate.maxHue) {
                return candidate;
            }
            if (candidate.maxHue > 1.0f && h >= 0.0f && h <= fract(candidate.maxHue)) {
                return candidate;
            }
            if (candidate.minHue < 0.0f && h >= fract(candidate.minHue) && h <= 1.0f) {
                return candidate;
            }
            if (h <= candidate.minHue && candidate.minHue - h < error) {
                best = candidate;
                fract = candidate.minHue - h;
            } else if (h >= candidate.maxHue && h - candidate.maxHue < error) {
                best = candidate;
                error = h - candidate.maxHue;
            } else if (candidate.maxHue <= 1.0f || h < fract(candidate.maxHue) || h - fract(candidate.maxHue) >= error) {
                if (candidate.minHue < 0.0f && h <= fract(candidate.minHue) && fract(candidate.minHue) - h < error) {
                    best = candidate;
                    fract = fract(candidate.minHue) - h;
                }
            } else {
                best = candidate;
                error = h - fract(candidate.maxHue);
            }
            error = fract;
        }
        return best;
    }

    private static float fract(float v) {
        return v - ((float) Math.floor((double) v));
    }

    static class TonalPalette {
        final float[] h;
        final float[] l;
        final float maxHue;
        final float minHue;
        final float[] s;

        TonalPalette(float[] h2, float[] s2, float[] l2) {
            if (h2.length == s2.length && s2.length == l2.length) {
                this.h = h2;
                this.s = s2;
                this.l = l2;
                float minHue2 = Float.POSITIVE_INFINITY;
                float maxHue2 = Float.NEGATIVE_INFINITY;
                for (float v : h2) {
                    minHue2 = Math.min(v, minHue2);
                    maxHue2 = Math.max(v, maxHue2);
                }
                this.minHue = minHue2;
                this.maxHue = maxHue2;
                return;
            }
            throw new IllegalArgumentException("All arrays should have the same size. h: " + Arrays.toString(h2) + " s: " + Arrays.toString(s2) + " l: " + Arrays.toString(l2));
        }
    }

    static class ColorRange {
        private Range<Float> mHue;
        private Range<Float> mLightness;
        private Range<Float> mSaturation;

        ColorRange(Range<Float> hue, Range<Float> saturation, Range<Float> lightness) {
            this.mHue = hue;
            this.mSaturation = saturation;
            this.mLightness = lightness;
        }

        /* access modifiers changed from: package-private */
        public boolean containsColor(float h, float s, float l) {
            if (this.mHue.contains(Float.valueOf(h)) && this.mSaturation.contains(Float.valueOf(s)) && this.mLightness.contains(Float.valueOf(l))) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public float[] getCenter() {
            return new float[]{this.mHue.getLower().floatValue() + ((this.mHue.getUpper().floatValue() - this.mHue.getLower().floatValue()) / 2.0f), this.mSaturation.getLower().floatValue() + ((this.mSaturation.getUpper().floatValue() - this.mSaturation.getLower().floatValue()) / 2.0f), this.mLightness.getLower().floatValue() + ((this.mLightness.getUpper().floatValue() - this.mLightness.getLower().floatValue()) / 2.0f)};
        }

        public String toString() {
            return String.format("H: %s, S: %s, L %s", new Object[]{this.mHue, this.mSaturation, this.mLightness});
        }
    }

    private static List<Integer> getMainColors(WallpaperColorsCompat wallpaperColors) {
        LinkedList<Integer> colors = new LinkedList<>();
        if (wallpaperColors.getPrimaryColor() != 0) {
            colors.add(Integer.valueOf(wallpaperColors.getPrimaryColor()));
        }
        if (wallpaperColors.getSecondaryColor() != 0) {
            colors.add(Integer.valueOf(wallpaperColors.getSecondaryColor()));
        }
        if (wallpaperColors.getTertiaryColor() != 0) {
            colors.add(Integer.valueOf(wallpaperColors.getTertiaryColor()));
        }
        return colors;
    }
}
