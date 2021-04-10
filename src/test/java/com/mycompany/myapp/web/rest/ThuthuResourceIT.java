package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Thuthu;
import com.mycompany.myapp.repository.ThuthuRepository;
import com.mycompany.myapp.repository.search.ThuthuSearchRepository;
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
 * Integration tests for the {@link ThuthuResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ThuthuResourceIT {

    private static final String DEFAULT_HO_TEN = "AAAAAAAAAA";
    private static final String UPDATED_HO_TEN = "BBBBBBBBBB";

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/thuthus";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/thuthus";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ThuthuRepository thuthuRepository;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.ThuthuSearchRepositoryMockConfiguration
     */
    @Autowired
    private ThuthuSearchRepository mockThuthuSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restThuthuMockMvc;

    private Thuthu thuthu;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Thuthu createEntity(EntityManager em) {
        Thuthu thuthu = new Thuthu().hoTen(DEFAULT_HO_TEN).username(DEFAULT_USERNAME).password(DEFAULT_PASSWORD);
        return thuthu;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Thuthu createUpdatedEntity(EntityManager em) {
        Thuthu thuthu = new Thuthu().hoTen(UPDATED_HO_TEN).username(UPDATED_USERNAME).password(UPDATED_PASSWORD);
        return thuthu;
    }

    @BeforeEach
    public void initTest() {
        thuthu = createEntity(em);
    }

    @Test
    @Transactional
    void createThuthu() throws Exception {
        int databaseSizeBeforeCreate = thuthuRepository.findAll().size();
        // Create the Thuthu
        restThuthuMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(thuthu)))
            .andExpect(status().isCreated());

        // Validate the Thuthu in the database
        List<Thuthu> thuthuList = thuthuRepository.findAll();
        assertThat(thuthuList).hasSize(databaseSizeBeforeCreate + 1);
        Thuthu testThuthu = thuthuList.get(thuthuList.size() - 1);
        assertThat(testThuthu.getHoTen()).isEqualTo(DEFAULT_HO_TEN);
        assertThat(testThuthu.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testThuthu.getPassword()).isEqualTo(DEFAULT_PASSWORD);

        // Validate the Thuthu in Elasticsearch
        verify(mockThuthuSearchRepository, times(1)).save(testThuthu);
    }

    @Test
    @Transactional
    void createThuthuWithExistingId() throws Exception {
        // Create the Thuthu with an existing ID
        thuthu.setId(1L);

        int databaseSizeBeforeCreate = thuthuRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restThuthuMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(thuthu)))
            .andExpect(status().isBadRequest());

        // Validate the Thuthu in the database
        List<Thuthu> thuthuList = thuthuRepository.findAll();
        assertThat(thuthuList).hasSize(databaseSizeBeforeCreate);

        // Validate the Thuthu in Elasticsearch
        verify(mockThuthuSearchRepository, times(0)).save(thuthu);
    }

    @Test
    @Transactional
    void getAllThuthus() throws Exception {
        // Initialize the database
        thuthuRepository.saveAndFlush(thuthu);

        // Get all the thuthuList
        restThuthuMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(thuthu.getId().intValue())))
            .andExpect(jsonPath("$.[*].hoTen").value(hasItem(DEFAULT_HO_TEN)))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)));
    }

    @Test
    @Transactional
    void getThuthu() throws Exception {
        // Initialize the database
        thuthuRepository.saveAndFlush(thuthu);

        // Get the thuthu
        restThuthuMockMvc
            .perform(get(ENTITY_API_URL_ID, thuthu.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(thuthu.getId().intValue()))
            .andExpect(jsonPath("$.hoTen").value(DEFAULT_HO_TEN))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD));
    }

    @Test
    @Transactional
    void getNonExistingThuthu() throws Exception {
        // Get the thuthu
        restThuthuMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewThuthu() throws Exception {
        // Initialize the database
        thuthuRepository.saveAndFlush(thuthu);

        int databaseSizeBeforeUpdate = thuthuRepository.findAll().size();

        // Update the thuthu
        Thuthu updatedThuthu = thuthuRepository.findById(thuthu.getId()).get();
        // Disconnect from session so that the updates on updatedThuthu are not directly saved in db
        em.detach(updatedThuthu);
        updatedThuthu.hoTen(UPDATED_HO_TEN).username(UPDATED_USERNAME).password(UPDATED_PASSWORD);

        restThuthuMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedThuthu.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedThuthu))
            )
            .andExpect(status().isOk());

        // Validate the Thuthu in the database
        List<Thuthu> thuthuList = thuthuRepository.findAll();
        assertThat(thuthuList).hasSize(databaseSizeBeforeUpdate);
        Thuthu testThuthu = thuthuList.get(thuthuList.size() - 1);
        assertThat(testThuthu.getHoTen()).isEqualTo(UPDATED_HO_TEN);
        assertThat(testThuthu.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testThuthu.getPassword()).isEqualTo(UPDATED_PASSWORD);

        // Validate the Thuthu in Elasticsearch
        verify(mockThuthuSearchRepository).save(testThuthu);
    }

    @Test
    @Transactional
    void putNonExistingThuthu() throws Exception {
        int databaseSizeBeforeUpdate = thuthuRepository.findAll().size();
        thuthu.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restThuthuMockMvc
            .perform(
                put(ENTITY_API_URL_ID, thuthu.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(thuthu))
            )
            .andExpect(status().isBadRequest());

        // Validate the Thuthu in the database
        List<Thuthu> thuthuList = thuthuRepository.findAll();
        assertThat(thuthuList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Thuthu in Elasticsearch
        verify(mockThuthuSearchRepository, times(0)).save(thuthu);
    }

    @Test
    @Transactional
    void putWithIdMismatchThuthu() throws Exception {
        int databaseSizeBeforeUpdate = thuthuRepository.findAll().size();
        thuthu.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restThuthuMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(thuthu))
            )
            .andExpect(status().isBadRequest());

        // Validate the Thuthu in the database
        List<Thuthu> thuthuList = thuthuRepository.findAll();
        assertThat(thuthuList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Thuthu in Elasticsearch
        verify(mockThuthuSearchRepository, times(0)).save(thuthu);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamThuthu() throws Exception {
        int databaseSizeBeforeUpdate = thuthuRepository.findAll().size();
        thuthu.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restThuthuMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(thuthu)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Thuthu in the database
        List<Thuthu> thuthuList = thuthuRepository.findAll();
        assertThat(thuthuList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Thuthu in Elasticsearch
        verify(mockThuthuSearchRepository, times(0)).save(thuthu);
    }

    @Test
    @Transactional
    void partialUpdateThuthuWithPatch() throws Exception {
        // Initialize the database
        thuthuRepository.saveAndFlush(thuthu);

        int databaseSizeBeforeUpdate = thuthuRepository.findAll().size();

        // Update the thuthu using partial update
        Thuthu partialUpdatedThuthu = new Thuthu();
        partialUpdatedThuthu.setId(thuthu.getId());

        partialUpdatedThuthu.hoTen(UPDATED_HO_TEN);

        restThuthuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedThuthu.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedThuthu))
            )
            .andExpect(status().isOk());

        // Validate the Thuthu in the database
        List<Thuthu> thuthuList = thuthuRepository.findAll();
        assertThat(thuthuList).hasSize(databaseSizeBeforeUpdate);
        Thuthu testThuthu = thuthuList.get(thuthuList.size() - 1);
        assertThat(testThuthu.getHoTen()).isEqualTo(UPDATED_HO_TEN);
        assertThat(testThuthu.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testThuthu.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    @Transactional
    void fullUpdateThuthuWithPatch() throws Exception {
        // Initialize the database
        thuthuRepository.saveAndFlush(thuthu);

        int databaseSizeBeforeUpdate = thuthuRepository.findAll().size();

        // Update the thuthu using partial update
        Thuthu partialUpdatedThuthu = new Thuthu();
        partialUpdatedThuthu.setId(thuthu.getId());

        partialUpdatedThuthu.hoTen(UPDATED_HO_TEN).username(UPDATED_USERNAME).password(UPDATED_PASSWORD);

        restThuthuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedThuthu.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedThuthu))
            )
            .andExpect(status().isOk());

        // Validate the Thuthu in the database
        List<Thuthu> thuthuList = thuthuRepository.findAll();
        assertThat(thuthuList).hasSize(databaseSizeBeforeUpdate);
        Thuthu testThuthu = thuthuList.get(thuthuList.size() - 1);
        assertThat(testThuthu.getHoTen()).isEqualTo(UPDATED_HO_TEN);
        assertThat(testThuthu.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testThuthu.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void patchNonExistingThuthu() throws Exception {
        int databaseSizeBeforeUpdate = thuthuRepository.findAll().size();
        thuthu.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restThuthuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, thuthu.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(thuthu))
            )
            .andExpect(status().isBadRequest());

        // Validate the Thuthu in the database
        List<Thuthu> thuthuList = thuthuRepository.findAll();
        assertThat(thuthuList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Thuthu in Elasticsearch
        verify(mockThuthuSearchRepository, times(0)).save(thuthu);
    }

    @Test
    @Transactional
    void patchWithIdMismatchThuthu() throws Exception {
        int databaseSizeBeforeUpdate = thuthuRepository.findAll().size();
        thuthu.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restThuthuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(thuthu))
            )
            .andExpect(status().isBadRequest());

        // Validate the Thuthu in the database
        List<Thuthu> thuthuList = thuthuRepository.findAll();
        assertThat(thuthuList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Thuthu in Elasticsearch
        verify(mockThuthuSearchRepository, times(0)).save(thuthu);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamThuthu() throws Exception {
        int databaseSizeBeforeUpdate = thuthuRepository.findAll().size();
        thuthu.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restThuthuMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(thuthu)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Thuthu in the database
        List<Thuthu> thuthuList = thuthuRepository.findAll();
        assertThat(thuthuList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Thuthu in Elasticsearch
        verify(mockThuthuSearchRepository, times(0)).save(thuthu);
    }

    @Test
    @Transactional
    void deleteThuthu() throws Exception {
        // Initialize the database
        thuthuRepository.saveAndFlush(thuthu);

        int databaseSizeBeforeDelete = thuthuRepository.findAll().size();

        // Delete the thuthu
        restThuthuMockMvc
            .perform(delete(ENTITY_API_URL_ID, thuthu.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Thuthu> thuthuList = thuthuRepository.findAll();
        assertThat(thuthuList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Thuthu in Elasticsearch
        verify(mockThuthuSearchRepository, times(1)).deleteById(thuthu.getId());
    }

    @Test
    @Transactional
    void searchThuthu() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        thuthuRepository.saveAndFlush(thuthu);
        when(mockThuthuSearchRepository.search(queryStringQuery("id:" + thuthu.getId()))).thenReturn(Collections.singletonList(thuthu));

        // Search the thuthu
        restThuthuMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + thuthu.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(thuthu.getId().intValue())))
            .andExpect(jsonPath("$.[*].hoTen").value(hasItem(DEFAULT_HO_TEN)))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)));
    }
}
