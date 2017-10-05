package ufmg_leic.meca;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import com.jjoe64.graphview.series.DataPoint;

import info.kiwiki.measurelib.ComponentStyle;
import info.kiwiki.measurelib.TermoMeter;
import info.kiwiki.measurelib.BaseComponent;
import info.kiwiki.measurelib.SevenSegment;
import info.kiwiki.measurelib.SevenSegment;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.view.View;

public class DisplayTermometro extends AppCompatActivity {

    int Unidade;

    BaseComponent Termometro;

    Double Temperatura;

    Thread ThreadLeitura;

    private final Handler mHandler = new Handler();

    Spinner SelecionarUnidade;

    String[ ] ArraySelecionarUnidade = { "Graus", "Kelvin", "Fahrenheit" };

    ArrayAdapter<String> SpinnerAdaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_termometro);

        Unidade = 1;

        Temperatura = 50.0;

        InicializaElementos( );

        InicializaTermometro( );

        ThreadLeitura = new Thread() {

            int i = 1;

            @Override
            public void run() {

                Temperatura = Temperatura + i;

                if( Temperatura == 100 ) i = -1;
                if( Temperatura == -20 ) i = 1;

                if( Unidade == 0 ) Termometro.setNewValue( Temperatura );
                if( Unidade == 1 ) Termometro.setNewValue( Temperatura + 273 );
                mHandler.postDelayed(this, 50);

            }
        };
        ThreadLeitura.start( );
    }


    private void InicializaTermometro( ){

        Termometro = ( BaseComponent )findViewById( R.id.Termometro );
        Termometro.setLabel( "Termometro" );

        if( Unidade == 0 ){

            Termometro.setLabel2( "[°C]" );
            Termometro.setMax( 100 );
            Termometro.setMin( -20 );
            Termometro.setWarningPercent( 60 );
            Termometro.setAlertPercent( 80 );
            Termometro.setColorTheme( ComponentStyle.THEME_CITRUS );

            Termometro.setNewValue( Temperatura );
        }

        if( Unidade == 1 ){


            Termometro.setLabel2("[K]");
            Termometro.setMax( 373 );
            Termometro.setMin(253);
            Termometro.setWarning( 333 );
            Termometro.setAlert( 353 );
            Termometro.setColorTheme(ComponentStyle.THEME_CITRUS);

            Termometro.setNewValue( Temperatura + 273 );
        }

    }

    private void InicializaElementos( ){

        SelecionarUnidade = ( Spinner )findViewById( R.id.spinner );

        SpinnerAdaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ArraySelecionarUnidade);
        SelecionarUnidade.setAdapter(SpinnerAdaptador);

        SelecionarUnidade.setOnItemSelectedListener(new OnItemSelectedListener( ) {

            @Override
            public void onItemSelected( AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Unidade = position;
                InicializaTermometro( );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

}
