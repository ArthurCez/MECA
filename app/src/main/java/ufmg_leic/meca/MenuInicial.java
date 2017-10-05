package ufmg_leic.meca;


//*********   Imports    **********//


// android
    import android.app.Activity;
    import android.content.Intent;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.view.View;
    import android.view.WindowManager;
    import android.view.animation.AlphaAnimation;
    import android.view.animation.Animation;
    import android.view.animation.AnimationSet;
    import android.view.animation.TranslateAnimation;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.TextView;

// java
    import com.dd.CircularProgressButton;

    import java.util.ArrayList;


//*****************************************************//



/********************************************************************************
 ***                                                                          ***
 ***  Activity: Menu Principal                                                ***
 ***  Funcao: Apresenta a animacao de logo e as opcoes iniciais de execucao   ***
 ***                                                                          ***
 ********************************************************************************/

public class MenuInicial extends AppCompatActivity implements View.OnClickListener {


//***** Declaracao de Variáveis *****//

    Activity mActivity = this;

    Animation FadeInBotoes;

    CircularProgressButton BotaoConect,
                           BotaoProfessor;

    GlobalSelect Global = GlobalSelect.getInstance();



//****** Implementacao dos Métodos *****//


    //*** Método OnCreate:
    //
    //    Executa quando a atividade é iniciada. Responsável pela criacao e exibicao da View.
    //
    //*******************************************************************************************//
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Define Activity como Fullscreen (sem barra de Menu ou de Status)
            getWindow( ).setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        // Define o Layout da pagina como em activity_menu_inicial.xml
            setContentView( R.layout.activity_menu_inicial );

        // Inicializa os elementos da página
            InicializaElementos( );


        // Inicializa elementos da variável global para uso posterior
            Global.ListaTurmas = new ArrayList< >( );
            Global.ListaExperimentos = new ArrayList< >( );
            Global.ListaGrupos = new ArrayList< >( );
            Global.ListaAluno = new ArrayList< >( );
    }



    //*** Método InicializaElementos:
    //
    //    Inicializacao das variáveis, associando-as aos elementos da view. Inicia as animacoes
    //
    //*********************************************************************************************//
    private void InicializaElementos( ){

        BotaoConect = (CircularProgressButton)findViewById(R.id.btnConectar);
        BotaoConect.setProgress(0);
        BotaoConect.setOnClickListener( this );


        BotaoConect = (CircularProgressButton)findViewById(R.id.btnÁreaProfessor);
        BotaoConect.setProgress(0);
        BotaoConect.setOnClickListener( this );


    }



    //*** Método onClick:
    //
    //    Captura cliques nos botoes e inicializa as respectivas activities
    //
    //*********************************************************************************************//
    public void onClick( View v ) {

        // Identifica qual botao foi clicado
            switch ( v.getId( ) ) {

                case R.id.btnConectar:

                    // Caso o botao conectar for clicado, declara intent e inicia activity Menu
                        Intent ClickConectar = new Intent( this, Conectar.class );
                        startActivity( ClickConectar );

                    break;

                case R.id.btnÁreaProfessor:

                    // Caso o botao sobre for clicado, declara intent e inicia activity ProfessorTurmas
                        Intent ClickSobre = new Intent( mActivity, ProfessorTurmas.class );
                        startActivity( ClickSobre );

                    break;
            }
    }
}
