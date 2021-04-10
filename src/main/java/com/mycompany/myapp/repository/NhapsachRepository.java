package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Nhapsach;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Nhapsach entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NhapsachRepository extends JpaRepository<Nhapsach, Long> {}
