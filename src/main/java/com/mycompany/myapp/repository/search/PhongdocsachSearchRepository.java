package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Phongdocsach;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Phongdocsach} entity.
 */
public interface PhongdocsachSearchRepository extends ElasticsearchRepository<Phongdocsach, Long> {}
