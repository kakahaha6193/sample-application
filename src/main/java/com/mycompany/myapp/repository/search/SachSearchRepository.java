package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Sach;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Sach} entity.
 */
public interface SachSearchRepository extends ElasticsearchRepository<Sach, Long> {}
