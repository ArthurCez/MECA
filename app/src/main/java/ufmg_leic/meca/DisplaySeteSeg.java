package ufmg_leic.meca;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.Callback;

import java.util.Set;

import info.kiwiki.measurelib.BaseComponent;
import info.kiwiki.measurelib.ComponentStyle;
import info.kiwiki.measurelib.SevenSegment;
import info.kiwiki.measurelib.SevenSegment;

public class DisplaySeteSeg extends AppCompatActivity {

    int Unidade;

    BaseComponent SeteSeg;

    Double Resposta;

    Thread ThreadLeitura;

    private final Handler mHandler = new Handler();

    Spinner SelecionarUnidade;

    String[ ] ArraySelecionarUnidade = { "Graus", "Kelvin", "Fahrenheit" };

    ArrayAdapter<String> SpinnerAdaptador;


    GlobalBean BeanGlobal = GlobalBean.getInstance();
    BeanListener Listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sete_seg);

        Unidade = 0;

        Resposta = 0.0;

        InicializaElementos( );

        InicializaSeteSeg( );

        ThreadLeitura = new Thread() {

            int i = 1;

            @Override
            public void run() {

                BeanGlobal.bean.readAcceleration(new Callback<Acceleration>() {
                    @Override
                    public void onResult(Acceleration result) {

                        Resposta = result.x();

                    }
                });

                SeteSeg.setNewValue(Resposta);
                mHandler.postDelayed(this, 250);

            }
        };
        ThreadLeitura.start( );
    }


    private void InicializaSeteSeg( ){

        SeteSeg.setLabel(" ");

        SeteSeg.setProperty(SevenSegment.SEGMENT_TYPE, SevenSegment.SEGMENT_DISPLAY_SIGN_ON);
        SeteSeg.setProperty(SevenSegment.SEGMENT_INT_LEN, 1);
        SeteSeg.setProperty(SevenSegment.SEGMENT_FLOAT_POINTS, 2);

        SeteSeg.setColorTheme(ComponentStyle.THEME_DARK);

        SeteSeg.setLabel2(" ");
        SeteSeg.setNewValue( Resposta);

    }



    private void InicializaElementos( ){

        SeteSeg = ( BaseComponent )findViewById( R.id.SeteSeg );
        SelecionarUnidade = ( Spinner )findViewById( R.id.spinnerD );

        SpinnerAdaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ArraySelecionarUnidade);
        SelecionarUnidade.setAdapter(SpinnerAdaptador);

        SelecionarUnidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener( ) {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Unidade = position;
                InicializaSeteSeg( );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }
}
