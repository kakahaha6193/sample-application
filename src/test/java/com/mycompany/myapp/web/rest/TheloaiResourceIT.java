package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Theloai;
import com.mycompany.myapp.repository.TheloaiRepository;
import com.mycompany.myapp.repository.search.TheloaiSearchRepository;
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
 * Integration tests for the {@link TheloaiResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TheloaiResourceIT {

    private static final String DEFAULT_TEN_THE_LOAI = "AAAAAAAAAA";
    private static final String UPDATED_TEN_THE_LOAI = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/theloais";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/theloais";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TheloaiRepository theloaiRepository;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.TheloaiSearchRepositoryMockConfiguration
     */
    @Autowired
    private TheloaiSearchRepository mockTheloaiSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTheloaiMockMvc;

    private Theloai theloai;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Theloai createEntity(EntityManager em) {
        Theloai theloai = new Theloai().tenTheLoai(DEFAULT_TEN_THE_LOAI);
        return theloai;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Theloai createUpdatedEntity(EntityManager em) {
        Theloai theloai = new Theloai().tenTheLoai(UPDATED_TEN_THE_LOAI);
        return theloai;
    }

    @BeforeEach
    public void initTest() {
        theloai = createEntity(em);
    }

    @Test
    @Transactional
    void createTheloai() throws Exception {
        int databaseSizeBeforeCreate = theloaiRepository.findAll().size();
        // Create the Theloai
        restTheloaiMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(theloai)))
            .andExpect(status().isCreated());

        // Validate the Theloai in the database
        List<Theloai> theloaiList = theloaiRepository.findAll();
        assertThat(theloaiList).hasSize(databaseSizeBeforeCreate + 1);
        Theloai testTheloai = theloaiList.get(theloaiList.size() - 1);
        assertThat(testTheloai.getTenTheLoai()).isEqualTo(DEFAULT_TEN_THE_LOAI);

        // Validate the Theloai in Elasticsearch
        verify(mockTheloaiSearchRepository, times(1)).save(testTheloai);
    }

    @Test
    @Transactional
    void createTheloaiWithExistingId() throws Exception {
        // Create the Theloai with an existing ID
        theloai.setId(1L);

        int databaseSizeBeforeCreate = theloaiRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTheloaiMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(theloai)))
            .andExpect(status().isBadRequest());

        // Validate the Theloai in the database
        List<Theloai> theloaiList = theloaiRepository.findAll();
        assertThat(theloaiList).hasSize(databaseSizeBeforeCreate);

        // Validate the Theloai in Elasticsearch
        verify(mockTheloaiSearchRepository, times(0)).save(theloai);
    }

    @Test
    @Transactional
    void getAllTheloais() throws Exception {
        // Initialize the database
        theloaiRepository.saveAndFlush(theloai);

        // Get all the theloaiList
        restTheloaiMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(theloai.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenTheLoai").value(hasItem(DEFAULT_TEN_THE_LOAI)));
    }

    @Test
    @Transactional
    void getTheloai() throws Exception {
        // Initialize the database
        theloaiRepository.saveAndFlush(theloai);

        // Get the theloai
        restTheloaiMockMvc
            .perform(get(ENTITY_API_URL_ID, theloai.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(theloai.getId().intValue()))
            .andExpect(jsonPath("$.tenTheLoai").value(DEFAULT_TEN_THE_LOAI));
    }

    @Test
    @Transactional
    void getNonExistingTheloai() throws Exception {
        // Get the theloai
        restTheloaiMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTheloai() throws Exception {
        // Initialize the database
        theloaiRepository.saveAndFlush(theloai);

        int databaseSizeBeforeUpdate = theloaiRepository.findAll().size();

        // Update the theloai
        Theloai updatedTheloai = theloaiRepository.findById(theloai.getId()).get();
        // Disconnect from session so that the updates on updatedTheloai are not directly saved in db
        em.detach(updatedTheloai);
        updatedTheloai.tenTheLoai(UPDATED_TEN_THE_LOAI);

        restTheloaiMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTheloai.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTheloai))
            )
            .andExpect(status().isOk());

        // Validate the Theloai in the database
        List<Theloai> theloaiList = theloaiRepository.findAll();
        assertThat(theloaiList).hasSize(databaseSizeBeforeUpdate);
        Theloai testTheloai = theloaiList.get(theloaiList.size() - 1);
        assertThat(testTheloai.getTenTheLoai()).isEqualTo(UPDATED_TEN_THE_LOAI);

        // Validate the Theloai in Elasticsearch
        verify(mockTheloaiSearchRepository).save(testTheloai);
    }

    @Test
    @Transactional
    void putNonExistingTheloai() throws Exception {
        int databaseSizeBeforeUpdate = theloaiRepository.findAll().size();
        theloai.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTheloaiMockMvc
            .perform(
                put(ENTITY_API_URL_ID, theloai.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(theloai))
            )
            .andExpect(status().isBadRequest());

        // Validate the Theloai in the database
        List<Theloai> theloaiList = theloaiRepository.findAll();
        assertThat(theloaiList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Theloai in Elasticsearch
        verify(mockTheloaiSearchRepository, times(0)).save(theloai);
    }

    @Test
    @Transactional
    void putWithIdMismatchTheloai() throws Exception {
        int databaseSizeBeforeUpdate = theloaiRepository.findAll().size();
        theloai.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTheloaiMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(theloai))
            )
            .andExpect(status().isBadRequest());

        // Validate the Theloai in the database
        List<Theloai> theloaiList = theloaiRepository.findAll();
        assertThat(theloaiList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Theloai in Elasticsearch
        verify(mockTheloaiSearchRepository, times(0)).save(theloai);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTheloai() throws Exception {
        int databaseSizeBeforeUpdate = theloaiRepository.findAll().size();
        theloai.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTheloaiMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(theloai)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Theloai in the database
        List<Theloai> theloaiList = theloaiRepository.findAll();
        assertThat(theloaiList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Theloai in Elasticsearch
        verify(mockTheloaiSearchRepository, times(0)).save(theloai);
    }

    @Test
    @Transactional
    void partialUpdateTheloaiWithPatch() throws Exception {
        // Initialize the database
        theloaiRepository.saveAndFlush(theloai);

        int databaseSizeBeforeUpdate = theloaiRepository.findAll().size();

        // Update the theloai using partial update
        Theloai partialUpdatedTheloai = new Theloai();
        partialUpdatedTheloai.setId(theloai.getId());

        restTheloaiMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTheloai.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTheloai))
            )
            .andExpect(status().isOk());

        // Validate the Theloai in the database
        List<Theloai> theloaiList = theloaiRepository.findAll();
        assertThat(theloaiList).hasSize(databaseSizeBeforeUpdate);
        Theloai testTheloai = theloaiList.get(theloaiList.size() - 1);
        assertThat(testTheloai.getTenTheLoai()).isEqualTo(DEFAULT_TEN_THE_LOAI);
    }

    @Test
    @Transactional
    void fullUpdateTheloaiWithPatch() throws Exception {
        // Initialize the database
        theloaiRepository.saveAndFlush(theloai);

        int databaseSizeBeforeUpdate = theloaiRepository.findAll().size();

        // Update the theloai using partial update
        Theloai partialUpdatedTheloai = new Theloai();
        partialUpdatedTheloai.setId(theloai.getId());

        partialUpdatedTheloai.tenTheLoai(UPDATED_TEN_THE_LOAI);

        restTheloaiMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTheloai.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTheloai))
            )
            .andExpect(status().isOk());

        // Validate the Theloai in the database
        List<Theloai> theloaiList = theloaiRepository.findAll();
        assertThat(theloaiList).hasSize(databaseSizeBeforeUpdate);
        Theloai testTheloai = theloaiList.get(theloaiList.size() - 1);
        assertThat(testTheloai.getTenTheLoai()).isEqualTo(UPDATED_TEN_THE_LOAI);
    }

    @Test
    @Transactional
    void patchNonExistingTheloai() throws Exception {
        int databaseSizeBeforeUpdate = theloaiRepository.findAll().size();
        theloai.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTheloaiMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, theloai.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(theloai))
            )
            .andExpect(status().isBadRequest());

        // Validate the Theloai in the database
        List<Theloai> theloaiList = theloaiRepository.findAll();
        assertThat(theloaiList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Theloai in Elasticsearch
        verify(mockTheloaiSearchRepository, times(0)).save(theloai);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTheloai() throws Exception {
        int databaseSizeBeforeUpdate = theloaiRepository.findAll().size();
        theloai.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTheloaiMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(theloai))
            )
            .andExpect(status().isBadRequest());

        // Validate the Theloai in the database
        List<Theloai> theloaiList = theloaiRepository.findAll();
        assertThat(theloaiList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Theloai in Elasticsearch
        verify(mockTheloaiSearchRepository, times(0)).save(theloai);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTheloai() throws Exception {
        int databaseSizeBeforeUpdate = theloaiRepository.findAll().size();
        theloai.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTheloaiMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(theloai)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Theloai in the database
        List<Theloai> theloaiList = theloaiRepository.findAll();
        assertThat(theloaiList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Theloai in Elasticsearch
        verify(mockTheloaiSearchRepository, times(0)).save(theloai);
    }

    @Test
    @Transactional
    void deleteTheloai() throws Exception {
        // Initialize the database
        theloaiRepository.saveAndFlush(theloai);

        int databaseSizeBeforeDelete = theloaiRepository.findAll().size();

        // Delete the theloai
        restTheloaiMockMvc
            .perform(delete(ENTITY_API_URL_ID, theloai.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Theloai> theloaiList = theloaiRepository.findAll();
        assertThat(theloaiList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Theloai in Elasticsearch
        verify(mockTheloaiSearchRepository, times(1)).deleteById(theloai.getId());
    }

    @Test
    @Transactional
    void searchTheloai() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        theloaiRepository.saveAndFlush(theloai);
        when(mockTheloaiSearchRepository.search(queryStringQuery("id:" + theloai.getId()))).thenReturn(Collections.singletonList(theloai));

        // Search the theloai
        restTheloaiMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + theloai.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(theloai.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenTheLoai").value(hasItem(DEFAULT_TEN_THE_LOAI)));
    }
}
