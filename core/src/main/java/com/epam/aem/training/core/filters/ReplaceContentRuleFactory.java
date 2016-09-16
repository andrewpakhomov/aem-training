/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Andrey_Pakhomov
 */
class ReplaceContentRuleFactory {
    
    private final AbstractReplaceContentRule jsonRule;
    private final AbstractReplaceContentRule htmlRule;
    private final NullReplaceContentRule nullReplaceFiler;
    
    private final Pattern jsonRulePattern;
    private final Pattern htmlRulePattern;
    
    ReplaceContentRuleFactory(String replaceFrom, String replaceTo) {
        this.jsonRule = new ReplaceJsonContentRule(replaceFrom, replaceTo);
        this.htmlRule = new ReplaceHtmlContentRule(replaceFrom, replaceTo);
        this.nullReplaceFiler = new NullReplaceContentRule(replaceFrom, replaceTo);
        this.htmlRulePattern = Pattern.compile(".*(?=(\\.xhtml)|(\\.jsp)|(\\.html)).*", Pattern.CASE_INSENSITIVE);
        this.jsonRulePattern = Pattern.compile(".*(?=(\\.json)).*", Pattern.CASE_INSENSITIVE);
    }

    AbstractReplaceContentRule getContentReplaceRuleForUri(String URI){
        Matcher htmlRuleMatcher = this.htmlRulePattern.matcher(URI);
        if (htmlRuleMatcher.matches()) return this.htmlRule;
        
        Matcher jsonRuleMatcher = this.jsonRulePattern.matcher(URI);
        if (jsonRuleMatcher.matches()) return this.jsonRule;
        
        return this.nullReplaceFiler;
    }
    
}
