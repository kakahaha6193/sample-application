package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Phongdungsach;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Phongdungsach} entity.
 */
public interface PhongdungsachSearchRepository extends ElasticsearchRepository<Phongdungsach, Long> {}
