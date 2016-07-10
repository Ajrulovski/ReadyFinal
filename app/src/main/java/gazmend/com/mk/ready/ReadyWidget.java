package gazmend.com.mk.ready;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class ReadyWidget extends AppWidgetProvider {
    public static final String EXTRA_LIST_VIEW_ROW_NUMBER = "gazmend.com.mk.ready.EXTRA_LIST_VIEW_ROW_NUMBER";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //String widgetText = getLocalOrdersData(context);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ready_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        //views.setRemoteAdapter(R.id.orderlist,new WidgetArrayAdapter(context, widgetText));


        ArrayList<String> ldata = getLocalOrdersData(context);

        // RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, WidgetService.class);
        // passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.putStringArrayListExtra("data", ldata);

        // setting adapter to listview of the widget
        views.setRemoteAdapter(R.id.orderlist, svcIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.ready_widget);
            Intent startActivityIntent = new Intent(context, ClientOrdersHistoryActivity.class);
            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setPendingIntentTemplate(R.id.orderlist, startActivityPendingIntent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static ArrayList<String> getLocalOrdersData(Context cntx)
    {
        // Retrieve student records
        String URL = "content://com.gazmend.provider.Ready/orders";
        ArrayList<String> list = new ArrayList<String>();
        Uri orders = Uri.parse(URL);
        //Cursor c = managedQuery(students, null, null, null, "name");
        //Cursor cursor = getContentResolver().query(contentUri, null, null, null, Contacts.DISPLAY_NAME);
        Cursor c  = new CursorLoader(cntx, orders, null, null, null, null).loadInBackground();
        String someorder = "";
        if (c.moveToFirst()) {
            do{
                someorder = c.getString(c.getColumnIndex(OrdersProvider._ID)) +
                        "|" +  c.getString(c.getColumnIndex( OrdersProvider.ORDERNAME)) +
                        "|" + c.getString(c.getColumnIndex( OrdersProvider.BUSNAME));
                list.add(someorder);
            } while (c.moveToNext());
        }

        return list;
    }
}

