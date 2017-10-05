package ufmg_leic.meca;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapeamentoFisico extends AppCompatActivity implements View.OnClickListener {

    Activity mActivity = this;

    Handler h;

    double TouchX, TouchY, Resposta,  Media = 0;

    int Contador = 0;

    int[] Quadrante;

    GlobalMapeamento Global = GlobalMapeamento.getInstance();

    GlobalSelect GlobalAPICall = GlobalSelect.getInstance();

    DesenhaImageView mapImageView;

    double[][] Medias;

    double[] BancoValores;

    Button Up;

    Switch switchMap;

    TextInputLayout Input;

    TextView Valor;

    Intent CallAPI;

    Runnable r;

    GlobalBean BeanGlobal = GlobalBean.getInstance();

    AlertDialog alert;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapeamento_fisico);

        InicializaElementos();

        InicializaThread();

        HandleClick();

        h.post( r );
    }



    private void InicializaElementos(){

        Up = ( Button )findViewById( R.id.buttonUpMap );
        Up.setOnClickListener(this);
        Up.setEnabled( false );
        Up.setVisibility(View.INVISIBLE);

        switchMap = (Switch)findViewById(R.id.switch2);
        switchMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    Input.setEnabled(true);
                    Input.setVisibility(View.VISIBLE);
                    Valor.setText( " " );
                }
                else {
                    Input.setVisibility(View.INVISIBLE);
                    Input.setEnabled(false);
                }
            }
        });

        Input = ( TextInputLayout )findViewById(R.id.textInputLayoutValor);
        Input.setEnabled(false);
        Input.setVisibility(View.INVISIBLE);

        Valor = (TextView)findViewById( R.id.textViewValor );

        Medias = new double[ Global.Y ][ Global.X ];
        BancoValores = new double[10];
        for( int i=0; i < 10; i++ ){
            BancoValores[Contador] = 0;
        }

        mapImageView = (DesenhaImageView)findViewById( R.id.widgetMap );


        AlertDialog.Builder builder = new AlertDialog.Builder( this );
            builder.setTitle( "Voce deseja sobrescrever?" );
            builder.setMessage( "Isso substituirá o valor antigo desse quadrante pelo valor atual. \n\nDeseja prosseguir?" );

            builder.setPositiveButton( "SIM", new DialogInterface.OnClickListener( ) {

                public void onClick( DialogInterface dialog, int which ) {

                    if (!switchMap.isChecked()) {

                        Medias[Global.Quad[0] - 1][Global.Quad[1] - 1] = Media;
                        mapImageView.invalidate();
                    }

                    else {
                        if ( !Input.getEditText().getText().toString().equals("") ) {

                           Medias[Global.Quad[0] - 1][Global.Quad[1] - 1] = Double.parseDouble(Input.getEditText().getText().toString());
                           mapImageView.invalidate();
                        }

                        mapImageView.invalidate();
                    }
                }
            });

            builder.setNegativeButton( "NAO", new DialogInterface.OnClickListener( ) {

                @Override
                public void onClick( DialogInterface dialog, int which ) {
                    dialog.dismiss( );
                }
            });

        alert = builder.create( );
    }



    private void InicializaThread(){

        h = new Handler();

        r = new Runnable() {
            @Override
            public void run(){

                Media = 0;

                Resposta = Calc();

                if( Contador < 10 ){

                    BancoValores[Contador] = Resposta;
                }
                else{
                    Contador = 0;
                }

                for( int i=0; i < 10; i++ ){
                    Media = Media + BancoValores[Contador];
                }

                Media = Media / 10;

                if (!switchMap.isChecked()) Valor.setText("Valor Atual :  " + String.format("%.2f", Media));

                h.postDelayed( r, 250 );
            }
        };
    }



    private void HandleClick(){

        mapImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TouchX = event.getX();
                TouchY = event.getY();

                if(event.getAction() == MotionEvent.ACTION_UP){
                    if( (TouchX > 3) && ( TouchX < 366 ) && ( TouchY > 3 ) && ( TouchY < 607 )  ) {

                        Quadrante = CalculaQuadrante();

                        Global.Quad = Quadrante;

                        if (Global.Mapa[Global.Quad[0] - 1][Global.Quad[1] - 1] == 1) {
                            alert.show();
                        }
                        else
                        {

                            if (!switchMap.isChecked()) {

                                Medias[Global.Quad[0] - 1][Global.Quad[1] - 1] = Media;
                                Global.Mapa[Global.Quad[0] - 1][Global.Quad[1] - 1] = 1;

                                if (TestaMapaCompleto()) {
                                    Up.setVisibility(View.VISIBLE);
                                    Up.setEnabled(true);
                                }

                                mapImageView.invalidate();
                            }

                            else {

                                if (!Input.getEditText().getText().toString().equals("")) {

                                    Medias[Global.Quad[0] - 1][Global.Quad[1] - 1] = Double.parseDouble(Input.getEditText().getText().toString());
                                    Global.Mapa[Global.Quad[0] - 1][Global.Quad[1] - 1] = 1;

                                    if (TestaMapaCompleto()) {
                                        Up.setVisibility(View.VISIBLE);
                                        Up.setEnabled(true);
                                    }

                                    mapImageView.invalidate();
                                }
                            }
                        }
                    }
                }

                return true;
            }
        });
    }



    private int[] CalculaQuadrante( ){

        int[] Quad;
        int QuadX = 0, QuadY = 0;

        for( int i = 1; i <= Global.X; i++ ){
            if( TouchX <= (369/Global.X)*i ){
                QuadY = i;
                break;
            }
        }

        for( int j = 1; j <= Global.Y; j++ ){
            if( TouchY <= (607/Global.Y)*j ){
                QuadX = j;
                break;
            }
        }

        Quad = new int[]{
                QuadX, QuadY
        };

        return Quad;
    }


    private boolean TestaMapaCompleto(){

        boolean result = true;

        for ( int k = 0; k < Global.Y; k++ ){
            for ( int l = 0; l < Global.X; l++ ){

                if ( Global.Mapa[k][l] != 1 ){

                    result = false;
                }
            }
        }

        return result;
    }


    private double CalculaMedia( ){

        double resultado = 0;

        for( int i = 0; i < Global.Y; i++ ){
            for ( int j = 0; j < Global.X; j++ ){

                resultado = resultado + Global.MatrizMedias[i][j];
            }
        }

        return( resultado / ( Global.X * Global.Y ) );
    }


    public void onClick( View v ) {

        // Identifica qual botao foi clicado
        switch ( v.getId( ) ) {

            case R.id.buttonUpMap:

                Global.MatrizMedias = Medias;
                Global.MediaSala = CalculaMedia();
                Global.JSONarray = new JSONArray();

                for( int i = 0; i < Global.Y; i++ ){
                    for ( int j = 0; j < Global.X; j++ ){

                        JSONObject JSONbuffer = new JSONObject();

                        try {

                            JSONbuffer.put("Pontos", Global.MatrizMedias[i][j]);
                        }
                        catch (JSONException e){

                        }

                        Global.JSONarray.put(JSONbuffer);
                    }
                }

                GlobalAPICall.Position = 11;
                CallAPI = new Intent( mActivity, GoogleAPIRequest.class );
                startActivityForResult( CallAPI, 1 );
                break;
        }
    }


    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

        if ( requestCode == 1 ) {

            // Com indicador de sucesso...
            if( resultCode == Activity.RESULT_OK ){
                finish();
            }
        }
    }


    private double Calc() {


        try {
            // Le o seu acelorometro para eixo x
            BeanGlobal.bean.readAcceleration(new Callback<Acceleration>() {
                @Override
                public void onResult(Acceleration result) {

                    Resposta = result.x();

                }
            });
        }
        finally {

        }

        // Retorna o valor lido do bean
        return( Resposta );
    }
}
