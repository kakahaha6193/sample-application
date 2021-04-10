package com.mycompany.myapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.Theloai;
import com.mycompany.myapp.repository.TheloaiRepository;
import com.mycompany.myapp.repository.search.TheloaiSearchRepository;
import com.mycompany.myapp.service.TheloaiService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Theloai}.
 */
@Service
@Transactional
public class TheloaiServiceImpl implements TheloaiService {

    private final Logger log = LoggerFactory.getLogger(TheloaiServiceImpl.class);

    private final TheloaiRepository theloaiRepository;

    private final TheloaiSearchRepository theloaiSearchRepository;

    public TheloaiServiceImpl(TheloaiRepository theloaiRepository, TheloaiSearchRepository theloaiSearchRepository) {
        this.theloaiRepository = theloaiRepository;
        this.theloaiSearchRepository = theloaiSearchRepository;
    }

    @Override
    public Theloai save(Theloai theloai) {
        log.debug("Request to save Theloai : {}", theloai);
        Theloai result = theloaiRepository.save(theloai);
        theloaiSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Theloai> partialUpdate(Theloai theloai) {
        log.debug("Request to partially update Theloai : {}", theloai);

        return theloaiRepository
            .findById(theloai.getId())
            .map(
                existingTheloai -> {
                    if (theloai.getTenTheLoai() != null) {
                        existingTheloai.setTenTheLoai(theloai.getTenTheLoai());
                    }

                    return existingTheloai;
                }
            )
            .map(theloaiRepository::save)
            .map(
                savedTheloai -> {
                    theloaiSearchRepository.save(savedTheloai);

                    return savedTheloai;
                }
            );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Theloai> findAll() {
        log.debug("Request to get all Theloais");
        return theloaiRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Theloai> findOne(Long id) {
        log.debug("Request to get Theloai : {}", id);
        return theloaiRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Theloai : {}", id);
        theloaiRepository.deleteById(id);
        theloaiSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Theloai> search(String query) {
        log.debug("Request to search Theloais for query {}", query);
        return StreamSupport
            .stream(theloaiSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
