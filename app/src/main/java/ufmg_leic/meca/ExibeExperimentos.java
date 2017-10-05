package ufmg_leic.meca;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ExibeExperimentos extends AppCompatActivity {

    Activity mActivity = this;

    ListView ListaExperimentos;

    ArrayList<String> ListaNomeExperimentos;

    ArrayAdapter<String> ListaNomeExperimentosAdapter;

    Intent CallAPI;

    GlobalSelect Global = GlobalSelect.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibe_experimentos);


        ListaExperimentos = (ListView)findViewById(R.id.ListaExperimentos);

        ListaNomeExperimentos = new ArrayList<>();
        ListaNomeExperimentosAdapter = new ArrayAdapter< >(this, android.R.layout.simple_list_item_1, ListaNomeExperimentos );
        ListaExperimentos.setAdapter(ListaNomeExperimentosAdapter );

        Global.Position = 10;
        CallAPI= new Intent(mActivity, GoogleAPIRequest.class);
        startActivityForResult( CallAPI, 2 );


        ListaExperimentos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String NomePasta;
                String subString[];

                for(int i = 0; i < Global.ListaAluno.size(); i++){
                    NomePasta = Global.ListaAluno.get(i);
                    subString = NomePasta.split("_");
                    if( subString[0].equals("MECA")){
                        if( subString[1].equals( ListaNomeExperimentos.get(position) ) ){
                            Global.SelectedSheet = NomePasta;
                        }
                    }
                }

                Intent Selected = new Intent( mActivity, Menu.class );
                startActivity(Selected);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String NomePasta;
        String subString[];

        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                ListaNomeExperimentos.clear();
                for(int i = 0; i < Global.ListaAluno.size(); i++){
                    NomePasta = Global.ListaAluno.get(i);
                    subString = NomePasta.split("_");
                    if(subString[0].equals("MECA")) ListaNomeExperimentos.add(subString[1]);
                }
                ListaNomeExperimentosAdapter.notifyDataSetChanged();
            }
        }
    }
}
