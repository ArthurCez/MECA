package ufmg_leic.meca;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.apache.commons.math3.util.MathArrays;

public class MapeamentoDimensao extends AppCompatActivity implements View.OnClickListener {

    Activity mActivity = this;

    Button Dim;

    Spinner spinnerX, spinnerY;

    String[] arraySpinnerX, arraySpinnerY;

    TextInputLayout Largura, Profundidade, Instituicao, Unidade, Sala, NumLum, NumLamp, Potencia;

    GlobalMapeamento Global = GlobalMapeamento.getInstance();

    Boolean Fail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapeamento_dimensao);

        InicializaElementos( );
    }


    private void InicializaElementos( ){

        Dim = ( Button )findViewById( R.id.buttonDim );
        Dim.setOnClickListener(this);

        Largura = (TextInputLayout)findViewById(R.id.textInputLayout3);
        Profundidade = (TextInputLayout)findViewById(R.id.textInputLayout4);
        Instituicao = (TextInputLayout)findViewById(R.id.textInputLayoutInst);
        Unidade  = (TextInputLayout)findViewById(R.id.textInputLayoutUni);
        Potencia  = (TextInputLayout)findViewById(R.id.textInputLayoutPotencia);
        Sala  = (TextInputLayout)findViewById(R.id.textInputLayoutNumSala);
        NumLum = (TextInputLayout)findViewById(R.id.textInputLayoutNumLuminarias);
        NumLamp = (TextInputLayout)findViewById(R.id.textInputLayoutLampadasperLum);


        arraySpinnerX = new String[ ] {
                "1", "2", "3", "4", "5"
        };

        arraySpinnerY = new String[ ] {
                "1", "2", "3", "4", "5"
        };

        spinnerX = ( Spinner ) findViewById( R.id.spinner4 );
        spinnerY = ( Spinner ) findViewById( R.id.spinner3 );

        ArrayAdapter< String > adapterX = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, arraySpinnerX );
        spinnerX.setAdapter( adapterX );
        spinnerX.setSelection( 2 );

        ArrayAdapter< String > adapterY = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, arraySpinnerY );
        spinnerY.setAdapter( adapterY );
        spinnerY.setSelection( 2 );

    }


    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }


    public void onClick( View v ) {

        // Identifica qual botao foi clicado
        switch (v.getId()) {

            // Caso o botao de adicionar experimento for clicado
            case R.id.buttonDim:
                if( isNumeric(Largura.getEditText().getText().toString()) && isNumeric(Profundidade.getEditText().getText().toString())
                        && !Unidade.getEditText().getText().toString().equals("") && !Instituicao.getEditText().getText().toString().equals("")
                        && !Sala.getEditText().getText().toString().equals("") && !NumLamp.getEditText().getText().toString().equals("")
                        && !NumLum.getEditText().getText().toString().equals("") && !Potencia.getEditText().getText().toString().equals("")){

                    Global.X = Integer.parseInt(spinnerX.getSelectedItem().toString());
                    Global.Y = Integer.parseInt(spinnerY.getSelectedItem().toString());

                    Global.Sala = Integer.parseInt( Sala.getEditText().getText().toString());
                    Global.NumLum = Integer.parseInt( NumLum.getEditText().getText().toString());
                    Global.NumLamp = Integer.parseInt( NumLamp.getEditText().getText().toString());

                    Global.Potencia = Double.parseDouble( Potencia.getEditText().getText().toString() );
                    Global.Largura = Double.parseDouble(Largura.getEditText().getText().toString());
                    Global.Profundidade = Double.parseDouble(Profundidade.getEditText().getText().toString());

                    Global.Instituicao = Instituicao.getEditText().getText().toString();
                    Global.Unidade = Unidade.getEditText().getText().toString();

                    Global.Mapa = new int[ Global.Y ][ Global.X ];
                    for ( int i = 0; i < Global.Y; i++ ){
                        for ( int j = 0; j < Global.X; j++ ){
                            Global.Mapa[i][j] = 0;
                        }
                    }

                    Intent ClickMap = new Intent( mActivity, MapeamentoFisico.class );
                    startActivity(ClickMap);
                    finish();
                }
                else{
                    Snackbar.make(findViewById(android.R.id.content), "Existem campos ainda nao preenchidos. Preencha todos os campos e tente de novo.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
        }
    }
}
