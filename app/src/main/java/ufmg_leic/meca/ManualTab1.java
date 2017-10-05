package ufmg_leic.meca;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.graphics.Rect;

//*** android ***//
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

//*** com.jjoe64.graphview.GraphView ***//
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


public class ManualTab1 extends Fragment implements View.OnClickListener {

    private ConstraintLayout mRoot;

    TextInputLayout InputAcc,
                    InputInacc;

    ListView ListaAcc,
             ListaInacc;

    Button Adicionar;

    ArrayAdapter<Double> ListaAdapterAcc,
                         ListaAdapterInacc;

    ArrayList<Double> ValoresAcc,
                      ValoresInacc;

    String InputStringAcc,
           InputStringInacc;

    View rootView;

    GlobalManual Global;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_manual_tab1, container, false);

        InicializaElementos( );

        SincronizaScroll( );

        return rootView;
    }


    private void InicializaElementos( ){

        InputAcc = (TextInputLayout)rootView.findViewById(R.id.InputAcc);

        InputInacc = (TextInputLayout)rootView.findViewById( R.id.InputInacc );

        ValoresAcc = new ArrayList<>();

        ValoresInacc = new ArrayList<>();

        ListaAdapterAcc = new ArrayAdapter<Double>(getActivity( ), android.R.layout.simple_list_item_1, ValoresAcc );

        ListaAdapterInacc = new ArrayAdapter<Double>(getActivity( ), android.R.layout.simple_list_item_1, ValoresInacc );

        ListaAcc = ( ListView )rootView.findViewById( R.id.listView1 );
        ListaAcc.setAdapter( ListaAdapterAcc );
        ListaAcc.setOnItemClickListener( OnClickLista( getActivity().getApplicationContext() ) );

        ListaInacc = ( ListView )rootView.findViewById( R.id.listView2 );
        ListaInacc.setAdapter( ListaAdapterInacc );
        ListaInacc.setOnItemClickListener( OnClickLista( getActivity().getApplicationContext() ) );

        Adicionar = (Button)rootView.findViewById( R.id.BotaoAdd );
        Adicionar.setOnClickListener( this );

        Global = GlobalManual.getInstance( );

    }


    public void SincronizaScroll( ){

        ListaAcc.setOnTouchListener( new OnTouchListener( ) {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                ListaInacc.dispatchTouchEvent(arg1);
                return false;
            }
        });
        ListaAcc.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }
            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
                if (ListaAcc.getChildAt(0) != null) {
                    Rect r = new Rect();
                    ListaAcc.getChildVisibleRect(ListaAcc.getChildAt(0), r, null);
                    ListaInacc.setSelectionFromTop(ListaAcc.getFirstVisiblePosition(), r.top);
                }
            }
        });

        ListaInacc.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }
            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
                if (ListaInacc.getChildAt(0) != null) {
                    Rect r = new Rect();
                    ListaInacc.getChildVisibleRect(ListaInacc.getChildAt(0), r, null);
                    ListaAcc.setSelectionFromTop(ListaInacc.getFirstVisiblePosition(), r.top);
                }
            }
        });

    }


    public void onClick( View v ) {

        // Identifica qual botao foi clicado
        switch ( v.getId( ) ) {

            case R.id.BotaoAdd:

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

                InputStringAcc = InputAcc.getEditText().getText().toString();
                InputAcc.getEditText().setText("");

                InputStringInacc = InputInacc.getEditText().getText().toString();
                InputInacc.getEditText().setText("");

                if( isNumeric( InputStringAcc ) && isNumeric( InputStringInacc ) ) {

                    ValoresAcc.add( Double.parseDouble( InputStringAcc ) );
                    ValoresInacc.add( Double.parseDouble( InputStringInacc ) );

                    ListaAdapterAcc.notifyDataSetChanged( );
                    ListaAdapterInacc.notifyDataSetChanged( );


                    if( ValoresAcc.size() > 6 ){
                        ListaAcc.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                        ListaInacc.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

                        ListaAcc.setStackFromBottom(true);
                        ListaInacc.setStackFromBottom(true);
                    }

                    Global.DefineValoresAcc( ValoresAcc );
                    Global.DefineValoresInacc( ValoresInacc );

                    Global.Sort();

                    if( ValoresAcc.size() > 1 ){
                        Global.Calc( );
                    }

                    Global.FlagAdd = true;
                }

                else{
                    Snackbar.make(rootView, "Dados Inválidos. Insira novamente.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

                break;

        }
    }


    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }


    public AdapterView.OnItemClickListener OnClickLista(final Context context) {
        return (new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long id) {

                ValoresAcc.remove( position );
                ValoresInacc.remove( position );

                ListaAdapterAcc.notifyDataSetChanged();
                ListaAdapterInacc.notifyDataSetChanged();

                if( ValoresAcc.size() < 6 ){
                    ListaAcc.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                    ListaInacc.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

                    ListaAcc.setStackFromBottom(false);
                    ListaInacc.setStackFromBottom(false);
                }

                Global.DefineValoresAcc( ValoresAcc );
                Global.DefineValoresInacc( ValoresInacc );

                Global.Sort();

                if( ValoresAcc.size() > 1 ){
                    Global.Calc( );
                }

                Global.FlagSub = true;
                Global.Position = position;
            }
        });
    }
}
