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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import de.jugmuenster.android.updates.item.Item;

public class ExtractorTest {

    @Test
    public void streamHasItems() throws IOException {
	final String rss = getFullRss();
	System.out.println(rss); //$NON-NLS-1$ // TODO: Remove
    }

    @Test
    public void itemsFromStringNotNull() throws Exception {
	assertNotNull(new Extractor().extract(getFullRss()));
    }

    @Test
    public void itemsFromStringNotEmpty() throws Exception {
	assertFalse(new Extractor().extract(getFullRss()).isEmpty());
    }

    @Test
    public void itemsNotNull() throws Exception {
	assertNotNull(extractTestItems());
    }

    @Test
    public void hasItems() throws Exception {
	assertFalse(extractTestItems().isEmpty());
    }

    private List<Item> extractTestItems() throws FactoryConfigurationError,
	    ParserConfigurationException, SAXException, IOException {
	final InputStream openStream = getTestStream();
	try {
	    return new Extractor().extract(openStream);
	} finally {
	    openStream.close();
	}
    }

    private InputStream getTestStream() {
	final InputStream resourceAsStream = RssItemsExtractorTest.clExtractorTesttream("jug-muenster-feed-2011-04-12.xml");
	return resourceAsStream;
    }

    private String getFullRss() throws IOException {
	final StringBuilder b = new StringBuilder();
	final InputStreamReader inputStreamReader = new InputStreamReader(
		getTestStream(), Charset.forName("utf-8"));
	final BufferedReader s = new BufferedReader(inputStreamReader);
	String line = null;
	while ((line = s.readLine()) != null) {
	    b.append(line + "\n");
	}
	final String rss = b.toString();
	return rss;
    }

}
