/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.versioncontrol.properiesdump.jcr;

/** Here we have an implicit contract:
 *  Field key in transport map should be equal to 
 *  specific enum value .toString() return value
 *
 * @author Andrey_Pakhomov
 */
public enum PropertyDtoFields {
    
    PROPERTY_PATH,
            
    PROPERTY_VALUE,
            
    IS_PROPERTY_MULTIPLE,
            
    PROPERTY_TYPE;
    
}
