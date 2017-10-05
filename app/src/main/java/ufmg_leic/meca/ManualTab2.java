package ufmg_leic.meca;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.graphics.Rect;

//*** android ***//
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

//*** com.jjoe64.graphview.GraphView ***//
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ManualTab2 extends Fragment implements View.OnClickListener {

    GraphView GraficoCalibragem,
              GraficoHistograma;

    Double Buffer,
           Result,
           Media,
           Anterior,
           Erro,
           Intervalo,
           Largura;

    Button Upload;

    Thread ThreadAtualiza;

    private final Handler mHandler = new Handler( );

    View rootView;

    private LineGraphSeries<DataPoint> Series;

    private PointsGraphSeries<DataPoint> Pontos;

    private BarGraphSeries<DataPoint> Hist;

    ArrayList<Double> Repeticoes;

    GlobalManual Global = GlobalManual.getInstance();

    TextView TextCoeficientes;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_manual_tab2, container, false);



        InicializaElementos( );

        ThreadAtualiza = new Thread() {

            int i = 1;

            @Override
            public void run() {

                InicializaGrafico1();
                mHandler.postDelayed(this, 500);

            }
        };
        ThreadAtualiza.start( );

        return rootView;
    }


    private void InicializaElementos( ){

        Repeticoes = new ArrayList<>();

        GraficoCalibragem = ( GraphView )rootView.findViewById( R.id.CurvaCalibracao );
        GraficoHistograma = ( GraphView )rootView.findViewById( R.id.Histograma );

        Series = new LineGraphSeries<>();
        Pontos = new PointsGraphSeries<>();
        Hist = new BarGraphSeries<>();

        Upload = (Button)rootView.findViewById( R.id.button_Up );
        Upload.setOnClickListener(this);

        TextCoeficientes = ( TextView )rootView.findViewById( R.id.TextViewCoeficientes );

        Largura = 0.0;

    }




    private void InicializaGrafico1( ) {

            if (( !Global.GlobalValoresAcc.isEmpty())&&( Global.FlagAdd ) && (Global.GlobalValoresAcc.size() > 1) ){

                GraficoCalibragem.removeAllSeries( );
                GraficoHistograma.removeAllSeries();

                Series = new LineGraphSeries<>( );
                Pontos = new PointsGraphSeries<>( );
                Hist = new BarGraphSeries<>();


                for( int i = 0; i < Global.GlobalValoresAcc.size(); i++ ){

                    Buffer = Global.GlobalValoresAcc.get( i );
                    Result = Global.CoefAngular * Buffer + Global.CoefLinear;

                    Pontos.appendData( new DataPoint( Global.GlobalValoresAcc.get(i), Global.GlobalValoresInacc.get( i )), true, 300 );
                    Series.appendData( new DataPoint( Buffer, Result), true, 300);

                    GraficoCalibragem.addSeries( Series );
                    GraficoCalibragem.addSeries( Pontos );
                }


                for( int k = 0; k < Global.GlobalValoresAcc.size(); k++){

                    if( k == 0 ) Anterior = Global.GlobalValoresAcc.get( 0 );

                    else{

                        if( !Global.GlobalValoresAcc.get( k ).equals( Anterior ) ){

                            Media = ObtemMedia( Anterior );
                            Erro = Math.abs( Media - ( Global.CoefAngular * Global.GlobalValoresAcc.get(k-1) + Global.CoefLinear ) );
                            Hist.appendData( new DataPoint( Buffer, Erro ), true, 300);
                            Anterior = Global.GlobalValoresAcc.get( k );

                        }
                    }
                }


                GraficoHistograma.addSeries(  Hist );

                GraficoHistograma.getViewport().setXAxisBoundsManual( true );
                GraficoHistograma.getViewport().setMinX( Hist.getLowestValueX() - 1);
                GraficoHistograma.getViewport().setMaxX( Hist.getHighestValueX() + 1  );


                GraficoCalibragem.getViewport().setXAxisBoundsManual( true );
                GraficoCalibragem.getViewport().setMinX( Series.getLowestValueX()  - ( Series.getHighestValueX() - Series.getLowestValueX() ) * 0.2  );
                GraficoCalibragem.getViewport().setMaxX( ( Series.getHighestValueX() - Series.getLowestValueX() ) * 0.2 + Series.getHighestValueX()  );

                GraficoCalibragem.getViewport().setYAxisBoundsManual( true );
                GraficoCalibragem.getViewport().setMinY( Pontos.getLowestValueY()  - ( Pontos.getHighestValueY() - Pontos.getLowestValueY() ) * 0.2  );
                GraficoCalibragem.getViewport().setMaxY( ( Pontos.getHighestValueY() - Pontos.getLowestValueY() ) * 0.2 + Pontos.getHighestValueY()  );


                Pontos.setColor(Color.RED);
                Pontos.setSize( 10 );

                Global.FlagAdd = false;

                TextCoeficientes.setText( " Coeficientes da reta de Calibracao:  \na = " + String.format("%.04f", Global.CoefAngular) + "   \nb = " + String.format("%.04f", Global.CoefLinear) );
            }



        if (( !Global.GlobalValoresAcc.isEmpty())&&( Global.FlagSub ) ){

            GraficoCalibragem.removeAllSeries( );
            GraficoHistograma.removeAllSeries();

            if( Global.GlobalValoresAcc.size() > 1 ){

                Series = new LineGraphSeries<>( );
                Pontos = new PointsGraphSeries<>( );
                Hist = new BarGraphSeries<>();


                for( int j = 0; j < Global.GlobalValoresAcc.size(); j++ ){

                    Buffer = Global.GlobalValoresAcc.get( j );
                    Result = Global.CoefAngular * Buffer + Global.CoefLinear;

                    Pontos.appendData( new DataPoint( Global.GlobalValoresAcc.get( j ), Global.GlobalValoresInacc.get( j )), true, 300 );
                    Series.appendData( new DataPoint( Global.GlobalValoresAcc.get(j), Result), true, 300);

                    GraficoCalibragem.addSeries( Series );
                    GraficoCalibragem.addSeries( Pontos );
                }


                for( int k = 0; k < Global.GlobalValoresAcc.size(); k++){

                    if( k == 0 ) Anterior = Global.GlobalValoresAcc.get( 0 );

                    else{

                        if( Global.GlobalValoresAcc.get( k ) != Anterior ){

                            Media = ObtemMedia( Anterior );
                            Erro = Math.abs( Media - ( Global.CoefAngular * Global.GlobalValoresAcc.get(k-1) + Global.CoefLinear ) );
                            Hist.appendData( new DataPoint( Global.GlobalValoresAcc.get(k-1), Erro ), true, 300);
                            Anterior = Global.GlobalValoresAcc.get( k );

                            Largura = Largura + 1;

                        }
                    }
                }


                GraficoHistograma.addSeries(  Hist );

                GraficoCalibragem.getViewport().setXAxisBoundsManual( true );
                GraficoCalibragem.getViewport().setMinX( Series.getLowestValueX()  - ( Series.getHighestValueX() - Series.getLowestValueX() ) * 0.2  );
                GraficoCalibragem.getViewport().setMaxX( ( Series.getHighestValueX() - Series.getLowestValueX() ) * 0.2 + Series.getHighestValueX()  );

                GraficoCalibragem.getViewport().setYAxisBoundsManual( true );
                GraficoCalibragem.getViewport().setMinY( Pontos.getLowestValueY()  - ( Pontos.getHighestValueY() - Pontos.getLowestValueY() ) * 0.2  );
                GraficoCalibragem.getViewport().setMaxY( ( Pontos.getHighestValueY() - Pontos.getLowestValueY() ) * 0.2 + Pontos.getHighestValueY()  );

                TextCoeficientes.setText( " Coeficientes da reta de Calibracao:  \na = " + String.format("%.04f", Global.CoefAngular) + "   \nb = " + String.format("%.04f", Global.CoefLinear) );

                Pontos.setColor(Color.RED);
                Pontos.setSize( 10 );

                Global.FlagSub = false;
            }
        }
    }


    private double ObtemMedia( double Valor ){

        double Somatorio = 0;

        int Contador = 0;

        for( int i = 0; i < Global.GlobalValoresAcc.size(); i++ ){

            if( Global.GlobalValoresAcc.get( i ) == Valor ){
                Somatorio = Somatorio + Global.GlobalValoresInacc.get( i );
                Contador++;
            }
        }

        return Somatorio/Contador;
    }


    public void onClick( View v ) {

    }
}