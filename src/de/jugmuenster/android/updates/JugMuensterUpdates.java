package de.jugmuenster.android.updates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
import org.xml.sax.helpers.DefaultHandler;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class JugMuensterUpdates extends ListActivity {

	final List<Item> items = new ArrayList<Item>();

	static class Item {
		String title;
		String link;
		String description;

		@Override
		public String toString() {
			return title;
		}
	}

	enum CurrentElement {
		TITLE, LINK, DESCRIPTION;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final List<String> strings = new ArrayList<String>();

		items.clear();

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

				DefaultHandler handler = new DefaultHandler() {

					Item current;
					StringBuilder builder;

					@Override
					public void startElement(String uri, String localName,
							String qName, org.xml.sax.Attributes attributes)
							throws SAXException {
						if (localName.equals("item")) {
							current = new Item();
						}
						builder = new StringBuilder();
					}

					@Override
					public void characters(char[] ch, int start, int length)
							throws SAXException {
						builder.append(ch, start, length);
					}

					@Override
					public void endElement(String uri, String localName,
							String qName) throws SAXException {
						if (current != null && localName.equals("title")) {
							current.title = builder.toString();
						}
						if (localName.equals("item")) {
							items.add(current);
							current = null;
						}
					}
				};

				saxParser.parse(content, handler);

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

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				R.layout.list_item);
		for (Item i : items)
			arrayAdapter.add(i.toString());
		setListAdapter(arrayAdapter);

	}
}