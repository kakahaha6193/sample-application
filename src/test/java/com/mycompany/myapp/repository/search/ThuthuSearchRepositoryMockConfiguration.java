package com.mycompany.myapp.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ThuthuSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ThuthuSearchRepositoryMockConfiguration {

    @MockBean
    private ThuthuSearchRepository mockThuthuSearchRepository;
}
