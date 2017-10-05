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
    import android.widget.Button;


//*****************************************************//



/********************************************************************************
 ***                                                                          ***
 ***  Activity: Criar Turma                                                   ***
 ***  Funcao: Interface em forma de diálogo que intermedia criacao de nova    ***
 ***          turma                                                           ***
 ***                                                                          ***
 ********************************************************************************/

public class CriarTurma extends AppCompatActivity implements View.OnClickListener{


//***** Declaracao de Variáveis *****//

    Intent returnIntent = new Intent( );

    GlobalSelect Global = GlobalSelect.getInstance( );

    Button Add;

    TextInputLayout NomeTurma;




//****** Implementacao dos Métodos *****//


    //*** Método OnCreate:
    //
    //    Executa quando a atividade é iniciada. Responsável pela criacao e exibicao da View.
    //
    //*******************************************************************************************//
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Define a view da página como enunciado no arquivo activity_criar_turma.xml
            setContentView( R.layout.activity_criar_turma );

        // Define o resultado da atividade como "cancelada". Isso só muda no retorno da atividade
            setResult( Activity.RESULT_CANCELED, returnIntent );

        // Inicializa elementos da view para uso posterior
            InicializaElementos( );
    }



    //*** Método InicializaElementos:
    //
    //    Inicializacao das variáveis, associando-as aos elementos da view.
    //
    //*********************************************************************************************//
    private void InicializaElementos( ){

        Add = ( Button )findViewById( R.id.buttonAddTurma );
        Add.setOnClickListener( this );

        NomeTurma = ( TextInputLayout )findViewById( R.id.textInputLayout );

    }



    //*** Método onClick:
    //
    //    Captura cliques nos botoes e executa as respectivas chamadas
    //
    //*********************************************************************************************//
    public void onClick( View v ) {

        // Identifica qual botao foi clicado
        switch ( v.getId( ) ) {

            // Caso o botao de adicionar turma for acionado...
            case R.id.buttonAddTurma:

                // E o nome da turma no campo de entrada de texto nao for vazio...
                    if ( NomeTurma.getEditText( ) != null && !TextUtils.isEmpty( NomeTurma.getEditText( ).getText( ) ) ) {
                        if ( !NomeTurma.getEditText( ).getText( ).toString( ).replaceAll( "\\s+", "" ).equals( "" ) ) {

                            // Define o resultado da atividade como bem sucedida
                            setResult( Activity.RESULT_OK, returnIntent );

                            // Salva na variável global o nome a ser dado para turma
                            Global.NomeTurma = NomeTurma.getEditText( ).getText( ).toString( );

                            // Define a variável indicadora para executar a chamada correta a API do google (ver classe GoogleAPIRequest)
                            Global.Position = 0;

                            // Chama classe para efetuar chamada a API do google script e aguarda pelo resultado desta classe com código 1
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

        // Se a classe GoogleAPIRequest retornar de sua execucao...
            if ( requestCode == 1 ) {

                // Com indicador de sucesso...
                if( resultCode == Activity.RESULT_OK ){

                    // Encerra esta atividade
                        finish( );
                }
            }
    }
}
