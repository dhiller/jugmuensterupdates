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

import java.util.Date;
import java.util.List;

import de.jugmuenster.android.updates.item.Item;
import de.jugmuenster.android.util.Test;

public class NewItemsMarker {
    private final Date latestItemDate;
    private Date newLatestItemDate;
    final List<Item> allItems;
    int noOfNewItems = 0;

    NewItemsMarker(Date latestItemDate, List<Item> allItems) {
	this.latestItemDate = Test.notNull(latestItemDate);
	this.setNewLatestItemDate(latestItemDate);
	this.allItems = Test.notNull(allItems);
    }

    void mark() {
	for (Item i : allItems) {
	    if (i.getFrom().compareTo(latestItemDate) > 0) {
		noOfNewItems++;
		i.setNew();
		if (i.getFrom().compareTo(newLatestItemDate()) > 0)
		    setNewLatestItemDate(i.getFrom());
	    }
	}
    }

    public Date newLatestItemDate() {
	return newLatestItemDate;
    }

    private void setNewLatestItemDate(Date newLatestItemDate) {
	this.newLatestItemDate = newLatestItemDate;
    }
}