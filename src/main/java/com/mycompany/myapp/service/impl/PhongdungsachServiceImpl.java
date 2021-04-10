package com.mycompany.myapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.Phongdungsach;
import com.mycompany.myapp.repository.PhongdungsachRepository;
import com.mycompany.myapp.repository.search.PhongdungsachSearchRepository;
import com.mycompany.myapp.service.PhongdungsachService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Phongdungsach}.
 */
@Service
@Transactional
public class PhongdungsachServiceImpl implements PhongdungsachService {

    private final Logger log = LoggerFactory.getLogger(PhongdungsachServiceImpl.class);

    private final PhongdungsachRepository phongdungsachRepository;

    private final PhongdungsachSearchRepository phongdungsachSearchRepository;

    public PhongdungsachServiceImpl(
        PhongdungsachRepository phongdungsachRepository,
        PhongdungsachSearchRepository phongdungsachSearchRepository
    ) {
        this.phongdungsachRepository = phongdungsachRepository;
        this.phongdungsachSearchRepository = phongdungsachSearchRepository;
    }

    @Override
    public Phongdungsach save(Phongdungsach phongdungsach) {
        log.debug("Request to save Phongdungsach : {}", phongdungsach);
        Phongdungsach result = phongdungsachRepository.save(phongdungsach);
        phongdungsachSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Phongdungsach> partialUpdate(Phongdungsach phongdungsach) {
        log.debug("Request to partially update Phongdungsach : {}", phongdungsach);

        return phongdungsachRepository
            .findById(phongdungsach.getId())
            .map(
                existingPhongdungsach -> {
                    if (phongdungsach.getTenPhong() != null) {
                        existingPhongdungsach.setTenPhong(phongdungsach.getTenPhong());
                    }
                    if (phongdungsach.getViTri() != null) {
                        existingPhongdungsach.setViTri(phongdungsach.getViTri());
                    }

                    return existingPhongdungsach;
                }
            )
            .map(phongdungsachRepository::save)
            .map(
                savedPhongdungsach -> {
                    phongdungsachSearchRepository.save(savedPhongdungsach);

                    return savedPhongdungsach;
                }
            );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phongdungsach> findAll() {
        log.debug("Request to get all Phongdungsaches");
        return phongdungsachRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Phongdungsach> findOne(Long id) {
        log.debug("Request to get Phongdungsach : {}", id);
        return phongdungsachRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Phongdungsach : {}", id);
        phongdungsachRepository.deleteById(id);
        phongdungsachSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phongdungsach> search(String query) {
        log.debug("Request to search Phongdungsaches for query {}", query);
        return StreamSupport
            .stream(phongdungsachSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
