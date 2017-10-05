package ufmg_leic.meca;


//*********   Imports    **********//


// android
    import android.app.Activity;
    import android.content.Intent;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.ListView;
    import android.widget.TextView;

// Java
    import java.util.ArrayList;


//*****************************************************//



/********************************************************************************
 ***                                                                          ***
 ***  Activity: ProfessorGrupos                                               ***
 ***  Funcao: Exibe os grupos já criados em tela e as ferramentas para        ***
 ***          seu manuseio                                                    ***
 ***                                                                          ***
 ********************************************************************************/

public class ProfessorGrupos extends AppCompatActivity implements View.OnClickListener{


//***** Declaracao de Variáveis *****//

    Activity mActivity = this;

    int Position;

    String  ExperimentoAtual,
            TurmaAtual,
            Aux[ ];

    Intent CallAPI;

    GlobalSelect Global = GlobalSelect.getInstance();

    TextView Titulo;

    ArrayList< String > ListaNomeGrupos;

    ArrayAdapter< String > ListaNomeGruposAdapter;

    ListView ListaGrupos;

    Button Rename,
           Componentes,
           Authorization;




//****** Implementacao dos Métodos *****//


    //*** Método OnCreate:
    //
    //    Executa quando a atividade é iniciada. Responsável pela criacao e exibicao da View.
    //
    //*******************************************************************************************//
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Carrega view como definida em activity_professor_grupos.xml
            setContentView( R.layout.activity_professor_grupos );

        // Inicializa os elementos da view
            InicializaElementos( );

        // Salva na variavel global os nomes da turma e do experimento atual
            Global.NomeTurmaSelecionada = "MECA_" + TurmaAtual;
            Global.NomeExperimento = ExperimentoAtual.substring( 0, ExperimentoAtual.length( ) - 1);

        // Seleciona o script a ser executado através da variável seletora e chama a classe com a API do Google com codigo 2
            Global.Position = 6;
            CallAPI= new Intent( mActivity, GoogleAPIRequest.class );
            startActivityForResult( CallAPI, 2 );
    }



    //*** Método InicializaElementos:
    //
    //    Inicializacao das variáveis, associando-as aos elementos da view. Inicializa AlertDialog
    //
    //************************************************************************************************//
    private void InicializaElementos( ){

        // Instancia o Textview do título e obtém o nome do experimento atual (titulo)
            Titulo = ( TextView )findViewById( R.id.TituloGrupos );
            ExperimentoAtual = getIntent( ).getStringExtra( "EXTRA2" );
            Titulo.setText( ExperimentoAtual );

        // Obtem o nome da turma atual em questao
            TurmaAtual = Global.NomeTurmaSelecionada;

        // Instancia listview com a lista de grupos em questao
            ListaGrupos = ( ListView )findViewById( R.id.ListaGrupos );

        // Cria um array de strings para guardar os nomes de grupos e o associa a listview atraves de um adapter
            ListaNomeGrupos = new ArrayList< >( );
            ListaNomeGruposAdapter = new ArrayAdapter< >( this, android.R.layout.simple_list_item_1, ListaNomeGrupos );
            ListaGrupos.setAdapter( ListaNomeGruposAdapter );

        // Instancia os tres botoes da view, os declara desabilitados (a principio) e cria um OnClickListener para cada
            Rename = ( Button )findViewById( R.id.buttonRename );
            Rename.setEnabled( false );
            Rename.setOnClickListener( this );

            Componentes = ( Button )findViewById( R.id.buttonComp );
            Componentes.setEnabled( false );
            Componentes.setOnClickListener( this );

            Authorization = ( Button )findViewById( R.id.buttonAuthorize );
            Authorization.setEnabled( false );
            Authorization.setOnClickListener( this );

        // Cria um clicklistener para os elementos da lista de grupos
            ListaGrupos.setOnItemClickListener( new AdapterView.OnItemClickListener( ) {
                public void onItemClick( AdapterView< ? > parent, View view, int position, long id ) {

                // Guarda a posicao do grupo selecionado na lista e habilita os botoes
                    Position = position;
                    Rename.setEnabled( true );
                    Componentes.setEnabled( true );
                    Authorization.setEnabled( true );
                }
            });
    }



    //*** Método onClick:
    //
    //    Captura cliques nos botoes e executa as respectivas chamadas
    //
    //*********************************************************************************************//
    public void onClick( View v ) {

        // Identifica qual botao foi clicado
        switch ( v.getId( ) ) {

            // Caso o botao Renomear for clicado...
            case R.id.buttonRename:

                // Salva a turma atual, o nome do experimento atual e do grupo a ser renomeado na variavel global
                    Global.NomeTurmaSelecionada = TurmaAtual;
                    Global.NomeExperimento = ExperimentoAtual.substring( 0, ExperimentoAtual.length( ) - 1 );

                    // é preciso separar o nome do grupo dos prefixos: "MECA_Nome do Experimento_Nome do Grupo" - "MECA_" - "Nome do Experimento_"
                        Global.NomeGrupoAntigo = ListaNomeGrupos.get( Position ).substring( 0, ListaNomeGrupos.get( Position ).length( ) -1 );
                        Aux = Global.NomeGrupoAntigo.split( "_" );
                        Global.NomeGrupoAntigo = Aux[ Aux.length - 1 ];

                // Inicia uma chamada a atividade RenomearGrupo com codigo de retorno 1
                    Intent CallRename = new Intent( mActivity, RenomearGrupo.class );
                    startActivityForResult( CallRename, 1 );

                break;



            // Caso o botao Autorizar dor Clicado...
            case R.id.buttonAuthorize:

                // Salva a turma atual, o nome do experimento atual e do grupo a ser renomeado na variavel global
                    Global.NomeTurmaSelecionada = TurmaAtual;
                    Global.NomeExperimento = ExperimentoAtual.substring( 0, ExperimentoAtual.length( ) - 1 );

                    // é preciso separar o nome do grupo dos prefixos: "MECA_Nome do Experimento_Nome do Grupo" - "MECA_" - "Nome do Experimento_"
                        Global.NomeGrupoAtual = ListaNomeGrupos.get( Position ).substring( 0, ListaNomeGrupos.get( Position ).length( ) -1 );
                        Aux = Global.NomeGrupoAtual.split( "_" );
                        Global.NomeGrupoAtual = Aux[ Aux.length - 1 ];

                // Inicia uma chamada a atividade AutorizarGrupo com codigo de retorno 1
                    Intent CallAuthorize = new Intent( mActivity, AutorizarGrupo.class );
                    startActivityForResult( CallAuthorize, 1 );

                break;



            // Caso o botao Componentoes for clicado...
            case R.id.buttonComp:

                // Inicia uma chamada a atividade AutorizarGrupo
                    Intent CallComp = new Intent( mActivity, GrupoMembros.class );
                    startActivity( CallComp );

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

        // Variáveis Auxiliares
            String NomePasta;
            String subString[ ];

        // Se o código de retorno for 1...
        if ( requestCode == 1 ) {

            // E o resultado for positivo...
                if( resultCode == Activity.RESULT_OK ){

                    // Chama a atividade GoogleAPIRequest para executar os script de renomear grupos
                        Global.Position = 6;
                        Global.NomeTurmaSelecionada = "MECA_" + TurmaAtual;
                        Global.NomeExperimento = ExperimentoAtual.substring( 0, ExperimentoAtual.length( ) - 1 );
                        startActivityForResult( CallAPI, 2 );
                }
        }


        // Se o código de retorno for 2...
        if ( requestCode == 2 ) {

            // E o resultado for positivo...
                if( resultCode == Activity.RESULT_OK ){

                    // Limpa lista de grupos para atualizacao
                    ListaNomeGrupos.clear( );

                    // Percorre a lista de grupos obtida pela API do google inserindo seus nomes na lista (depois de retirados o sprefixos)
                    for( int i = 0; i < Global.ListaGrupos.size( ); i++ ){

                        NomePasta = Global.ListaGrupos.get( i );
                        subString = NomePasta.split( "_" );
                        NomePasta = subString[ subString.length - 1 ];

                        ListaNomeGrupos.add( NomePasta );
                    }

                    // Ordena a lista de nomes em ordem alfabética e notifica a mudanca para atualizacao da view
                        java.util.Collections.sort( ListaNomeGrupos );
                        ListaNomeGruposAdapter.notifyDataSetChanged( );
                }
        }
    }
}
