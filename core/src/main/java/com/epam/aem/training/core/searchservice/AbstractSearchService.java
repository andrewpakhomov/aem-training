/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.searchservice;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.log.LogService;

/**
 *
 * @author Andrey_Pakhomov
 */
public abstract class AbstractSearchService {
    
    protected final List<String> searchLocations;
    
    protected final String searchString;
    
    protected final ResourceResolver resourceResolver;
    
    protected final LogService logService;

    protected AbstractSearchService(List<String> searchLocations, String searchString, ResourceResolver resourceResolver, LogService logService) {
        this.searchLocations = searchLocations;
        this.searchString = searchString;
        this.resourceResolver = resourceResolver;
        this.logService = logService;
    }
    
    abstract protected List<Node> doSearch() throws RepositoryException;
       
    public List<SearchResultViewModel> search(){
        try{
             List<Node> searchResult = this.doSearch();
            final List<SearchResultViewModel> result = new LinkedList<>();
            for (Node currentNode : searchResult){
                result.add(new SearchResultViewModel(currentNode.getName(), currentNode.getPath()));
            }
            return result;
        }
        catch (RepositoryException ex){
            logService.log(LogService.LOG_ERROR, "Error during executing search on repository. Emty result list returned as a viewModel", ex);
        }
        return Collections.EMPTY_LIST;
    }
    
}
