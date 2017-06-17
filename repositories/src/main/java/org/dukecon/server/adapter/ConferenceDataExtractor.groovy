package org.dukecon.server.adapter

import org.dukecon.model.Conference

/**
 * Extracts conference data from any input file, implementations of this interface provide specific behaviour to parse
 * input data and returns conference information.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
interface ConferenceDataExtractor {
    Conference getConference()
    RawDataMapper getRawDataMapper()
}