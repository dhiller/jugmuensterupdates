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

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import de.jugmuenster.android.updates.App.NotificationData;
import de.jugmuenster.android.updates.item.Item;
import de.jugmuenster.android.util.Test;

final class ItemsLoader extends AsyncTask<Object, Integer, List<Item>> {

    private static final String LATEST_ITEM_DATE = "latestItemDate";
    private final Application a;
    private final ProgressDialog progressDialog;
    private int noOfNewItems = 0;
    private Date latestItemDate = new Date(0);

    ItemsLoader(Application a) {
	this.a = Test.notNull(a);
	progressDialog = new ProgressDialog((Context) a);
    }

    @Override
    protected void onPreExecute() {
	progressDialog.setTitle("Lade Elemente");
	progressDialog.setIndeterminate(true);
	progressDialog.show();
    }

    @Override
    protected List<Item> doInBackground(Object... unused) {
	restoreLatestItemDate();
	Date newLatestItemDate = latestItemDate;
	final List<Item> allItems = getAllItems();
	for (Item i : allItems) {
	    if (i.getFrom().compareTo(latestItemDate) > 0) {
		noOfNewItems++;
		i.setNew();
		if (i.getFrom().compareTo(newLatestItemDate) > 0)
		    newLatestItemDate = i.getFrom();
	    }
	}
	latestItemDate = newLatestItemDate;
	saveLatestItemDate();
	return allItems;
    }

    @Override
    protected void onPostExecute(List<Item> result) {
	show(result);
	progressDialog.dismiss();
	if (noOfNewItems > 0) {
	    final NotificationData notificationData = new NotificationData(
		    App.NOTIFICATION_NEW_ITEMS, "Neue JUG Elemente",
		    MessageFormat.format("{0} neue JUG Elemente", noOfNewItems));
	    notify(notificationData);
	}
    }

    protected Application application() {
	return a;
    }

    private List<Item> getAllItems() {
	return application().getAllItems();
    }

    private void handleError(ParseException e) {
	application().handleError(e, LATEST_ITEM_DATE,
		"Could not restore latestItemDate",
		"Konnte letzte Aktualisierung nicht wieder herstellen!");
    }

    private void notify(final NotificationData notificationData) {
	application().notify(notificationData);
    }

    private void show(List<Item> result) {
	application().show(result);
    }

    private void saveLatestItemDate() {
	setPreference(LATEST_ITEM_DATE,
		Utils.newGMTDateFormat().format(latestItemDate));
    }

    public void setPreference(final String name, final String newValue) {
	application().setPreference(name, newValue);
    }

    private void restoreLatestItemDate() {
	final String name = LATEST_ITEM_DATE;
	final String defaultValue = null;
	final String savedLatestItemDate = getPreference(name, defaultValue);
	if (savedLatestItemDate != null)
	    try {
		final DateFormat simpleDateFormat = Utils.newGMTDateFormat();
		latestItemDate = simpleDateFormat.parse(savedLatestItemDate);
	    } catch (ParseException e) {
		handleError(e);
	    }
    }

    public String getPreference(final String name, final String defaultValue) {
	return application().getPreference(name, defaultValue);
    }

}