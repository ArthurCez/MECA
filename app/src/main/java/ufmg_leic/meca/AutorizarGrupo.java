package ufmg_leic.meca;


//*********   Imports    **********//


// android
    import android.app.Activity;
    import android.content.Intent;
    import android.support.design.widget.TextInputLayout;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.view.View;
    import android.widget.Button;


//*****************************************************//



/**********************************************************************************
 ***                                                                            ***
 ***  Activity: AutorizarGrupo                                                  ***
 ***  Funcao: Manuseia atividade que comanda a concessao de autorizacoes de     ***
 ***          edicao de sheets a email inserido                                 ***
 ***                                                                            ***
 **********************************************************************************/

public class AutorizarGrupo extends AppCompatActivity implements View.OnClickListener {



    //***** Declaracao de Variáveis *****//

    Activity mActivity = this;

    Intent returnIntent,
            CallAPI;

    GlobalSelect Global = GlobalSelect.getInstance( );

    Button Autorizar;

    TextInputLayout Email;




//****** Implementacao dos Métodos *****//


    //*** Método OnCreate:
    //
    //    Executa quando a atividade é iniciada. Responsável pela criacao e exibicao da View.
    //
    //*******************************************************************************************//
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Define a view como o enunciado no arquivo activity_autorizar_grupo
            setContentView( R.layout.activity_autorizar_grupo );

        // Define resultado da atividade como cancelada até segunda ordem
            setResult( Activity.RESULT_CANCELED, returnIntent );

        // Inicializa Elementos da view
            InicializaElementos( );
    }



    //*** Método InicializaElementos:
    //
    //    Inicializacao das variáveis, associando-as aos elementos da view.
    //
    //************************************************************************************************//
    private void InicializaElementos( ){

        // Inicializa o botao autorizar e cria um onclicklistener associado a ele
            Autorizar = ( Button )findViewById( R.id.buttonautoriza );
            Autorizar.setOnClickListener( this );

        // Inicializa campo de insercao de texto
            Email = ( TextInputLayout )findViewById( R.id.textInputLayoutaut );
    }



    //*** Método onClick:
    //
    //    Captura cliques nos botoes e executa as respectivas chamadas
    //
    //*********************************************************************************************//
    public void onClick( View v ) {

        // Identifica qual botao foi clicado
        switch ( v.getId( ) ) {

            // Se o botao autorizar for clicado...
            case R.id.buttonautoriza:

                // E o texto inserido pelo usuario for valido...
                    if (  Email.getEditText( ) != null && !TextUtils.isEmpty( Email.getEditText( ).getText( ) ) ) {
                        if ( !Email.getEditText( ).getText( ).toString( ).equals( "" ) ) {

                            // Obtém o email inserido pelo user e o salva na variavel global
                                Global.Email = Email.getEditText( ).getText( ).toString( );

                            // Chama a API do google para autorizacao de email com codigo de retorno 1
                                Global.Position = 8;
                                CallAPI = new Intent( mActivity, GoogleAPIRequest.class );
                                startActivityForResult( CallAPI, 1 );
                        }
                    }
                break;
        }
    }



    //*** Método onActivityResult:
    //
    //    Executa quando uma atividade chamada por essa activity retorna de sua execucao
    //
    //************************************************************************************************//
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

        // Se o codigo de retorno for 1...
        if ( requestCode == 1 ) {

            // E o retorno indicar tarefa positivamente executada...
                if( resultCode == Activity.RESULT_OK ){

                    // Define resultado como ok e encerra atividade
                        setResult( Activity.RESULT_OK, returnIntent );
                        finish( );
                }
        }
    }
}
