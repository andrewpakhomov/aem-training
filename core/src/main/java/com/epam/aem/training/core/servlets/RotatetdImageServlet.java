/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.servlets;

import com.adobe.acs.commons.dam.RenditionPatternPicker;
import com.day.cq.commons.PathInfo;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.foundation.Image;
import com.day.image.Layer;
import com.epam.aem.training.imagerotator.ImageRotator;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Based on
 * <a href="https://github.com/Adobe-Consulting-Services/acs-aem-commons/blob/master/bundle/src/main/java/com/adobe/acs/commons/images/impl/NamedTransformImageServlet.java">
 * NamedTransformImageServlet example</a>
 *
 * @author Andrey_Pakhomov
 */
@SlingServlet(
        extensions = {"jpg", "png"},
        selectors = {"ud"},
        methods = {"GET"}, 
        resourceTypes = {"sling/servlet/default"}
)
public class RotatetdImageServlet extends SlingSafeMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(RotatetdImageServlet.class);
    
    private static final String DEFAULT_ASSET_RENDITION_PICKER_REGEX = "cq5dam\\.web\\.(.*)";
    
     public static final String NAME_IMAGE = "image";

       public static final String RT_LOCAL_SOCIAL_IMAGE = "social:asiFile";

public static final String RT_REMOTE_SOCIAL_IMAGE = "nt:adobesocialtype";
     
    private static RenditionPatternPicker renditionPatternPicker
            = new RenditionPatternPicker(Pattern.compile(DEFAULT_ASSET_RENDITION_PICKER_REGEX));

    @Reference
    private ImageRotator imageRotator;

    @Override
    protected void doGet(SlingHttpServletRequest req,
            SlingHttpServletResponse resp)
            throws ServletException, IOException {

        Image image = this.resolveImage(req);
        if (image == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!image.hasContent()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // get pure layer
        Layer layer = this.getLayer(image);
        if (layer == null){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        //rotating layer
        this.imageRotator.rotateUpDown(layer);
        try {
            final String mimeType = image.getMimeType();
            layer.write(mimeType, 1, resp.getOutputStream());
        } catch (RepositoryException ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        resp.flushBuffer();
    }

    /**
     * Intelligently determines how to find the Image based on the associated
     * SlingRequest.
     *
     * Some copy paste from
     * <a href="https://github.com/Adobe-Consulting-Services/acs-aem-commons/blob/be1264f733a52d08346c87818881f5d30c8254a9/bundle/src/main/java/com/adobe/acs/commons/images/impl/NamedTransformImageServlet.java">Here</a>
     *
     * @param request the SlingRequest Obj
     * @return the Image object configured w the info of where the image to
     * render is stored in CRX
     */
    protected final Image resolveImage(final SlingHttpServletRequest request) {
        //removing selectors to match correct resource
//        final String requestedResourcePath = request.getPathInfo();
        
        final ResourceResolver resourceResolver = request.getResourceResolver();
        PathInfo pathInfo = new PathInfo(resourceResolver,request.getRequestURI());
        final String actualResourcePath = request.getRequestURI().replace(pathInfo.getSelectorString(),"");
        final Resource resource = resourceResolver.getResource(actualResourcePath);
        
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        final Page page = pageManager.getContainingPage(resource);

        if (DamUtil.isAsset(resource)) {
            // For assets, pick the configured rendition if it exists
            // If rendition does not exist, use original

            final Asset asset = DamUtil.resolveToAsset(resource);
            Rendition rendition = asset.getRendition(renditionPatternPicker);

            if (rendition == null) {
                log.warn("Could not find rendition [ {} ] for [ {} ]", renditionPatternPicker.toString(),
                        resource.getPath());
                rendition = asset.getOriginal();
            }

            final Resource renditionResource = request.getResourceResolver().getResource(rendition.getPath());

            final Image image = new Image(resource);
            image.set(Image.PN_REFERENCE, renditionResource.getPath());
            return image;

        } else if (DamUtil.isRendition(resource)
                || resourceResolver.isResourceType(resource, JcrConstants.NT_FILE)
                || resourceResolver.isResourceType(resource, JcrConstants.NT_RESOURCE)) {
            // For renditions; use the requested rendition
            final Image image = new Image(resource);
            image.set(Image.PN_REFERENCE, resource.getPath());
            return image;

        } else if (page != null) {
            if (resourceResolver.isResourceType(resource, NameConstants.NT_PAGE)
                    || StringUtils.equals(resource.getPath(), page.getContentResource().getPath())) {
                // Is a Page or Page's Content Resource; use the Page's image resource
                return new Image(page.getContentResource(), NAME_IMAGE);
            } else {
                return new Image(resource);
            }
        } 
        return resource == null ? null : new Image(resource);
    }

    /**
     * Gets the Image layer.
     *
     * @param image The Image to get the layer from
     * @return the image's Layer
     * @throws IOException
     */
    private Layer getLayer(final Image image) throws IOException {
        Layer layer = null;

        try {
            layer = image.getLayer(false, false, false);
        } catch (RepositoryException ex) {
            return null;
        }
        return layer;
    }

}
