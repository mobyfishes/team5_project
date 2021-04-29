/*
 * Created by Zihao Cai on 2021.3.21
 * Copyright © 2021 Osman Balci. All rights reserved.
 */
package edu.vt.controllers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named(value = "sliderController")
@RequestScoped

public class SliderController {

    // Each String object in the List contains the image filename, e.g., photo1.png
    private List<String> sliderImages;

    /*
    The PostConstruct annotation is used on a method that needs to be executed after
    dependency injection is done to perform any initialization. The initialization 
    method init() is the first method invoked before this class is put into service. 
    */
    @PostConstruct
    public void init() {

        sliderImages = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            sliderImages.add("photo" + i + ".png");
        }
    }

    /*
    =============
    Getter Method
    =============
     */
    public List<String> getSliderImages() {
        return sliderImages;
    }
    
    /*
    ===============
    Instance Method
    ===============
     */
    public String description(String image) {

        String imageDescription = "";

        switch (image) {
            case "photo1.png":
                imageDescription = "Multimedia content typically includes videos";
                break;
            case "photo2.png":
                imageDescription = "Videos are used all over the planet of earth";
                break;
            case "photo3.png":
                imageDescription = "A spiraling view of hundreds of videos";
                break;
            case "photo4.png":
                imageDescription = "Old fashioned film containing images";
                break;
            case "photo5.png":
                imageDescription = "Videos on hundreds of TV screens";
                break;
            case "photo6.png":
                imageDescription = "Users can watch and upload their own videos on YouTube";
                break;
            case "photo7.png":
                imageDescription = "A variety of videos on many TV screens";
                break;
            case "photo8.png":
                imageDescription = "High definition TVs show videos with high quality";
                break;
            case "photo9.png":
                imageDescription = "Videos improve the user experience";
                break;
            case "photo10.png":
                imageDescription = "YouTube provides music videos, trends, and channels";
                break;
            case "photo11.png":
                imageDescription = "Digital technologies enable widespread use of videos";
                break;
            case "photo12.png":
                imageDescription = "Touch-based interaction with videos enriches user experience";
                break;
        }

        return imageDescription;
    }
}
