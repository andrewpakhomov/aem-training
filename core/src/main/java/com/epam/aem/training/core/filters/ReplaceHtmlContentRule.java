/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**Replace some "replaceFrom" text to "replaceTo" text inside of HTML page <i>content</i>
 * 
 * ThreadSafe class, allow concurrent calls of doReplace method after construction has been completed.
 * @author Andrey_Pakhomov
 */
class ReplaceHtmlContentRule extends AbstractReplaceContentRule {

    private final Pattern pattern;
    

    ReplaceHtmlContentRule(String replaceFrom, String replaceTo) {
        super(replaceFrom, replaceTo);
        this.pattern = Pattern.compile("<(?!(script))[^>]*>[^>]*(" + REPLACE_GROUP_NAME_DECLARATION + this.replaceFrom + ")[^>]*</", Pattern.CASE_INSENSITIVE);
    }

    @Override
    protected String doReplace(String source) {
       return this.replaceWithRegExpPattern(pattern, source, REPLACE_GROUP_NAME);
    }
 }
    

