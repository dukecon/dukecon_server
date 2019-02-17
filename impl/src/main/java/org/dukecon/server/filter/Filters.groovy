package org.dukecon.server.filter

import groovy.transform.TypeChecked
import groovy.transform.builder.Builder

import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Builder
@TypeChecked
@Entity(name = "filters")
class Filters {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    long id

    @Column(name = "principal_id", nullable = false, unique = true)
    private String principalId

    @Column(name = "onlyFavorites")
    private boolean favourites = false

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> languages = []

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> levels = []

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> tracks = []

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> locations = []
}
