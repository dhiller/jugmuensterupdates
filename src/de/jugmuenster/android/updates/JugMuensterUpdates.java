package de.jugmuenster.android.updates;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

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
	    URI uri = new URI("http://www.jug-muenster.de/feed/");

	    HttpClient client = new DefaultHttpClient();
	    HttpGet request = new HttpGet();
	    request.setURI(uri);
	    HttpResponse response = client.execute(request);
	    InputStream content = response.getEntity().getContent();
	    try {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		final RssItemsExtractor handler = new RssItemsExtractor();

		saxParser.parse(content, handler);

		items.clear();
		items.addAll(handler.items);

	    } finally {
		content.close();
	    }

	} catch (ParserConfigurationException e) {
	    strings.add(e.toString());
	} catch (SAXException e) {
	    strings.add(e.toString());
	} catch (MalformedURLException e) {
	    strings.add(e.toString());
	} catch (IOException e) {
	    strings.add(e.toString());
	} catch (URISyntaxException e) {
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
}