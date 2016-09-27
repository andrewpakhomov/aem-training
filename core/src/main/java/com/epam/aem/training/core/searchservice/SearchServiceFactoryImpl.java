/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.searchservice;

import java.util.LinkedList;
import java.util.List;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.log.LogService;

/**
 *
 * @author Andrey_Pakhomov
 */
@Service
@Component
public class SearchServiceFactoryImpl implements SearchServiceFactory {

    @Reference
    private LogService logService;

    public SearchServiceFactoryImpl() {
    }
    
    @Override
    public AbstractSearchService getSearchService(List<String> searchLocation, String searchString, SearchApiType type, ResourceResolver resourceResolver ){
        switch (type){
            case QUERY_BUILDER_API:{
                return new QueryBuilderSearchServiceImpl(searchLocation, searchString, resourceResolver, this.logService);
            }
            case QUERY_MANAGER:{
                return new QueryManagerSearchServiceImpl(searchLocation, searchString, resourceResolver, this.logService);
            }
            default:{
                throw new RuntimeException("Unknown search service type");
            }
        }
    }
    
    
    
    
    
    

}
