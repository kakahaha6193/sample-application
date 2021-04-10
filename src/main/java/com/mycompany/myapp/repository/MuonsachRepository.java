package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Muonsach;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Muonsach entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MuonsachRepository extends JpaRepository<Muonsach, Long> {}
