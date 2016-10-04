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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.log.LogService;

/**
 *
 * @author Andrey_Pakhomov
 */
@Component
@Service(WorkflowProcess.class)
@Property(name ="procecc.label", value = "Training move page step")
public class MovePageStep implements WorkflowProcess{
    
    @Reference
    private LogService logService;
    

    @Override
    public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {
        Session jcrSession = session.adaptTo(Session.class);
        try {
            Node targetNode = jcrSession.getNode(item.getWorkflow().getWorkflowData().getPayload().toString());
        } catch (RepositoryException ex) {
           this.logService.log(LogService.LOG_ERROR, "Error during moving page", ex);
        }
        
        
    }
    
}
