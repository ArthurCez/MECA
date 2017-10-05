package ufmg_leic.meca;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GraficoOpcoes extends AppCompatActivity implements View.OnClickListener{

    Activity mActivity = this;

    Button Regressao,
           Upload;

    Intent CallAPI;

    GlobalSelect Global = GlobalSelect.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico_opcoes);

        Regressao = (Button)findViewById( R.id.buttonRegressao );
        Regressao.setOnClickListener(this);

        Upload = (Button)findViewById(R.id.buttonUpload);
        Upload.setOnClickListener(this);

    }


    public void onClick( View v ) {

        // Identifica qual botao foi clicado
        switch ( v.getId( ) ) {

            case R.id.buttonRegressao:

                // Caso o botao conectar for clicado, declara intent e inicia activity Menu
                Intent ClickReg = new Intent( this, GraficoRegressao.class );
                startActivity( ClickReg );
                finish();
                break;


            case R.id.buttonUpload:
                Global.Position = 9;
                CallAPI= new Intent(mActivity, GoogleAPIRequest.class);
                startActivity(CallAPI);
                finish();
                break;

        }
    }
}
