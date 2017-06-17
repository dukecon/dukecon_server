package org.dukecon.server.repositories

import org.dukecon.model.Conference

/**
 * Extracts conferences data from any input file, implementations of this interface provide specific behaviour to parse
 * input data and returns conferences information.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
interface ConferenceDataExtractor {
    Conference getConference()
    RawDataMapper getRawDataMapper()
}