package org.dukecon.server.convert

import org.dukecon.server.convert.impl.StylesCssResource
import org.junit.Test

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class StylesCssResourceTests {

    @Test
    void testCreateStylesCss() {
        def stylesCssResource = new StylesCssResource("testconfid", [dark: 'darkblue'], '/templates/styles.ftl')
        assert 'conferences/testconfid/styles.css' == stylesCssResource.getFileName()
        assert '''.dark {
    color: darkblue;
    fill: darkblue;
    border-color: darkblue;
}
.darkBack {
    background-color: darkblue;
}
.darkLink {
    color: #1aa3b1;
    fill: #1aa3b1;
    border-color: #1aa3b1;
}
.darkLinkBack {
    background-color: #1aa3b1;
}
.hover {
    color: #00c3d7;
    fill: #00c3d7;
}
.hoverBack {
    background-color: #00c3d7;
}
.hoverLink {
    color: #00c3d7;
    fill: #00c3d7;
}
.hoverLinkBack {
    background-color: #00c3d7;
}
.reverse {
    color: #fff;
    fill: #fff;
}
.reverseBack {
    background-color: #fff;
}
.highlight {
    color: #ddee55;
    fill: #ddee55;
}
.highlightBack {
    background-color: #ddee55;
}
.alternate {
    color: #bf5a00;
    fill: #bf5a00;
}
.alternateBack {
    background-color: #bf5a00;
    fill: #bf5a00;
}
a {
    color: #1aa3b1;
    fill: #1aa3b1;
}
a:hover {
    color: #00c3d7;
}
h1 a:hover {
    background-color: #00c3d7;
}
h1 a.active:hover {
    background-color:#fff;
    color: darkblue;
    fill: darkblue;
}
button.highlightBack:hover {
    background-color: #ddee55;
}
''' == stylesCssResource.getContent()
    }

    @Test
    void testLoadTemplate() {
        assert '''.dark {
    color: ${styles.dark};
    fill: ${styles.dark};
    border-color: ${styles.dark};
}
.darkBack {
    background-color: ${styles.dark};
}
.darkLink {
    color: ${styles.darkLink};
    fill: ${styles.darkLink};
    border-color: ${styles.darkLink};
}
.darkLinkBack {
    background-color: ${styles.darkLink};
}
.hover {
    color: ${styles.hover};
    fill: ${styles.hover};
}
.hoverBack {
    background-color: ${styles.hover};
}
.hoverLink {
    color: ${styles.hoverLink};
    fill: ${styles.hoverLink};
}
.hoverLinkBack {
    background-color: ${styles.hoverLink};
}
.reverse {
    color: ${styles.reverse};
    fill: ${styles.reverse};
}
.reverseBack {
    background-color: ${styles.reverse};
}
.highlight {
    color: ${styles.highlight};
    fill: ${styles.highlight};
}
.highlightBack {
    background-color: ${styles.highlight};
}
.alternate {
    color: ${styles.alternate};
    fill: ${styles.alternate};
}
.alternateBack {
    background-color: ${styles.alternate};
    fill: ${styles.alternate};
}
a {
    color: ${styles.darkLink};
    fill: ${styles.darkLink};
}
a:hover {
    color: ${styles.hoverLink};
}
h1 a:hover {
    background-color: ${styles.hover};
}
h1 a.active:hover {
    background-color:${styles.reverse};
    color: ${styles.dark};
    fill: ${styles.dark};
}
button.highlightBack:hover {
    background-color: ${styles.highlight};
}
''' == this.getClass().getResource('/templates/styles.ftl').text
    }
}
