package gazmend.com.mk.ready;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

/**
 * Created by Gazmend on 7/7/2016.
 */

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        ArrayList<String> test = intent.getStringArrayListExtra("data");
        RemoteViewsFactory listProvidder = new CallsListRemoteViewsFactory(this.getApplicationContext(), intent, test);
        return listProvidder;
    }

}
