package com.szchoiceway.index;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class AddAdapter extends BaseAdapter {
    public static final int ITEM_APPLICATION = 2;
    public static final int ITEM_APPWIDGET = 1;
    public static final int ITEM_SHORTCUT = 0;
    public static final int ITEM_WALLPAPER = 3;
    private final LayoutInflater mInflater;
    private final ArrayList<ListItem> mItems = new ArrayList<>();

    public class ListItem {
        public final int actionTag;
        public final Drawable image;
        public final CharSequence text;

        public ListItem(Resources res, int textResourceId, int imageResourceId, int actionTag2) {
            this.text = res.getString(textResourceId);
            if (imageResourceId != -1) {
                this.image = res.getDrawable(imageResourceId);
            } else {
                this.image = null;
            }
            this.actionTag = actionTag2;
        }
    }

    public AddAdapter(Launcher launcher) {
        this.mInflater = (LayoutInflater) launcher.getSystemService("layout_inflater");
        this.mItems.add(new ListItem(launcher.getResources(), R.string.group_wallpapers, R.mipmap.ic_launcher_wallpaper, 3));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ListItem item = (ListItem) getItem(position);
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.add_list_item, parent, false);
        }
        TextView textView = (TextView) convertView;
        textView.setTag(item);
        textView.setText(item.text);
        textView.setCompoundDrawablesWithIntrinsicBounds(item.image, (Drawable) null, (Drawable) null, (Drawable) null);
        return convertView;
    }

    public int getCount() {
        return this.mItems.size();
    }

    public Object getItem(int position) {
        return this.mItems.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }
}
