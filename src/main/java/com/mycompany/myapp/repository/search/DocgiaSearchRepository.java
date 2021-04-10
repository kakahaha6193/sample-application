package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Docgia;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Docgia} entity.
 */
public interface DocgiaSearchRepository extends ElasticsearchRepository<Docgia, Long> {}
