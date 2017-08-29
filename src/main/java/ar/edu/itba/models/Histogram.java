package ar.edu.itba.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nicolas Castano on 8/25/17.
 */
public class Histogram {

    private int pixelCount;
    private Map<Integer,Double> greyLevelMap;


    public Histogram(GreyImageMatrix gim){
        this.greyLevelMap = new HashMap<>();
        this.pixelCount = 0;

        GreyPixel gp;
        Double count;
        for(int i=0; i<gim.getWidth(); i++){
            for (int j=0; j<gim.getHeight(); j++){
                gp = (GreyPixel) gim.getPixelColor(i, j);
                count = greyLevelMap.get(gp.getGrey());
                if (count == null)
                    greyLevelMap.put(gp.getGrey(), 0.0);
                else
                    greyLevelMap.put(gp.getGrey(),count + 1);
                pixelCount++;
            }
        }
        greyLevelMap.forEach((key, value) -> greyLevelMap.put(key, value / pixelCount));
    }

    private double getSumOfFrequencies(){
        double s=0;

        for(Double i: greyLevelMap.values()){
            s+= (i/pixelCount);
        }

        return s;

    }

    private double getMinFrequency(){
        double sMin=greyLevelMap.get(0.0);

        for(Double i: greyLevelMap.values()){
            if(i<sMin){
                sMin=i;
            }
        }

        return sMin/pixelCount;
    }

    public int discreteTransformation(){
        double min= getMinFrequency();
        return (int)(((getSumOfFrequencies()-min)/(1-min)) + 0.5);
    }

    public double getFrequency(int category){
        return greyLevelMap.get(category);
    }

    public Iterable<Integer> getCategories() {
        return greyLevelMap.keySet();
    }
}
