package com.aasquare.mockhouse.category;

import com.aasquare.mockhouse.util.Util;

public class Category {

    private int id;
    private String category;
    private Long hits;
    private String type;

    public Category(String category) {
        this(category,0l);
    }
    public Category(String category, Long count) {
        this(0,category,count);
    }
    public Category(int id,String category, Long count) {
        validateParameters(category, hits);
        this.id = id;
        this.category = category;
        this.hits = hits;
    }

    public Category(int id,String category, Long count,String type) {
        this(id,category,count);
        if(Util.isNullOrBlank(type))
            throw new IllegalArgumentException("Invalid type for Category Type attribute");
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    private void validateParameters(String category, Long count) {

        if (Util.isNullOrBlank(category))
            throw new IllegalArgumentException("Invalid category");
        if (Util.isNegative(count))
            throw new IllegalArgumentException("Invalid hits value");

    }

    public Long getHits() {
        //Get Category Count
        return hits;
    }


    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }


    public String getCategory() {
        return category;
    }


    public void setCategory(String cat) {
        category = cat;

    }
    public String getStringDescription() {

        return category + ((Util.isPositive(hits))?"("+ hits +")":"");
    }

}
