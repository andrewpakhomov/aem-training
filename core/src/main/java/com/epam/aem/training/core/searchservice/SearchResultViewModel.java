/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.core.searchservice;

/**
 *
 * @author Andrey_Pakhomov
 */
public class SearchResultViewModel {
    
    private final String name;
    
    private final String path;

    public SearchResultViewModel(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
    
    

}
