/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.filters;

import com.epam.aem.training.core.filters.ReplaceHtmlContentRule;
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
public class TestReplaceHtmlContentRule {
    
    private final static String BEFORE_REPLACEMENT = "Geometrixx";
    private final static String AFTER_REPLACEMENT = "test";
//    
    private final ReplaceHtmlContentRule rule;
    
    private final String textBeforeReplacement;
    private final String expectedTextAfterReplacement;
    
    
    
    
    @Parameterized.Parameters
    public static Collection<Object[]> getTestParams(){
        List<Object[]> testData = new LinkedList<>();
        
        //must replace
        testData.add(new Object[]{
                "<html><title> some text 1"+BEFORE_REPLACEMENT+" some other text 2</title>"
                , "<html><title> some text 1"+AFTER_REPLACEMENT+" some other text 2</title>"});
        
        testData.add(new Object[]{
               "<html><body><p> "+BEFORE_REPLACEMENT+" </p></body>"
                ,"<html><body><p> "+AFTER_REPLACEMENT+" </p></body>"
            });
        
        //Regression
        //Test more than one occurence
        testData.add(new Object[]{
               "<html><body><p>"+BEFORE_REPLACEMENT+"</p><p>"+BEFORE_REPLACEMENT+"</p></body>"
                ,"<html><body><p>"+AFTER_REPLACEMENT+"</p><p>"+AFTER_REPLACEMENT+"</p></body>"
            });
        
        //must not replace
        //tag attributes
        testData.add(new Object[]{
                "<html><script src=\"/"+BEFORE_REPLACEMENT+"\"></src>"
                , "<html><script src=\"/"+BEFORE_REPLACEMENT+"\"></src>"});
        testData.add(new Object[]{
                "<html><body><script type=\"text/js\" src=\""+BEFORE_REPLACEMENT+"/en\"></script></body>"
                , "<html><body><script type=\"text/js\" src=\""+BEFORE_REPLACEMENT+"/en\"></script></body>"});
        //inside JS urls
        testData.add(new Object[]{
                "<html><body><script type=\"text/js\"> "+BEFORE_REPLACEMENT+"/en.html </script></body>"
                , "<html><body><script type=\"text/js\"> "+BEFORE_REPLACEMENT+"/en.html </script></body>"});
        
        //http headers
        testData.add(new Object[]{
             "Location: http://www.example.org/"+BEFORE_REPLACEMENT
             , "Location: http://www.example.org/"+BEFORE_REPLACEMENT
            });
        
        return testData;
    }

   
    
    public TestReplaceHtmlContentRule(String textBeforeReplacement, String expectedTextAfterReplacement) {
        this.textBeforeReplacement = textBeforeReplacement;
        this.expectedTextAfterReplacement = expectedTextAfterReplacement;
        rule = new ReplaceHtmlContentRule(BEFORE_REPLACEMENT, AFTER_REPLACEMENT);
    }
    
    @Test
    public void testReplacement(){
        String actualReplacementResult = rule.doReplace(this.textBeforeReplacement);
        assertEquals(this.expectedTextAfterReplacement, actualReplacementResult);           
    }
    
    
}
