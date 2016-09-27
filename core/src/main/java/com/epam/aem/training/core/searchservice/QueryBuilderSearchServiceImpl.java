/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.searchservice;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.log.LogService;

/**
 *
 * @author Andrey_Pakhomov
 */
final class QueryBuilderSearchServiceImpl extends AbstractSearchService {

    public QueryBuilderSearchServiceImpl(List<String> searchLocation, String searchString, ResourceResolver resolver, LogService logService) {
        super(searchLocation, searchString, resolver, logService);
    }

    @Override
    protected List<Node> doSearch() throws RepositoryException {
        final QueryBuilder queryBuilder = this.resourceResolver.adaptTo(QueryBuilder.class);
        final Session session = this.resourceResolver.adaptTo(Session.class);
        final String PATH_KEY = "path";
        final String FULLTEXT_KEY = "fulltext";
        final String TYPE_KEY = "type";
        final List<Node> nodes = new LinkedList<>();
        for (String searchLocation : this.searchLocations) {
            final Map<String, String> searchParams = new HashMap<>();
            searchParams.put(PATH_KEY, searchLocation);
            searchParams.put(TYPE_KEY, "[nt:base]");
            searchParams.put(FULLTEXT_KEY, this.searchString);
            this.logService.log(LogService.LOG_INFO, "Executing query via query builder: " + searchParams.toString());
            final Query query = queryBuilder.createQuery(PredicateGroup.create(searchParams), session);
            final SearchResult searchResult = query.getResult();
            List<Hit> hits = searchResult.getHits();
            for (Hit currentHit : hits){
                nodes.add(currentHit.getNode());
            }
        }
        return nodes;
    }
    
}
