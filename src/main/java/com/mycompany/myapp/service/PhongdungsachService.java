package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Phongdungsach;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Phongdungsach}.
 */
public interface PhongdungsachService {
    /**
     * Save a phongdungsach.
     *
     * @param phongdungsach the entity to save.
     * @return the persisted entity.
     */
    Phongdungsach save(Phongdungsach phongdungsach);

    /**
     * Partially updates a phongdungsach.
     *
     * @param phongdungsach the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Phongdungsach> partialUpdate(Phongdungsach phongdungsach);

    /**
     * Get all the phongdungsaches.
     *
     * @return the list of entities.
     */
    List<Phongdungsach> findAll();

    /**
     * Get the "id" phongdungsach.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Phongdungsach> findOne(Long id);

    /**
     * Delete the "id" phongdungsach.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the phongdungsach corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @return the list of entities.
     */
    List<Phongdungsach> search(String query);
}
