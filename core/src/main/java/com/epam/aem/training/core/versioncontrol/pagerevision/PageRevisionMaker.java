/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.versioncontrol.pagerevision;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionManager;
import org.osgi.service.log.LogService;

/**
 *
 * @author Andrey_Pakhomov
 */
public class PageRevisionMaker implements EventListener {

    private final static String VERSIONABLE_NODE_TYPE_MIXIN = "mix:versionable";

    private final String DATE_TIME_FORMAT_STRING = "Y-M-d h-m-s";

    private final static String PAGE_NODE_TYPE = "cq:Page";

    public final static int EVENT_TYPE_FILTER = Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED | Event.NODE_ADDED | Event.NODE_REMOVED;

    private final VersionManager versionManager;
    private final Session session;
    private final String rootOfObservedTreePath;

    private final LogService logService;

    private final PageNodeRequirementsTester pageNodeTester;

    /**
     *
     * @param session
     * @param versionManager
     * @param observedDirectory should never ends with trailing slash
     */
    public PageRevisionMaker(Session session, VersionManager versionManager, String observedDirectory, LogService logService) {
        this.versionManager = versionManager;
        this.session = session;
        this.rootOfObservedTreePath = observedDirectory;
        this.logService = logService;
        this.pageNodeTester = new PageNodeRequirementsTester(logService, rootOfObservedTreePath);
    }

    @Override
    public void onEvent(EventIterator events) {
        final Set<String> affectedPagesPaths = new HashSet<>();
        events.forEachRemaining(new Consumer<Event>() {

            @Override
            public void accept(Event t) {
                try {
                    boolean isEventSourceRemoved = t.getType() == Event.NODE_REMOVED || t.getType() == Event.PROPERTY_REMOVED;
                    Node affectedPage = getAffectedPageNode(t.getPath(), isEventSourceRemoved);
                    if (affectedPage != null) {

                        String pagePath = affectedPage.getPath();
                        if (affectedPagesPaths.contains(pagePath)) return;
                        
                        if (pageNodeTester.test(affectedPage)) {
                            affectedPagesPaths.add(pagePath);
                        } else {
                            logService.log(LogService.LOG_DEBUG, "Page " + pagePath + " rejected");
                        }
                    } else {
                        logService.log(LogService.LOG_DEBUG, "Node with path " + t.getPath() + " was rejected, because it doesn't rely to any page");
                    }
                } catch (RepositoryException ex) {
                    logService.log(LogService.LOG_ERROR, "Error during processing event " + t.toString(), ex);
                }
            }
        }
        );

        for (String affectedPagePath : affectedPagesPaths) {
            final String currentPageJcrContentNodePath = affectedPagePath + "/jcr:content";
            try {
                Node node = this.session.getNode(currentPageJcrContentNodePath);
                if (! this.isNodeVersionable(node)){
                    this.makeNodeVersionable(node);
                }
                this.createPageVersion(node);
                logService.log(LogService.LOG_DEBUG, "Revision created for page " + affectedPagePath);
            } catch (RepositoryException ex) {
                logService.log(LogService.LOG_ERROR, "Error during creating of revision for page " + affectedPagePath, ex);
            }
        }

    }

    /**
     * Looking up the cq:Page ancestor of node
     *
     * @param chnagedNodeAbsPath node absolute path
     * @return cq:Page ancestor of node, or null if it was not found
     * @throws RepositoryException
     */
    private Node getAffectedPageNode(String chnagedNodeAbsPath, boolean isPathRemoved) throws RepositoryException {
        final Node NO_AFFECTED_PAGE_RETURN_VALUE = null;
        if (isPathRemoved){
            chnagedNodeAbsPath = chnagedNodeAbsPath.substring(0, chnagedNodeAbsPath.lastIndexOf("/"));
        }
        Item item = this.session.getItem(chnagedNodeAbsPath);
        Item currentAncestor = item;
        do {
            if (currentAncestor.isNode()) {
                Node node = (Node) currentAncestor;
                if (node.isNodeType(PAGE_NODE_TYPE)) {
                    return node;
                }
            }
            currentAncestor = currentAncestor.getParent();
        } while (!currentAncestor.getPath().equals(this.rootOfObservedTreePath));
        return NO_AFFECTED_PAGE_RETURN_VALUE;
    }

    private boolean isNodeVersionable(Node node) throws RepositoryException {
        for (NodeType current : node.getMixinNodeTypes()) {
            if (current.isNodeType(VERSIONABLE_NODE_TYPE_MIXIN)) {
                return true;
            }
        }
        return false;
    }

    private void createPageVersion(Node pageJcrContentNode) throws RepositoryException {
        String nodePath = pageJcrContentNode.getPath();
        Version newVersion = versionManager.checkpoint(nodePath);
        VersionHistory versionHistory = versionManager.getVersionHistory(nodePath);
        String label = "Auto generated label at " + new SimpleDateFormat(DATE_TIME_FORMAT_STRING).format(new Date());
        versionHistory.addVersionLabel(newVersion.getName(), label, false);
    }

    private void makeNodeVersionable(Node pageJcrContentNode) throws RepositoryException {
        logService.log(LogService.LOG_WARNING, "Node  " + pageJcrContentNode.getPath() + " is not versionable, so the " + VERSIONABLE_NODE_TYPE_MIXIN + " mixin added to node");
        pageJcrContentNode.addMixin(VERSIONABLE_NODE_TYPE_MIXIN);
        session.save();
    }

}
