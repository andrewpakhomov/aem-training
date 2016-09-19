/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.filters;

import com.adobe.acs.commons.util.BufferingResponse;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.settings.SlingSettingsService;


/**
 *
 * @author Andrey_Pakhomov
 */
@SlingFilter(generateComponent = true
        , generateService = true
        , metatype = true
        , order = -700
        , scope = SlingFilterScope.REQUEST)
public class ReplaceFilter implements Filter{

    private final static String REPLACE_FROM = "Geometrixx";
    
    private  ReplaceContentRuleFactory replaceContentRuleFactory;
    
    @Reference
    private SlingSettingsService slingSettings;
    
    @Override
    public void init(FilterConfig fc) throws ServletException {
        ReplacementStringByRunMode replacement = new ReplacementStringByRunMode(new LinkedHashSet<>(this.slingSettings.getRunModes()));
        String replaceTo = replacement.getReplacement();
        this.replaceContentRuleFactory = new ReplaceContentRuleFactory(REPLACE_FROM, replaceTo);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String requestUri = httpRequest.getRequestURI();
        AbstractReplaceContentRule replaceContentRule = this.replaceContentRuleFactory.getContentReplaceRuleForUri(requestUri);
            BufferingResponse bufferedResponse = new BufferingResponse((HttpServletResponse) response);
            fc.doFilter(request, bufferedResponse);
            String pageRenderingResult = bufferedResponse.getContents();
            if (pageRenderingResult != null){
                //Surpise! Some AEM servlets use direct output to ServletOutputStream interface instead of PrintWriter interface.
                //In this case NPE occurs.
                //Of course we can override this behaviour with our own class. ACS Commons BufferingResponse class
                //doesn't know how to buffer binary output :\
                String filteredResult = replaceContentRule.doReplace(pageRenderingResult);
                response.getWriter().append(filteredResult);
            }
    }

    @Override
    public void destroy() {
        
    }
    
}
