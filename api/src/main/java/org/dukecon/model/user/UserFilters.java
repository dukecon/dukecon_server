package org.dukecon.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilters {
    private boolean favourites = false;
    private Set<String> levels = new HashSet<>();
    private Set<String> languages = new HashSet<>();
    private Set<String> tracks = new HashSet<>();
    private Set<String> locations = new HashSet<>();
}
