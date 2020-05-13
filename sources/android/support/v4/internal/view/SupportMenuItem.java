package android.support.v4.internal.view;

import android.support.annotation.RestrictTo;
import android.support.v4.view.ActionProvider;
import android.view.MenuItem;
import android.view.View;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public interface SupportMenuItem extends MenuItem {
    public static final int SHOW_AS_ACTION_ALWAYS = 2;
    public static final int SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW = 8;
    public static final int SHOW_AS_ACTION_IF_ROOM = 1;
    public static final int SHOW_AS_ACTION_NEVER = 0;
    public static final int SHOW_AS_ACTION_WITH_TEXT = 4;

    boolean collapseActionView();

    boolean expandActionView();

    View getActionView();

    CharSequence getContentDescription();

    ActionProvider getSupportActionProvider();

    CharSequence getTooltipText();

    boolean isActionViewExpanded();

    MenuItem setActionView(int i);

    MenuItem setActionView(View view);

    SupportMenuItem setContentDescription(CharSequence charSequence);

    void setShowAsAction(int i);

    MenuItem setShowAsActionFlags(int i);

    SupportMenuItem setSupportActionProvider(ActionProvider actionProvider);

    SupportMenuItem setSupportOnActionExpandListener(MenuItem.OnActionExpandListener onActionExpandListener);

    SupportMenuItem setTooltipText(CharSequence charSequence);
}
