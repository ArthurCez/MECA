package ufmg_leic.meca;

import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.Callback;

import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;

import info.kiwiki.measurelib.BaseComponent;
import info.kiwiki.measurelib.ComponentStyle;
import info.kiwiki.measurelib.SevenSegment;

import static ufmg_leic.meca.R.id.graph;

public class Grafico extends AppCompatActivity implements View.OnClickListener {

    private LineGraphSeries<DataPoint> Series;

    GlobalBean BeanGlobal = GlobalBean.getInstance();

    GraphView graph;

    Button playpause,
           config,
           reset;

    BaseComponent SeteSeg;

    int rescale = 30,
        Amostragem = 100,
        Contador = 0;

    double Value = 0.0,
           time = 0.0,
           Buffer = 0.0,
           resposta = 0.0;

    long tic,
         toc = 0;

    Thread thread;

    boolean TimerFlag = true,
            stop = false,
            isPaused = false;

    private final Handler mHandler = new Handler();

    WeightedObservedPoints Points;

    GlobalGrafico GraficoGlobal = GlobalGrafico.getInstance();

    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);

        InicializaElementos();
        InicializaGrafico();
        InicializaDisplay();
        InicializaThread();
    }


    private void InicializaElementos(){

            stop = false;
            isPaused = false;

        // Impede que a tela bloqueie
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Cria e adiciona ao gráfico uma série de dados (vetor de pontos que serao representados)
            Series = new LineGraphSeries<>();
            graph = (GraphView) findViewById(R.id.graph);
            graph.addSeries(Series);

        // Cria views dos botoes da tela e cria um clicklistener pra cada um
            playpause = (Button)findViewById(R.id.buttonpp);
            playpause.setOnClickListener(this);

            config = (Button)findViewById(R.id.buttonconfig);
            config.setOnClickListener(this);
            config.setEnabled(false);

            reset = (Button)findViewById(R.id.buttonreset);
            reset.setOnClickListener(this);

        // Instancia display de sete segmantos
            SeteSeg = (BaseComponent)findViewById( R.id.SeteSeggraph );

        Points = new WeightedObservedPoints();

        jsonArray = new JSONArray();

        GraficoGlobal.JSONarray = new JSONArray();
    }


    private void InicializaGrafico(){

        // Seta os limites do grafico no eixo x (de 0 a 200)
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setYAxisBoundsManual(false);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(rescale);

            Series.setThickness(8);
    }


    private void InicializaDisplay(){

        SeteSeg.setLabel(" Posicao ");

        SeteSeg.setProperty(SevenSegment.SEGMENT_TYPE, SevenSegment.SEGMENT_DISPLAY_SIGN_ON);
        SeteSeg.setProperty(SevenSegment.SEGMENT_INT_LEN, 1);
        SeteSeg.setProperty(SevenSegment.SEGMENT_FLOAT_POINTS, 2);

        SeteSeg.setColorTheme(ComponentStyle.THEME_DARK);

        SeteSeg.setLabel2(" ");
        SeteSeg.setNewValue( Value );
    }


    private double Calc() {

        try {
            // Le o seu acelorometro para eixo x
            BeanGlobal.bean.readAcceleration(new Callback<Acceleration>() {
                @Override
                public void onResult(Acceleration result) {

                    resposta = result.x();

                }
            });
        }
        finally {

        }

        // Retorna o valor lido do bean
        return( resposta );
    }



    private void InicializaThread(){

        thread = new Thread() {

            @Override
            public void run() {

                    if(!stop) {

                        if (!BeanGlobal.bean.isConnected()){
                            finish();
                        }

                        if (time > rescale) {

                            rescale = rescale + 10;
                            graph.getViewport().setMaxX(rescale);
                        }

                        Buffer = Calc();

                        toc = System.currentTimeMillis();

                        time = (toc - tic);
                        time = time / 1000;

                        if (!isPaused) {
                            Contador++;
                            Series.appendData(new DataPoint(time, Buffer), true, 10000);
                            SeteSeg.setNewValue(Buffer);
                            Points.add(time, Buffer);

                            JSONObject JSONbuffer = new JSONObject();

                            try {

                                JSONbuffer.put("id", Contador);
                                JSONbuffer.put("X", time);
                                JSONbuffer.put("Y", Buffer);
                            }
                            catch (JSONException e){

                            }

                            jsonArray.put(JSONbuffer);
                        }
                    }

                    mHandler.postDelayed(this, Amostragem);
            }

        };

        // Armazena momento inicial de execucao (em ms)
        tic = System.currentTimeMillis();

        // Dispara thread de leitura do bean e plotagem
        thread.start();
    }



    public void onClick(View v) {

        // Identifica qual botao foi clicado
        switch (v.getId()) {

            // Se for o botao Pause, pausa o gráfico: Tutorial
            case R.id.buttonpp:

                if(!isPaused){

                    isPaused = true;
                    playpause.setText("Play");
                    config.setEnabled(true);

                    graph.getViewport().setYAxisBoundsManual(true);
                    graph.getViewport().setScrollable(true); // enables horizontal scrolling
                    graph.getViewport().setScrollableY(true); // enables vertical scrolling
                    graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
                    graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
                }
                else{
                    isPaused = false;
                    playpause.setText("Pause");
                    config.setEnabled(false);

                    graph.getViewport().setYAxisBoundsManual(false);
                    graph.getViewport().setMinX(0);
                    graph.getViewport().setMaxX(rescale);

                    graph.getViewport().setScrollable(false); // enables horizontal scrolling
                    graph.getViewport().setScrollableY(false); // enables vertical scrolling
                    graph.getViewport().setScalable(false); // enables horizontal zooming and scrolling
                    graph.getViewport().setScalableY(false); // enables vertical zooming and scrolling
                }
                break;




            case R.id.buttonconfig:

                GraficoGlobal.Points = Points;
                GraficoGlobal.Series = Series;

                GraficoGlobal.JSONarray = jsonArray;

                Intent ClickConfig = new Intent(this, GraficoOpcoes.class);
                startActivity(ClickConfig);
                break;



            case R.id.buttonreset:

                stop = true;
                SystemClock.sleep(2*Amostragem);

                graph.removeAllSeries();
                rescale = 30;
                graph.getViewport().setMinX(0);
                graph.getViewport().setMaxX(rescale);
                Series = new LineGraphSeries<>();
                graph.addSeries(Series);
                Points.clear();
                jsonArray = new JSONArray();
                tic = System.currentTimeMillis();

                if (isPaused){

                    isPaused = false;
                    playpause.setText("Pause");
                    config.setEnabled(false);

                    graph.getViewport().setYAxisBoundsManual(false);
                    graph.getViewport().setMinX(0);
                    graph.getViewport().setMaxX(rescale);

                    graph.getViewport().setScrollable(false); // enables horizontal scrolling
                    graph.getViewport().setScrollableY(false); // enables vertical scrolling
                    graph.getViewport().setScalable(false); // enables horizontal zooming and scrolling
                    graph.getViewport().setScalableY(false); // enables vertical zooming and scrolling
                }

                stop = false;

                break;
        }
    }

    @Override
    public void onDestroy(){
        stop = true;
        super.onDestroy();
    }
}

