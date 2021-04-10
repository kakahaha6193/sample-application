package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Muonsach;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Muonsach} entity.
 */
public interface MuonsachSearchRepository extends ElasticsearchRepository<Muonsach, Long> {}
