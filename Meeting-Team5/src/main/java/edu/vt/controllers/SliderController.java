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

        for (int i = 1; i <= 13; i++) {
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
                imageDescription = "New Year (January 1)";
                break;
            case "photo2.png":
                imageDescription = "Lunar New Year (January-February)";
                break;
            case "photo3.png":
                imageDescription = "Mardi Gras (February-March)";
                break;
            case "photo4.png":
                imageDescription = "Valentine’s Day (February 14)";
                break;
            case "photo5.png":
                imageDescription = "St. Patrick’s Day (March 17)";
                break;
            case "photo6.png":
                imageDescription = "Holi (March)";
                break;
            case "photo7.png":
                imageDescription = "Easter (March-April)";
                break;
            case "photo8.png":
                imageDescription = "Ramadan (April-May)";
                break;
            case "photo9.png":
                imageDescription = "Midsummer (June)";
                break;
            case "photo10.png":
                imageDescription = "Diwali (October-November)";
                break;
            case "photo11.png":
                imageDescription = "Day of the Dead (November)";
                break;
            case "photo12.png":
                imageDescription = "Hanukkah (December)";
                break;
            case "photo13.png":
                imageDescription = "Christmas (December 25)";
                break;
        }

        return imageDescription;
    }

    public String description_cn(String image) {

        String imageDescription = "";

        switch (image) {
            case "photo1.png":
                imageDescription = "新年 一月一号";
                break;
            case "photo2.png":
                imageDescription = "农历新年 一月到二月";
                break;
            case "photo3.png":
                imageDescription = "忏悔节 二月到三月";
                break;
            case "photo4.png":
                imageDescription = "情人节 二月十四号";
                break;
            case "photo5.png":
                imageDescription = "圣-帕特里克节 三月十七号";
                break;
            case "photo6.png":
                imageDescription = "侯丽节 三月";
                break;
            case "photo7.png":
                imageDescription = "复活节 三月到四月";
                break;
            case "photo8.png":
                imageDescription = "斋戒月 四月到五月";
                break;
            case "photo9.png":
                imageDescription = "仲夏节 六月";
                break;
            case "photo10.png":
                imageDescription = "排灯节 十月到十一月";
                break;
            case "photo11.png":
                imageDescription = "亡灵节 十一月";
                break;
            case "photo12.png":
                imageDescription = "光明节 十二月";
                break;
            case "photo13.png":
                imageDescription = "圣诞节 十二月二十五号";
                break;
        }

        return imageDescription;
    }
}
