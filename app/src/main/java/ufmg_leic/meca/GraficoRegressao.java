package ufmg_leic.meca;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;

public class GraficoRegressao extends AppCompatActivity {

    private LineGraphSeries<DataPoint> Resultado;

    GraphView graph;

    GlobalGrafico GraficoGlobal = GlobalGrafico.getInstance();

    private String[] arraySpinner;

    Spinner s;

    TextView coeff;

    PolynomialCurveFitter fitter;

    double[] coeficientes;

    double maxX, reg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico_regressao);

        InicializaElementos();


        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if( s.getSelectedItemPosition() == 0){

                    coeff.setText(" ");
                    graph.removeAllSeries();


                    Resultado = new LineGraphSeries<>();
                    Resultado.setColor(Color.RED);

                    graph.addSeries(Resultado);
                    graph.addSeries(GraficoGlobal.Series);
                }

                if( s.getSelectedItemPosition() == 1){

                    graph.removeSeries(Resultado);
                    Resultado = new LineGraphSeries<>();
                    Resultado.setColor(Color.RED);

                    fitter = PolynomialCurveFitter.create(1);
                    coeficientes = fitter.fit(GraficoGlobal.Points.toList());

                    coeff.setText("Coeficiente Angular = "+ String.format("%.4f", coeficientes[1]) + "\n\n");
                    coeff.append("Coeficiente Linear = "+ String.format("%.4f", coeficientes[0]) + "\n\n");

                    maxX = graph.getViewport().getMaxX(true);

                    for (double i = 0; i < maxX; i = i + 0.2) {
                        reg = i * coeficientes[1] + coeficientes[0];
                        Resultado.appendData(new DataPoint(i, reg), true, 10000);
                    }

                    graph.addSeries(Resultado);
                }


                if( s.getSelectedItemPosition() == 2){

                    graph.removeSeries(Resultado);
                    Resultado = new LineGraphSeries<>();
                    Resultado.setColor(Color.RED);

                    fitter = PolynomialCurveFitter.create(2);
                    coeficientes = fitter.fit(GraficoGlobal.Points.toList());

                    coeff.setText("Coeficiente de grau 2 = "+ String.format("%.4f", coeficientes[2]) + "\n\n");
                    coeff.append("Coeficiente de grau 1 = "+ String.format("%.4f", coeficientes[1]) + "\n\n");
                    coeff.append("Coeficiente de grau 0 = "+ String.format("%.4f", coeficientes[0]) + "\n\n");

                    maxX = graph.getViewport().getMaxX(true);

                    for (double i = 0; i < maxX; i = i + 0.2) {
                        reg = coeficientes[2] * Math.pow(i,2) + i * coeficientes[1] + coeficientes[0];
                        Resultado.appendData(new DataPoint(i, reg), true, 10000);
                    }

                    graph.addSeries(Resultado);
                }


                if( s.getSelectedItemPosition() == 3){

                    graph.removeSeries(Resultado);
                    Resultado = new LineGraphSeries<>();
                    Resultado.setColor(Color.RED);

                    fitter = PolynomialCurveFitter.create(3);
                    coeficientes = fitter.fit(GraficoGlobal.Points.toList());

                    coeff.setText("Coeficiente de grau 3 = "+ String.format("%.4f", coeficientes[3]) + "\n\n");
                    coeff.append("Coeficiente de grau 2 = "+ String.format("%.4f", coeficientes[2]) + "\n\n");
                    coeff.append("Coeficiente de grau 1 = "+ String.format("%.4f", coeficientes[1]) + "\n\n");
                    coeff.append("Coeficiente de grau 0 = "+ String.format("%.4f", coeficientes[0]) + "\n\n");

                    maxX = graph.getViewport().getMaxX(true);

                    for (double i = 0; i < maxX; i = i + 0.2) {
                        reg = Math.pow(i,3)*coeficientes[3] +Math.pow(i,2)*coeficientes[2] + i * coeficientes[1] + coeficientes[0];
                        Resultado.appendData(new DataPoint(i, reg), true, 10000);
                    }

                    graph.addSeries(Resultado);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }


    private void InicializaElementos(){

        Resultado = new LineGraphSeries<>();
        Resultado.setColor(Color.RED);

        graph = (GraphView) findViewById(R.id.graphreg);
        graph.addSeries(GraficoGlobal.Series);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(GraficoGlobal.Series.getLowestValueX());
        graph.getViewport().setMaxX(GraficoGlobal.Series.getHighestValueX());
        graph.getViewport().setMaxY(GraficoGlobal.Series.getHighestValueY());
        graph.getViewport().setMinY(GraficoGlobal.Series.getLowestValueY());

        graph.addSeries(GraficoGlobal.Series);
        graph.addSeries(Resultado);

        this.arraySpinner = new String[] {
                " ", "1", "2", "3"
        };


        s = (Spinner) findViewById(R.id.spinnerreg);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);

        coeff = (TextView)findViewById(R.id.textViewcoef);
        if( s.getSelectedItemPosition() == 0) coeff.setText(" ");
    }
}
