package org.dukecon.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilters {
    private boolean favourites = false;
    private List<String> levels = new ArrayList<>();
    private List<String> languages = new ArrayList<>();
    private List<String> tracks = new ArrayList<>();
    private List<String> locations = new ArrayList<>();
}
