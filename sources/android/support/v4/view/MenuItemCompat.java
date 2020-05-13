package android.support.v4.view;

import android.annotation.TargetApi;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.os.BuildCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public final class MenuItemCompat {
    static final MenuVersionImpl IMPL;
    @Deprecated
    public static final int SHOW_AS_ACTION_ALWAYS = 2;
    @Deprecated
    public static final int SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW = 8;
    @Deprecated
    public static final int SHOW_AS_ACTION_IF_ROOM = 1;
    @Deprecated
    public static final int SHOW_AS_ACTION_NEVER = 0;
    @Deprecated
    public static final int SHOW_AS_ACTION_WITH_TEXT = 4;
    private static final String TAG = "MenuItemCompat";

    interface MenuVersionImpl {
        CharSequence getContentDescription(MenuItem menuItem);

        CharSequence getTooltipText(MenuItem menuItem);

        void setContentDescription(MenuItem menuItem, CharSequence charSequence);

        void setTooltipText(MenuItem menuItem, CharSequence charSequence);
    }

    @Deprecated
    public interface OnActionExpandListener {
        boolean onMenuItemActionCollapse(MenuItem menuItem);

        boolean onMenuItemActionExpand(MenuItem menuItem);
    }

    static class MenuItemCompatBaseImpl implements MenuVersionImpl {
        MenuItemCompatBaseImpl() {
        }

        public void setContentDescription(MenuItem item, CharSequence contentDescription) {
        }

        public CharSequence getContentDescription(MenuItem item) {
            return null;
        }

        public void setTooltipText(MenuItem item, CharSequence tooltipText) {
        }

        public CharSequence getTooltipText(MenuItem item) {
            return null;
        }
    }

    @TargetApi(26)
    static class MenuItemCompatApi26Impl extends MenuItemCompatBaseImpl {
        MenuItemCompatApi26Impl() {
        }

        public void setContentDescription(MenuItem item, CharSequence contentDescription) {
            item.setContentDescription(contentDescription);
        }

        public CharSequence getContentDescription(MenuItem item) {
            return item.getContentDescription();
        }

        public void setTooltipText(MenuItem item, CharSequence tooltipText) {
            item.setTooltipText(tooltipText);
        }

        public CharSequence getTooltipText(MenuItem item) {
            return item.getTooltipText();
        }
    }

    static {
        if (BuildCompat.isAtLeastO()) {
            IMPL = new MenuItemCompatApi26Impl();
        } else {
            IMPL = new MenuItemCompatBaseImpl();
        }
    }

    @Deprecated
    public static void setShowAsAction(MenuItem item, int actionEnum) {
        item.setShowAsAction(actionEnum);
    }

    @Deprecated
    public static MenuItem setActionView(MenuItem item, View view) {
        return item.setActionView(view);
    }

    @Deprecated
    public static MenuItem setActionView(MenuItem item, int resId) {
        return item.setActionView(resId);
    }

    @Deprecated
    public static View getActionView(MenuItem item) {
        return item.getActionView();
    }

    public static MenuItem setActionProvider(MenuItem item, ActionProvider provider) {
        if (item instanceof SupportMenuItem) {
            return ((SupportMenuItem) item).setSupportActionProvider(provider);
        }
        Log.w(TAG, "setActionProvider: item does not implement SupportMenuItem; ignoring");
        return item;
    }

    public static ActionProvider getActionProvider(MenuItem item) {
        if (item instanceof SupportMenuItem) {
            return ((SupportMenuItem) item).getSupportActionProvider();
        }
        Log.w(TAG, "getActionProvider: item does not implement SupportMenuItem; returning null");
        return null;
    }

    @Deprecated
    public static boolean expandActionView(MenuItem item) {
        return item.expandActionView();
    }

    @Deprecated
    public static boolean collapseActionView(MenuItem item) {
        return item.collapseActionView();
    }

    @Deprecated
    public static boolean isActionViewExpanded(MenuItem item) {
        return item.isActionViewExpanded();
    }

    @Deprecated
    public static MenuItem setOnActionExpandListener(MenuItem item, final OnActionExpandListener listener) {
        return item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            public boolean onMenuItemActionExpand(MenuItem item) {
                return listener.onMenuItemActionExpand(item);
            }

            public boolean onMenuItemActionCollapse(MenuItem item) {
                return listener.onMenuItemActionCollapse(item);
            }
        });
    }

    public static void setContentDescription(MenuItem item, CharSequence contentDescription) {
        if (item instanceof SupportMenuItem) {
            ((SupportMenuItem) item).setContentDescription(contentDescription);
        } else {
            IMPL.setContentDescription(item, contentDescription);
        }
    }

    public static CharSequence getContentDescription(MenuItem item) {
        if (item instanceof SupportMenuItem) {
            return ((SupportMenuItem) item).getContentDescription();
        }
        return IMPL.getContentDescription(item);
    }

    public static void setTooltipText(MenuItem item, CharSequence tooltipText) {
        if (item instanceof SupportMenuItem) {
            ((SupportMenuItem) item).setTooltipText(tooltipText);
        } else {
            IMPL.setTooltipText(item, tooltipText);
        }
    }

    public static CharSequence getTooltipText(MenuItem item) {
        if (item instanceof SupportMenuItem) {
            return ((SupportMenuItem) item).getTooltipText();
        }
        return IMPL.getTooltipText(item);
    }

    private MenuItemCompat() {
    }
}
