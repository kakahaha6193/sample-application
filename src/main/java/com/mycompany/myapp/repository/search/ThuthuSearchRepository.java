package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Thuthu;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Thuthu} entity.
 */
public interface ThuthuSearchRepository extends ElasticsearchRepository<Thuthu, Long> {}
