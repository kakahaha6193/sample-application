package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Cuonsach;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Cuonsach} entity.
 */
public interface CuonsachSearchRepository extends ElasticsearchRepository<Cuonsach, Long> {}
