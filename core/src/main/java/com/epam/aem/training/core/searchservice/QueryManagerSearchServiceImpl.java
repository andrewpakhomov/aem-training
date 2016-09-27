/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.searchservice;

import java.util.LinkedList;
import java.util.List;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.log.LogService;

/**
 *
 * @author Andrey_Pakhomov
 */
final class QueryManagerSearchServiceImpl extends AbstractSearchService {
    private final String QUERY_STRING_TEMPLATE = "SELECT * from [nt:base] as allnodes where ISDESCENDANTNODE(allnodes, \"1%s\") and CONTAINS ( allnodes.*, \"2%s\")";

    public QueryManagerSearchServiceImpl(List<String> searchLocation, String searchString, ResourceResolver resolver, LogService logService) {
        super(searchLocation, searchString, resolver, logService);
    }

    @Override
    protected List<Node> doSearch() throws RepositoryException {
        Session session = this.resourceResolver.adaptTo(Session.class);
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        final List<Node> nodes = new LinkedList<>();
        for (String searchLocation : this.searchLocations) {
            String queryString = String.format(QUERY_STRING_TEMPLATE, searchLocation, this.searchString);
//            this.logService.log(LogService.LOG_DEBUG, "Executing query via QueryManager: "+queryString);
            Query query = queryManager.createQuery(queryString, "JCR-SQL2");
            QueryResult queryResult = query.execute();
            NodeIterator it = queryResult.getNodes();
            it.forEachRemaining((Object obj) -> {
                nodes.add((Node) obj);
            });
        }
        return nodes;
    }
    
}
