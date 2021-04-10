package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Giasach;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Giasach}.
 */
public interface GiasachService {
    /**
     * Save a giasach.
     *
     * @param giasach the entity to save.
     * @return the persisted entity.
     */
    Giasach save(Giasach giasach);

    /**
     * Partially updates a giasach.
     *
     * @param giasach the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Giasach> partialUpdate(Giasach giasach);

    /**
     * Get all the giasaches.
     *
     * @return the list of entities.
     */
    List<Giasach> findAll();

    /**
     * Get the "id" giasach.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Giasach> findOne(Long id);

    /**
     * Delete the "id" giasach.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the giasach corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @return the list of entities.
     */
    List<Giasach> search(String query);
}
