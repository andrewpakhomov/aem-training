/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.versioncontrol.properiesdump.sling;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

/**
 *
 * @author Andrey_Pakhomov
 */
@Component(
    immediate = true
)
@Service
@Properties({
        @Property(name = EventConstants.EVENT_TOPIC, value = {"org/apache/sling/api/resource/Resource/CHANGED"}, propertyPrivate = true),
        @Property(name= EventConstants.EVENT_FILTER, value={"(&(resourceRemovedAttributes=*)(path=/content/training/*))"})
})
public class SlingEventHandler implements EventHandler{

    private final static String PATH_PROPERTY_KEY = "path";
    private final static String PROPERTY_NAME_KEY = "resourceRemovedAttributes";
    
    @Reference
    private JobManager jobManager;
    
    @Reference
    private LogService logService;

    public SlingEventHandler() {
    }
    
    
    
    
    @Override
    public void handleEvent(Event event) {
        this.logService.log(LogService.LOG_DEBUG,"Event received:" + event.toString());
        String resourcePath = (String)event.getProperty(PATH_PROPERTY_KEY);
        String[] removedProperties = (String[]) event.getProperty(PROPERTY_NAME_KEY);
        PropertyDto propertyDto = PropertyDto.construct(removedProperties, resourcePath);
        this.jobManager.addJob(Constants.DO_SAVE_PROPERTY_DUMP_JOB_TOPIC, propertyDto.toProperties());
    }
    
}
