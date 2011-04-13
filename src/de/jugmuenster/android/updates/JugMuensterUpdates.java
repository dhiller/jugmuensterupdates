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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import de.jugmuenster.android.updates.rss.RssContentProvider;
import de.jugmuenster.android.updates.rss.RssItem;
import de.jugmuenster.android.updates.rss.RssItemsExtractor;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class JugMuensterUpdates extends ListActivity {

    final List<RssItem> items = new ArrayList<RssItem>();

    enum CurrentElement {
	TITLE, LINK, DESCRIPTION;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	final List<String> strings = new ArrayList<String>();

	try {
	    InputStream content = provideContent();
	    try {

		items.clear();
		items.addAll(new RssItemsExtractor().extract(content));

	    } finally {
		content.close();
	    }

	} catch (Exception e) {
	    strings.add(e.toString());
	}

	ArrayAdapter<RssItem> arrayAdapter = new ArrayAdapter<RssItem>(this,
		R.layout.list_item);
	for (RssItem i : items)
	    arrayAdapter.add(i);
	setListAdapter(arrayAdapter);

	getListView().setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		    long arg3) {
		final RssItem item = (RssItem) arg0.getItemAtPosition(arg2);
		Intent browserIntent = new Intent("android.intent.action.VIEW",
			Uri.parse(item.link));
		startActivity(browserIntent);
	    }
	});

    }

    private InputStream provideContent() throws Exception {
	class DefaultRssContentProvider implements RssContentProvider {

	    @Override
	    public InputStream provideContent() throws Exception {
		URI uri = new URI("http://www.jug-muenster.de/feed/");
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		request.setURI(uri);
		HttpResponse response = client.execute(request);
		InputStream content = response.getEntity().getContent();
		return content;
	    }

	}
	return new DefaultRssContentProvider().provideContent();
    }
}