package com.grdck.converter;

import com.grdck.converter.model.CourseList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class Utils {

    private Utils() {
    }

    public static CourseList getCoursesFromXml(final String courses) throws Exception {
        Serializer serializer = new Persister();
        return serializer.read(CourseList.class, courses);
    }

}
