package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaData {

    private Conference conference;

}
