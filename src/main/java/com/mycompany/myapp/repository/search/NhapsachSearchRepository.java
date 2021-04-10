package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Nhapsach;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Nhapsach} entity.
 */
public interface NhapsachSearchRepository extends ElasticsearchRepository<Nhapsach, Long> {}
