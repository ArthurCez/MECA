package ufmg_leic.meca;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.util.ArrayList;
import java.util.List;

import static ufmg_leic.meca.BeanocularActivity.MY_PERMISSIONS_REQUEST;


public class Conectar extends AppCompatActivity implements View.OnClickListener{


    private static final int REQUEST_ENABLE_BT = 1;

    Activity mActivity = this;

    Button Buscar;

    String action;

    int bluetoothState;

    IntentFilter filter;

    ListView ListaNomes;

    ArrayAdapter<String> ListaAdapter;

    ArrayList<String> NomesBeans = new ArrayList<>();

    List<Bean> beans = new ArrayList<>();

    GlobalBean BeanGlobal = GlobalBean.setInstance();




    BeanDiscoveryListener Listener = new BeanDiscoveryListener( ) {

        // ******** Método OnBeanDiscovered: Executa ao encontrar um bean ******* //

        @Override
        public void onBeanDiscovered(Bean bean, int rssi) {

            // Se o Bean encontrado ainda nao estiver na lista de beans encontrados
            if(!beans.contains(bean)){

                // Adiciona o nome do bean a lista de nomes dos beans encontrados
                NomesBeans.add(bean.getDevice().getName() + "   ");
                ListaAdapter.notifyDataSetChanged();
            }

            // Adiciona bean a lista de beans encontrados
            beans.add(bean);

        }

        // ******** Método OnDiscoveryComplete: Executa terminar a busca ******* //

        // EM DESENVOLVIMENTO
        @Override
        public void onDiscoveryComplete( ) {

            Buscar.setEnabled( true );
            //Buscar.setVisibility( View.VISIBLE );

        }

    };

    BeanListener beanListener = new BeanListener() {

        // ******** Método onReadRemoteRssi: Vazio ******* //

        @Override
        public void onReadRemoteRssi(int Rssi){
        }


        // ******** Método onConnected: Executa ao conectar com o Bean ******* //

        @Override
        public void onConnected() {

            Intent intent = new Intent(mActivity, ExibeExperimentos.class);
            startActivity(intent);
            finish();
        }


        // ******** Método onError: Vazio ******* //

        // EM DESENVOLVIMENTO

        @Override
        public void onError(BeanError berr){
            System.out.println("Bean has errors..");
        }



        // ******** Método onConnectionFailed: Vazio ******* //

        // EM DESENVOLVIMENTO

        @Override
        public void onConnectionFailed(){
            System.out.println("Bean connection failed");

            //Inserir aqui dialog box com pedido de refresh da activity
        }



        // ******** Método onDisconnect: Vazio ******* //

        // EM DESENVOLVIMENTO

        @Override
        public void onDisconnected(){
            System.out.println("Bean disconnected");
            Intent intent = new Intent(mActivity, MenuInicial.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        // ******** Método onScratchValueChanged: Indica em Console ******* //

        @Override
        public void onScratchValueChanged(ScratchBank bank, byte[] value){
            System.out.println("Bean scratch value changed");
        }

        // ******** Método onSerialMessageReceived: Indica em console ******* //

        @Override
        public void onSerialMessageReceived(byte[] data){
            System.out.println("data received: " + data.toString());
        }

    };


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );


        ArrayList<String> arrPerm = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.BLUETOOTH_ADMIN);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.BLUETOOTH);
        }
        if(!arrPerm.isEmpty()) {
            String[] permissions = new String[arrPerm.size()];
            permissions = arrPerm.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
        }
        

        // Coloca atividade em Fullscreen
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView( R.layout.activity_conectar );

        // Define um filtro para captar mudancas de estado Enabled/Disabled do Bluetooth
            filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);

        // Inicializa elementos
            InicializaElementos();

        // Inicia operacoes com Bluetooth
            HandleBluetoothOperations( );
    }




    private void InicializaElementos( ){

        Buscar = ( Button )findViewById( R.id.BotaoBuscar );
        Buscar.setOnClickListener( this );
        Buscar.setEnabled( false );
        //Buscar.setVisibility( View.INVISIBLE );

        ListaNomes = ( ListView )findViewById( R.id.ListView );

        ListaAdapter = new ArrayAdapter< >(this, android.R.layout.simple_list_item_1, NomesBeans );

        ListaNomes.setAdapter( ListaAdapter );

        // Cria OnClickListener para a lista
         ListaNomes.setOnItemClickListener( OnClickLista( this ) );

    }




    private void HandleBluetoothOperations( ){

        // Obtem o adaptador bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Se bluetooth está desligado, requisita ao usuário que o ligue
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            // OBS: Se o usuário aceitar ligar o bluetooth, a classe BroadcastReceiver é acionada
            //      para iniciar a busca automaticamente.
        }

        // Se bluetooth já está ligado, inicia a busca por beans
        else{
            BeanManager.getInstance().setScanTimeout(15);
            BeanManager.getInstance().startDiscovery(Listener);
        }

    }




    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            action = intent.getAction();

            // Executa as acoes neste laco apenas se o estado Enabled/Disabled do adaptador bluetooth mudar
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                // Já que o estado do Bluetooth foi alterado pelo user, testamos se ele foi ligado ou  desligado
                switch (bluetoothState) {

                    // Se foi ligado...
                    case BluetoothAdapter.STATE_ON:
                        //... Inicia a busca por beans
                        BeanManager.getInstance().setScanTimeout(15);
                        BeanManager.getInstance().startDiscovery(Listener);
                        break;
                }
            }
        }
    };




    // ******** Classe OnClickLista: Detecta clique em bean listado e faz a conexao ********//

    public AdapterView.OnItemClickListener OnClickLista(final Context context){
        return(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long id) {

                //*** Declaracao de variáveis ***//

                Bean beanbuffer;

                //*** Corpo do Programa ***//

                // Variável "bean" recebe o bean da posicao selecionada
                beanbuffer = beans.get(position);

                BeanGlobal.bean = beanbuffer;

                // Efetua conexao com o bean selecionado
                BeanGlobal.bean.connect(mActivity, beanListener);

            }
        });
    }


    public void onClick( View v ) {

        // Identifica qual botao foi clicado
        switch ( v.getId( ) ) {

            case R.id.BotaoBuscar:
                Buscar.setEnabled( false );
                //Buscar.setVisibility( View.INVISIBLE );

                BeanManager.getInstance().setScanTimeout(15);
                BeanManager.getInstance().startDiscovery(Listener);
                break;
        }
    }
}
