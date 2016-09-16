/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.filters;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Andrey_Pakhomov
 */
class ReplacementStringByRunMode {

    private final String DEFAULT_RUN_MODE_KEY  = "DEFAULT";
    
    private final Set<String> aemRunMode;
    private final HashMap<String, String> replacementDicitonary;
    
    public ReplacementStringByRunMode(Set<String> aemRunMode) {
        this.aemRunMode = aemRunMode;
        this.replacementDicitonary = new HashMap<>();
        this.replacementDicitonary.put("algebra", "Algebraixx");
        this.replacementDicitonary.put("trigo", "Trigonometrixx");
        this.replacementDicitonary.put(DEFAULT_RUN_MODE_KEY, "Geometrio, LLC");
    }
    
    public String getReplacement(){
        String replacementMode = null;
        for (String runMode : this.aemRunMode){
            System.out.println("RUN_MODE:"+runMode);
            if (this.replacementDicitonary.containsKey(runMode)){
                replacementMode = runMode;
                break;
            }
        }
        if (replacementMode == null) replacementMode = DEFAULT_RUN_MODE_KEY;
        return this.replacementDicitonary.get(replacementMode);
    }
    
}
