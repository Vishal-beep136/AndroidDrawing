package com.example.item;

public class SliderItem {

    private Integer integer;
    private String url;

    public SliderItem(Integer integer, String url) {
        this.integer = integer;
        this.url = url;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
