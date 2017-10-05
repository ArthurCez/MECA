package ufmg_leic.meca;


//*********   Imports    **********//


// Android
    import android.Manifest;
    import android.accounts.AccountManager;
    import android.app.Activity;
    import android.app.Dialog;
    import android.app.ProgressDialog;
    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.net.ConnectivityManager;
    import android.net.NetworkInfo;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.support.annotation.NonNull;
    import android.text.method.ScrollingMovementMethod;
    import android.view.ViewGroup;
    import android.widget.LinearLayout;
    import android.widget.TextView;

// Google API
    import com.google.android.gms.auth.GoogleAuthException;
    import com.google.android.gms.common.ConnectionResult;
    import com.google.android.gms.common.GoogleApiAvailability;
    import com.google.api.client.extensions.android.http.AndroidHttp;
    import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
    import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
    import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
    import com.google.api.client.http.HttpRequest;
    import com.google.api.client.http.HttpRequestInitializer;
    import com.google.api.client.http.HttpTransport;
    import com.google.api.client.json.JsonFactory;
    import com.google.api.client.json.jackson2.JacksonFactory;
    import com.google.api.client.util.ExponentialBackOff;
    import com.google.api.services.script.model.ExecutionRequest;
    import com.google.api.services.script.model.Operation;

// Java
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;
    import java.util.Map;

// EasyPermissions
    import pub.devrel.easypermissions.AfterPermissionGranted;
    import pub.devrel.easypermissions.EasyPermissions;



//*****************************************************//



/**********************************************************************************
 ***                                                                            ***
 ***  Activity: GoogleAPIRequest                                                ***
 ***  Funcao: Realiza a chamada da Execution API, desenvolvida pelo Google      ***
 ***          para executar remotamente, através do celular, scripts capazes    ***
 ***          de editar arquivos no google drive do usuário                     ***
 ***                                                                            ***
 ***  Esta atividade é uma adaptacao do exemplo de uso da Google Scripts        ***
 ***  execution API, disponível em:                                             ***
 ***  https://developers.google.com/apps-script/guides/rest/quickstart/android  ***
 ***                                                                            ***
 **********************************************************************************/

public class GoogleAPIRequest extends Activity implements EasyPermissions.PermissionCallbacks {


//***** Declaracao de Variáveis *****//


    GlobalSelect Global = GlobalSelect.getInstance( );

    GlobalGrafico GraficoGlobal = GlobalGrafico.getInstance( );

    GlobalMapeamento MapGlobal = GlobalMapeamento.getInstance();

    ProgressDialog mProgress;

    private TextView mOutputText;

    GoogleAccountCredential mCredential;

    static final int REQUEST_ACCOUNT_PICKER = 1000,
                     REQUEST_AUTHORIZATION = 1001,
                     REQUEST_GOOGLE_PLAY_SERVICES = 1002,
                     REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    List<String> SCOPES = Arrays.asList( "https://www.googleapis.com/auth/drive",
                                         "https://www.googleapis.com/auth/spreadsheets",
                                          "https://www.googleapis.com/auth/script.send_mail" );




//****** Implementacao dos Métodos *****//



    //*** Método OnCreate:
    //
    //    Executa quando a atividade é iniciada. Responsável pela criacao e exibicao da View.
    //
    //*******************************************************************************************//
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        // Define layout da atividade como Linear e define parametros do layout
            LinearLayout activityLayout = new LinearLayout( this );
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,
                                                                          LinearLayout.LayoutParams.MATCH_PARENT );
            activityLayout.setLayoutParams( lp );
            activityLayout.setOrientation( LinearLayout.VERTICAL );
            activityLayout.setPadding( 16, 16, 16, 16 );

        // Adiciona o LinearLayout a um novo viewGroup
            ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                     ViewGroup.LayoutParams.WRAP_CONTENT );

        // Define textview a ser adicionada a view
            mOutputText = new TextView( this );
            mOutputText.setLayoutParams( tlp );
            mOutputText.setPadding( 16, 16, 16, 16 );
            mOutputText.setVerticalScrollBarEnabled( true );
            mOutputText.setMovementMethod( new ScrollingMovementMethod( ) );

            String textoinicial = "Chamada a API Execution Google";
            mOutputText.setText( textoinicial );
            activityLayout.addView( mOutputText );


        // Cria uma nova caixa de dialogo do tipo ProgressDialog
            mProgress = new ProgressDialog( this );


        // O texto do ProgressDialog depende do script para o qual a atividade GoogleAPIRequest foi chamada. Para tanto,
        //    detectamos essa informacao (script desejado) pelaa variável Global.Position, que é definida pela atividade que chamou
        //    a classe GoogleAPIRequest
            switch( Global.Position ){

                case 0:
                    mProgress.setMessage( "Criando Turma..." );
                    break;

                case 1:
                    mProgress.setMessage( "Atualizando suas Turmas" );
                    break;

                case 2:
                    mProgress.setMessage( "Deletando turma " + Global.NomeTurmaToDelete );
                    break;

                case 3:
                    mProgress.setMessage( "Atualizando seus Experimentos" );
                    break;

                case 4:
                    mProgress.setMessage( "Cria novo Experimento" );
                    break;

                case 5:
                    mProgress.setMessage( "Deletando turma " + Global.NomeExperimentoToDelete );
                    break;

                case 6:
                    mProgress.setMessage( "Atualizando Grupos" );
                    break;

                case 7:
                    mProgress.setMessage( "Renomeando Grupo" );
                    break;

                case 8:
                    mProgress.setMessage( "Adicionando Autorizacao para " + Global.Email );
                    break;

                case 9:
                    mProgress.setMessage( "Enviando dados do Gráfico " );
                    break;

                case 10:
                    mProgress.setMessage( "Atualizando seus Experimentos" );
                    break;

                case 11:
                    mProgress.setMessage( "Adicionando Sala" );
                    break;
            }


        // Define a view da atividade como a implementada na variavel activityLayout
            setContentView( activityLayout );

        // Inicializa credenciais google e objeto de servico, utilizando os escopos requisitados pelo script
            mCredential = GoogleAccountCredential.usingOAuth2( getApplicationContext( ), SCOPES ).setBackOff( new ExponentialBackOff( ) );

        // Chama a funcao que obterá os resultados retornados pela API Execution
            ObtemResultadosdaAPI( );
    }



    //*** Objeto setHttpTimeout:
    //
    //    Extende o HttpRequestInitializer (geralmente um cobjeto de credentials) com instrucoes
    //    adicionais de Initialize( ). Recebe o inicializador a ser copiado e ajustado e retorna um
    //    inicializador com um timeout extendido
    //
    //*********************************************************************************************//
    private static HttpRequestInitializer setHttpTimeout( final HttpRequestInitializer requestInitializer ) {

        // Retorna novo Inicilizador do HttpRequest...
            return new HttpRequestInitializer( ) {


                @Override
                public void initialize( HttpRequest httpRequest )
                        throws IOException {

                            // Esta mudanca na chamada a API é usada para evitar o esgotamento de tempo
                            // ao executar o httpRequest, permitindo funcoes que executem por até 6 minutos
                            // (tempo máximo de execucao para qualquer script google), mais algum pequeno overhead

                                requestInitializer.initialize( httpRequest );
                                httpRequest.setReadTimeout( 380000 );
                        }
            };
    }



    //*** Método ObtemResultadoAPI:
    //
    //    Tentativa de chamar a API, após verificadas todas as pré-condicoes. Estas pré condicoes
    //    sao: Ter os servicos do Google Play instalados no celular, uma conta google selecionada
    //    e acesso a internet. Caso alguma destas requisicoes nao seja atendida, informa ao usuario
    //
    //************************************************************************************************//
    private void ObtemResultadosdaAPI( ) {

        // Caso os Google Play Services nao estiver disponivel...
            if ( !isGooglePlayServicesAvailable( ) ) {

                // Invoca método para obter o GooglePlayServices.
                    acquireGooglePlayServices( );
            }

        // Caso nao houver uma conta google selecionada...
            else if ( mCredential.getSelectedAccountName( ) == null ) {

                // Invoca metodo para selecao da conta
                    chooseAccount();
            }

        // Caso o dispositivo estiver offline...
            else if ( !isDeviceOnline( ) ) {

                // Informa a falta de conexao ao usuário
                    String InformaConexao = "Sem conexao a Internet";
                    mOutputText.setText( InformaConexao );
            }

        // Caso todas os requisitos forem atendidos...
            else {

                // Executa tarefa de chamada a API
                    new MakeRequestTask( mCredential ).execute( );
            }
    }



    //*** Método ChoseAccount:
    //
    //    Tenta acoplar a conta utilizada com as credenciais da API. Se uma conta foi préviamente
    //    salva ele a usará. Caso nao houver uma conta pré selecionada, um dialogbox com um seletor
    //    será dado ao usuário. Note que para configurar a conta ao uso do objeto de credenciais requer
    //    que este aplicativo tenha a permissao GET_ACCOUNTS, que é requisitada aqui, caso já nao esteja
    //    presente no arquivo manifest. A notacao AfterPermissionGranted garante que o método será
    //    reiniciado automaticamente assim que a permissao GET_ACCOUNTS for concedida
    //
    //************************************************************************************************//
    @AfterPermissionGranted( REQUEST_PERMISSION_GET_ACCOUNTS )
    private void chooseAccount( ) {

        // Caso o aplicativo tiver a permissao GET_ACCOUNTS...
            if ( EasyPermissions.hasPermissions( this, Manifest.permission.GET_ACCOUNTS ) ) {

                // Obtem o nome da conta do usuário
                    String accountName = getPreferences( Context.MODE_PRIVATE ).getString( PREF_ACCOUNT_NAME, null );

                // Se o valor nao for nulo ( havia uma conta previamente associada )...
                    if ( accountName != null ) {

                        // Seta a conta na credencial da API e chama ObtemResultadosdaAPI
                            mCredential.setSelectedAccountName( accountName );
                            ObtemResultadosdaAPI( );
                    }

                // Se nao havia conta previamente associada...
                    else {

                        // Prompta uma caixa de dialogo para insercao de nova conta
                            startActivityForResult( mCredential.newChooseAccountIntent( ), REQUEST_ACCOUNT_PICKER );
                    }
            }

        // Caso o app ainda nao possuir a permissao GET_ACCOUNTS...
            else {

                // Requisita a permissao GET_ACCOUNTS ao usuário através de um dialogbox
                EasyPermissions.requestPermissions( this,
                                                    "Este aplicativo necessita acesso a sua conta google (via Contatos).",
                                                    REQUEST_PERMISSION_GET_ACCOUNTS,
                                                    Manifest.permission.GET_ACCOUNTS );
            }
    }



    //*** Método OnActivityResult:
    //
    //    Chamada quando uma atividade lancada por esta classe (especificamente, AccountPicker e
    //    authorization) encerra, retornando um RequestCode o pelo qual ela foi iniciada além de
    //    qualquer dado adicional de sua opracao. Recebe o requestCode, indicando a atividade que foi
    //    encerrada, o ResultCode, indicando o resultado da atividade encerrada e o parametro data,
    //    contendo os dados resultados da operacao da atividade que retornou
    //
    //************************************************************************************************//
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );

        // Escuta o retorno de atividades
            switch( requestCode ) {

                // Caso esteja retornando da requisicao de googleplayservices...
                case REQUEST_GOOGLE_PLAY_SERVICES:

                    // E o resultado da request nao for de sucesso
                        if ( resultCode != RESULT_OK ) {

                            // Indica ao usuário que este aplicativo requer o googleplayservices
                                String GooglePlayMessage = "Este aplicativo requer o Google Play Services. Favor instalar " +
                                                           "o Google Play Services em seu dispositivo e reinicie este aplicativo.";
                                mOutputText.setText( GooglePlayMessage );
                        }

                    // Se o resultado da request for bem sucedido...
                        else {

                            // Continua a execucao chamando ObtemResultadosAPI
                                ObtemResultadosdaAPI( );
                        }

                   break;


                // Caso capture o retorno da atividade de selecao de contas...
                case REQUEST_ACCOUNT_PICKER:

                    // O resulado de retorno for de sucesso e houver dados de retorno...
                        if ( resultCode == RESULT_OK && data != null && data.getExtras() != null ) {

                            // Salva o nome da conta na string accountName
                                String accountName = data.getStringExtra( AccountManager.KEY_ACCOUNT_NAME );

                            // Se account name tiver uma string valida...
                                if ( accountName != null ) {

                                    // Aplica a accountName selecionada a credencial necessaria a API
                                        SharedPreferences settings = getPreferences( Context.MODE_PRIVATE );
                                        SharedPreferences.Editor editor = settings.edit( );
                                        editor.putString( PREF_ACCOUNT_NAME, accountName );
                                        editor.apply( );
                                        mCredential.setSelectedAccountName( accountName );

                                    // Continua a execucao pela chamada da funcao ObtemResultadosdaAPI
                                        ObtemResultadosdaAPI( );
                                }
                        }

                    break;


                // Caso detectado o retorno de pedido por autorizacao...
                case REQUEST_AUTHORIZATION:

                    // E a autorizacao houver sido concedida...
                        if ( resultCode == RESULT_OK ) {

                            // Continua a execucao pela chamada da funcao ObtemResultadosdaAPI
                                ObtemResultadosdaAPI( );
                        }

                    break;
            }
    }



    //*** Método OnRequestPermitsResult:
    //
    //    Respond a requisicoes de permissoes para API 23 ou superiores.
    //
    //************************************************************************************************//
    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[ ] permissions, @NonNull int[ ] grantResults ) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );

        EasyPermissions.onRequestPermissionsResult( requestCode, permissions, grantResults, this );
    }



    //*** Método OnPermissionGranted:
    //
    //    Callback de permissao garantida usando EasyPermissions. Nao executa nenhum código
    //
    //************************************************************************************************//
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {

    }



    //*** Método OnPermissionDenied:
    //
    //    Callback de permissao é negada usando EasyPermissions. Nao executa nenhum código
    //
    //************************************************************************************************//
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {

    }



    //*** Método isDeviceOnline:
    //
    //    Testa se o dispositivo tem acesso a internet. Retorna true caso afirmativo e false caso
    //    contrário
    //
    //************************************************************************************************//
    private boolean isDeviceOnline( ) {

        // Instancia objeto ConnectivityManager
            ConnectivityManager connMgr = ( ConnectivityManager )getSystemService( Context.CONNECTIVITY_SERVICE );

        // Instancia objeto NetworkInfo
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo( );

        // Retorna booleano com estado de conexao do dispositivo
            return ( networkInfo != null && networkInfo.isConnected( ) );
    }



    //*** Método isGooglePlayServicesAvailable:
    //
    //    Testa se o Google Play Services APK esta instalado e atualizado. Retorna true se há
    //    disponibilidade e false caso contrário
    //
    //************************************************************************************************//
    private boolean isGooglePlayServicesAvailable( ) {

        // Obtém instancia de GoogleApiAvailability
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance( );

        // Obtém codigo de conexao ao GooglePlayServices
            final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable( this );

        // Retorna o resultado da comparacao do codigo de conexao anterior a um código de conexao bem-sucedido
            return connectionStatusCode == ConnectionResult.SUCCESS;
    }



    //*** Método acquireGooglePlayServices
    //
    //    Tenta corrigir a falta de uma APK válida e atializada do GooglePlayServices através de
    //    uma caixa de diálogo, se possível for
    //
    //************************************************************************************************//
    private void acquireGooglePlayServices( ) {

        // Instancia objeto GoogleAPIAvailability
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        // Obtem o codigo de status da conexao com o servico
            final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable( this );

        // Testa se o problema de conexao apresenta um erro corrigível pelo usuário
            if ( apiAvailability.isUserResolvableError( connectionStatusCode ) ) {

                // Caso o seja, exibe mensagem de erro do Google Play Services para o erro identificado
                    showGooglePlayServicesAvailabilityErrorDialog( connectionStatusCode );
            }
    }



    //*** Método showGooglePlayServicesAvailabilityErrorDialog
    //
    //    Exibe uma caixa de dialogo de erro mostrando que o Googleplayservices nao está presente ou
    //    está desatualizado. Recebe como parametro o codigo de status da conexao com o servico
    //
    //************************************************************************************************//
    void showGooglePlayServicesAvailabilityErrorDialog( final int connectionStatusCode ) {

        // Obtem instancia do objeto GooglaApiAvailability
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance( );

        // Cria dialogo de erro provida pela api do googlw
            Dialog dialog = apiAvailability.getErrorDialog( GoogleAPIRequest.this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES );

        // Exibe dialogo de erro
            dialog.show( );
    }



    //*** Tarefa Assincrona: MakeRequestTask
    //
    //    Uma tarefa assíncrona que manuseia a API Google Script Execution. Colocar as chamadas a
    //    API em uma tarefa própria garente que a UI continue responsiva.
    //
    //************************************************************************************************//
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.script.Script mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.script.Script.Builder(
                    transport, jsonFactory, setHttpTimeout(credential))
                    .setApplicationName("Google Apps Script Execution API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Apps Script Execution API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Call the API to run an Apps Script function that returns a list
         * of folders within the user's root directory on Drive.
         *
         * @return list of String folder names and their IDs
         * @ throws IOException
         */
        private List<String> getDataFromApi()
                throws IOException, GoogleAuthException {
            // ID of the script to call. Acquire this from the Apps Script editor,
            // under Publish > Deploy as API executable.
            String scriptId = "MZpiJCDq7fPhrWAQCTacSp5jxIb0T_nRy";

            List<String> folderList = new ArrayList<>();

            String Buffer = "";


             /* ********* Legenda:
             *
             *  Se postition for 0: Executa script referente a Criacao de novas Turmas
             *  Se position for 1: Executa script referente a exibicao das turmas vigentes
             *  Se position for 2: Executa script referente a rotina de Apagar uma turma
             *
             ****************************************************************/


            if( Global.Position == 0 ) Buffer = "CriaTurma";
            if( Global.Position == 1 ) Buffer = "LerTurmas";
            if( Global.Position == 2 ) Buffer = "DeletarPasta";
            if( Global.Position == 3 ) Buffer = "LerExperimentos";
            if( Global.Position == 4 ) Buffer = "CriaExperimento";
            if( Global.Position == 5 ) Buffer = "DeletarExperimento";
            if( Global.Position == 6 ) Buffer = "LerGrupos";
            if( Global.Position == 7 ) Buffer = "RenomearGrupo";
            if( Global.Position == 8 ) Buffer = "AdicionarEditor";
            if( Global.Position == 9 ) Buffer = "RecebeDadoGrafico";
            if( Global.Position == 10 ) Buffer = "LerExperimentosAluno";
            if( Global.Position == 11 ) Buffer = "AddSala";



            // Create an execution request object.
            ExecutionRequest request = new ExecutionRequest()
                    .setFunction(Buffer);



            if( Global.Position == 0 ) {
                List<Object> parameters = new ArrayList<>();
                String TurmaNome = "MECA_" + Global.NomeTurma;
                parameters.add(TurmaNome);
                request.setParameters(parameters);
            }

            if( Global.Position == 2 ) {
                List<Object> parameters = new ArrayList<>();
                String ToDeleteName = Global.NomeTurmaToDelete;
                parameters.add(ToDeleteName);
                request.setParameters(parameters);
            }

            if( Global.Position == 3 ) {
                List<Object> parameters = new ArrayList<>();
                String TurmaNome = Global.NomeTurmaSelecionada;
                parameters.add(TurmaNome);
                request.setParameters(parameters);
            }

            if( Global.Position == 4 ) {
                List<Object> parameters = new ArrayList<>();
                String TurmaNome = Global.NomeTurmaSelecionada;
                String NomeEXP = Global.NomeExperimento;
                String Num = Global.NumGrupos;
                parameters.add(TurmaNome);
                parameters.add(NomeEXP);
                parameters.add(Num);
                request.setParameters(parameters);
            }

            if( Global.Position == 5 ) {
                List<Object> parameters = new ArrayList<>();
                String TurmaNome = Global.NomeTurmaSelecionada;
                String NomeEXP = Global.NomeExperimentoToDelete;
                parameters.add(TurmaNome);
                parameters.add(NomeEXP);
                request.setParameters(parameters);
            }

            if( Global.Position == 6 ) {
                List<Object> parameters = new ArrayList<>();
                String TurmaNome = Global.NomeTurmaSelecionada;
                String NomeEXP = Global.NomeExperimento;
                parameters.add(TurmaNome);
                parameters.add(NomeEXP);
                request.setParameters(parameters);
            }

            if( Global.Position == 7 ) {
                List<Object> parameters = new ArrayList<>();
                String TurmaNome = Global.NomeTurmaSelecionada;
                String NomeEXP = Global.NomeExperimento;
                parameters.add(TurmaNome);
                parameters.add(NomeEXP);
                parameters.add(Global.NomeGrupoAntigo);
                parameters.add(Global.NomeGrupoNovo);
                request.setParameters(parameters);
            }

            if( Global.Position == 8 ) {
                List<Object> parameters = new ArrayList<>();
                String TurmaNome = Global.NomeTurmaSelecionada;
                String NomeEXP = Global.NomeExperimento;
                parameters.add(TurmaNome);
                parameters.add(NomeEXP);
                parameters.add(Global.NomeGrupoAtual);
                parameters.add(Global.Email);
                request.setParameters(parameters);
            }


            if( Global.Position == 9 ) {
                List<Object> parameters = new ArrayList<>();
                parameters.add(GraficoGlobal.JSONarray.toString());
                parameters.add(Global.SelectedSheet);
                request.setParameters(parameters);
            }

            if( Global.Position == 11 ) {
                List<Object> parameters = new ArrayList<>();
                parameters.add(Global.SelectedSheet);
                parameters.add(MapGlobal.Instituicao);
                parameters.add(MapGlobal.Unidade);
                parameters.add(MapGlobal.Sala);
                parameters.add(MapGlobal.NumLum);
                parameters.add(MapGlobal.NumLamp);
                parameters.add(MapGlobal.Potencia);
                parameters.add(MapGlobal.Largura);
                parameters.add(MapGlobal.Profundidade);
                parameters.add( MapGlobal.MediaSala );
                parameters.add(MapGlobal.Y);
                parameters.add(MapGlobal.X);
                parameters.add(MapGlobal.JSONarray.toString());
                request.setParameters(parameters);
            }



            // Make the request.
            Operation op =
                    mService.scripts().run(scriptId, request).execute();


            Global.ListaTurmas = new ArrayList<>();
            Global.ListaExperimentos = new ArrayList<>();
            Global.ListaGrupos = new ArrayList<>();
            Global.ListaAluno = new ArrayList<>();


            // Print results of request.
            if (op.getError() != null) {
                throw new IOException(getScriptError(op));
            }
            if (op.getResponse() != null && op.getResponse().get("result") != null) {
                Map<String, String> folderSet = (Map<String, String>)(op.getResponse().get("result"));

                for (String id: folderSet.keySet()) {
                    if(Global.Position == 1 )Global.ListaTurmas.add(String.format("%s ", folderSet.get(id)));
                    if(Global.Position == 3 )Global.ListaExperimentos.add(String.format("%s ", folderSet.get(id)));
                    if(Global.Position == 6 )Global.ListaGrupos.add(String.format("%s ", folderSet.get(id)));
                    if(Global.Position == 10 )Global.ListaAluno.add(String.format("%s", folderSet.get(id)));
                }
            }

            return folderList;
        }

        /**
         * Interpret an error response returned by the API and return a String
         * summary.
         *
         * @param op the Operation returning an error response
         * @return summary of error response, or null if Operation returned no
         *     error
         */
        private String getScriptError(Operation op) {
            if (op.getError() == null) {
                return null;
            }

            // Extract the first (and only) set of error details and cast as a Map.
            // The values of this map are the script's 'errorMessage' and
            // 'errorType', and an array of stack trace elements (which also need to
            // be cast as Maps).
            Map<String, Object> detail = op.getError().getDetails().get(0);
            List<Map<String, Object>> stacktrace =
                    (List<Map<String, Object>>)detail.get("scriptStackTraceElements");

            StringBuilder sb =
                    new StringBuilder("\nScript error message: ");
            sb.append(detail.get("errorMessage"));

            if (stacktrace != null) {
                // There may not be a stacktrace if the script didn't start
                // executing.
                sb.append("\nScript error stacktrace:");
                for (Map<String, Object> elem : stacktrace) {
                    sb.append("\n  ");
                    sb.append(elem.get("function"));
                    sb.append(":");
                    sb.append(elem.get("lineNumber"));
                }
            }
            sb.append("\n");
            return sb.toString();
        }


        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();

            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);

            finish();
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleAPIRequest.REQUEST_AUTHORIZATION);
                } else {
                    String following = "The following error occurred:\n";

                    mOutputText.setText("The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                finish();
                String Cancelled = "Request cancelled.";
                mOutputText.setText(Cancelled);
            }
        }
    }
}