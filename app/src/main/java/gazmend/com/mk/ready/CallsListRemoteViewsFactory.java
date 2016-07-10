package gazmend.com.mk.ready;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import static android.R.style.Widget;

public class CallsListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    //private CallsDataSource mDataSource;
    private ArrayList<String> mCallsList;
    private Context mContext = null;
    //private

    public CallsListRemoteViewsFactory(Context context, Intent intent, ArrayList<String> data) {
        this.mContext = context;
        this.mCallsList = data;
    }

    @Override
    public int getCount() {
        return mCallsList.size();
        //return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.widget_row);
        String values = mCallsList.get(position);
        // split the input string
        String[] recordvals = values.split("\\|");
        String jsonorder = recordvals[1];
        String orderdate = recordvals[2];

        //split the jsonorder
        String[] ordervals = jsonorder.split("\\,");
        String finalordertext = "";

        for(int i=0; i<ordervals.length;i++)
        {
            String ordertext = ordervals[i].replace("\"","");
            String[] orderitems = ordertext.split("\\:")[1].split("\\;");
            finalordertext = finalordertext +orderitems[2]+" x "+orderitems[0]+",";
        }

        row.setTextViewText(R.id.busname, orderdate);
        row.setTextViewText(R.id.order, finalordertext);

        //row.setOnClickPendingIntent();
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(ReadyWidget.EXTRA_LIST_VIEW_ROW_NUMBER, position);
        row.setOnClickFillInIntent(R.id.orderlist, fillInIntent);

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        //mDataSource = new CallsDataSrouce(mContext);
        //mDataSource.open();
        //mCallsList = mDataSource.findAllCalls();
        //mCallsList = getLocalOrdersData(mContext);
    }

    @Override
    public void onDataSetChanged() {
        //mCallsList = mDataSource.findAllCalls();
        //mCallsList = getLocalOrdersData(mContext);
    }

    @Override
    public void onDestroy() {
    }
}