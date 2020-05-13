package com.szchoiceway.index;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import java.io.IOException;
import java.util.ArrayList;

public class WallpaperChooserDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private static final String EMBEDDED_KEY = "com.szchoiceway.index.WallpaperChooserDialogFragment.EMBEDDED_KEY";
    private static final String TAG = "Launcher.WallpaperChooserDialogFragment";
    /* access modifiers changed from: private */
    public Bitmap mBitmap = null;
    private boolean mEmbedded;
    /* access modifiers changed from: private */
    public ArrayList<Integer> mImages;
    /* access modifiers changed from: private */
    public WallpaperLoader mLoader;
    /* access modifiers changed from: private */
    public ArrayList<Integer> mThumbs;
    /* access modifiers changed from: private */
    public WallpaperDrawable mWallpaperDrawable = new WallpaperDrawable();

    public static WallpaperChooserDialogFragment newInstance() {
        WallpaperChooserDialogFragment fragment = new WallpaperChooserDialogFragment();
        fragment.setCancelable(true);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey(EMBEDDED_KEY)) {
            this.mEmbedded = isInLayout();
        } else {
            this.mEmbedded = savedInstanceState.getBoolean(EMBEDDED_KEY);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EMBEDDED_KEY, this.mEmbedded);
    }

    private void cancelLoader() {
        if (this.mLoader != null && this.mLoader.getStatus() != AsyncTask.Status.FINISHED) {
            this.mLoader.cancel(true);
            this.mLoader = null;
        }
    }

    public void onDetach() {
        super.onDetach();
        cancelLoader();
    }

    public void onDestroy() {
        super.onDestroy();
        cancelLoader();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        findWallpapers();
        return null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        findWallpapers();
        if (!this.mEmbedded) {
            return null;
        }
        View view = inflater.inflate(R.layout.wallpaper_chooser, container, false);
        view.setBackground(this.mWallpaperDrawable);
        final Gallery gallery = (Gallery) view.findViewById(R.id.gallery);
        gallery.setCallbackDuringFling(false);
        gallery.setOnItemSelectedListener(this);
        gallery.setAdapter(new ImageAdapter(getActivity()));
        view.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                WallpaperChooserDialogFragment.this.selectWallpaper(gallery.getSelectedItemPosition());
            }
        });
        return view;
    }

    /* access modifiers changed from: private */
    @SuppressLint({"ServiceCast"})
    public void selectWallpaper(int position) {
        try {
            ((WallpaperManager) getActivity().getSystemService("wallpaper")).setResource(this.mImages.get(position).intValue());
            Activity activity = getActivity();
            activity.setResult(-1);
            activity.finish();
        } catch (IOException e) {
            Log.e(TAG, "Failed to set wallpaper: " + e);
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        selectWallpaper(position);
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (!(this.mLoader == null || this.mLoader.getStatus() == AsyncTask.Status.FINISHED)) {
            this.mLoader.cancel();
        }
        this.mLoader = (WallpaperLoader) new WallpaperLoader().execute(new Integer[]{Integer.valueOf(position)});
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void findWallpapers() {
        this.mThumbs = new ArrayList<>(24);
        this.mImages = new ArrayList<>(24);
        Resources resources = getResources();
        if (LauncherApplication.m_iUITypeVer == 41) {
            String packageName = resources.getResourcePackageName(R.array.ksw_wallpapers);
            addWallpapers(resources, packageName, R.array.ksw_wallpapers);
            addWallpapers(resources, packageName, R.array.extra_wallpapers);
            return;
        }
        String packageName2 = resources.getResourcePackageName(R.array.wallpapers);
        addWallpapers(resources, packageName2, R.array.wallpapers);
        addWallpapers(resources, packageName2, R.array.extra_wallpapers);
    }

    private void addWallpapers(Resources resources, String packageName, int list) {
        int thumbRes;
        for (String extra : resources.getStringArray(list)) {
            int res = resources.getIdentifier(extra, "drawable", packageName);
            if (!(res == 0 || (thumbRes = resources.getIdentifier(extra + "_small", "drawable", packageName)) == 0)) {
                this.mThumbs.add(Integer.valueOf(thumbRes));
                this.mImages.add(Integer.valueOf(res));
            }
        }
    }

    private class ImageAdapter extends BaseAdapter implements ListAdapter, SpinnerAdapter {
        private LayoutInflater mLayoutInflater;

        ImageAdapter(Activity activity) {
            this.mLayoutInflater = activity.getLayoutInflater();
        }

        public int getCount() {
            return WallpaperChooserDialogFragment.this.mThumbs.size();
        }

        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = this.mLayoutInflater.inflate(R.layout.wallpaper_item, parent, false);
            } else {
                view = convertView;
            }
            ImageView image = (ImageView) view.findViewById(R.id.wallpaper_image);
            int thumbRes = ((Integer) WallpaperChooserDialogFragment.this.mThumbs.get(position)).intValue();
            image.setImageResource(thumbRes);
            Drawable thumbDrawable = image.getDrawable();
            if (thumbDrawable != null) {
                thumbDrawable.setDither(true);
            } else {
                Log.e(WallpaperChooserDialogFragment.TAG, "Error decoding thumbnail resId=" + thumbRes + " for wallpaper #" + position);
            }
            return view;
        }
    }

    class WallpaperLoader extends AsyncTask<Integer, Void, Bitmap> {
        BitmapFactory.Options mOptions = new BitmapFactory.Options();

        WallpaperLoader() {
            this.mOptions.inDither = false;
            this.mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        }

        /* access modifiers changed from: protected */
        public Bitmap doInBackground(Integer... params) {
            if (isCancelled()) {
                return null;
            }
            try {
                return BitmapFactory.decodeResource(WallpaperChooserDialogFragment.this.getResources(), ((Integer) WallpaperChooserDialogFragment.this.mImages.get(params[0].intValue())).intValue(), this.mOptions);
            } catch (OutOfMemoryError e) {
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Bitmap b) {
            if (b != null) {
                if (isCancelled() || this.mOptions.mCancel) {
                    b.recycle();
                    return;
                }
                if (WallpaperChooserDialogFragment.this.mBitmap != null) {
                    WallpaperChooserDialogFragment.this.mBitmap.recycle();
                }
                View v = WallpaperChooserDialogFragment.this.getView();
                if (v != null) {
                    Bitmap unused = WallpaperChooserDialogFragment.this.mBitmap = b;
                    WallpaperChooserDialogFragment.this.mWallpaperDrawable.setBitmap(b);
                    v.postInvalidate();
                } else {
                    Bitmap unused2 = WallpaperChooserDialogFragment.this.mBitmap = null;
                    WallpaperChooserDialogFragment.this.mWallpaperDrawable.setBitmap((Bitmap) null);
                }
                WallpaperLoader unused3 = WallpaperChooserDialogFragment.this.mLoader = null;
            }
        }

        /* access modifiers changed from: package-private */
        public void cancel() {
            this.mOptions.requestCancelDecode();
            super.cancel(true);
        }
    }

    static class WallpaperDrawable extends Drawable {
        Bitmap mBitmap;
        int mIntrinsicHeight;
        int mIntrinsicWidth;

        WallpaperDrawable() {
        }

        /* access modifiers changed from: package-private */
        public void setBitmap(Bitmap bitmap) {
            this.mBitmap = bitmap;
            if (this.mBitmap != null) {
                this.mIntrinsicWidth = this.mBitmap.getWidth();
                this.mIntrinsicHeight = this.mBitmap.getHeight();
            }
        }

        public void draw(Canvas canvas) {
            if (this.mBitmap != null) {
                int width = canvas.getWidth();
                int height = canvas.getHeight();
                canvas.drawBitmap(this.mBitmap, (float) ((width - this.mIntrinsicWidth) / 2), (float) ((height - this.mIntrinsicHeight) / 2), (Paint) null);
            }
        }

        public int getOpacity() {
            return -1;
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter cf) {
        }
    }
}
