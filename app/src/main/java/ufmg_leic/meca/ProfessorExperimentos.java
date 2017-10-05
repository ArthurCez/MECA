package ufmg_leic.meca;


//*********   Imports    **********//


// android
    import android.app.Activity;
    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.support.design.widget.FloatingActionButton;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.ListView;
    import android.widget.TextView;

// Java
    import java.util.ArrayList;


//*****************************************************//



/********************************************************************************
 ***                                                                          ***
 ***  Activity: ProfessorExperimentos                                         ***
 ***  Funcao: Exibe os experimentos já criados em tela e as ferramentas para  ***
 ***          seu manuseio                                                    ***
 ***                                                                          ***
 ********************************************************************************/

public class ProfessorExperimentos extends AppCompatActivity {


//***** Declaracao de Variáveis *****//

    Activity mActivity = this;

    int Position;

    GlobalSelect Global = GlobalSelect.getInstance();

    Intent CallAPI;

    TextView Titulo;

    ListView ListaExperimentos;

    ArrayList< String > ListaNomeExperimentos;

    ArrayAdapter< String > ListaNomeExperimentosAdapter;

    String TurmaAtual;

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

        // Define view como o enunciado no arquivo activity_professor_experimentos
            setContentView( R.layout.activity_professor_experimentos );

        // Chama método para inicializacao dos elementos da view
            InicializaElementos( );

        // Define variável seletora da funcao a ser chamada pela API do google (ver classe GoogleAPIRequest para mais detalhes)
            Global.Position = 3;

        // Define nome da turma a qual o experimento atual pertence + prefixo de identificacao
            Global.NomeTurmaSelecionada = "MECA_" + TurmaAtual;

        // Chama a atividade GoogleAPIRequest com código de retorno igual a 2
            CallAPI= new Intent( mActivity, GoogleAPIRequest.class );
            startActivityForResult( CallAPI, 2 );
    }



    //*** Método InicializaElementos:
    //
    //    Inicializacao das variáveis, associando-as aos elementos da view. Inicializa AlertDialog
    //
    //************************************************************************************************//
    private void InicializaElementos( ){

        // Inicializa texto de título da janela e escreve o nome da turma a qual o exp. atual pertence
            Titulo = ( TextView )findViewById( R.id.TituloEXP );
            TurmaAtual = getIntent().getStringExtra("EXTRA");
            Titulo.setText( TurmaAtual );

        // Instancia a listview para lista de experimentos
            ListaExperimentos = ( ListView )findViewById( R.id.ListaEXP );

        // Inicializa a lista de nome dos experimentos e adiciona um adaptador a listview associado a lista de nomes
            ListaNomeExperimentos = new ArrayList< >( );
            ListaNomeExperimentosAdapter = new ArrayAdapter< >( this, android.R.layout.simple_list_item_1, ListaNomeExperimentos );
            ListaExperimentos.setAdapter( ListaNomeExperimentosAdapter );



        // Inicializa botao flutuante (criar turmas) e cria um clicklistener associado a ele
            FloatingActionButton fab = ( FloatingActionButton ) findViewById( R.id.floatingActionButtonEXP );
            fab.setOnClickListener( new View.OnClickListener( ) {
                @Override
                public void onClick( View view ) {

                    // Caso o botao seja clicado, salva a turma atual em variável global...
                        Global.NomeTurmaSelecionada = TurmaAtual;

                    // E chama atividade para criacao de novo experimento
                        Intent CriarEXP = new Intent( mActivity, CriaExperimento.class );
                        startActivityForResult( CriarEXP, 1 );
                }
            });


        // Cria um LongClickListener para os elementos da lista. Caso este seja acionado, inicia processo de deletar experimento
            ListaExperimentos.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener( ) {
                @Override
                public boolean onItemLongClick( AdapterView< ? > arg0, View arg1, int pos, long id ) {

                    // Se houver um clique longo, salva a posicao do item clicado e mostra a alertbox definida mais abaixo
                        Position = pos;
                        alert.show( );
                        return true;
                }
            });


        // Cria um ClickListener para os elementos da lista de experimentos
            ListaExperimentos.setOnItemClickListener( new AdapterView.OnItemClickListener( ) {
                public void onItemClick( AdapterView< ? > parent, View view, int position, long id ) {

                    // Se um elemento foi clicado, salva a Turma a qual ele pertence na variável global...
                        Global.NomeTurmaSelecionada = TurmaAtual;

                    // E chama atividade ProfessorGrupos, passando o nome do experimento selecionado como argumento
                        Intent MenuExp = new Intent( mActivity, ProfessorGrupos.class );
                        MenuExp.putExtra( "EXTRA2", ListaNomeExperimentos.get( position ) );
                        startActivity( MenuExp );
                }
            });


        // Processo de criacao do alertbox para deletar um experimento:

            // Cria um builder para o alertbox
                AlertDialog.Builder builder = new AlertDialog.Builder( this );

            // Define um título e mensagem a ser exibida na alertbox
                builder.setTitle( "Deletar Experimento" );
                builder.setMessage( "Voce está certo de que quer deletar esse Experimento? \n\n(Todo o conteúdo do Experimento também será perdido)" );

            // Define rotina a ser executada caso botao positivo for selecionado
                builder.setPositiveButton( "SIM", new DialogInterface.OnClickListener( ) {

                    public void onClick( DialogInterface dialog, int which ) {

                        // Obtém nome do experimento a ser deletado e salva na variável global
                            Global.NomeExperimentoToDelete = ListaNomeExperimentos.get( Position ).substring(0, ListaNomeExperimentos.get( Position ).length( ) - 1 );

                        // Salva também o nome da turma atual na variável global
                            Global.NomeTurmaSelecionada = "MECA_" + TurmaAtual;

                        // Remove o experimento da lista e notifica mudanca
                            ListaNomeExperimentos.remove( Position );
                            ListaNomeExperimentosAdapter.notifyDataSetChanged( );

                        // Fecha o alertbox e seta a variável seletora de funcoes da GoogleAPIRequest
                            dialog.dismiss( );
                            Global.Position = 5;

                        // Inicia chamada GoogleAPIRequest com codigo 1
                            startActivityForResult( CallAPI, 1 );
                    }
                });


            // Caso botao negativo for selecionado, simplesmente fecha a alertbox
                builder.setNegativeButton( "NAO", new DialogInterface.OnClickListener( ) {

                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        // Do nothing
                        dialog.dismiss( );
                    }
                });

            // Cria a alertbox
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

        // Caso atividade foi requisitada com código 1...
            if ( requestCode == 1 ) {

                // E tenha retornado codigo de sucesso
                    if( resultCode == Activity.RESULT_OK ){

                        // Chama Google API para atualizar a lista de experimentos, com codigo de retorno 2
                            Global.Position = 3;
                            Global.NomeTurmaSelecionada = "MECA_" + TurmaAtual;
                            startActivityForResult( CallAPI, 2 );
                    }
            }

        // Caso atividade foi requisitada com código 2...
            if ( requestCode == 2 ) {

                // E tenha retorno com código de sucesso
                    if( resultCode == Activity.RESULT_OK ){

                        // Limpa a lista de experimentos para atualizacao
                            ListaNomeExperimentos.clear( );

                        // Adiciona os nomes dos experimentos lidos pela api a listview e notifica a mudanca
                            for( int i = 0; i < Global.ListaExperimentos.size( ); i++ ){
                                NomePasta = Global.ListaExperimentos.get( i );
                                ListaNomeExperimentos.add( NomePasta );
                            }
                            ListaNomeExperimentosAdapter.notifyDataSetChanged( );
                    }
            }
    }
}
