package de.jugmuenster.android.updates.rss;

public class RssItem {
    public String title;
    public String link;
    public String description;

    @Override
    public String toString() {
	return title;
    }
}