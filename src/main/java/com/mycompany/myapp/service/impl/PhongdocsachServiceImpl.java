package com.mycompany.myapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.Phongdocsach;
import com.mycompany.myapp.repository.PhongdocsachRepository;
import com.mycompany.myapp.repository.search.PhongdocsachSearchRepository;
import com.mycompany.myapp.service.PhongdocsachService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Phongdocsach}.
 */
@Service
@Transactional
public class PhongdocsachServiceImpl implements PhongdocsachService {

    private final Logger log = LoggerFactory.getLogger(PhongdocsachServiceImpl.class);

    private final PhongdocsachRepository phongdocsachRepository;

    private final PhongdocsachSearchRepository phongdocsachSearchRepository;

    public PhongdocsachServiceImpl(
        PhongdocsachRepository phongdocsachRepository,
        PhongdocsachSearchRepository phongdocsachSearchRepository
    ) {
        this.phongdocsachRepository = phongdocsachRepository;
        this.phongdocsachSearchRepository = phongdocsachSearchRepository;
    }

    @Override
    public Phongdocsach save(Phongdocsach phongdocsach) {
        log.debug("Request to save Phongdocsach : {}", phongdocsach);
        Phongdocsach result = phongdocsachRepository.save(phongdocsach);
        phongdocsachSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Phongdocsach> partialUpdate(Phongdocsach phongdocsach) {
        log.debug("Request to partially update Phongdocsach : {}", phongdocsach);

        return phongdocsachRepository
            .findById(phongdocsach.getId())
            .map(
                existingPhongdocsach -> {
                    if (phongdocsach.getTenPhong() != null) {
                        existingPhongdocsach.setTenPhong(phongdocsach.getTenPhong());
                    }
                    if (phongdocsach.getViTri() != null) {
                        existingPhongdocsach.setViTri(phongdocsach.getViTri());
                    }
                    if (phongdocsach.getSucChua() != null) {
                        existingPhongdocsach.setSucChua(phongdocsach.getSucChua());
                    }
                    if (phongdocsach.getGiaThue() != null) {
                        existingPhongdocsach.setGiaThue(phongdocsach.getGiaThue());
                    }

                    return existingPhongdocsach;
                }
            )
            .map(phongdocsachRepository::save)
            .map(
                savedPhongdocsach -> {
                    phongdocsachSearchRepository.save(savedPhongdocsach);

                    return savedPhongdocsach;
                }
            );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phongdocsach> findAll() {
        log.debug("Request to get all Phongdocsaches");
        return phongdocsachRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Phongdocsach> findOne(Long id) {
        log.debug("Request to get Phongdocsach : {}", id);
        return phongdocsachRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Phongdocsach : {}", id);
        phongdocsachRepository.deleteById(id);
        phongdocsachSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phongdocsach> search(String query) {
        log.debug("Request to search Phongdocsaches for query {}", query);
        return StreamSupport
            .stream(phongdocsachSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
