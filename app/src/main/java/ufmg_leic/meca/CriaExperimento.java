package ufmg_leic.meca;


//*********   Imports    **********//


// Android
    import android.app.Activity;
    import android.content.Intent;
    import android.support.design.widget.TextInputLayout;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.view.View;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.Spinner;


//*****************************************************//



/********************************************************************************
 ***                                                                          ***
 ***  Activity: CriarExperimento                                              ***
 ***  Funcao: Interface em forma de diálogo que intermedia criacao de novos   ***
 ***          experimentos                                                    ***
 ***                                                                          ***
 ********************************************************************************/

public class CriaExperimento extends AppCompatActivity implements View.OnClickListener {


//***** Declaracao de Variáveis *****//

    Intent returnIntent = new Intent( );

    GlobalSelect Global = GlobalSelect.getInstance( );

    Button Add;

    TextInputLayout NomeExperimento;

    String[ ] arraySpinner;

    Spinner s;




//****** Implementacao dos Métodos *****//


    //*** Método OnCreate:
    //
    //    Executa quando a atividade é iniciada. Responsável pela criacao e exibicao da View.
    //
    //*******************************************************************************************//
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Define view com base no arquivo activity_cria_experimento.xml
            setContentView( R.layout.activity_cria_experimento );

        // Define resultado como cancelado ( é mudado quando a atividade cria o experimento )
            setResult( Activity.RESULT_CANCELED, returnIntent );

        // Inicializa os elementos da view
            InicializaElementos( );
    }



    //*** Método InicializaElementos:
    //
    //    Inicializacao das variáveis, associando-as aos elementos da view.
    //
    //*********************************************************************************************//
    private void InicializaElementos( ){

        // Instancia botao de adicionar turma
            Add = ( Button )findViewById( R.id.buttonAddEXP );
            Add.setOnClickListener( this );

        // Instancia o campo de entrada de texto
            NomeExperimento = ( TextInputLayout )findViewById( R.id.textInputLayout2 );

        // Cria array de opcoes para o spinner, o instancia e cria o adapter que o relaciona com o array
            arraySpinner = new String[ ] {
                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
            };

            s = ( Spinner ) findViewById( R.id.spinner2 );

            ArrayAdapter< String > adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, arraySpinner );
            s.setAdapter( adapter );
    }



    //*** Método onClick:
    //
    //    Captura cliques nos botoes e executa as respectivas chamadas
    //
    //*********************************************************************************************//
    public void onClick( View v ) {

        // Identifica qual botao foi clicado
        switch ( v.getId( ) ) {

            // Caso o botao de adicionar experimento for clicado
            case R.id.buttonAddEXP:

                // E o campo do nome nao estiver vazio
                    if (  NomeExperimento.getEditText( ) != null && !TextUtils.isEmpty( NomeExperimento.getEditText( ).getText( ) ) ) {
                        if ( ( !NomeExperimento.getEditText( ).getText( ).toString( ).replaceAll( "\\s+", "" ).equals( "" ) ) && ( s.getSelectedItem( ) != null ) ) {

                            // Define o resultado da actividade como bem sucedida
                                setResult( Activity.RESULT_OK, returnIntent );

                            // Copia informacoes de Nome do experimento e numero de grupos a se criar para variavel global
                                Global.NomeExperimento = NomeExperimento.getEditText( ).getText( ).toString( );
                                Global.NumGrupos = s.getSelectedItem( ).toString( );

                            // Inicia chamada da API Google para criacao de experimento com codigo de retorno 1
                            Global.Position = 4;
                            Intent AddTurma = new Intent( this, GoogleAPIRequest.class );
                            startActivityForResult( AddTurma, 1 );
                        }
                    }
                break;
        }
    }



    //*** Método onActivityResult:
    //
    //    Captura fim de execucao de uma atividade chamada por esta classe
    //
    //*********************************************************************************************//
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

        // Se o codigo de retorno for 1...
        if ( requestCode == 1 ) {

            // E o codigo de retorno indicar execucao bem sucedida...
                if( resultCode == Activity.RESULT_OK ){

                    // Finaliza esta atividade
                    finish( );
                }
        }
    }
}
