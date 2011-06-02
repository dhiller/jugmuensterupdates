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

package de.jugmuenster.android.updates.rss;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.jugmuenster.android.updates.item.Item;
import de.jugmuenster.android.updates.item.Source;
import de.jugmuenster.android.updates.item.Type;
import de.jugmuenster.android.util.Test;

public final class Extractor {

    final SAXParserFactory factory = SAXParserFactory.newInstance();
    final Handler handler;
    final SAXParser saxParser;
    final Source source;

    public Extractor(Source s) throws ParserConfigurationException,
	    SAXException {
	saxParser = factory.newSAXParser();
	this.source = Test.notNull(s);
	this.handler = new Handler(this.source);
    }

    static DateFormat newRSSGMTDateFormat() {
	final SimpleDateFormat gmtDateFormat = new SimpleDateFormat(
		"EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
	gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	return gmtDateFormat;
    }

    private static final class Handler extends DefaultHandler {

	private final DateFormat RSS_DATE_PARSER = newRSSGMTDateFormat();

	final List<Item> items = new ArrayList<Item>();
	Item current;
	StringBuilder builder;

	private Source source;

	public Handler(Source source) {
	    this.source = source;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
		org.xml.sax.Attributes attributes) throws SAXException {
	    if (elementName(localName, qName).equals("item")) {
		current = new Item();
		current.setType(Type.RSS);
		current.setMarker(source.shortName());
	    }
	    builder = new StringBuilder();
	}

	@Override
	public void characters(char[] ch, int start, int length)
		throws SAXException {
	    builder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
		throws SAXException {
	    if (current != null
		    && elementName(localName, qName).equals("title")) {
		current.setTitle(builder.toString());
	    }
	    if (current != null && elementName(localName, qName).equals("link")) {
		current.setLink(builder.toString());
	    }
	    if (current != null
		    && elementName(localName, qName).equals("pubDate")) {
		final String dateAsString = builder.toString();
		try {
		    current.setFrom(RSS_DATE_PARSER.parse(dateAsString));
		} catch (ParseException e) {
		    System.out.println(RSS_DATE_PARSER.format(new Date())); //$NON-NLS-1$ // TODO: Remove
		    e.printStackTrace(); // TODO
		}
	    }
	    if (elementName(localName, qName).equals("item")) {
		items.add(current);
		current = null;
	    }
	}

	/**
	 * Within a Junit Test the runtime behaves seemingly different, so we
	 * have to use the name value that's set?! TODO: find out why
	 * 
	 * @param localName
	 * @param qName
	 * @return
	 */
	private String elementName(String localName, String qName) {
	    return localName != null && localName.length() > 0 ? localName
		    : qName;
	}

    }

    public List<Item> extract(InputStream content)
	    throws FactoryConfigurationError, ParserConfigurationException,
	    SAXException, IOException {
	saxParser.parse(new InputSource(new BufferedReader(
		new InputStreamReader(content, Charset.forName("utf-8")))),
		handler);
	return handler.items;
    }

    public List<Item> extract(String fullRss) throws SAXException, IOException {
	saxParser.parse(new ByteArrayInputStream(fullRss.getBytes("UTF-8")),
		handler);
	return handler.items;
    }
}