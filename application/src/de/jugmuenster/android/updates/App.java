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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import de.jugmuenster.android.updates.item.ContentProvider;
import de.jugmuenster.android.updates.item.Item;
import de.jugmuenster.android.updates.item.Source;
import de.jugmuenster.android.updates.item.Type;

public class App extends ListActivity implements Application {

    private static final long NOT_LOADED = -1L;
    static final int NOTIFICATION_NEW_ITEMS = 1;
    private long lastLoad = NOT_LOADED;

    public static final class NotificationData {
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
    }

    @Override
    protected void onStart() {
	super.onStart();
	loadItems();
    }

    @Override
    protected void onResume() {
	super.onResume();
	((NotificationManager) this.getSystemService(NOTIFICATION_SERVICE))
		.cancel(App.NOTIFICATION_NEW_ITEMS);
	loadItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	final boolean onCreateOptionsMenu = super.onCreateOptionsMenu(menu);
	final MenuItem update = menu.add("Aktualisieren");
	update.setOnMenuItemClickListener(new OnMenuItemClickListener() {

	    @Override
	    public boolean onMenuItemClick(MenuItem item) {
		new ItemsLoader(App.this, new ProgressDialogController())
			.execute();
		return true;
	    }
	});
	return onCreateOptionsMenu;
    }

    /**
     * @see de.jugmuenster.android.updates.Application#show(java.util.List)
     */
    @Override
    public void show(final List<Item> items) {
	final ArrayAdapter<Item> arrayAdapter = new ArrayAdapter<Item>(this,
		R.layout.list_item);
	for (Item i : items)
	    arrayAdapter.add(i);
	setListAdapter(arrayAdapter);
    }

    /**
     * @see de.jugmuenster.android.updates.Application#getAllItems()
     */
    @Override
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

    /**
     * @see de.jugmuenster.android.updates.Application#handleError(java.lang.Throwable,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void handleError(Throwable t, final String logTag,
	    final String logMessage, String userMessage) {
	ErrorHandler.handleError(this, t, logTag, logMessage, userMessage);
    }

    /**
     * @see de.jugmuenster.android.updates.Application#getProviders()
     */
    @Override
    public List<ContentProvider> getProviders() {
	final List<ContentProvider> providers = new ArrayList<ContentProvider>();
	try {
	    final Source sourceBlog = new Source("Blog", "B", Type.RSS,
		    new URI("http://www.jug-muenster.de/feed/"));
	    providers.add(sourceBlog.createProvider());

	    final Source sourceTwitter = new Source(
		    "Twitter",
		    "T",
		    Type.RSS,
		    new URI(
			    "http://search.twitter.com/search.rss?q=from%3AJug_MS%20include%3Aretweets"));
	    providers.add(sourceTwitter.createProvider());

	} catch (Exception e) {
	    handleError(e, "GetProviders", "Could not get item providers!",
		    "Beim Ermitteln der Nachrichtenquellen ist ein Fehler aufgetreten!");
	}
	return providers;
    }

    /**
     * @see de.jugmuenster.android.updates.Application#notify(de.jugmuenster.android.updates.App.NotificationData)
     */
    @Override
    public void notify(final NotificationData notificationData) {
	final Notification notification = this
		.newNotification(notificationData);
	((NotificationManager) this.getSystemService(NOTIFICATION_SERVICE))
		.notify(notificationData.notificationID, notification);
    }

    private SharedPreferences getPreferences() {
	return getPreferences(App.MODE_PRIVATE);
    }

    public String getPreference(final String name, final String defaultValue) {
	return getPreferences().getString(name, defaultValue);
    }

    public void setPreference(final String name, final String newValue) {
	final SharedPreferences preferences = getPreferences();
	preferences.edit().putString(name, newValue).commit();
    }

    Notification newNotification(final NotificationData notificationData) {
	final Notification notification = new Notification(R.drawable.icon,
		"Neue JUG Elemente", System.currentTimeMillis());
	notification.defaults = Notification.FLAG_ONLY_ALERT_ONCE
		| Notification.FLAG_AUTO_CANCEL;
	notification.when = System.currentTimeMillis();
	Context context = getApplicationContext();
	Intent notificationIntent = new Intent(this, App.class);
	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	notification.setLatestEventInfo(context, notificationData.contentTitle,
		notificationData.contentText, contentIntent);
	return notification;
    }

    void loadItems() {
	if (lastLoad != NOT_LOADED
		&& System.currentTimeMillis() - lastLoad < 15 * 60 * 1000)
	    return;
	new ItemsLoader(this, new ProgressDialogController()).execute();
	lastLoad = System.currentTimeMillis();
    }

}