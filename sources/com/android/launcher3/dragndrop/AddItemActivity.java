package com.android.launcher3.dragndrop;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.InstallShortcutReceiver;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetHost;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.LauncherAppsCompatVO;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.InstantAppResolver;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.WidgetHostViewLoader;
import com.android.launcher3.widget.WidgetImageView;

@TargetApi(26)
public class AddItemActivity extends BaseActivity implements View.OnLongClickListener, View.OnTouchListener {
    private static final int REQUEST_BIND_APPWIDGET = 1;
    private static final int SHADOW_SIZE = 10;
    private static final String STATE_EXTRA_WIDGET_ID = "state.widget.id";
    private LauncherAppState mApp;
    private LauncherAppWidgetHost mAppWidgetHost;
    private AppWidgetManagerCompat mAppWidgetManager;
    private boolean mFinishOnPause = false;
    private InvariantDeviceProfile mIdp;
    private InstantAppResolver mInstantAppResolver;
    private final PointF mLastTouchPos = new PointF();
    private int mPendingBindWidgetId;
    private PendingAddWidgetInfo mPendingWidgetInfo;
    private LauncherApps.PinItemRequest mRequest;
    private LivePreviewWidgetCell mWidgetCell;
    private Bundle mWidgetOptions;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mRequest = LauncherAppsCompatVO.getPinItemRequest(getIntent());
        if (this.mRequest == null) {
            finish();
            return;
        }
        this.mApp = LauncherAppState.getInstance(this);
        this.mIdp = this.mApp.getInvariantDeviceProfile();
        this.mInstantAppResolver = InstantAppResolver.newInstance(this);
        this.mDeviceProfile = this.mIdp.getDeviceProfile(getApplicationContext());
        setContentView(R.layout.add_item_confirmation_activity);
        this.mWidgetCell = (LivePreviewWidgetCell) findViewById(R.id.widget_cell);
        if (this.mRequest.getRequestType() == 1) {
            setupShortcut();
        } else if (!setupWidget()) {
            finish();
        }
        this.mWidgetCell.setOnTouchListener(this);
        this.mWidgetCell.setOnLongClickListener(this);
        if (savedInstanceState == null) {
            logCommand(2);
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        this.mLastTouchPos.set(motionEvent.getX(), motionEvent.getY());
        return false;
    }

    public boolean onLongClick(View view) {
        WidgetImageView img = this.mWidgetCell.getWidgetView();
        if (img.getBitmap() == null) {
            return false;
        }
        Rect bounds = img.getBitmapBounds();
        bounds.offset(img.getLeft() - ((int) this.mLastTouchPos.x), img.getTop() - ((int) this.mLastTouchPos.y));
        PinItemDragListener listener = new PinItemDragListener(this.mRequest, bounds, img.getBitmap().getWidth(), img.getWidth());
        Intent homeIntent = listener.addToIntent(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").setPackage(getPackageName()).setFlags(268435456));
        listener.initWhenReady();
        startActivity(homeIntent, ActivityOptions.makeCustomAnimation(this, 0, 17432577).toBundle());
        this.mFinishOnPause = true;
        view.startDragAndDrop(new ClipData(new ClipDescription("", new String[]{listener.getMimeType()}), new ClipData.Item("")), new View.DragShadowBuilder(view) {
            public void onDrawShadow(Canvas canvas) {
            }

            public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
                outShadowSize.set(10, 10);
                outShadowTouchPoint.set(5, 5);
            }
        }, (Object) null, 256);
        return false;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.mFinishOnPause) {
            finish();
        }
    }

    private void setupShortcut() {
        PinShortcutRequestActivityInfo shortcutInfo = new PinShortcutRequestActivityInfo(this.mRequest, this);
        WidgetItem item = new WidgetItem(shortcutInfo);
        this.mWidgetCell.getWidgetView().setTag(new PendingAddShortcutInfo(shortcutInfo));
        this.mWidgetCell.applyFromCellItem(item, this.mApp.getWidgetCache());
        this.mWidgetCell.ensurePreview();
    }

    private boolean setupWidget() {
        LauncherAppWidgetProviderInfo widgetInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(this, this.mRequest.getAppWidgetProviderInfo(this));
        if (widgetInfo.minSpanX > this.mIdp.numColumns || widgetInfo.minSpanY > this.mIdp.numRows) {
            return false;
        }
        this.mWidgetCell.setPreview(PinItemDragListener.getPreview(this.mRequest));
        this.mAppWidgetManager = AppWidgetManagerCompat.getInstance(this);
        this.mAppWidgetHost = new LauncherAppWidgetHost(this);
        this.mPendingWidgetInfo = new PendingAddWidgetInfo(widgetInfo);
        this.mPendingWidgetInfo.spanX = Math.min(this.mIdp.numColumns, widgetInfo.spanX);
        this.mPendingWidgetInfo.spanY = Math.min(this.mIdp.numRows, widgetInfo.spanY);
        this.mWidgetOptions = WidgetHostViewLoader.getDefaultOptionsForWidget(this, this.mPendingWidgetInfo);
        WidgetItem item = new WidgetItem(widgetInfo, getPackageManager(), this.mIdp);
        this.mWidgetCell.getWidgetView().setTag(this.mPendingWidgetInfo);
        this.mWidgetCell.applyFromCellItem(item, this.mApp.getWidgetCache());
        this.mWidgetCell.ensurePreview();
        return true;
    }

    public void onCancelClick(View v) {
        logCommand(3);
        finish();
    }

    public void onPlaceAutomaticallyClick(View v) {
        if (this.mRequest.getRequestType() == 1) {
            InstallShortcutReceiver.queueShortcut(new ShortcutInfoCompat(this.mRequest.getShortcutInfo()), this);
            logCommand(4);
            this.mRequest.accept();
            finish();
            return;
        }
        this.mPendingBindWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        if (this.mAppWidgetManager.bindAppWidgetIdIfAllowed(this.mPendingBindWidgetId, this.mRequest.getAppWidgetProviderInfo(this), this.mWidgetOptions)) {
            acceptWidget(this.mPendingBindWidgetId);
        } else {
            this.mAppWidgetHost.startBindFlow(this, this.mPendingBindWidgetId, this.mRequest.getAppWidgetProviderInfo(this), 1);
        }
    }

    private void acceptWidget(int widgetId) {
        InstallShortcutReceiver.queueWidget(this.mRequest.getAppWidgetProviderInfo(this), widgetId, this);
        this.mWidgetOptions.putInt(LauncherSettings.Favorites.APPWIDGET_ID, widgetId);
        this.mRequest.accept(this.mWidgetOptions);
        logCommand(4);
        finish();
    }

    public void onBackPressed() {
        logCommand(1);
        super.onBackPressed();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            int widgetId = data != null ? data.getIntExtra(LauncherSettings.Favorites.APPWIDGET_ID, this.mPendingBindWidgetId) : this.mPendingBindWidgetId;
            if (resultCode == -1) {
                acceptWidget(widgetId);
                return;
            }
            this.mAppWidgetHost.deleteAppWidgetId(widgetId);
            this.mPendingBindWidgetId = -1;
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_EXTRA_WIDGET_ID, this.mPendingBindWidgetId);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mPendingBindWidgetId = savedInstanceState.getInt(STATE_EXTRA_WIDGET_ID, this.mPendingBindWidgetId);
    }

    private void logCommand(int command) {
        getUserEventDispatcher().dispatchUserEvent(LoggerUtils.newLauncherEvent(LoggerUtils.newCommandAction(command), LoggerUtils.newItemTarget((View) this.mWidgetCell.getWidgetView(), this.mInstantAppResolver), LoggerUtils.newContainerTarget(10)), (Intent) null);
    }
}
