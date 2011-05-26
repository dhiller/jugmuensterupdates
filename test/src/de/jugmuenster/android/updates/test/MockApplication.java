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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.jugmuenster.android.updates.Application;
import de.jugmuenster.android.updates.ItemsLoader;
import de.jugmuenster.android.updates.Utils;
import de.jugmuenster.android.updates.App.NotificationData;
import de.jugmuenster.android.updates.item.ContentProvider;
import de.jugmuenster.android.updates.item.Item;
import de.jugmuenster.android.util.Test;

final class MockApplication extends
        android.test.mock.MockApplication implements Application {

    private Date latestItemDate;
    private List<Item> items = Collections.<Item> emptyList();
    private final Set<String> getPreferenceCalled = new HashSet<String>();
    private final Set<String> setPreferenceCalled = new HashSet<String>();
    private boolean getAllItemsCalled;

    Date getLatestItemDate() {
        return latestItemDate;
    }

    void setLatestItemDate(Date latestItemDate) {
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
	getAllItemsCalled = true;
        return this.items;
    }

    @Override
    public String getPreference(String name, String defaultValue) {
        getPreferenceCalled.add(name);
        if (name.equals(ItemsLoader.LATEST_ITEM_DATE)
    	    && latestItemDate != null) {
    	return Utils.newGMTDateFormat().format(latestItemDate);
        }
        return null;
    }

    @Override
    public void setPreference(String name, String newValue) {
        setPreferenceCalled.add(name);
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

    public boolean getPreferenceCalled(String name) {
        return getPreferenceCalled.contains(name);
    }

    public boolean setPreferenceCalled(String name) {
	return setPreferenceCalled.contains(name);
    }

    public boolean getAllItemsCalled() {
	return getAllItemsCalled;
    }
}