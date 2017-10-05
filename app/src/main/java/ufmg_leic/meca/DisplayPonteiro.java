package ufmg_leic.meca;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BatteryLevel;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.ScratchBank;
import com.punchthrough.bean.sdk.message.ScratchData;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import info.kiwiki.measurelib.BaseComponent;
import info.kiwiki.measurelib.ComponentStyle;

import static com.punchthrough.bean.sdk.message.ScratchBank.BANK_1;


public class DisplayPonteiro extends AppCompatActivity {

    int Unidade;

    float voltage;

    BaseComponent Ponteiro;

    Double Temperatura,
           Fahrenheit;

    Thread ThreadLeitura;

    private final Handler mHandler = new Handler();

    Spinner SelecionarUnidade;

    String[ ] ArraySelecionarUnidade = { "Graus", "Kelvin", "Fahrenheit" };

    ArrayAdapter<String> SpinnerAdaptador;

    GlobalBean BeanGlobal = GlobalBean.getInstance();

    boolean stop = false;

    TextView Teste;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ponteiro);

        stop = false;

        Unidade = 1;

        Temperatura = 0.0;

        InicializaElementos( );

        InicializaPonteiro( );

        BeanGlobal.bean.readBatteryLevel( new Callback<BatteryLevel>(){
            @Override
            public void onResult(BatteryLevel result) {

                voltage = result.getVoltage();

                Teste.setText("Voltagem = " + voltage);

            }
        } );


        ThreadLeitura = new Thread() {

            int i = 1;

            @Override
            public void run() {

                if( !stop ) {

                    if(!BeanGlobal.bean.isConnected()) finish();


                    BeanGlobal.bean.readScratchData(BANK_1, new Callback<ScratchData>() {
                                @Override
                                public void onResult(ScratchData result) {

                                    ByteBuffer bb;
                                    bb = ByteBuffer.wrap(result.data());
                                    bb.order(ByteOrder.LITTLE_ENDIAN);

                                    double calc = ((bb.getInt() * voltage)/1024 + 0.1);

                                    Temperatura = (calc - 2.8864)/0.0001;

                                    Teste.setText("Leitura: " + calc);

                                }
                            });



                    /*BeanGlobal.bean.readTemperature(new Callback<Integer>() {
                        @Override
                        public void onResult(Integer temp) {

                            Temperatura = (double) temp;
                        }

                    });*/

                    Fahrenheit = Temperatura * 1.8 + 32;

                    if (Unidade == 0) Ponteiro.setNewValue(Temperatura);
                    if (Unidade == 1) Ponteiro.setNewValue(Temperatura + 273);
                    if (Unidade == 2) Ponteiro.setNewValue(Fahrenheit);

                }

                    mHandler.postDelayed(this, 600);
            }
        };

        ThreadLeitura.start( );
    }


    private void InicializaElementos( ){

        Teste = (TextView)findViewById(R.id.textView4);

        SelecionarUnidade = ( Spinner )findViewById( R.id.spinnerP );

        SpinnerAdaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ArraySelecionarUnidade);
        SelecionarUnidade.setAdapter(SpinnerAdaptador);

        SelecionarUnidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener( ) {

            @Override
            public void onItemSelected( AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Unidade = position;
                InicializaPonteiro( );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }


    private void InicializaPonteiro( ){

        Ponteiro = ( BaseComponent )findViewById( R.id.Ponteiro );
        Ponteiro.setLabel( " " );

        if( Unidade == 0 ){

            Ponteiro.setLabel2( "[°C]" );
            Ponteiro.setMax( 1000 );
            Ponteiro.setMin( -100 );
            Ponteiro.setWarningPercent( 800 );
            Ponteiro.setAlertPercent( 900 );
            Ponteiro.setColorTheme( ComponentStyle.THEME_ANDROID );

            Ponteiro.setNewValue( Temperatura );
        }

        if( Unidade == 1 ){


            Ponteiro.setLabel2("[K]");
            Ponteiro.setMax( 373 );
            Ponteiro.setMin(253);
            Ponteiro.setWarning( 333 );
            Ponteiro.setAlert( 353 );
            Ponteiro.setColorTheme(ComponentStyle.THEME_DARK);

            Ponteiro.setNewValue( Temperatura + 273 );
        }

        if( Unidade == 2 ){



            Ponteiro.setLabel2("[°F]");
            Ponteiro.setMax( 212 );
            Ponteiro.setMin(-4);
            Ponteiro.setWarning( 140 );
            Ponteiro.setAlert( 176 );
            Ponteiro.setColorTheme(ComponentStyle.THEME_PASTEL);

            Ponteiro.setNewValue( Fahrenheit );
        }

    }

    @Override
    public void onDestroy(){
        stop = true;
        finish();
        super.onDestroy();
    }
}
