package org.dukecon.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.dukecon.model.Identifyable;

/**
 * @author Gerd Aschemann <gerd@aschemann.net>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference implements Identifyable {
    private String id;
    @NonNull private String eventId;
    private int version;
}
