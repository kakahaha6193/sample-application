package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Thuthu;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Thuthu entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ThuthuRepository extends JpaRepository<Thuthu, Long> {}
