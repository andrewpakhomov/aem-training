/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.searchservice;

import java.util.List;
import org.apache.sling.api.resource.ResourceResolver;

/**
 *
 * @author Andrey_Pakhomov
 */
public interface SearchServiceFactory {

    AbstractSearchService getSearchService(List<String> searchLocation, String searchString, SearchApiType type, ResourceResolver resourceResolver);
    
}
