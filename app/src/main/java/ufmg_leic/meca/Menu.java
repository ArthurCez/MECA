package ufmg_leic.meca;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;



public class Menu extends AppCompatActivity implements View.OnClickListener {

    Activity mActivity = this;

    Button Display,
           Permanente,
           Transitorio,
           Grafico,
           Mapeamento,
           Config;

    int GrandezaMedida;

    Intent ClickDisplay,
           ClickPermanente;

    GlobalBean BeanGlobal = GlobalBean.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Coloca atividade em Fullscreen
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_menu);

        // Inicializa Elementos
            InicializaElementos( );

        // Obtem TipoGrandeza
            GrandezaMedida = IdentificaSensor( );

    }



    private void InicializaElementos( ){

        Display = ( Button )findViewById( R.id.Display );
        Display.setOnClickListener( this );

        Permanente = ( Button )findViewById( R.id.Permanente );
        Permanente.setOnClickListener( this );

        Grafico = ( Button )findViewById( R.id.Grafico );
        Grafico.setOnClickListener( this );

        Transitorio = ( Button )findViewById( R.id.Transitorio );
        Transitorio.setOnClickListener( this );

        Config = ( Button )findViewById( R.id.Configuracoes );
        Config.setOnClickListener( this );

        Mapeamento = ( Button )findViewById( R.id.Mapeamento );
        Mapeamento.setOnClickListener( this );
    }


    private int IdentificaSensor( ) {
        return ( 1 );
    }


    public void onClick( View v ) {

        // Identifica qual botao foi clicado
        switch ( v.getId( ) ) {

            case R.id.Display:

                if( GrandezaMedida == 0 ) ClickDisplay = new Intent(mActivity, DisplayTermometro.class);

                else if( GrandezaMedida == 1 ) ClickDisplay = new Intent(mActivity, DisplayPonteiro.class);

                else if( GrandezaMedida == 2 ) ClickDisplay = new Intent(mActivity, DisplaySeteSeg.class);

                startActivity( ClickDisplay );
                break;


            case R.id.Permanente:
                ClickPermanente = new Intent(mActivity, SubMenuPermanente.class);
                startActivity( ClickPermanente );
                break;

            case R.id.Grafico:
                Intent ClickGrafico = new Intent(mActivity, Grafico.class);
                startActivity( ClickGrafico );
                break;

            case R.id.Mapeamento:
                Intent ClickMapeamento = new Intent(mActivity, MapeamentoDimensao.class);
                startActivity( ClickMapeamento )                                ;
                break;

            case R.id.Transitorio:
                break;

            case R.id.Configuracoes:
                break;

        }
    }


    @Override
    protected void onResume(  ){
        super.onResume();

        if(!BeanGlobal.bean.isConnected()){
            BeanGlobal.bean.disconnect();
            finish();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(!BeanGlobal.bean.isConnected()){
            BeanGlobal.bean.disconnect();
            finish();
        }
    }
}
