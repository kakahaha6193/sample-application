package com.mycompany.myapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.Thuthu;
import com.mycompany.myapp.repository.ThuthuRepository;
import com.mycompany.myapp.repository.search.ThuthuSearchRepository;
import com.mycompany.myapp.service.ThuthuService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Thuthu}.
 */
@Service
@Transactional
public class ThuthuServiceImpl implements ThuthuService {

    private final Logger log = LoggerFactory.getLogger(ThuthuServiceImpl.class);

    private final ThuthuRepository thuthuRepository;

    private final ThuthuSearchRepository thuthuSearchRepository;

    public ThuthuServiceImpl(ThuthuRepository thuthuRepository, ThuthuSearchRepository thuthuSearchRepository) {
        this.thuthuRepository = thuthuRepository;
        this.thuthuSearchRepository = thuthuSearchRepository;
    }

    @Override
    public Thuthu save(Thuthu thuthu) {
        log.debug("Request to save Thuthu : {}", thuthu);
        Thuthu result = thuthuRepository.save(thuthu);
        thuthuSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Thuthu> partialUpdate(Thuthu thuthu) {
        log.debug("Request to partially update Thuthu : {}", thuthu);

        return thuthuRepository
            .findById(thuthu.getId())
            .map(
                existingThuthu -> {
                    if (thuthu.getHoTen() != null) {
                        existingThuthu.setHoTen(thuthu.getHoTen());
                    }
                    if (thuthu.getUsername() != null) {
                        existingThuthu.setUsername(thuthu.getUsername());
                    }
                    if (thuthu.getPassword() != null) {
                        existingThuthu.setPassword(thuthu.getPassword());
                    }

                    return existingThuthu;
                }
            )
            .map(thuthuRepository::save)
            .map(
                savedThuthu -> {
                    thuthuSearchRepository.save(savedThuthu);

                    return savedThuthu;
                }
            );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Thuthu> findAll() {
        log.debug("Request to get all Thuthus");
        return thuthuRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Thuthu> findOne(Long id) {
        log.debug("Request to get Thuthu : {}", id);
        return thuthuRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Thuthu : {}", id);
        thuthuRepository.deleteById(id);
        thuthuSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Thuthu> search(String query) {
        log.debug("Request to search Thuthus for query {}", query);
        return StreamSupport
            .stream(thuthuSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
