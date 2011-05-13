/*
 * Copyright (c) 2011, Java User Group Münster, NRW, Germany, 
 * http://www.jug-muenster.de
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  - 	Redistributions of source code must retain the above copyright notice, this 
 * 	list of conditions and the following disclaimer.
 *  - 	Redistributions in binary form must reproduce the above copyright notice, 
 * 	this list of conditions and the following disclaimer in the documentation 
 * 	and/or other materials provided with the distribution.
 *  - 	Neither the name of the Java User Group Münster nor the names of its contributors may 
 * 	be used to endorse or promote products derived from this software without 
 * 	specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.jugmuenster.android.updates;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import de.jugmuenster.android.updates.item.ContentProvider;
import de.jugmuenster.android.updates.item.Item;
import de.jugmuenster.android.updates.item.Source;
import de.jugmuenster.android.updates.item.Type;

public class App extends ListActivity {

    static final int NOTIFICATION_NEW_ITEMS = 1;

    static final class NotificationData {
	final int notificationID;
	final CharSequence contentTitle;
	final CharSequence contentText;

	public NotificationData(int notificationID, CharSequence contentTitle,
		CharSequence contentText) {
	    this.notificationID = notificationID;
	    this.contentTitle = contentTitle;
	    this.contentText = contentText;
	}
    }

    private final class OnClickShowItemLinkInBrowser implements
	    OnItemClickListener {
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		long arg3) {
	    final Item item = (Item) arg0.getItemAtPosition(arg2);
	    Intent browserIntent = new Intent("android.intent.action.VIEW",
		    Uri.parse(item.getLink()));
	    startActivity(browserIntent);
	}
    }

    public App() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getListView()
		.setOnItemClickListener(new OnClickShowItemLinkInBrowser());
	loadItems();
	final Intent intent = new Intent(this, UpdateService.class);
	final ComponentName componentName = startService(intent);
	// (intent, new ServiceConnection() {
	//
	// private IBinder service;
	//
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// Toast.makeText(App.this, "UpdateService connected!",
	// Toast.LENGTH_SHORT).show();
	// this.service = service;
	// }
	//
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	// Toast.makeText(App.this, "UpdateService disconnected!",
	// Toast.LENGTH_SHORT).show();
	// this.service = null;
	// }
	// }, 0);
	Toast.makeText(App.this,
		MessageFormat.format("Started service {0}!", componentName),
		Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	final boolean onCreateOptionsMenu = super.onCreateOptionsMenu(menu);
	final MenuItem update = menu.add("Aktualisieren");
	update.setOnMenuItemClickListener(new OnMenuItemClickListener() {

	    @Override
	    public boolean onMenuItemClick(MenuItem item) {
		new ItemsLoader(App.this).execute();
		return false;
	    }
	});
	return onCreateOptionsMenu;
    }

    public void show(final List<Item> items) {
	final ArrayAdapter<Item> arrayAdapter = new ArrayAdapter<Item>(this,
		R.layout.list_item);
	for (Item i : items)
	    arrayAdapter.add(i);
	setListAdapter(arrayAdapter);
    }

    public List<Item> getAllItems() {
	final List<Item> items = new ArrayList<Item>();
	for (ContentProvider p : getProviders()) {
	    try {
		items.addAll(p.extract());
	    } catch (Exception e) {
		handleError(e, "GetItems", "Could not fetch new items!",
			"Beim Ermitteln der neuen Beiträge ist ein Fehler aufgetreten!");
	    }
	}
	Collections.sort(items);
	return items;
    }

    public void handleError(Throwable t, final String logTag,
	    final String logMessage, String userMessage) {
	ErrorHandler.handleError(this, t, logTag, logMessage, userMessage);
    }

    public List<ContentProvider> getProviders() {
	final List<ContentProvider> providers = new ArrayList<ContentProvider>();
	try {
	    final Source source = new Source("Blog", Type.RSS, new URI(
		    "http://www.jug-muenster.de/feed/"));
	    providers.add(source.createProvider());
	} catch (Exception e) {
	    handleError(e, "GetProviders", "Could not get item providers!",
		    "Beim Ermitteln der Nachrichtenquellen ist ein Fehler aufgetreten!");
	}
	return providers;
    }

    public void notify(final NotificationData notificationData) {
	final Notification notification = this
		.newNotification(notificationData);
	((NotificationManager) this.getSystemService(NOTIFICATION_SERVICE))
		.notify(notificationData.notificationID, notification);
    }

    public SharedPreferences getPreferences() {
	return getPreferences(App.MODE_PRIVATE);
    }

    Notification newNotification(final NotificationData notificationData) {
	final Notification notification = new Notification(R.drawable.icon,
		"Neue JUG Elemente", System.currentTimeMillis());
	notification.when = System.currentTimeMillis();
	Context context = getApplicationContext();
	Intent notificationIntent = new Intent(this, App.class);
	PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		notificationIntent, 0);
	notification.setLatestEventInfo(context, notificationData.contentTitle,
		notificationData.contentText, contentIntent);
	return notification;
    }

    void loadItems() {
	new ItemsLoader(this).execute();
    }

}