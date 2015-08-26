package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import barqsoft.footballscores.R;
import barqsoft.footballscores.activity.MainActivity;
import barqsoft.footballscores.content.DatabaseContract;
import barqsoft.footballscores.fragment.MainScreenFragment;
import barqsoft.footballscores.model.Match;

/**
 * Created by Laptop on 8/23/2015.
 */
public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            int number = (new Random().nextInt(100));

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            view.setTextViewText(R.id.score, number + "");
//            views.setOnClickPendingIntent(R.id.button, pendingIntent);


//            // Register an onClickListener
//            Intent intent = new Intent(context, AppWidgetProvider.class);
//
//            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            String[] dateArgs = new String[1];
            Date fragmentdate = new Date(System.currentTimeMillis());
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

            dateArgs[0] = mformat.format(fragmentdate);


            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(DatabaseContract.scores_table.buildScoreWithDate(),
                    DatabaseContract.scores_table.allColumns, null, null, null);
//                    DatabaseContract.scores_table.allColumns,null,dateArgs,null);

            CursorLoader cx = new CursorLoader(context, DatabaseContract.scores_table.buildScoreWithDate(),
                    null,null,dateArgs,null);

            Boolean result = c.moveToFirst();
//            Match match = new Match(c);
//            getLoaderManager().initLoader(SCORES_LOADER,null,this);
            c.close();

            view.setOnClickPendingIntent(R.id.score, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, view);
        }
    }
    public static void updateWidget(AppWidgetManager appWidgetManager, RemoteViews view, int appWidgetId) { //TODO combine data from fetchService


        appWidgetManager.updateAppWidget(appWidgetId, view);
    }


}
