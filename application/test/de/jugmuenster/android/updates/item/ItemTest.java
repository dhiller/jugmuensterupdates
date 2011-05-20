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

package de.jugmuenster.android.updates.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import de.jugmuenster.android.updates.item.Item;

public class ItemTest {

    @Test
    public void isComparable() throws Exception {
	final Item item = new Item();
	assertTrue(item instanceof Comparable);
    }

    @Test
    public void compareNoDate() throws Exception {
	assertEquals(0, new Item().compareTo(new Item()));
    }

    @Test
    public void itemWithDateSetIsLessThanItemWithoutDate() throws Exception {
	assertTrue(new Item().compareTo(newItem(new Date())) > 0);
    }

    @Test
    public void itemWithoutDateSetIsGreaterThanItemWithDateSet()
	    throws Exception {
	assertTrue(newItem(new Date()).compareTo(new Item()) < 0);
    }

    @Test
    public void itemWithLaterDateIsLessThanItemWithEarlierDate()
	    throws Exception {
	final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
	final Date earlierDate = parser.parse("2011-05-01");
	final Date laterDate = parser.parse("2011-05-02");
	assertTrue(newItem(laterDate).compareTo(newItem(earlierDate)) < 0);
    }

    private Item newItem(final Date from) {
	final Item another = new Item();
	another.setFrom(from);
	return another;
    }

}
