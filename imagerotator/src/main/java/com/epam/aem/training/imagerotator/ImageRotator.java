/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.imagerotator;

import com.day.image.Layer;

/**
 *
 * @author Andrey_Pakhomov
 */
public interface ImageRotator {
    
    public Layer rotateUpDown(Layer layer);
    
}
