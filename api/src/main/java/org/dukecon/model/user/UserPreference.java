package org.dukecon.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author Gerd Aschemann <gerd@aschemann.net>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {
    @NonNull private String eventId;
    private int version;
}
