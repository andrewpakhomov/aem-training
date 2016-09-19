/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.filters;

import com.epam.aem.training.core.filters.ReplaceJsonContentRule;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Andrey_Pakhomov
 */
@RunWith(Parameterized.class)
public class TestReplaceJsonContentRule {
 
    private final static String BEFORE_REPLACEMENT = "Geometrixx";
    private final static String AFTER_REPLACEMENT = "test";
//    
    private final ReplaceJsonContentRule rule;
    
    private final String textBeforeReplacement;
    private final String expectedTextAfterReplacement;
    
    
     @Parameterized.Parameters
    public static Collection<Object[]> getTestParams(){
        List<Object[]> testData = new LinkedList<>();
     
         //must replace
        testData.add(new Object[]{
                "{key:\" some text 1 "+BEFORE_REPLACEMENT+" some other text 2\"}"
                ,"{key:\" some text 1 "+AFTER_REPLACEMENT+" some other text 2\"}"});
        
        //Checking question '?' sign in regulaer non url expressions
        testData.add(new Object[]{
                "{key:\" some text 1 "+BEFORE_REPLACEMENT+" some other text 2? No!\"}"
                ,"{key:\" some text 1 "+AFTER_REPLACEMENT+" some other text 2? No!\"}"});
         
        //must not replace urls
        testData.add(new Object[]{
                "{url: value: \""+"/path1/"+BEFORE_REPLACEMENT+"\"}"
                ,"{url: value: \""+"/path1/"+BEFORE_REPLACEMENT+"\"}"});
        testData.add(new Object[]{
                "{url: value: \""+"/path1/test"+BEFORE_REPLACEMENT+"\"}"
                ,"{url: value: \""+"/path1/test"+BEFORE_REPLACEMENT+"\"}"});
        testData.add(new Object[]{
                "{url: value: \""+"/path1/"+BEFORE_REPLACEMENT+"/\"}"
                ,"{url: value: \""+"/path1/"+BEFORE_REPLACEMENT+"/\"}"});
        testData.add(new Object[]{
                "{url: value: \""+"/path1/"+BEFORE_REPLACEMENT+"/a.html?b=c\"}"
                ,"{url: value: \""+"/path1/"+BEFORE_REPLACEMENT+"/a.html?b=c\"}"});
        testData.add(new Object[]{
                "{url: value: \""+"/path1/"+BEFORE_REPLACEMENT+"?b=c\"}"
                ,"{url: value: \""+"/path1/"+BEFORE_REPLACEMENT+"?b=c\"}"});
        
        return testData;
    }
    

    public TestReplaceJsonContentRule(String textBeforeReplacement, String expectedTextAfterReplacement) {
        this.textBeforeReplacement = textBeforeReplacement;
        this.expectedTextAfterReplacement = expectedTextAfterReplacement;
        rule = new ReplaceJsonContentRule(BEFORE_REPLACEMENT, AFTER_REPLACEMENT);
    }
     
    @Test
    public void testReplacement(){
        String actualReplacementResult = rule.doReplace(this.textBeforeReplacement);
        assertEquals(this.expectedTextAfterReplacement, actualReplacementResult);           
    }
    
}
