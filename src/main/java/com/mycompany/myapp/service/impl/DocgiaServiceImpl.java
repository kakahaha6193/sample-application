package com.mycompany.myapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.Docgia;
import com.mycompany.myapp.repository.DocgiaRepository;
import com.mycompany.myapp.repository.search.DocgiaSearchRepository;
import com.mycompany.myapp.service.DocgiaService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Docgia}.
 */
@Service
@Transactional
public class DocgiaServiceImpl implements DocgiaService {

    private final Logger log = LoggerFactory.getLogger(DocgiaServiceImpl.class);

    private final DocgiaRepository docgiaRepository;

    private final DocgiaSearchRepository docgiaSearchRepository;

    public DocgiaServiceImpl(DocgiaRepository docgiaRepository, DocgiaSearchRepository docgiaSearchRepository) {
        this.docgiaRepository = docgiaRepository;
        this.docgiaSearchRepository = docgiaSearchRepository;
    }

    @Override
    public Docgia save(Docgia docgia) {
        log.debug("Request to save Docgia : {}", docgia);
        Docgia result = docgiaRepository.save(docgia);
        docgiaSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Docgia> partialUpdate(Docgia docgia) {
        log.debug("Request to partially update Docgia : {}", docgia);

        return docgiaRepository
            .findById(docgia.getId())
            .map(
                existingDocgia -> {
                    if (docgia.getHoTen() != null) {
                        existingDocgia.setHoTen(docgia.getHoTen());
                    }
                    if (docgia.getNgaySinh() != null) {
                        existingDocgia.setNgaySinh(docgia.getNgaySinh());
                    }
                    if (docgia.getDiaChi() != null) {
                        existingDocgia.setDiaChi(docgia.getDiaChi());
                    }
                    if (docgia.getCmt() != null) {
                        existingDocgia.setCmt(docgia.getCmt());
                    }
                    if (docgia.getTrangThai() != null) {
                        existingDocgia.setTrangThai(docgia.getTrangThai());
                    }
                    if (docgia.getTienCoc() != null) {
                        existingDocgia.setTienCoc(docgia.getTienCoc());
                    }

                    return existingDocgia;
                }
            )
            .map(docgiaRepository::save)
            .map(
                savedDocgia -> {
                    docgiaSearchRepository.save(savedDocgia);

                    return savedDocgia;
                }
            );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Docgia> findAll() {
        log.debug("Request to get all Docgias");
        return docgiaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Docgia> findOne(Long id) {
        log.debug("Request to get Docgia : {}", id);
        return docgiaRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Docgia : {}", id);
        docgiaRepository.deleteById(id);
        docgiaSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Docgia> search(String query) {
        log.debug("Request to search Docgias for query {}", query);
        return StreamSupport
            .stream(docgiaSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
