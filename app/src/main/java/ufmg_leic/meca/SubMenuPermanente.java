package ufmg_leic.meca;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class SubMenuPermanente extends AppCompatActivity implements View.OnClickListener {

    Button Manual,
           ManualAuto,
           Auto;

    Intent Click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sub_menu_permanente);

        InicializaElementos( );
    }


    private void InicializaElementos( ){

        Manual = ( Button )findViewById( R.id.BotaoManual );
        Manual.setOnClickListener( this );

        ManualAuto = ( Button )findViewById( R.id.BotaoSemi );
        ManualAuto.setOnClickListener( this );

        Auto = ( Button )findViewById( R.id.BotaoAuto );
        Auto.setOnClickListener( this );

    }

    public void onClick( View v ) {

        // Identifica qual botao foi clicado
        switch ( v.getId( ) ) {

            case R.id.BotaoManual:
                Click = new Intent( this, CalibrarManual.class );
                startActivity( Click );
                break;

            case R.id.BotaoSemi:

                break;

            case R.id.BotaoAuto:

                break;

        }
    }
}
