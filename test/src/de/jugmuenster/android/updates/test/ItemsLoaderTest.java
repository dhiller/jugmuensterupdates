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

import java.util.List;

import android.content.Context;

import junit.framework.TestCase;
import de.jugmuenster.android.updates.App.NotificationData;
import de.jugmuenster.android.updates.Application;
import de.jugmuenster.android.updates.ItemsLoader;
import de.jugmuenster.android.updates.ProgressDialogController;
import de.jugmuenster.android.updates.item.ContentProvider;
import de.jugmuenster.android.updates.item.Item;

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
	    return null;
	}

	@Override
	public String getPreference(String name, String defaultValue) {
	    return null;
	}

	@Override
	public void setPreference(String name, String newValue) {
	}
    }

    public void testApplicationNull() throws Exception {
	try {
	    new ItemsLoader(null, new MockDialogController());
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

    protected ItemsLoader newInstance() {
	return new ItemsLoader(new MockApplication(),
		new MockDialogController());
    }

}
