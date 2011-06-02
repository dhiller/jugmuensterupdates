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

package de.jugmuenster.android.updates.test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;


import android.content.Context;
import android.os.AsyncTask.Status;
import android.test.InstrumentationTestCase;
import de.jugmuenster.android.updates.ItemsLoader;
import de.jugmuenster.android.updates.ProgressDialogController;
import de.jugmuenster.android.updates.Utils;
import de.jugmuenster.android.updates.item.Item;

public class ItemsLoaderTest extends InstrumentationTestCase {

    public ItemsLoaderTest() {
	super();
    }

    private final class MockDialogController extends ProgressDialogController {
	private boolean createProgressDialog;

	@Override
	public void createProgressDialog(Context c) {
	    createProgressDialog = true;
	}

	@Override
	public void showProgressDialog(String title, boolean indeterminate) {
	}

	@Override
	public void dismissProgressDialog() {
	}

	public boolean createProgressDialogCalled() {
	    return createProgressDialog;
	}

    }

    public void testApplicationNull() throws Throwable {
	try {
	    new ItemsLoader(null, newController());
	    fail("IllegalArgumentException expected!");
	} catch (IllegalArgumentException e) {
	}
    }

    public void testDialogControllerNull() throws Throwable {
	try {
	    new ItemsLoader(new MockApplication(), null);
	    fail("IllegalArgumentException expected!");
	} catch (IllegalArgumentException e) {
	}
    }

    public void testCreation() throws Throwable {
	newInstance();
    }

    public void testExecute() throws Throwable {
	executeInUIThread(newInstance());
    }

    public void testExecuteStatus() throws Throwable {
	final ItemsLoader newInstance = newInstance();
	executeInUIThread(newInstance);
	AsyncAssertion.assertOrTimeout(new AsyncAssertion() {
	    @Override
	    public void run() {
		assertEquals(Status.FINISHED, newInstance.getStatus());
	    }
	});
    }

    public void testExecuteCallsCreateProgressDialog() throws Throwable {
	final MockDialogController newController = newController();
	executeInUIThread(newInstance(newApplication(latestItemDate()),
		newController));
	AsyncAssertion.assertOrTimeout(new AsyncAssertion() {

	    @Override
	    public void run() {
		assertTrue(newController.createProgressDialogCalled());
	    }
	});
    }

    public void testExecuteCallsGetPreference() throws Throwable {
	final MockDialogController newController = newController();
	final MockApplication newApplication = newApplication(latestItemDate());
	executeInUIThread(newInstance(newApplication, newController));
	AsyncAssertion.assertOrTimeout(new AsyncAssertion() {

	    @Override
	    public void run() {
		assertTrue(newApplication.getPreferenceCalled(ItemsLoader.LATEST_ITEM_DATE));
	    }
	});
    }

    public void testExecuteCallsGetAllItems() throws Throwable {
	final MockDialogController newController = newController();
	final MockApplication newApplication = newApplication(latestItemDate());
	executeInUIThread(newInstance(newApplication, newController));
	AsyncAssertion.assertOrTimeout(new AsyncAssertion() {
	    public void run() {
		assertTrue(newApplication.getAllItemsCalled());
	    }
	});
    }

    public void testExecuteWithLatestItemDate() throws Throwable {
	final MockApplication newApplication = newApplication(latestItemDate());
	executeInUIThread(newInstance(newApplication));
	AsyncAssertion.assertOrTimeout(new AsyncAssertion() {

	    @Override
	    public void run() throws Throwable {
		assertEquals(latestItemDate(),
			newApplication.getLatestItemDate());
	    }
	});
    }

    public void testExecuteWithNewerItemUpdatesNoOfNewItems() throws Throwable {
	final MockApplication newApplication = newApplication(latestItemDate());
	newApplication.setItems(Arrays.asList(newItem()));
	final ItemsLoader newInstance = (ItemsLoader) newInstance(newApplication);
	executeInUIThread(newInstance);
	AsyncAssertion.assertOrTimeout(new AsyncAssertion() {

	    @Override
	    public void run() throws Throwable {
		assertEquals(1, newInstance.noOfNewItems());
	    }
	});
    }

    public void testExecuteWithNewerItemUpdatesLatestItemDate()
	    throws Throwable {
	final MockApplication newApplication = newApplication(latestItemDate());
	final Item newerItem = newItem();
	newApplication.setItems(Arrays.asList(newerItem));
	executeInUIThread(newInstance(newApplication));
	AsyncAssertion.assertOrTimeout(new AsyncAssertion() {

	    @Override
	    public void run() throws Throwable {
		assertEquals(laterDate(), newApplication.getLatestItemDate());
	    }
	});
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
	final MockDialogController c = newController();
	return newInstance(a, c);
    }

    protected ItemsLoader newInstance(final MockApplication a,
	    final MockDialogController c) {
	return new ItemsLoader(a, c);
    }

    protected MockDialogController newController() {
	return new MockDialogController();
    }

    protected MockApplication newApplication(final Date latestItemDate) {
	final MockApplication a = new MockApplication();
	a.setLatestItemDate(latestItemDate);
	return a;
    }

    protected void executeInUIThread(final ItemsLoader l) throws Throwable {
	runTestOnUiThread(new Runnable() {
	    @Override
	    public void run() {
		l.execute();
	    }
	});
    }

}
