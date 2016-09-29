/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.versioncontrol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import org.osgi.service.log.LogService;

/**
 *
 * @author Andrey_Pakhomov
 */
public class PageNodeRequirementsTester{
    
    private final String rootOfObservedTreePath;
   
    private final LogService logService;

    private final Pattern pageLevelTester;
   
    
    public PageNodeRequirementsTester(LogService logService, String rootOfObservedTreePath) {
        this.logService = logService;
        this.rootOfObservedTreePath = rootOfObservedTreePath;
        this.pageLevelTester = Pattern.compile(this.rootOfObservedTreePath + "/" + "[^/]+", Pattern.CASE_INSENSITIVE);
    }

    public boolean test(Node node) throws RepositoryException {
         boolean isPageMatchPathConditions = checkIfPagePathMatchesProcessConditions(node);
         boolean isPagePropertiesMatchProcessConditions = checkIfPagePropertiesMatchProcessConditions(node);
         boolean testResult = isPageMatchPathConditions && isPagePropertiesMatchProcessConditions;
         logService.log(LogService.LOG_DEBUG, "Page " + node.getPath() + " test results:  matchPathCondition=" + isPageMatchPathConditions
                                    + ", propertiesMatchConditions=" + isPagePropertiesMatchProcessConditions);
         return testResult;
    }
    
    private boolean checkIfPagePathMatchesProcessConditions(Node pageNode) throws RepositoryException {
        Matcher m = this.pageLevelTester.matcher(pageNode.getPath());
        return m.matches();
    }

    private boolean checkIfPagePropertiesMatchProcessConditions(Node pageNode) throws RepositoryException {
        try {
            Property pageDescription = pageNode.getProperty("jcr:content/jcr:description");
            return pageDescription != null;
        } catch (PathNotFoundException ex) {
            logService.log(LogService.LOG_DEBUG, "No jcr:content/jcr:description on Page " + pageNode.getPath());
            return false;
        }
    }
    
    
}
