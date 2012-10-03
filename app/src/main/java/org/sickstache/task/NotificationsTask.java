package org.sickstache.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import org.sickbeard.History;
import org.sickbeard.HistoryItem;
import org.sickstache.HistoryActivity;
import org.sickstache.NotificationDismissService;
import org.sickstache.helper.Preferences;

import java.io.*;

// marking this deprecated just so no one tries anything funny
@Deprecated
public class NotificationsTask extends SickTask<Void,Void,History> {
	
	// current last item that the user has seen
	private HistoryItem last = null;
	// the last item that we have seen that may or may not have been seen
	// when the user dismisses the notification this will be assigned to last
	private HistoryItem lastOnDismiss = null;
	
	protected Exception e;
	
	private Context c;
	private NotificationManager nm;
	
	public NotificationsTask( Context c, NotificationManager nm ) {
		this.c = c;
		this.nm = nm;
	}
	
	public static void onNotificationDismiss( Context c )
	{
		File cache = new File( c.getExternalCacheDir(), "history" );
		File lastFile = new File( cache, "last.ser" );
		File lastOnDismissFile = new File( cache, "lastOnDismiss.ser" );
		lastOnDismissFile.renameTo(lastFile);
	}
	
	public static void updateLastHistoryItem( Context c, HistoryItem last )
	{
//		NotificationsTask.last = last;
//		lastOnDismiss = last;
	}
	
	private void serializeLast( HistoryItem last )
	{
		try {
			File cache = new File( c.getExternalCacheDir(), "history" );
			File lastFile = new File( cache, "last.ser" );
			FileOutputStream fileOut = new FileOutputStream(lastFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(last);
		} catch (Exception e) {
			;
		}
	}
	
	private HistoryItem deserializeLast()
	{
		try {
			File cache = new File( c.getExternalCacheDir(), "history" );
			File last = new File( cache, "last.ser" );
			if ( last.exists() == false )
				return null;
			FileInputStream fileIn = new FileInputStream(last);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			return (HistoryItem)in.readObject();
		} catch (Exception e) {
			return null;
		}
	}
	
	private void serializeLastOnDismiss( HistoryItem lastOnDismiss )
	{
		try {
			File cache = new File( c.getExternalCacheDir(), "history" );
			File lastFile = new File( cache, "lastOnDismiss.ser" );
			FileOutputStream fileOut = new FileOutputStream(lastFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(last);
		} catch (Exception e) {
			;
		}
	}
	
	private HistoryItem deserializeLastOnDismiss()
	{
		try {
			File cache = new File( c.getExternalCacheDir(), "history" );
			File last = new File( cache, "lastOnDismiss.ser" );
			if ( last.exists() == false )
				return null;
			FileInputStream fileIn = new FileInputStream(last);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			return (HistoryItem)in.readObject();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getTaskLogName() {
		return "NotificationsTask";
	}
	
	@Override
	protected History doInBackground(Void... arg0) {
		try {
			if ( last == null )
				last = deserializeLast();
			History h = Preferences.getSingleton(c).getSickBeard().history(Preferences.getSingleton(c).getHistoryMax());
			if ( h.items.size() > 0 ) {
				if ( last == null )
					serializeLast( h.items.get(0) );
				else
					serializeLastOnDismiss( h.items.get(0) );
			}
			return h;
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
				// used to tell the service that it needs to change the last item
				Notification n = new NotificationCompat.BigTextStyle(new NotificationCompat.Builder(c)
					.setContentTitle( i + " New SickBeard Items")
					.setTicker("New SickBeard Items")
					.setNumber(i)
					.setContentText( downloaded + " items downloaded, " + snatched + " items snatched")
					.setSmallIcon(android.R.drawable.stat_notify_chat)
					.setWhen(System.currentTimeMillis())
					.setContentIntent( PendingIntent.getActivity(c, 0, new Intent(c,HistoryActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
					.setDeleteIntent( PendingIntent.getService(c, 0, new Intent(c, NotificationDismissService.class), PendingIntent.FLAG_ONE_SHOT))
					.setAutoCancel(true))
					.bigText(bigText.toString())
					.build();
				
				nm.notify(0, n);
			}
			// don't forget to set the last item on dismiss for next time
			lastOnDismiss = result.items.get(0);
		}
	}
	
}
