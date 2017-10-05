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
 ***  Activity: RenomearGrupo                                                   ***
 ***  Funcao: Manuseia atividade que comeanda a renomeacao de grupos            ***
 ***                                                                            ***
 **********************************************************************************/

public class RenomearGrupo extends AppCompatActivity implements View.OnClickListener {



    //***** Declaracao de Variáveis *****//

    Activity mActivity = this;

    Intent returnIntent;

    Intent CallAPI;

    GlobalSelect Global = GlobalSelect.getInstance( );

    Button Renomear;

    TextInputLayout NovoNome;




//****** Implementacao dos Métodos *****//


    //*** Método OnCreate:
    //
    //    Executa quando a atividade é iniciada. Responsável pela criacao e exibicao da View.
    //
    //*******************************************************************************************//
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Define a view como o enunciado no arquivo activity_renomear_grupo
            setContentView( R.layout.activity_renomear_grupo );

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

        // Instancia o botao renomear e cria um onclicklistener para este
            Renomear = ( Button )findViewById( R.id.buttonrenomear );
            Renomear.setOnClickListener( this );

        // Instancia campo de insercao de texto
            NovoNome = (TextInputLayout)findViewById(R.id.textInputLayoutrenomear);

        // Iniciliza intent de retorno
            returnIntent = new Intent();
    }



    //*** Método onClick:
    //
    //    Captura cliques nos botoes e executa as respectivas chamadas
    //
    //*********************************************************************************************//
    public void onClick( View v ) {

        // Identifica qual botao foi clicado
        switch ( v.getId( ) ) {

            // Caso botao renomear for clicado
            case R.id.buttonrenomear:

                // E campo de nome nao for vazio...
                    if (  NovoNome.getEditText( ) != null && !TextUtils.isEmpty( NovoNome.getEditText( ).getText( ) ) ) {
                        if (!NovoNome.getEditText( ).getText( ).toString( ).replaceAll( "\\s+", "" ).equals( "" )) {

                            // Armazena o novo nome na variavel global
                                Global.NomeGrupoNovo = NovoNome.getEditText( ).getText( ).toString( );

                            // Chama a API do google com variavel seletora correspondente ao script de renomear grupos, com codigo de retorno 1
                                Global.Position = 7;
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
