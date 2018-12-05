package com.example.darthkiler.myapplication;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Spiral {

    ArrayList<Point> points=new ArrayList<>();
    float r,a;

    Spiral(float r,float a)
    {

        this.a=a;
        this.r=r;

        for(float i=0;i<a;i+=0.1) {
            float x=(float) (-r*i*Math.sin(i));
            float y=(float) (r+r*i*Math.cos(i));
            float z=1f;
            points.add(new Point(x,y,z));
        }
    }

    public ArrayList<Float> getPoints()
    {
        //float f[]=new float[points.size()*3];
        ArrayList<Float> list=new ArrayList<>();
        for (Point p:
             points) {
            list.add(p.x);
            list.add(p.y);
            list.add(p.z);
        }
        return list;
    }

    public class Point {
        Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        float x;
        float y;
        float z;
    }

}
