package com.mycompany.myapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.Giasach;
import com.mycompany.myapp.repository.GiasachRepository;
import com.mycompany.myapp.repository.search.GiasachSearchRepository;
import com.mycompany.myapp.service.GiasachService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Giasach}.
 */
@Service
@Transactional
public class GiasachServiceImpl implements GiasachService {

    private final Logger log = LoggerFactory.getLogger(GiasachServiceImpl.class);

    private final GiasachRepository giasachRepository;

    private final GiasachSearchRepository giasachSearchRepository;

    public GiasachServiceImpl(GiasachRepository giasachRepository, GiasachSearchRepository giasachSearchRepository) {
        this.giasachRepository = giasachRepository;
        this.giasachSearchRepository = giasachSearchRepository;
    }

    @Override
    public Giasach save(Giasach giasach) {
        log.debug("Request to save Giasach : {}", giasach);
        Giasach result = giasachRepository.save(giasach);
        giasachSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Giasach> partialUpdate(Giasach giasach) {
        log.debug("Request to partially update Giasach : {}", giasach);

        return giasachRepository
            .findById(giasach.getId())
            .map(
                existingGiasach -> {
                    if (giasach.getThuTu() != null) {
                        existingGiasach.setThuTu(giasach.getThuTu());
                    }

                    return existingGiasach;
                }
            )
            .map(giasachRepository::save)
            .map(
                savedGiasach -> {
                    giasachSearchRepository.save(savedGiasach);

                    return savedGiasach;
                }
            );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Giasach> findAll() {
        log.debug("Request to get all Giasaches");
        return giasachRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Giasach> findOne(Long id) {
        log.debug("Request to get Giasach : {}", id);
        return giasachRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Giasach : {}", id);
        giasachRepository.deleteById(id);
        giasachSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Giasach> search(String query) {
        log.debug("Request to search Giasaches for query {}", query);
        return StreamSupport
            .stream(giasachSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
