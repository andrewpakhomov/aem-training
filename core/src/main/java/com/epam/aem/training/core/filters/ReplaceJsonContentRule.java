/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.filters;

import java.util.regex.Pattern;

/**
 *
 * @author Andrey_Pakhomov
 */
class ReplaceJsonContentRule extends AbstractReplaceContentRule{

    private final Pattern pattern;
    
    public ReplaceJsonContentRule(String replaceFrom, String replaceTo) {
        super(replaceFrom, replaceTo);
        //dont change inside resource urls
        this.pattern = Pattern.compile(".*("+REPLACE_GROUP_NAME_DECLARATION+this.replaceFrom+").*", Pattern.CASE_INSENSITIVE);
    }

    
    @Override
    protected String doReplace(String source) {
        return this.replaceWithRegExpPattern(pattern, source, REPLACE_GROUP_NAME);
    }
    
}
