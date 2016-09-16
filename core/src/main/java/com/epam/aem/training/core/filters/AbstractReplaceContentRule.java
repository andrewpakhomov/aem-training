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
abstract class AbstractReplaceContentRule {
    
    protected final static String REPLACE_GROUP_NAME = "REPLACEGROUP";
    protected final static String REPLACE_GROUP_NAME_DECLARATION = "?<"+REPLACE_GROUP_NAME+">";
    
    protected final String replaceFrom;
    protected final String replaceTo;

    AbstractReplaceContentRule(String replaceFrom, String replaceTo) {
        this.replaceFrom = replaceFrom;
        this.replaceTo = replaceTo;
    }

    protected abstract String doReplace(String source);
    
    
    protected String replaceWithRegExpPattern(Pattern pattern, String source, String replaceGroupName){
        StringBuilder result = new StringBuilder(source.length());
        final Matcher m = pattern.matcher(source);
        int previousEndPosition = 0;
        for (; m.find();) {
            final int startPosition = m.start(replaceGroupName);
            final int endPosition = m.end(replaceGroupName);
            result.append(source.substring(previousEndPosition, startPosition));
            result.append(this.replaceTo);
            previousEndPosition = endPosition;
        }
        result.append(source.substring(previousEndPosition, source.length()));
        return result.toString();
    }
      
}
