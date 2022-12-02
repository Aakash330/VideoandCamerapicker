package com.aakash.model;

import com.aakash.videobasesamplegpuv.FilterType;

public class FilterRvModel {
    private FilterType name;
    private String imageUrl;

    public FilterRvModel(FilterType name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public FilterType getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
