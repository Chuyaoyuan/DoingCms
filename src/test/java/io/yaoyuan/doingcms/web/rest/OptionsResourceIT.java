package io.yaoyuan.doingcms.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.yaoyuan.doingcms.IntegrationTest;
import io.yaoyuan.doingcms.domain.Options;
import io.yaoyuan.doingcms.repository.OptionsRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OptionsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OptionsResourceIT {

    private static final String DEFAULT_OPTION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OPTION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OPTION_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_OPTION_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_AUTOLOAD = "AAAAAAAAAA";
    private static final String UPDATED_AUTOLOAD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/options";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OptionsRepository optionsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOptionsMockMvc;

    private Options options;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Options createEntity(EntityManager em) {
        Options options = new Options().optionName(DEFAULT_OPTION_NAME).optionValue(DEFAULT_OPTION_VALUE).autoload(DEFAULT_AUTOLOAD);
        return options;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Options createUpdatedEntity(EntityManager em) {
        Options options = new Options().optionName(UPDATED_OPTION_NAME).optionValue(UPDATED_OPTION_VALUE).autoload(UPDATED_AUTOLOAD);
        return options;
    }

    @BeforeEach
    public void initTest() {
        options = createEntity(em);
    }

    @Test
    @Transactional
    void createOptions() throws Exception {
        int databaseSizeBeforeCreate = optionsRepository.findAll().size();
        // Create the Options
        restOptionsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(options)))
            .andExpect(status().isCreated());

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeCreate + 1);
        Options testOptions = optionsList.get(optionsList.size() - 1);
        assertThat(testOptions.getOptionName()).isEqualTo(DEFAULT_OPTION_NAME);
        assertThat(testOptions.getOptionValue()).isEqualTo(DEFAULT_OPTION_VALUE);
        assertThat(testOptions.getAutoload()).isEqualTo(DEFAULT_AUTOLOAD);
    }

    @Test
    @Transactional
    void createOptionsWithExistingId() throws Exception {
        // Create the Options with an existing ID
        options.setId(1L);

        int databaseSizeBeforeCreate = optionsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOptionsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(options)))
            .andExpect(status().isBadRequest());

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOptionNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = optionsRepository.findAll().size();
        // set the field null
        options.setOptionName(null);

        // Create the Options, which fails.

        restOptionsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(options)))
            .andExpect(status().isBadRequest());

        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOptionValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = optionsRepository.findAll().size();
        // set the field null
        options.setOptionValue(null);

        // Create the Options, which fails.

        restOptionsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(options)))
            .andExpect(status().isBadRequest());

        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAutoloadIsRequired() throws Exception {
        int databaseSizeBeforeTest = optionsRepository.findAll().size();
        // set the field null
        options.setAutoload(null);

        // Create the Options, which fails.

        restOptionsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(options)))
            .andExpect(status().isBadRequest());

        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOptions() throws Exception {
        // Initialize the database
        optionsRepository.saveAndFlush(options);

        // Get all the optionsList
        restOptionsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(options.getId().intValue())))
            .andExpect(jsonPath("$.[*].optionName").value(hasItem(DEFAULT_OPTION_NAME)))
            .andExpect(jsonPath("$.[*].optionValue").value(hasItem(DEFAULT_OPTION_VALUE)))
            .andExpect(jsonPath("$.[*].autoload").value(hasItem(DEFAULT_AUTOLOAD)));
    }

    @Test
    @Transactional
    void getOptions() throws Exception {
        // Initialize the database
        optionsRepository.saveAndFlush(options);

        // Get the options
        restOptionsMockMvc
            .perform(get(ENTITY_API_URL_ID, options.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(options.getId().intValue()))
            .andExpect(jsonPath("$.optionName").value(DEFAULT_OPTION_NAME))
            .andExpect(jsonPath("$.optionValue").value(DEFAULT_OPTION_VALUE))
            .andExpect(jsonPath("$.autoload").value(DEFAULT_AUTOLOAD));
    }

    @Test
    @Transactional
    void getNonExistingOptions() throws Exception {
        // Get the options
        restOptionsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOptions() throws Exception {
        // Initialize the database
        optionsRepository.saveAndFlush(options);

        int databaseSizeBeforeUpdate = optionsRepository.findAll().size();

        // Update the options
        Options updatedOptions = optionsRepository.findById(options.getId()).get();
        // Disconnect from session so that the updates on updatedOptions are not directly saved in db
        em.detach(updatedOptions);
        updatedOptions.optionName(UPDATED_OPTION_NAME).optionValue(UPDATED_OPTION_VALUE).autoload(UPDATED_AUTOLOAD);

        restOptionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOptions.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOptions))
            )
            .andExpect(status().isOk());

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
        Options testOptions = optionsList.get(optionsList.size() - 1);
        assertThat(testOptions.getOptionName()).isEqualTo(UPDATED_OPTION_NAME);
        assertThat(testOptions.getOptionValue()).isEqualTo(UPDATED_OPTION_VALUE);
        assertThat(testOptions.getAutoload()).isEqualTo(UPDATED_AUTOLOAD);
    }

    @Test
    @Transactional
    void putNonExistingOptions() throws Exception {
        int databaseSizeBeforeUpdate = optionsRepository.findAll().size();
        options.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOptionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, options.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(options))
            )
            .andExpect(status().isBadRequest());

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOptions() throws Exception {
        int databaseSizeBeforeUpdate = optionsRepository.findAll().size();
        options.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOptionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(options))
            )
            .andExpect(status().isBadRequest());

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOptions() throws Exception {
        int databaseSizeBeforeUpdate = optionsRepository.findAll().size();
        options.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOptionsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(options)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOptionsWithPatch() throws Exception {
        // Initialize the database
        optionsRepository.saveAndFlush(options);

        int databaseSizeBeforeUpdate = optionsRepository.findAll().size();

        // Update the options using partial update
        Options partialUpdatedOptions = new Options();
        partialUpdatedOptions.setId(options.getId());

        partialUpdatedOptions.optionName(UPDATED_OPTION_NAME).autoload(UPDATED_AUTOLOAD);

        restOptionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOptions.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOptions))
            )
            .andExpect(status().isOk());

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
        Options testOptions = optionsList.get(optionsList.size() - 1);
        assertThat(testOptions.getOptionName()).isEqualTo(UPDATED_OPTION_NAME);
        assertThat(testOptions.getOptionValue()).isEqualTo(DEFAULT_OPTION_VALUE);
        assertThat(testOptions.getAutoload()).isEqualTo(UPDATED_AUTOLOAD);
    }

    @Test
    @Transactional
    void fullUpdateOptionsWithPatch() throws Exception {
        // Initialize the database
        optionsRepository.saveAndFlush(options);

        int databaseSizeBeforeUpdate = optionsRepository.findAll().size();

        // Update the options using partial update
        Options partialUpdatedOptions = new Options();
        partialUpdatedOptions.setId(options.getId());

        partialUpdatedOptions.optionName(UPDATED_OPTION_NAME).optionValue(UPDATED_OPTION_VALUE).autoload(UPDATED_AUTOLOAD);

        restOptionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOptions.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOptions))
            )
            .andExpect(status().isOk());

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
        Options testOptions = optionsList.get(optionsList.size() - 1);
        assertThat(testOptions.getOptionName()).isEqualTo(UPDATED_OPTION_NAME);
        assertThat(testOptions.getOptionValue()).isEqualTo(UPDATED_OPTION_VALUE);
        assertThat(testOptions.getAutoload()).isEqualTo(UPDATED_AUTOLOAD);
    }

    @Test
    @Transactional
    void patchNonExistingOptions() throws Exception {
        int databaseSizeBeforeUpdate = optionsRepository.findAll().size();
        options.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOptionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, options.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(options))
            )
            .andExpect(status().isBadRequest());

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOptions() throws Exception {
        int databaseSizeBeforeUpdate = optionsRepository.findAll().size();
        options.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOptionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(options))
            )
            .andExpect(status().isBadRequest());

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOptions() throws Exception {
        int databaseSizeBeforeUpdate = optionsRepository.findAll().size();
        options.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOptionsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(options)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOptions() throws Exception {
        // Initialize the database
        optionsRepository.saveAndFlush(options);

        int databaseSizeBeforeDelete = optionsRepository.findAll().size();

        // Delete the options
        restOptionsMockMvc
            .perform(delete(ENTITY_API_URL_ID, options.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Options> optionsList = optionsRepository.findAll();
        assertThat(optionsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
