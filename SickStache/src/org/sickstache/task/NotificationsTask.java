package org.sickstache.task;

import org.sickbeard.History;
import org.sickbeard.HistoryItem;
import org.sickstache.HistoryActivity;
import org.sickstache.helper.Preferences;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationsTask extends SickTask<Void,Void,History> {
	
	private static HistoryItem last;
	
	protected Exception e;
	
	private Context c;
	private NotificationManager nm;
	
	public NotificationsTask( Context c, NotificationManager nm ) {
		this.c = c;
		this.nm = nm;
	}

	@Override
	public String getTaskLogName() {
		return "NotificationsTask";
	}
	
	@Override
	protected History doInBackground(Void... arg0) {
		try {
			// TODO make it only get ten items at a time
			return Preferences.singleton.getSickBeard().history(Preferences.singleton.getHistoryMax());
		} catch (Exception e) {
			this.e = e;
			return null;
		}
	}

	@Override
	protected void onPostExecute(History result) {
		super.onPostExecute(result);
		// If this happens then there is no point to go on
		if ( result == null || result.items.size() <= 0 )
			return;
		
		if ( last == null ) {
			last = result.items.get(0);
		} else {
			int i = 0;
			for ( ; i < result.items.size(); i++ ) {
				HistoryItem item = result.items.get(i);
				// deep comparison
				// the only way this will fail is if the same show is download in a different quality right after it was downloaded
				if ( item.show.compareTo(last.show) == 0
						&& item.season.compareTo(last.season) == 0
						&& item.episode.compareTo(last.episode) == 0
						&& item.status.compareTo(last.status) == 0
						&& item.date.compareTo(item.date) == 0 ) {
//					i = 4; // This is only here for testing purposes
					break;
				}
			}
			// all the items before i are new
			// build notification
			if ( i > 0 ) {
				StringBuilder bigText = new StringBuilder();
				int downloaded = 0;
				int snatched = 0;
				for ( int j=0; j < i; j++ ) {
					HistoryItem current = result.items.get(j);
					bigText.append( String.format("%s - %sx%s %s\n", current.show, current.season, current.episode, current.status ) );
					if ( current.status.compareTo("Downloaded") == 0 ) {
						downloaded++;
					} else {
						snatched++;
					}
				}
				Notification n = new NotificationCompat.BigTextStyle(new NotificationCompat.Builder(c)
					.setContentTitle( i + " New SickBeard Items")
					.setContentText( downloaded + " items downloaded, " + snatched + " items snatched")
					.setSmallIcon(android.R.drawable.stat_notify_chat)
					.setContentIntent( PendingIntent.getActivity(c, 0, new Intent(c,HistoryActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
					.setAutoCancel(true))
					.bigText(bigText.toString())
					.build();
				
				nm.notify(0, n);
			}
			// don't forget to set the last item for next time
			last = result.items.get(0);
		}
	}
	
}
