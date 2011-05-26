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

import java.util.Date;

public class Item implements Comparable<Item> {

    private Type type;
    private String marker;
    
    private String title;
    private String link;
    private String description;
    private Date from;

    private boolean isNew;

    public Item() {
	super();
    }

    @Override
    public String toString() {
	return getMarker() + ": " + getTitle() + (isNew() ? " (*)" : "");
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getTitle() {
	return title;
    }

    public void setLink(String link) {
	this.link = link;
    }

    public String getLink() {
	return link;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getDescription() {
	return description;
    }

    public Type getType() {
	return type;
    }

    public void setType(Type type) {
	this.type = type;
    }
    
    public String getMarker(){
    	return marker;
    }
    
    public void setMarker(String marker){
    	this.marker = marker;
    }

    public void setFrom(Date from) {
	this.from = from;
    }

    public Date getFrom() {
	return from;
    }

    @Override
    public int compareTo(Item another) {
	if (this.getFrom() == null)
	    return (another.getFrom() == null ? 0 : 1);
	return (another.getFrom() != null ? this.getFrom().compareTo(
		another.getFrom())
		* -1 : -1);
    }

    public void setNew() {
	this.isNew = true;
    }

    public boolean isNew() {
	return this.isNew;
    }

}