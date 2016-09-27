/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.filters;

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
public class TestReplaceContentRuleFactory {
    
    private final static ReplaceContentRuleFactory factory = new ReplaceContentRuleFactory("a", "b");
    
    private final String URI;
    private final Class expectedRuleClass;

    @Parameterized.Parameters
    public static Collection<Object[]> getTestData(){
        List<Object[]> testData = new LinkedList<>();
        testData.add(new Object[]{"/geometixx/content.json", ReplaceJsonContentRule.class});
        testData.add(new Object[]{"/geometixx/content.html", ReplaceHtmlContentRule.class});
        testData.add(new Object[]{"/geometixx/style.css", NullReplaceContentRule.class});
        
        return testData;
    }
    
    
    public TestReplaceContentRuleFactory(String URI, Class expectedRuleClass) {
        this.URI = URI;
        this.expectedRuleClass = expectedRuleClass;
    }
    
    @Test
    public void testReturnedRuleClass(){
        AbstractReplaceContentRule rule = factory.getContentReplaceRuleForUri(this.URI);
        assertEquals(this.expectedRuleClass, rule.getClass());
        
    }
    
}
