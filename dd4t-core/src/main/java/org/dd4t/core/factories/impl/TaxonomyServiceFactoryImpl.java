package org.dd4t.core.factories.impl;

import org.dd4t.contentmodel.Keyword;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.factories.TaxonomyFactory;
import org.dd4t.core.factories.TaxonomyServiceFactory;
import org.dd4t.core.services.LabelService;
import org.dd4t.core.services.TaxonomyService;
import org.dd4t.core.services.impl.TaxonomyServiceImpl;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;

/**
 * Service class that wraps around a Taxonomy Keyword and offers utility methods for retrieving an entire Taxonomy
 * (as Keyword) or individual Keywords identified by their name, key, desciption or TCMURI.
 *
 * @author Mihai Cadariu
 */
public class TaxonomyServiceFactoryImpl implements TaxonomyServiceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TaxonomyServiceFactoryImpl.class);
    private static final TaxonomyServiceFactory INSTANCE = new TaxonomyServiceFactoryImpl();
    private LabelService labelService;
    private TaxonomyFactory taxonomyFactory;

    @Autowired
    private CacheProvider cacheProvider;

    private TaxonomyServiceFactoryImpl() {
        LOG.debug("Create new instance");
    }

    public static TaxonomyServiceFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Generic taxonomy service.
     *
     * @param category String representing the Tridion Category XML name or TCMURI
     * @return TaxonomyService
     * @throws IOException if something goes wrong while fetching the taxonomy
     */
    @Override
    public TaxonomyService getTaxonomyService(String category) throws IOException {
        String categoryURI = labelService.getCategoryLabel(category);

        String key = getCacheKey(categoryURI);
        CacheElement<TaxonomyService> cacheElement = cacheProvider.loadFromLocalCache(key);
        TaxonomyService result;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);

                    Keyword taxonomy = taxonomyFactory.getTaxonomy(categoryURI);
                    result = new TaxonomyServiceImpl(taxonomy);
                    cacheElement.setPayload(result);

                    try {
                        TCMURI tcmUri = new TCMURI(categoryURI);
                        cacheProvider.storeInItemCache(key, cacheElement, tcmUri.getPublicationId(), tcmUri.getItemId());
                        LOG.debug("Added TaxonomyService with uri: {} to cache", categoryURI);
                    } catch (ParseException e) {
                        LOG.error("Invalid Category URI found: " + categoryURI);
                        cacheProvider.storeInItemCache(key, cacheElement);

                        throw new IOException("Invalid Category URI found: " + categoryURI, e);
                    }
                } else {
                    LOG.debug("Return TaxonomyService with uri: {} from cache", categoryURI);
                    result = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return TaxonomyService with uri: {} from cache", categoryURI);
            result = cacheElement.getPayload();
        }

        return result;
    }

    /**
     * Generic taxonomy service that retrieves only classified Component based on the given SchemaURI.
     *
     * @param category String representing the Tridion Category XML name or TCMURI
     * @param schema   String representing the filter for classified related Components to return for each Keyword
     * @return TaxonomyService
     * @throws IOException if something goes wrong while fetching the taxonomy
     */
    @Override
    public TaxonomyService getTaxonomyBySchemaService(String category, String schema)
            throws IOException {
        String categoryURI = labelService.getCategoryLabel(category);
        String schemaURI = labelService.getSchemaLabel(schema);

        String key = getCacheKey(categoryURI, schemaURI);
        CacheElement<TaxonomyService> cacheElement = cacheProvider.loadFromLocalCache(key);
        TaxonomyService result;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);

                    Keyword taxonomy = taxonomyFactory.getTaxonomyFilterBySchema(categoryURI, schemaURI);
                    result = new TaxonomyServiceImpl(taxonomy);
                    cacheElement.setPayload(result);

                    try {
                        TCMURI tcmUri = new TCMURI(categoryURI);
                        cacheProvider.storeInItemCache(key, cacheElement, tcmUri.getPublicationId(), tcmUri.getItemId());
                        LOG.debug("Added TaxonomyService with uri: {} and schema: {} to cache", categoryURI, schemaURI);
                    } catch (ParseException e) {
                        LOG.error("Invalid Category URI found: " + categoryURI);
                        cacheProvider.storeInItemCache(key, cacheElement);

                        throw new IOException("Invalid Category URI found: " + categoryURI, e);
                    }
                } else {
                    LOG.debug("Return TaxonomyService with uri: {} and schema: {} from cache", categoryURI, schemaURI);
                    result = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return TaxonomyService with uri: {} and schema: {} from cache", categoryURI, schemaURI);
            result = cacheElement.getPayload();
        }

        return result;
    }

    private String getCacheKey(String categoryURI) {
        return String.format("TS-%s", categoryURI);
    }

    private String getCacheKey(String categoryURI, String schemaURI) {
        return String.format("TS-%s-%s", categoryURI, schemaURI);
    }

    /**
     * Set LabelService value
     *
     * @param labelService
     */
    @Autowired
    public void setLabelService(LabelService labelService) {
        LOG.debug("Set LabelService " + labelService);
        this.labelService = labelService;
    }

    /**
     * Set TaxonomyFactory value
     *
     * @param taxonomyFactory
     */
    @Autowired
    public void setTaxonomyFactory(TaxonomyFactory taxonomyFactory) {
        LOG.debug("Set TaxonomyFactory" + taxonomyFactory);
        this.taxonomyFactory = taxonomyFactory;
    }
}