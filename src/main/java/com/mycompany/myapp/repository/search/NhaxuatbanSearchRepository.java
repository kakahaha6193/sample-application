package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Nhaxuatban;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Nhaxuatban} entity.
 */
public interface NhaxuatbanSearchRepository extends ElasticsearchRepository<Nhaxuatban, Long> {}
