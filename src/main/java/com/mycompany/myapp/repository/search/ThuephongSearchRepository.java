package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Thuephong;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Thuephong} entity.
 */
public interface ThuephongSearchRepository extends ElasticsearchRepository<Thuephong, Long> {}
