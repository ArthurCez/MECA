package ufmg_leic.meca;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Arthur on 06/08/2017.
 */

public class GlobalGrafico {

    private static GlobalGrafico instance;

    WeightedObservedPoints Points;

    LineGraphSeries<DataPoint> Series;

    JSONArray JSONarray = new JSONArray();


    public static synchronized GlobalGrafico getInstance(){
        if(instance==null){
            instance = new GlobalGrafico();
        }
        return(instance);
    }
}
