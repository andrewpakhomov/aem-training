/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.aem.training.imagerotator;

import com.day.image.Layer;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

/**
 *
 * @author Andrey_Pakhomov
 */
@Component(
         label = "ACS AEM Samples - Basic OSGi Service",
        description = "Sample implementation of an OSGi service"
)
@Service
public class ImageRotatorImpl implements ImageRotator{

    @Override
    public Layer rotateUpDown(Layer layer) {
        layer.rotate(180);
        return layer;
    }
    
}
