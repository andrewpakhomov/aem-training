/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.filters;

/** JUST A NULL Object pattern for ReplaceRuleFactory
 *
 * @author Andrey_Pakhomov
 */
class NullReplaceContentRule extends AbstractReplaceContentRule{

    public NullReplaceContentRule(String replaceFrom, String replaceTo) {
        super(replaceFrom, replaceTo);
    }

    @Override
    protected String doReplace(String source) {
        return source;
    }
    
}
