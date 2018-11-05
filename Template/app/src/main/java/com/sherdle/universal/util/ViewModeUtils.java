package com.sherdle.universal.util;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sherdle.universal.Config;
import com.sherdle.universal.R;
import com.sherdle.universal.providers.facebook.FacebookItem;
import com.sherdle.universal.providers.rss.ui.RssFragment;
import com.sherdle.universal.providers.woocommerce.ui.WooCommerceFragment;
import com.sherdle.universal.providers.wordpress.ui.WordpressFragment;
import com.sherdle.universal.providers.youtube.ui.YoutubeFragment;

public class ViewModeUtils {

    public static final int UNKNOWN = -1;
    public static final int COMPACT = 0;
    public static final int NORMAL = 1;
    public static final int IMMERSIVE = 2;

    private Class mClass;
    private Context context;

    public ViewModeUtils(Context context, Class mClass){
        this.context = context;
        this.mClass = mClass;
    }

    public void inflateOptionsMenu(Menu menu, MenuInflater inflater){
        if (!Config.EDITABLE_VIEWMODE) return;

        inflater.inflate(R.menu.view_mode_menu, menu);
        if (!immersiveSupported())
            menu.findItem(R.id.immersive).setVisible(false);

        switch (getViewMode()){
            case NORMAL:
                menu.findItem(R.id.normal).setChecked(true);
                break;
            case IMMERSIVE:
                menu.findItem(R.id.immersive).setChecked(true);
                break;
            case COMPACT:
                menu.findItem(R.id.compact).setChecked(true);
                break;
        }
    }

    private boolean immersiveSupported(){
        return (mClass.equals(WordpressFragment.class) || mClass.equals(YoutubeFragment.class));
    }


    public boolean handleSelection(MenuItem item, ChangeListener listener) {
        switch (item.getItemId()) {
            case R.id.immersive:
                item.setChecked(true);
                saveToPreferences(IMMERSIVE);
                listener.modeChanged();
                return true;
            case R.id.normal:
                item.setChecked(true);
                saveToPreferences(NORMAL);
                listener.modeChanged();
                return true;
            case R.id.compact:
                item.setChecked(true);
                saveToPreferences(COMPACT);
                listener.modeChanged();
                return true;
        }
        return false;
    }

    public void saveToPreferences(int item){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(mClass.getName(), item).apply();
    }

    private int getFromPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(mClass.getName(), UNKNOWN);
    }

    public int getViewMode(){
        int viewMode = UNKNOWN;
        if (Config.EDITABLE_VIEWMODE)
            viewMode = getFromPreferences();
        if (viewMode != UNKNOWN) return viewMode;

        if (mClass.equals(WordpressFragment.class)){
            viewMode = Config.WP_ROW_MODE;
        } else if (mClass.equals(RssFragment.class)){
            viewMode = Config.RSS_ROW_MODE;
        } else if (mClass.equals(YoutubeFragment.class)){
            viewMode = Config.YT_ROW_MODE;
        } else if (mClass.equals(WooCommerceFragment.class)){
            viewMode = Config.WC_ROW_MODE;
        }

        if ((viewMode == IMMERSIVE && !immersiveSupported())
                || viewMode == UNKNOWN) viewMode = NORMAL;

        return viewMode;
    }

    public interface ChangeListener {
        void modeChanged();
    }

}
