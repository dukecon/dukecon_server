package org.dukecon.model;

import java.util.Properties;

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

    public Styles(Properties properties) {
        dark = properties.getProperty("styles.dark");
        darkLink = properties.getProperty("styles.darkLink");
        hover = properties.getProperty("styles.hover");
        hoverLink = properties.getProperty("styles.hoverLink");
        reverse = properties.getProperty("styles.reverse");
        highlight = properties.getProperty("styles.highlight");
        alternate = properties.getProperty("styles.alternate");
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
