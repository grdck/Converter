package com.grdck.converter.model;

import android.support.annotation.NonNull;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;

@Root(name = "ValCurs")
public class CourseList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(name = "Date")
    private String date;
    @Attribute
    private String name;
    @ElementList(inline = true)
    private List<Valute> valutes;

    @NonNull
    public List<Valute> getValutes() {
        return valutes;
    }

}
