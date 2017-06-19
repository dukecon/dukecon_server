package org.dukecon.model;

import java.util.Map;

/**
 * Created by christoferdutz on 16.10.16.
 */
public class Styles {

    private String dark;
    private String darkLink;
    private String hover;
    private String hoverLink;
    private String reverse;
    private String highlight;
    private String alternate;

    public Styles() {
    }

    public Styles(Map<String, String> styles) {
        dark = styles.get("dark");
        darkLink = styles.get("darkLink");
        hover = styles.get("hover");
        hoverLink = styles.get("hoverLink");
        reverse = styles.get("reverse");
        highlight = styles.get("highlight");
        alternate = styles.get("alternate");
    }

    public String getDark() {
        return dark;
    }

    public void setDark(String dark) {
        this.dark = dark;
    }

    public String getDarkLink() {
        return darkLink;
    }

    public void setDarkLink(String darkLink) {
        this.darkLink = darkLink;
    }

    public String getHover() {
        return hover;
    }

    public void setHover(String hover) {
        this.hover = hover;
    }

    public String getHoverLink() {
        return hoverLink;
    }

    public void setHoverLink(String hoverLink) {
        this.hoverLink = hoverLink;
    }

    public String getReverse() {
        return reverse;
    }

    public void setReverse(String reverse) {
        this.reverse = reverse;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public String getAlternate() {
        return alternate;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }

}
