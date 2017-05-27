package org.dukecon.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

/**
 * Created by ascheman on 27.05.17.
 */
@Data
@Builder
@JsonSerialize(using=CoreImagesSerializer.class)
public class CoreImages extends AbstractCoreImages {
}
