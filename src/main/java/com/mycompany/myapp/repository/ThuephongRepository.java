package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Thuephong;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Thuephong entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ThuephongRepository extends JpaRepository<Thuephong, Long> {}
