package com.example.amrahmed.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import com.example.amrahmed.bakingapp.Activities.MainActivity;
import com.example.amrahmed.bakingapp.R;
import com.squareup.picasso.Picasso;
/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String WIDGET_PIC_KEY= context.getResources().getString(R.string.WIDGET_PIC_KEY);
        String WIDGET_NAME_KEY = context.getResources().getString(R.string.WIDGET_NAME_KEY);
        String WIDGET_INGREDIENTS_KEY = context.getResources().getString(R.string.WIDGET_INGREDIENTS_KEY);


        String img = pref.getString(WIDGET_PIC_KEY, null);
        String name =pref.getString(WIDGET_NAME_KEY, null);
        String ingredients=pref.getString(WIDGET_INGREDIENTS_KEY, null);


        if(ingredients != null) {
            try {
                Picasso.get()
                        .load(img)
                        .error(R.drawable.splashimg)
                        .into(views, R.id.widget_img, new int[]{appWidgetId});
            } catch (Exception e) {
                e.printStackTrace();
            }
            views.setTextViewText(R.id.widget_name, name);
            views.setTextViewText(R.id.widget_ingredients, ingredients);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_img, pendingIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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
}

