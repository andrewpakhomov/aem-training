/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.log.LogService;

/**
 *
 * @author Andrey_Pakhomov
 */
@Component
@Service(WorkflowProcess.class)
@Property(name ="procecc.label", value = "Training move page step")
public class MovePageStep implements WorkflowProcess{
    
    Pattern VALID_PATH_PATTERN = Pattern.compile("(/[^/:\\[\\]|\\*]*)*", Pattern.CASE_INSENSITIVE);
    
    @Reference
    private LogService logService;
    

    @Override
    public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {
        ResourceResolver resolver = session.adaptTo(ResourceResolver.class);
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        final String pagePath = item.getWorkflow().getWorkflowData().getPayload().toString();
        Session jcrSession = session.adaptTo(Session.class);
        final String pathToMovePage;
        try {
            pathToMovePage = jcrSession.getNode(pagePath).getProperty("jcr:content/pathtomove").getString();
        } catch (RepositoryException ex) {
             this.logService.log(LogService.LOG_ERROR, "Error during receiving move to path, page "+pagePath+" will not be moved", ex);
             return;
        }
        if (! checkIfPageMoveIsPossible(pathToMovePage, pagePath)) return;
        try {
            Resource page = resolver.getResource(pagePath);
            pageManager.move(page, pathToMovePage, null, false, true, null);
        } catch (SlingException | WCMException ex) {
             this.logService.log(LogService.LOG_ERROR, "Error during moving page", ex);
             return;
        }
        this.logService.log(LogService.LOG_INFO, pagePath + "was successfully moved to " + pathToMovePage);
        
    }
    
    
    private boolean checkIfPageMoveIsPossible(String pathToMovePage, String targetPagePath){
         if (pathToMovePage == null || pathToMovePage.isEmpty()){
            logService.log(LogService.LOG_DEBUG, "Page "+targetPagePath+" can not be moved, because movePath is not specified");
            return false;
        }
        if (pathToMovePage.equals(targetPagePath)){
            logService.log(LogService.LOG_DEBUG, "Page "+targetPagePath+" can not be moved, because movePath is same as current page path");
            return false;
        }
        Matcher m = VALID_PATH_PATTERN.matcher(targetPagePath);
        if (! m.matches()){
            logService.log(LogService.LOG_DEBUG, "Page "+targetPagePath+" can not be moved, because movePath is not valid path");
            return false;
        }
        
        return true;
    }
    
}
