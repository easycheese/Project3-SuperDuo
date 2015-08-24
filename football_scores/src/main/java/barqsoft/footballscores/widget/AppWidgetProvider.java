package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Random;

import barqsoft.footballscores.R;
import barqsoft.footballscores.activity.MainActivity;

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
            view.setOnClickPendingIntent(R.id.score, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, view);
        }
    }
    public static void updateWidget(AppWidgetManager appWidgetManager, RemoteViews view, int appWidgetId) { //TODO combine data from fetchService


        appWidgetManager.updateAppWidget(appWidgetId, view);
    }
}
