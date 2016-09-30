/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.versioncontrol.properiesdump.jcr;

import com.epam.aem.training.core.versioncontrol.properiesdump.sling.Constants;
import java.util.function.Consumer;
import javax.jcr.Item;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.log.LogService;

/**
 *
 * @author Andrey_Pakhomov
 */
public class PropertyRemovalListener implements EventListener{
    
    public final static int EVENT_TYPE_FILTER = Event.PROPERTY_REMOVED;
    
    private final JobManager jobManager;
    private final Session session;
    private final LogService logService;

    public PropertyRemovalListener(JobManager jobManager, Session session, LogService logService) {
        this.jobManager = jobManager;
        this.session = session;
        this.logService = logService;
    }

    @Override
    public void onEvent(EventIterator events) {
        events.forEachRemaining( new Consumer<Event>() {

            @Override
            public void accept(Event t) {
                try {
                    Item item = session.getItem(t.getPath());
                    //Processing just properies
                    if (item.isNode()) return;
                    
                    Property property = (Property) item;
                    final String propertyPath = item.getPath();
                    
                    PropertyDto propertyDto;
                    if (property.isMultiple()){
                        propertyDto = PropertyDto.transorm(session.getValueFactory(), propertyPath, property.getValues());
                    }else{
                        propertyDto = PropertyDto.transorm(session.getValueFactory(), propertyPath, property.getValue());
                    }
                    jobManager.addJob(Constants.DO_SAVE_PROPERTY_DUMP_JOB_TOPIC, propertyDto.serializeToJobProperies());
                } catch (RepositoryException | DtoTransformException ex) {
                    logService.log(LogService.LOG_ERROR, "Can't process repository event ", ex);
                }
            }
        });
    }
    
    
    
    
}
