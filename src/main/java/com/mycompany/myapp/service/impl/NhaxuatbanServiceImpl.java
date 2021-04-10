package com.mycompany.myapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.Nhaxuatban;
import com.mycompany.myapp.repository.NhaxuatbanRepository;
import com.mycompany.myapp.repository.search.NhaxuatbanSearchRepository;
import com.mycompany.myapp.service.NhaxuatbanService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Nhaxuatban}.
 */
@Service
@Transactional
public class NhaxuatbanServiceImpl implements NhaxuatbanService {

    private final Logger log = LoggerFactory.getLogger(NhaxuatbanServiceImpl.class);

    private final NhaxuatbanRepository nhaxuatbanRepository;

    private final NhaxuatbanSearchRepository nhaxuatbanSearchRepository;

    public NhaxuatbanServiceImpl(NhaxuatbanRepository nhaxuatbanRepository, NhaxuatbanSearchRepository nhaxuatbanSearchRepository) {
        this.nhaxuatbanRepository = nhaxuatbanRepository;
        this.nhaxuatbanSearchRepository = nhaxuatbanSearchRepository;
    }

    @Override
    public Nhaxuatban save(Nhaxuatban nhaxuatban) {
        log.debug("Request to save Nhaxuatban : {}", nhaxuatban);
        Nhaxuatban result = nhaxuatbanRepository.save(nhaxuatban);
        nhaxuatbanSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Nhaxuatban> partialUpdate(Nhaxuatban nhaxuatban) {
        log.debug("Request to partially update Nhaxuatban : {}", nhaxuatban);

        return nhaxuatbanRepository
            .findById(nhaxuatban.getId())
            .map(
                existingNhaxuatban -> {
                    if (nhaxuatban.getTenNXB() != null) {
                        existingNhaxuatban.setTenNXB(nhaxuatban.getTenNXB());
                    }
                    if (nhaxuatban.getDiaChi() != null) {
                        existingNhaxuatban.setDiaChi(nhaxuatban.getDiaChi());
                    }

                    return existingNhaxuatban;
                }
            )
            .map(nhaxuatbanRepository::save)
            .map(
                savedNhaxuatban -> {
                    nhaxuatbanSearchRepository.save(savedNhaxuatban);

                    return savedNhaxuatban;
                }
            );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Nhaxuatban> findAll() {
        log.debug("Request to get all Nhaxuatbans");
        return nhaxuatbanRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Nhaxuatban> findOne(Long id) {
        log.debug("Request to get Nhaxuatban : {}", id);
        return nhaxuatbanRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Nhaxuatban : {}", id);
        nhaxuatbanRepository.deleteById(id);
        nhaxuatbanSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Nhaxuatban> search(String query) {
        log.debug("Request to search Nhaxuatbans for query {}", query);
        return StreamSupport
            .stream(nhaxuatbanSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
