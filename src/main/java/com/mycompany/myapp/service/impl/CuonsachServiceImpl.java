package com.mycompany.myapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.Cuonsach;
import com.mycompany.myapp.repository.CuonsachRepository;
import com.mycompany.myapp.repository.search.CuonsachSearchRepository;
import com.mycompany.myapp.service.CuonsachService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Cuonsach}.
 */
@Service
@Transactional
public class CuonsachServiceImpl implements CuonsachService {

    private final Logger log = LoggerFactory.getLogger(CuonsachServiceImpl.class);

    private final CuonsachRepository cuonsachRepository;

    private final CuonsachSearchRepository cuonsachSearchRepository;

    public CuonsachServiceImpl(CuonsachRepository cuonsachRepository, CuonsachSearchRepository cuonsachSearchRepository) {
        this.cuonsachRepository = cuonsachRepository;
        this.cuonsachSearchRepository = cuonsachSearchRepository;
    }

    @Override
    public Cuonsach save(Cuonsach cuonsach) {
        log.debug("Request to save Cuonsach : {}", cuonsach);
        Cuonsach result = cuonsachRepository.save(cuonsach);
        cuonsachSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Cuonsach> partialUpdate(Cuonsach cuonsach) {
        log.debug("Request to partially update Cuonsach : {}", cuonsach);

        return cuonsachRepository
            .findById(cuonsach.getId())
            .map(
                existingCuonsach -> {
                    if (cuonsach.getNgayHetHan() != null) {
                        existingCuonsach.setNgayHetHan(cuonsach.getNgayHetHan());
                    }
                    if (cuonsach.getTrangThai() != null) {
                        existingCuonsach.setTrangThai(cuonsach.getTrangThai());
                    }

                    return existingCuonsach;
                }
            )
            .map(cuonsachRepository::save)
            .map(
                savedCuonsach -> {
                    cuonsachSearchRepository.save(savedCuonsach);

                    return savedCuonsach;
                }
            );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuonsach> findAll() {
        log.debug("Request to get all Cuonsaches");
        return cuonsachRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cuonsach> findOne(Long id) {
        log.debug("Request to get Cuonsach : {}", id);
        return cuonsachRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Cuonsach : {}", id);
        cuonsachRepository.deleteById(id);
        cuonsachSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuonsach> search(String query) {
        log.debug("Request to search Cuonsaches for query {}", query);
        return StreamSupport
            .stream(cuonsachSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
