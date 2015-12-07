package org.dukecon.server.gui.tag;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IsTag(name = "Page: Detail")
@Retention(RetentionPolicy.RUNTIME)
public @interface DetailPageTag {
}
