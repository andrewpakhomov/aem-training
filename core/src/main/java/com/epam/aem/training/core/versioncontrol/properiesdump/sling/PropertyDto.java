/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.versioncontrol.properiesdump.sling;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.event.jobs.Job;

/**
 *
 * @author Andrey_Pakhomov
 */
public class PropertyDto implements Serializable{
    
    private final HashMap<String, Object> data;

    private PropertyDto() {
        this.data = new HashMap<>();
    }
    
    public static PropertyDto construct(Job job) throws PropertyDtoEception{
        PropertyDto instance = new PropertyDto();
         for (PropertyDtoField field : PropertyDtoField.values()) {
            String fieldKey = field.toString();
            Object value = job.getProperty(fieldKey);
            if (value == null) {
                throw new PropertyDtoEception("Bad input hashmap format, no " + fieldKey + " field");
            }
            instance.data.put(fieldKey, value);
        }
        return instance;
    }
    
    public static PropertyDto construct(String propertyNames[], String propertyPath){
        PropertyDto instance = new PropertyDto();
        instance.data.put(PropertyDtoField.PROPERTY_NAME.toString(), propertyNames);
        instance.data.put(PropertyDtoField.PROPERTY_PATH.toString(), propertyPath);
        return instance;
    }
    
    public String[] getPropertyNames(){
        return (String[])this.data.get(PropertyDtoField.PROPERTY_NAME.toString());
    }
    
    public String getPropertyPath(){
        return (String) this.data.get(PropertyDtoField.PROPERTY_PATH.toString());
    }
    
    public Map<String, Object> toProperties(){
        return new HashMap<>(this.data);
    }
    
}
