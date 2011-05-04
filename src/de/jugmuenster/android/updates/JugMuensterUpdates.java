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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import de.jugmuenster.android.updates.item.ContentProvider;
import de.jugmuenster.android.updates.item.HttpURIContentProvider;
import de.jugmuenster.android.updates.item.Item;
import de.jugmuenster.android.updates.item.Source;
import de.jugmuenster.android.updates.item.Type;

public class JugMuensterUpdates extends ListActivity {

    private final List<ContentProvider> providers = new ArrayList<ContentProvider>();
    private final ArrayAdapter<Item> arrayAdapter = new ArrayAdapter<Item>(
	    this, R.layout.list_item);

    public JugMuensterUpdates() throws URISyntaxException {
	final URI jugMuensterHomepageFeed = new URI(
		"http://www.jug-muenster.de/feed/");
	providers.add(new HttpURIContentProvider(new Source(Type.RSS,
		jugMuensterHomepageFeed)));
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

    enum CurrentElement {
	TITLE, LINK, DESCRIPTION;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getListView()
		.setOnItemClickListener(new OnClickShowItemLinkInBrowser());
	startLoadingItems();

    }

    private void startLoadingItems() {
	// TODO: in einen Background Thread verschieben
	show(getAllItems());
    }

    private void show(final List<Item> items) {
	for (Item i : items)
	    arrayAdapter.add(i);
	setListAdapter(arrayAdapter);
    }

    private List<Item> getAllItems() {
	final List<Item> items = new ArrayList<Item>();

	try {
	    for (ContentProvider p : providers) {
		items.addAll(p.extract());
	    }

	} catch (Exception e) {
	    // TODO: Fehler Nachricht besser anzeigen
	    throw new RuntimeException(e);
	}
	return items;
    }
}