package ufmg_leic.meca;


//*********   Imports    **********//


// android
    import android.app.Activity;
    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.os.Bundle;
    import android.support.design.widget.FloatingActionButton;
    import android.support.v7.app.AppCompatActivity;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.ListView;


// java
    import java.util.ArrayList;


//*****************************************************//



/**********************************************************************************
 ***                                                                            ***
 ***  Activity: Professor Turmas                                                ***
 ***  Funcao: Consulta pastas do Google drive, exibe turmas MECA e permite a    ***
 ***          edicao destas turmas (criar, deletar, selecionar)                 ***
 ***                                                                            ***
 **********************************************************************************/

public class ProfessorTurmas extends AppCompatActivity {



//***** Declaracao de Variáveis *****//

    Activity mActivity = this;

    int Position;

    GlobalSelect Global;

    ListView ListaTurmas;

    Intent CallAPI;

    ArrayAdapter< String > ListaNomeTurmasAdapter;

    ArrayList< String > ListaNomeTurmas;

    AlertDialog alert;




//****** Implementacao dos Métodos *****//


    //*** Método OnCreate:
    //
    //    Executa quando a atividade é iniciada. Responsável pela criacao e exibicao da View.
    //
    //*******************************************************************************************//
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Define a view da atividade como a contida em activity_professor_turmas.xml
            setContentView( R.layout.activity_professor_turmas );

        // Chama método que inicializa elementos da View
            InicializaElementos( );

        // Seleciona funcao da API a ser executada (pela variavel Position) e executa a atividade GoogleAPIRequest
            Global.Position = 1;
            startActivityForResult( CallAPI, 2 );
    }



    //*** Método InicializaElementos:
    //
    //    Inicializacao das variáveis, associando-as aos elementos da view.
    //
    //************************************************************************************************//
    private void InicializaElementos( ){


        // Instancia variável Global
            Global = GlobalSelect.getInstance( );

        // Associa Listaturmas a listview da view
            ListaTurmas = ( ListView )findViewById( R.id.ListaTurmas );

        // Inicializa Lista de nomes das turmas e um adapter para associa-la a listview acima definida
            ListaNomeTurmas = new ArrayList< >( );
            ListaNomeTurmasAdapter = new ArrayAdapter< >( this, android.R.layout.simple_list_item_1, ListaNomeTurmas );
            ListaTurmas.setAdapter( ListaNomeTurmasAdapter );

        // Intent para chamar activity GoogleAPIRequest
            CallAPI = new Intent( mActivity, GoogleAPIRequest.class );



        // Instancia o FloatingActionButton (botao redondo no canto da view) e define sua tarefa
        FloatingActionButton fab = ( FloatingActionButton ) findViewById( R.id.floatingActionButton );
        fab.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick( View view ) {

                // Caso clickado, inicia a activity CrairTurmas
                    Intent CriarTurmas = new Intent( mActivity, CriarTurma.class );
                    startActivityForResult( CriarTurmas, 1 );
            }
        });


        // Cria um longclicklistener para a listview para detectar cliques longos (que indicam que o usuário quer deletar a turma clickada)
            ListaTurmas.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener( ) {
                @Override
                public boolean onItemLongClick( AdapterView< ? > arg0, View arg1, int pos, long id ) {

                    // Guarda a posicao da turma clickada
                    Position = pos;

                    // Exibe AlertDialog para confirmar que se quer deletar a turma
                    alert.show( );

                    return true;
                }
            });

        // Cria um onClickListener para capturar cliques no Listview
            ListaTurmas.setOnItemClickListener( new AdapterView.OnItemClickListener( ) {
                public void onItemClick( AdapterView< ? > parent, View view, int position, long id ) {

                    // Caso uma turma tenha sido clickada, inicia a atividade ProfessorExperimentos, enviando o nome da turma selecionada
                        Intent MenuExp = new Intent( mActivity, ProfessorExperimentos.class );
                        MenuExp.putExtra( "EXTRA", ListaNomeTurmas.get( position ) );
                        startActivity( MenuExp );
                }
            });


        //Cria Builder para um AlertDialog que irá aparecer quando se desejar deletar uma turma
            AlertDialog.Builder builder = new AlertDialog.Builder( this );

            // Define titulo do AlertDialog
                builder.setTitle( "Deletar Turma" );

            // Define mensagem do AlertDialog
                builder.setMessage( "Voce está certo de que quer deletar essa turma? \n\n(Todo o conteúdo da turma também será perdido)" );

            // Define botao positivo ("SIM") e tarefa que executará caso ele seja clickado
                builder.setPositiveButton( "SIM", new DialogInterface.OnClickListener( ) {

                public void onClick( DialogInterface dialog, int which ) {

                    // Obtem qual a turma selecionada para ser deletada
                        Global.NomeTurmaToDelete = ListaNomeTurmas.get( Position );

                    // Remove essa turma da lista de turmas e notifica a mudanca
                        ListaNomeTurmas.remove( Position );
                        ListaNomeTurmasAdapter.notifyDataSetChanged( );

                    // Cancela exibicao do AlertDialog e chama a API do google para deletar a turma
                        dialog.dismiss( );
                        Global.Position = 2;
                        startActivityForResult( CallAPI, 1 );
                }
            });

            // Define Botao negativo ("NAO") e a tarefa que executará caso clickado
                builder.setNegativeButton( "NAO", new DialogInterface.OnClickListener( ) {

                @Override
                public void onClick( DialogInterface dialog, int which ) {
                        // Cancela exibicao do AlertDialog
                            dialog.dismiss( );
                    }
            });

            alert = builder.create( );
    }



    //*** Método onActivityResult:
    //
    //    Executa quando uma atividade chamada por essa activity retorna de sua execucao
    //
    //************************************************************************************************//
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

        // Variáveis Auxiliares
            String NomePasta;
            String subString;

        // Caso atividade foi requisitada com código 1...
            if ( requestCode == 1 ) {

                // E tenha retornado um código de sucesso...
                    if( resultCode == Activity.RESULT_OK ){

                        // Chama GoogleAPIRequest para atualizar a lista de turmas vigentes
                            Global.Position = 1;
                            startActivityForResult( CallAPI, 2 );

                    }
            }

        // Caso atividade foi requisitada com código 2...
            if ( requestCode == 2 ) {

                // E tenha retornado um código de sucesso...
                    if( resultCode == Activity.RESULT_OK ){

                        // Limpa a lista de nomes de turmas
                            ListaNomeTurmas.clear( );

                        // Le a lista de pastas na raiz do drive uma por uma...
                            for( int i = 0; i < Global.ListaTurmas.size( ); i++ ){

                                // Obtem os quatro primeiros caracteres da pasta lida
                                    NomePasta = Global.ListaTurmas.get( i );
                                    subString = NomePasta.substring( 0, 4 );

                                // Caso a pasta tiver o prefixo MECA, indicando que ela representa uma turma, adiciona o sufixo (nome da turma) a lista de nomes de turmas
                                    if( subString.equals( "MECA" ) ) ListaNomeTurmas.add( NomePasta.substring( 5, NomePasta.length( ) - 1 ) );
                            }

                        // Notifica alteracoes da lista de turmas
                            ListaNomeTurmasAdapter.notifyDataSetChanged( );
                    }
            }
    }
}
