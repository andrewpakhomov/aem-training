/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.versioncontrol.properiesdump.sling;

import com.day.cq.commons.jcr.JcrUtil;
import com.epam.aem.training.core.versioncontrol.properiesdump.sling.Constants;
import java.util.UUID;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.log.LogService;

/**
 *
 * @author Andrey_Pakhomov
 */
@Component(immediate = true)
@Service
@Property(name = JobConsumer.PROPERTY_TOPICS, value = Constants.DO_SAVE_PROPERTY_DUMP_JOB_TOPIC)
public class PropertyDumper implements JobConsumer {

    @Reference
    private LogService logService;

    @Reference
    private SlingRepository repository;

    private final static String DUMP_NODE_TYPE = "nt:unstructured";
    private final static String PROPERTY_PATH_PROPERTY_NAME = "path";
    private final static String PROPERTY_NAME_PROPERTY_NAME = "name";

    private Session session;

    private String userId = "system-user";

    private String password = "system-user";

    private String dumpPropertiesRoot = "/var/log/removedProperties/";

    @Override
    public JobResult process(Job job) {
        PropertyDto dto;
        try {
            dto = PropertyDto.construct(job);
        } catch (PropertyDtoEception ex) {
            logService.log(LogService.LOG_ERROR, "Error during processing job +" + job.getTopic(), ex);
            //return Cancel, because job params are broken and we can't process this job at all
            return JobResult.CANCEL;
        }
        try {
            this.openRepoSession();
            for (String currentPropertyInBatch : dto.getPropertyNames()) {
                String newNodePath = this.doDumpPropertyToLog(currentPropertyInBatch, dto.getPropertyPath());
                this.logService.log(LogService.LOG_DEBUG, "Trying to create dump node at " + newNodePath);
            }
            this.closeRepoSession();
        } catch (RepositoryException ex) {
            this.logService.log(LogService.LOG_ERROR, "Error during processing job. Job will be retried soon! ;)", ex);
            return JobResult.FAILED;
        }
        this.logService.log(LogService.LOG_DEBUG, "All dump nodes successfullu created, job finished");
        return JobResult.OK;
    }

    private String doDumpPropertyToLog(String propertyName, String propertyPath) throws RepositoryException {
        String newNodeName = UUID.randomUUID().toString();
        String newNodePath = this.dumpPropertiesRoot + newNodeName;
        Node node = JcrUtil.createPath(newNodePath, DUMP_NODE_TYPE, session);
        node.setProperty(PROPERTY_NAME_PROPERTY_NAME, propertyName);
        node.setProperty(PROPERTY_PATH_PROPERTY_NAME, propertyPath);
        return newNodePath;
    }

    private void openRepoSession() throws RepositoryException {
        this.session = this.repository.login(new SimpleCredentials(this.userId, this.password.toCharArray()));
    }

    private void closeRepoSession() throws RepositoryException {
        this.session.save();
        this.session.logout();
    }

}
