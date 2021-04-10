package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Giasach;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Giasach} entity.
 */
public interface GiasachSearchRepository extends ElasticsearchRepository<Giasach, Long> {}
