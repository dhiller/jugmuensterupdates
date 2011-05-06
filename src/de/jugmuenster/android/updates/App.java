/*
 * Copyright (c) 2011, Java User Group M�nster, NRW, Germany, 
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
 *  - 	Neither the name of the Java User Group M�nster nor the names of its contributors may 
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import de.jugmuenster.android.util.Test;

public class App extends ListActivity {

    private final Preferences userNodeForPackage = Preferences
	    .userNodeForPackage(App.class);
    private Date latestItemDate = new Date(0);

    private static final class ItemsLoader extends
	    AsyncTask<Object, Integer, List<Item>> {

	private final App a;
	private final ProgressDialog progressDialog;

	private ItemsLoader(App a) {
	    this.a = Test.notNull(a);
	    progressDialog = new ProgressDialog(a);
	}

	@Override
	protected void onPreExecute() {
	    progressDialog.setTitle("Lade Elemente");
	    progressDialog.setIndeterminate(true);
	    progressDialog.show();
	    super.onPreExecute();
	}

	@Override
	protected List<Item> doInBackground(Object... unused) {
	    Date newLatestItemDate = a.getLatestItemDate();
	    final List<Item> allItems = a.getAllItems(a.getProviders());
	    for (Item i : allItems) {
		if (i.getFrom().compareTo(a.getLatestItemDate()) > 0) {
		    i.setNew();
		    if (i.getFrom().compareTo(newLatestItemDate) > 0)
			newLatestItemDate = i.getFrom();
		}
	    }
	    a.setLatestItemDate(newLatestItemDate);
	    return allItems;
	}

	@Override
	protected void onPostExecute(List<Item> result) {
	    a.show(result);
	    progressDialog.dismiss();
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
	restoreLatestItemDate();
	new ItemsLoader(this).execute();
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

    private void restoreLatestItemDate() {
	final String savedLatestItemDate = userNodeForPackage.get(
		"latestItemDate", null);
	if (savedLatestItemDate != null)
	    try {
		latestItemDate = new SimpleDateFormat()
			.parse(savedLatestItemDate);
	    } catch (ParseException e) {
		handleError(e, "latestItemDate",
			"Could not restore latestItemDate",
			"Konnte letzte Aktualisierung nicht wieder herstellen!");
	    }
    }

    @Override
    protected void onPause() {
	super.onPause();
	userNodeForPackage.put("latestItemDate",
		new SimpleDateFormat().format(getLatestItemDate()));
    }

    private void show(final List<Item> items) {
	final ArrayAdapter<Item> arrayAdapter = new ArrayAdapter<Item>(this,
		R.layout.list_item);
	for (Item i : items)
	    arrayAdapter.add(i);
	setListAdapter(arrayAdapter);
    }

    private List<Item> getAllItems(List<ContentProvider> providers) {
	final List<Item> items = new ArrayList<Item>();
	for (ContentProvider p : providers) {
	    try {
		items.addAll(p.extract());
	    } catch (Exception e) {
		handleError(e, "GetItems", "Could not fetch new items!",
			"Beim Ermitteln der neuen Beitr�ge ist ein Fehler aufgetreten!");
	    }
	}
	Collections.sort(items);
	return items;
    }

    void handleError(Throwable t, final String logTag, final String logMessage,
	    String userMessage) {
	Log.e(logTag, logMessage, t);
	final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Fehler");
	builder.setMessage(MessageFormat.format("{0}\n\n{1}", userMessage, t));
	builder.create().show();
    }

    private List<ContentProvider> getProviders() {
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

    private Date getLatestItemDate() {
	return latestItemDate;
    }

    private void setLatestItemDate(Date latestItemDate) {
	this.latestItemDate = latestItemDate;
    }

}