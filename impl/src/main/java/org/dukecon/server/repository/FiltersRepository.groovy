package org.dukecon.server.repository

import org.dukecon.server.model.Filters
import org.springframework.data.repository.CrudRepository

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
interface FiltersRepository extends CrudRepository<Filters, Long> {
    Filters findByPrincipalId(String principalId)
}
