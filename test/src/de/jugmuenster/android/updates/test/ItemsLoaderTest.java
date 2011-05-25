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

package de.jugmuenster.android.updates.test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;

import junit.framework.TestCase;
import de.jugmuenster.android.updates.App.NotificationData;
import de.jugmuenster.android.updates.Application;
import de.jugmuenster.android.updates.ItemsLoader;
import de.jugmuenster.android.updates.ProgressDialogController;
import de.jugmuenster.android.updates.Utils;
import de.jugmuenster.android.updates.item.ContentProvider;
import de.jugmuenster.android.updates.item.Item;
import de.jugmuenster.android.util.Test;

public class ItemsLoaderTest extends TestCase {

    private final class MockDialogController extends ProgressDialogController {
	@Override
	public void createProgressDialog(Context c) {
	}

	@Override
	public void showProgressDialog(String title, boolean indeterminate) {
	}

	@Override
	public void dismissProgressDialog() {
	}
    }

    private final class MockApplication extends
	    android.test.mock.MockApplication implements Application {

	private Date latestItemDate;
	private List<Item> items = Collections.<Item> emptyList();

	private Date getLatestItemDate() {
	    return latestItemDate;
	}

	private void setLatestItemDate(Date latestItemDate) {
	    this.latestItemDate = latestItemDate;
	}

	@Override
	public void show(List<Item> items) {
	}

	@Override
	public void notify(NotificationData notificationData) {
	}

	@Override
	public void handleError(Throwable t, String logTag, String logMessage,
		String userMessage) {
	}

	@Override
	public List<ContentProvider> getProviders() {
	    return null;
	}

	@Override
	public List<Item> getAllItems() {
	    return this.items;
	}

	@Override
	public String getPreference(String name, String defaultValue) {
	    if (name.equals(ItemsLoader.LATEST_ITEM_DATE)
		    && latestItemDate != null) {
		return Utils.newGMTDateFormat().format(latestItemDate);
	    }
	    return null;
	}

	@Override
	public void setPreference(String name, String newValue) {
	    if (name.equals(ItemsLoader.LATEST_ITEM_DATE)) {
		try {
		    setLatestItemDate(Utils.newGMTDateFormat().parse(newValue));
		} catch (ParseException e) {
		    throw new AssertionError(e);
		}
	    }
	}

	public void setItems(List<Item> items) {
	    this.items = Test.notNull(items);
	}
    }

    public void testApplicationNull() throws Exception {
	try {
	    new ItemsLoader(null, new MockDialogController());
	    fail("IllegalArgumentException expected!");
	} catch (IllegalArgumentException e) {
	}
    }

    public void testDialogControllerNull() throws Exception {
	try {
	    new ItemsLoader(new MockApplication(), null);
	    fail("IllegalArgumentException expected!");
	} catch (IllegalArgumentException e) {
	}
    }

    public void testCreation() throws Exception {
	newInstance();
    }

    public void testExecute() throws Exception {
	newInstance().execute();
    }

    public void testExecuteWithLatestItemDate() throws Exception {
	final MockApplication newApplication = newApplication(latestItemDate());
	newInstance(newApplication).execute();
	assertEquals(latestItemDate(), newApplication.getLatestItemDate());
    }

    public void testExecuteWithNewerItemUpdatesNoOfNewItems() throws Exception {
	final MockApplication newApplication = newApplication(latestItemDate());
	newApplication.setItems(Arrays.asList(newItem()));
	final ItemsLoader newInstance = (ItemsLoader) newInstance(
		newApplication).execute();
	assertEquals(1, newInstance.noOfNewItems());
    }

    public void testExecuteWithNewerItemUpdatesLatestItemDate()
	    throws Exception {
	final MockApplication newApplication = newApplication(latestItemDate());
	final Item newerItem = newItem();
	newApplication.setItems(Arrays.asList(newerItem));
	newInstance(newApplication).execute();
	assertEquals(laterDate(), newApplication.getLatestItemDate());
    }

    protected Item newItem() throws ParseException {
	final Item newerItem = new Item();
	newerItem.setFrom(laterDate());
	return newerItem;
    }

    protected Date laterDate() throws ParseException {
	return Utils.newGMTDateFormat().parse("1/1/11 7:37 PM");
    }

    protected Date latestItemDate() throws ParseException {
	// 5/25/11 5:09 AM
	return Utils.newGMTDateFormat().parse("12/31/10 5:42 AM");
    }

    protected ItemsLoader newInstance() {
	return newInstanceWithDate((Date) null);
    }

    protected ItemsLoader newInstanceWithDate(final Date latestItemDate) {
	final MockApplication a = newApplication(latestItemDate);
	return newInstance(a);
    }

    protected ItemsLoader newInstance(final MockApplication a) {
	return new ItemsLoader(a, new MockDialogController());
    }

    protected MockApplication newApplication(final Date latestItemDate) {
	final MockApplication a = new MockApplication();
	a.setLatestItemDate(latestItemDate);
	return a;
    }

}
