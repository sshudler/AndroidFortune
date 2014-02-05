package com.sergeis.androidfortune;

import java.io.IOException;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class FortuneWidget extends AppWidgetProvider {
		
	public static final String FORT_WIDGET_CLICK_ACTION = "FortuneWidgetClickAction";
	public static final String SCROLL_UP_CLICK_ACTION = "ScrollUpClickAction";
	public static final String SCROLL_DOWN_CLICK_ACTION = "ScrollDownClickAction";
	
	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		super.onReceive(context, intent);
		if (intent.getAction().equals(FORT_WIDGET_CLICK_ACTION)) {
			context.startService(new Intent(context, FortuneUpdateService.class));
		}
		else if (intent.getAction().equals(SCROLL_UP_CLICK_ACTION)) {
			Intent nIntent = new Intent(context, FortuneUpdateService.class);
			nIntent.setAction(SCROLL_UP_CLICK_ACTION);
			context.startService(nIntent);
		}
		else if (intent.getAction().equals(SCROLL_DOWN_CLICK_ACTION)) {
			Intent nIntent = new Intent(context, FortuneUpdateService.class);
			nIntent.setAction(SCROLL_DOWN_CLICK_ACTION);
			context.startService(nIntent);
		}
	}

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onUpdate(android.content.Context, android.appwidget.AppWidgetManager, int[])
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		context.startService(new Intent(context, FortuneUpdateService.class));
	}
	
	public static class FortuneUpdateService extends Service {

		private static final int SCROLL_DELTA = 50; 
		private FortuneFileReader mFortuneFileReader;
		private String mCurrFortuneStr;
		private int mCurrPosInStr;
		
		/* (non-Javadoc)
		 * @see android.app.Service#onStart(android.content.Intent, int)
		 */
		@Override
		public void onStart(Intent intent, int startId) {
			
			boolean isFirstUpdate = false;
			String intentAction = intent.getAction();
			
			if (intentAction == null) {
				try
				{
					if (mFortuneFileReader == null)
					{
						mFortuneFileReader = new FortuneFileReader();
						mFortuneFileReader.init(this);
						isFirstUpdate = true;
					}
					mCurrFortuneStr = mFortuneFileReader.getNextFortune();
					mCurrPosInStr = 0;
				}
				catch (IOException ioe) {
					mCurrFortuneStr = "Couldn't read any fortunes!";
				}
			}
			else {
				if (intentAction.equals(SCROLL_UP_CLICK_ACTION))
					mCurrPosInStr -= SCROLL_DELTA;
				else if (intentAction.equals(SCROLL_DOWN_CLICK_ACTION))
					mCurrPosInStr += SCROLL_DELTA;
				mCurrPosInStr = Math.max(0, mCurrPosInStr);
				mCurrPosInStr = Math.min(mCurrFortuneStr.length()-1, mCurrPosInStr);
			}
			
			RemoteViews rviews = new RemoteViews(getPackageName(), R.layout.fortwidget);
			rviews.setTextViewText(R.id.fortwidgetText, mCurrFortuneStr.substring(mCurrPosInStr));
			
			if (isFirstUpdate) 
			{
				// Clicking on the fortune text handler
				Intent listenerIntent = new Intent(this, FortuneWidget.class);
				listenerIntent.setAction(FORT_WIDGET_CLICK_ACTION);
				rviews.setOnClickPendingIntent(R.id.fortwidgetText, PendingIntent.getBroadcast(this, 0, listenerIntent, 0));
				
				// Scroll up handler
				listenerIntent = new Intent(this, FortuneWidget.class);
				listenerIntent.setAction(SCROLL_UP_CLICK_ACTION);
				rviews.setOnClickPendingIntent(R.id.scrollUp, PendingIntent.getBroadcast(this, 0, listenerIntent, 0));
				
				// Scroll down handler
				listenerIntent = new Intent(this, FortuneWidget.class);
				listenerIntent.setAction(SCROLL_DOWN_CLICK_ACTION);
				rviews.setOnClickPendingIntent(R.id.scrollDown, PendingIntent.getBroadcast(this, 0, listenerIntent, 0));
			}
			ComponentName cname = new ComponentName(this, FortuneWidget.class);
			AppWidgetManager.getInstance(this).updateAppWidget(cname, rviews);
		}

		/* (non-Javadoc)
		 * @see android.app.Service#onBind(android.content.Intent)
		 */
		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}
			
	}

}
