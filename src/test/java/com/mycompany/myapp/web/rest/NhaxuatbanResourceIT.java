package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Nhaxuatban;
import com.mycompany.myapp.repository.NhaxuatbanRepository;
import com.mycompany.myapp.repository.search.NhaxuatbanSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link NhaxuatbanResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class NhaxuatbanResourceIT {

    private static final String DEFAULT_TEN_NXB = "AAAAAAAAAA";
    private static final String UPDATED_TEN_NXB = "BBBBBBBBBB";

    private static final String DEFAULT_DIA_CHI = "AAAAAAAAAA";
    private static final String UPDATED_DIA_CHI = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/nhaxuatbans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/nhaxuatbans";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NhaxuatbanRepository nhaxuatbanRepository;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.NhaxuatbanSearchRepositoryMockConfiguration
     */
    @Autowired
    private NhaxuatbanSearchRepository mockNhaxuatbanSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNhaxuatbanMockMvc;

    private Nhaxuatban nhaxuatban;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Nhaxuatban createEntity(EntityManager em) {
        Nhaxuatban nhaxuatban = new Nhaxuatban().tenNXB(DEFAULT_TEN_NXB).diaChi(DEFAULT_DIA_CHI);
        return nhaxuatban;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Nhaxuatban createUpdatedEntity(EntityManager em) {
        Nhaxuatban nhaxuatban = new Nhaxuatban().tenNXB(UPDATED_TEN_NXB).diaChi(UPDATED_DIA_CHI);
        return nhaxuatban;
    }

    @BeforeEach
    public void initTest() {
        nhaxuatban = createEntity(em);
    }

    @Test
    @Transactional
    void createNhaxuatban() throws Exception {
        int databaseSizeBeforeCreate = nhaxuatbanRepository.findAll().size();
        // Create the Nhaxuatban
        restNhaxuatbanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(nhaxuatban)))
            .andExpect(status().isCreated());

        // Validate the Nhaxuatban in the database
        List<Nhaxuatban> nhaxuatbanList = nhaxuatbanRepository.findAll();
        assertThat(nhaxuatbanList).hasSize(databaseSizeBeforeCreate + 1);
        Nhaxuatban testNhaxuatban = nhaxuatbanList.get(nhaxuatbanList.size() - 1);
        assertThat(testNhaxuatban.getTenNXB()).isEqualTo(DEFAULT_TEN_NXB);
        assertThat(testNhaxuatban.getDiaChi()).isEqualTo(DEFAULT_DIA_CHI);

        // Validate the Nhaxuatban in Elasticsearch
        verify(mockNhaxuatbanSearchRepository, times(1)).save(testNhaxuatban);
    }

    @Test
    @Transactional
    void createNhaxuatbanWithExistingId() throws Exception {
        // Create the Nhaxuatban with an existing ID
        nhaxuatban.setId(1L);

        int databaseSizeBeforeCreate = nhaxuatbanRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNhaxuatbanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(nhaxuatban)))
            .andExpect(status().isBadRequest());

        // Validate the Nhaxuatban in the database
        List<Nhaxuatban> nhaxuatbanList = nhaxuatbanRepository.findAll();
        assertThat(nhaxuatbanList).hasSize(databaseSizeBeforeCreate);

        // Validate the Nhaxuatban in Elasticsearch
        verify(mockNhaxuatbanSearchRepository, times(0)).save(nhaxuatban);
    }

    @Test
    @Transactional
    void getAllNhaxuatbans() throws Exception {
        // Initialize the database
        nhaxuatbanRepository.saveAndFlush(nhaxuatban);

        // Get all the nhaxuatbanList
        restNhaxuatbanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nhaxuatban.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenNXB").value(hasItem(DEFAULT_TEN_NXB)))
            .andExpect(jsonPath("$.[*].diaChi").value(hasItem(DEFAULT_DIA_CHI)));
    }

    @Test
    @Transactional
    void getNhaxuatban() throws Exception {
        // Initialize the database
        nhaxuatbanRepository.saveAndFlush(nhaxuatban);

        // Get the nhaxuatban
        restNhaxuatbanMockMvc
            .perform(get(ENTITY_API_URL_ID, nhaxuatban.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(nhaxuatban.getId().intValue()))
            .andExpect(jsonPath("$.tenNXB").value(DEFAULT_TEN_NXB))
            .andExpect(jsonPath("$.diaChi").value(DEFAULT_DIA_CHI));
    }

    @Test
    @Transactional
    void getNonExistingNhaxuatban() throws Exception {
        // Get the nhaxuatban
        restNhaxuatbanMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewNhaxuatban() throws Exception {
        // Initialize the database
        nhaxuatbanRepository.saveAndFlush(nhaxuatban);

        int databaseSizeBeforeUpdate = nhaxuatbanRepository.findAll().size();

        // Update the nhaxuatban
        Nhaxuatban updatedNhaxuatban = nhaxuatbanRepository.findById(nhaxuatban.getId()).get();
        // Disconnect from session so that the updates on updatedNhaxuatban are not directly saved in db
        em.detach(updatedNhaxuatban);
        updatedNhaxuatban.tenNXB(UPDATED_TEN_NXB).diaChi(UPDATED_DIA_CHI);

        restNhaxuatbanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedNhaxuatban.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedNhaxuatban))
            )
            .andExpect(status().isOk());

        // Validate the Nhaxuatban in the database
        List<Nhaxuatban> nhaxuatbanList = nhaxuatbanRepository.findAll();
        assertThat(nhaxuatbanList).hasSize(databaseSizeBeforeUpdate);
        Nhaxuatban testNhaxuatban = nhaxuatbanList.get(nhaxuatbanList.size() - 1);
        assertThat(testNhaxuatban.getTenNXB()).isEqualTo(UPDATED_TEN_NXB);
        assertThat(testNhaxuatban.getDiaChi()).isEqualTo(UPDATED_DIA_CHI);

        // Validate the Nhaxuatban in Elasticsearch
        verify(mockNhaxuatbanSearchRepository).save(testNhaxuatban);
    }

    @Test
    @Transactional
    void putNonExistingNhaxuatban() throws Exception {
        int databaseSizeBeforeUpdate = nhaxuatbanRepository.findAll().size();
        nhaxuatban.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNhaxuatbanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, nhaxuatban.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(nhaxuatban))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nhaxuatban in the database
        List<Nhaxuatban> nhaxuatbanList = nhaxuatbanRepository.findAll();
        assertThat(nhaxuatbanList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Nhaxuatban in Elasticsearch
        verify(mockNhaxuatbanSearchRepository, times(0)).save(nhaxuatban);
    }

    @Test
    @Transactional
    void putWithIdMismatchNhaxuatban() throws Exception {
        int databaseSizeBeforeUpdate = nhaxuatbanRepository.findAll().size();
        nhaxuatban.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNhaxuatbanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(nhaxuatban))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nhaxuatban in the database
        List<Nhaxuatban> nhaxuatbanList = nhaxuatbanRepository.findAll();
        assertThat(nhaxuatbanList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Nhaxuatban in Elasticsearch
        verify(mockNhaxuatbanSearchRepository, times(0)).save(nhaxuatban);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNhaxuatban() throws Exception {
        int databaseSizeBeforeUpdate = nhaxuatbanRepository.findAll().size();
        nhaxuatban.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNhaxuatbanMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(nhaxuatban)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Nhaxuatban in the database
        List<Nhaxuatban> nhaxuatbanList = nhaxuatbanRepository.findAll();
        assertThat(nhaxuatbanList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Nhaxuatban in Elasticsearch
        verify(mockNhaxuatbanSearchRepository, times(0)).save(nhaxuatban);
    }

    @Test
    @Transactional
    void partialUpdateNhaxuatbanWithPatch() throws Exception {
        // Initialize the database
        nhaxuatbanRepository.saveAndFlush(nhaxuatban);

        int databaseSizeBeforeUpdate = nhaxuatbanRepository.findAll().size();

        // Update the nhaxuatban using partial update
        Nhaxuatban partialUpdatedNhaxuatban = new Nhaxuatban();
        partialUpdatedNhaxuatban.setId(nhaxuatban.getId());

        restNhaxuatbanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNhaxuatban.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNhaxuatban))
            )
            .andExpect(status().isOk());

        // Validate the Nhaxuatban in the database
        List<Nhaxuatban> nhaxuatbanList = nhaxuatbanRepository.findAll();
        assertThat(nhaxuatbanList).hasSize(databaseSizeBeforeUpdate);
        Nhaxuatban testNhaxuatban = nhaxuatbanList.get(nhaxuatbanList.size() - 1);
        assertThat(testNhaxuatban.getTenNXB()).isEqualTo(DEFAULT_TEN_NXB);
        assertThat(testNhaxuatban.getDiaChi()).isEqualTo(DEFAULT_DIA_CHI);
    }

    @Test
    @Transactional
    void fullUpdateNhaxuatbanWithPatch() throws Exception {
        // Initialize the database
        nhaxuatbanRepository.saveAndFlush(nhaxuatban);

        int databaseSizeBeforeUpdate = nhaxuatbanRepository.findAll().size();

        // Update the nhaxuatban using partial update
        Nhaxuatban partialUpdatedNhaxuatban = new Nhaxuatban();
        partialUpdatedNhaxuatban.setId(nhaxuatban.getId());

        partialUpdatedNhaxuatban.tenNXB(UPDATED_TEN_NXB).diaChi(UPDATED_DIA_CHI);

        restNhaxuatbanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNhaxuatban.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNhaxuatban))
            )
            .andExpect(status().isOk());

        // Validate the Nhaxuatban in the database
        List<Nhaxuatban> nhaxuatbanList = nhaxuatbanRepository.findAll();
        assertThat(nhaxuatbanList).hasSize(databaseSizeBeforeUpdate);
        Nhaxuatban testNhaxuatban = nhaxuatbanList.get(nhaxuatbanList.size() - 1);
        assertThat(testNhaxuatban.getTenNXB()).isEqualTo(UPDATED_TEN_NXB);
        assertThat(testNhaxuatban.getDiaChi()).isEqualTo(UPDATED_DIA_CHI);
    }

    @Test
    @Transactional
    void patchNonExistingNhaxuatban() throws Exception {
        int databaseSizeBeforeUpdate = nhaxuatbanRepository.findAll().size();
        nhaxuatban.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNhaxuatbanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, nhaxuatban.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(nhaxuatban))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nhaxuatban in the database
        List<Nhaxuatban> nhaxuatbanList = nhaxuatbanRepository.findAll();
        assertThat(nhaxuatbanList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Nhaxuatban in Elasticsearch
        verify(mockNhaxuatbanSearchRepository, times(0)).save(nhaxuatban);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNhaxuatban() throws Exception {
        int databaseSizeBeforeUpdate = nhaxuatbanRepository.findAll().size();
        nhaxuatban.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNhaxuatbanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(nhaxuatban))
            )
            .andExpect(status().isBadRequest());

        // Validate the Nhaxuatban in the database
        List<Nhaxuatban> nhaxuatbanList = nhaxuatbanRepository.findAll();
        assertThat(nhaxuatbanList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Nhaxuatban in Elasticsearch
        verify(mockNhaxuatbanSearchRepository, times(0)).save(nhaxuatban);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNhaxuatban() throws Exception {
        int databaseSizeBeforeUpdate = nhaxuatbanRepository.findAll().size();
        nhaxuatban.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNhaxuatbanMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(nhaxuatban))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Nhaxuatban in the database
        List<Nhaxuatban> nhaxuatbanList = nhaxuatbanRepository.findAll();
        assertThat(nhaxuatbanList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Nhaxuatban in Elasticsearch
        verify(mockNhaxuatbanSearchRepository, times(0)).save(nhaxuatban);
    }

    @Test
    @Transactional
    void deleteNhaxuatban() throws Exception {
        // Initialize the database
        nhaxuatbanRepository.saveAndFlush(nhaxuatban);

        int databaseSizeBeforeDelete = nhaxuatbanRepository.findAll().size();

        // Delete the nhaxuatban
        restNhaxuatbanMockMvc
            .perform(delete(ENTITY_API_URL_ID, nhaxuatban.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Nhaxuatban> nhaxuatbanList = nhaxuatbanRepository.findAll();
        assertThat(nhaxuatbanList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Nhaxuatban in Elasticsearch
        verify(mockNhaxuatbanSearchRepository, times(1)).deleteById(nhaxuatban.getId());
    }

    @Test
    @Transactional
    void searchNhaxuatban() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        nhaxuatbanRepository.saveAndFlush(nhaxuatban);
        when(mockNhaxuatbanSearchRepository.search(queryStringQuery("id:" + nhaxuatban.getId())))
            .thenReturn(Collections.singletonList(nhaxuatban));

        // Search the nhaxuatban
        restNhaxuatbanMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + nhaxuatban.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nhaxuatban.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenNXB").value(hasItem(DEFAULT_TEN_NXB)))
            .andExpect(jsonPath("$.[*].diaChi").value(hasItem(DEFAULT_DIA_CHI)));
    }
}
