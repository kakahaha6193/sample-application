package com.mycompany.myapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.Nhapsach;
import com.mycompany.myapp.repository.NhapsachRepository;
import com.mycompany.myapp.repository.search.NhapsachSearchRepository;
import com.mycompany.myapp.service.NhapsachService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Nhapsach}.
 */
@Service
@Transactional
public class NhapsachServiceImpl implements NhapsachService {

    private final Logger log = LoggerFactory.getLogger(NhapsachServiceImpl.class);

    private final NhapsachRepository nhapsachRepository;

    private final NhapsachSearchRepository nhapsachSearchRepository;

    public NhapsachServiceImpl(NhapsachRepository nhapsachRepository, NhapsachSearchRepository nhapsachSearchRepository) {
        this.nhapsachRepository = nhapsachRepository;
        this.nhapsachSearchRepository = nhapsachSearchRepository;
    }

    @Override
    public Nhapsach save(Nhapsach nhapsach) {
        log.debug("Request to save Nhapsach : {}", nhapsach);
        Nhapsach result = nhapsachRepository.save(nhapsach);
        nhapsachSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Nhapsach> partialUpdate(Nhapsach nhapsach) {
        log.debug("Request to partially update Nhapsach : {}", nhapsach);

        return nhapsachRepository
            .findById(nhapsach.getId())
            .map(
                existingNhapsach -> {
                    if (nhapsach.getNgayGioNhap() != null) {
                        existingNhapsach.setNgayGioNhap(nhapsach.getNgayGioNhap());
                    }
                    if (nhapsach.getSoLuong() != null) {
                        existingNhapsach.setSoLuong(nhapsach.getSoLuong());
                    }

                    return existingNhapsach;
                }
            )
            .map(nhapsachRepository::save)
            .map(
                savedNhapsach -> {
                    nhapsachSearchRepository.save(savedNhapsach);

                    return savedNhapsach;
                }
            );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Nhapsach> findAll() {
        log.debug("Request to get all Nhapsaches");
        return nhapsachRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Nhapsach> findOne(Long id) {
        log.debug("Request to get Nhapsach : {}", id);
        return nhapsachRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Nhapsach : {}", id);
        nhapsachRepository.deleteById(id);
        nhapsachSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Nhapsach> search(String query) {
        log.debug("Request to search Nhapsaches for query {}", query);
        return StreamSupport
            .stream(nhapsachSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
