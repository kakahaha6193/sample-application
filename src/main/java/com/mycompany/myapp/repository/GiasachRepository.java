package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Giasach;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Giasach entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GiasachRepository extends JpaRepository<Giasach, Long> {}
