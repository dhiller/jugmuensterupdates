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

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import de.jugmuenster.android.util.Test;

public abstract class AsyncAssertion {
    private final String message;
    private int timeOutAfterMSecs;
    private int retryEachMSecs;

    public AsyncAssertion() {
	this("", 1000, 50);
    }

    public AsyncAssertion(String message) {
	this(message, 1000, 50);
    }

    public AsyncAssertion(String message, int timeOutAfterMSecs,
	    int retryEachMSecs) {
	this.message = Test.notNull(message);
	this.timeOutAfterMSecs = Test.greaterThanZero(timeOutAfterMSecs);
	this.retryEachMSecs = Test.greaterThanZero(retryEachMSecs);
    }

    public String message() {
	return message;
    }

    public abstract void run() throws Throwable;

    public static void assertOrTimeout(final AsyncAssertion assertion)
	    throws Throwable {
	final long start = System.currentTimeMillis();
	AssertionFailedError error = null;
	while (System.currentTimeMillis() - start < assertion.timeOutAfterMSecs) {
	    try {
		assertion.run();
		return;
	    } catch (AssertionFailedError e) {
		error = e;
	    }
	    final Object mutex = new Object();
	    synchronized (mutex) {
		mutex.wait(assertion.retryEachMSecs);
	    }
	}
	if (error != null)
	    throw error;
	Assert.fail("No assertion failed ?!");
    }
}