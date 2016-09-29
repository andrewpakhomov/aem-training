/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.versioncontrol;

import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.observation.ObservationManager;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentException;
import org.osgi.service.log.LogService;

/**
 *
 * @author Andrey_Pakhomov
 */
@Component(immediate = true, metatype = true, label = "PageRevisionControlService", description = "Component that listen for page updates under specific directory and ...")
public class PageVersionControlService{
    
    
    private final static String NODE_ABSOLUTE_PATH_PROPERTIES_KEY  = "pageversioncontrol.absolutepath";

    private Session observationSession;
    private ObservationManager observationManager;
    private Workspace workspace;
    
    @Reference
    private LogService logService;
    
    @Property(name = NODE_ABSOLUTE_PATH_PROPERTIES_KEY, value = "/content/training", label = "Listened subtree path", description = "Absolute subtree path, listened for changes")
    private String observedNodesAbsolutePath;
    
    
    
    @Reference
    private SlingRepository repository;
    
    private PageRevisionMaker pageEventListener;
    
    
    @Activate
    public void activate(final Map<String, String> config){
        try{
           
                this.observationSession = repository.loginAdministrative(null);
                this.workspace = this.observationSession.getWorkspace();
                this.observationManager  = this.workspace.getObservationManager();
                this.changeEventListener(config);
        }catch(RepositoryException ex){
                 throw new ComponentException("JCR Session didn't oped", ex);
        }   
    }
    
     @Modified
     public void changeEventListener(final Map<String, String> config){
         try{
            if (this.pageEventListener != null) observationManager.removeEventListener(this.pageEventListener);
            this.observedNodesAbsolutePath=config.getOrDefault(NODE_ABSOLUTE_PATH_PROPERTIES_KEY, "/content/training");
            if (observedNodesAbsolutePath.endsWith("/")){
                this.observedNodesAbsolutePath=this.observedNodesAbsolutePath.substring(0, this.observedNodesAbsolutePath.length()-1);
            }
            this.pageEventListener = new PageRevisionMaker(this.observationSession, this.workspace.getVersionManager(), this.observedNodesAbsolutePath, logService);
            
            observationManager.addEventListener(this.pageEventListener, PageRevisionMaker.EVENT_TYPE_FILTER, this.observedNodesAbsolutePath, true,
                    null, null, true);
         }catch(RepositoryException ex){
             throw new ComponentException("Can't register jcr event listener", ex);
         }
     }
    

    @Deactivate
    public void deactivate(final Map<String, String> config) throws RepositoryException {
        try {
            // Get JCR ObservationManager from Workspace
          

            if (this.observationManager != null) {
                // Un-register event handler
                observationManager.removeEventListener(this.pageEventListener);
            }
        } finally {
            // Good housekeeping; Close your JCR Session when you are done w them!
            if (observationSession != null) {
                observationSession.logout();
            }
        }
}
    
}
