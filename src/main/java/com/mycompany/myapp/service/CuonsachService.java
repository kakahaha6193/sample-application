package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Cuonsach;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Cuonsach}.
 */
public interface CuonsachService {
    /**
     * Save a cuonsach.
     *
     * @param cuonsach the entity to save.
     * @return the persisted entity.
     */
    Cuonsach save(Cuonsach cuonsach);

    /**
     * Partially updates a cuonsach.
     *
     * @param cuonsach the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Cuonsach> partialUpdate(Cuonsach cuonsach);

    /**
     * Get all the cuonsaches.
     *
     * @return the list of entities.
     */
    List<Cuonsach> findAll();

    /**
     * Get the "id" cuonsach.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Cuonsach> findOne(Long id);

    /**
     * Delete the "id" cuonsach.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the cuonsach corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @return the list of entities.
     */
    List<Cuonsach> search(String query);
}
