package org.dukecon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author Gerd Aschemann <gerd@aschemann.net>
 */
@Data
@Builder
public class UserPreference {
    @NonNull private String talkId;
    private int version;
}
