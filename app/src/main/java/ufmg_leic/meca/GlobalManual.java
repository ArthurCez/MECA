package ufmg_leic.meca;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Jama.Matrix;
import Jama.Matrix.*;


public class GlobalManual {

    private static GlobalManual instance;

    ArrayList<Double> GlobalValoresAcc,
                      GlobalValoresInacc;

    Double CoefAngular,
           CoefLinear;

    Matrix MatrizA,
           MatrizAI,
           MatrizY,
           MatrixResultado;

    Boolean FlagAdd,
            FlagSub;

    int Position;


    public void DefineValoresAcc( ArrayList<Double> Valores ){

        GlobalValoresAcc = Valores;
    }

    public void DefineValoresInacc( ArrayList<Double> Valores ){

        GlobalValoresInacc = Valores;
    }

    // *** Para Instanceamento da Classe *** //
    public static synchronized GlobalManual getInstance(){

        if(instance==null){
            instance = new GlobalManual();
        }
        return(instance);
    }


    public void Calc( ){

        MatrizA = new Matrix( GlobalValoresAcc.size( ), 2 );
        MatrizY = new Matrix( GlobalValoresInacc.size( ), 1 );

        if( !( GlobalValoresAcc.isEmpty( ) || GlobalValoresInacc.isEmpty( ) ) ){

            for( int i = 0; i < GlobalValoresAcc.size( ); i++ ){
                MatrizA.set( i, 0, GlobalValoresAcc.get( i ) );
                MatrizA.set( i, 1, 1 );
            }

            for( int j = 0; j < GlobalValoresInacc.size( ); j++ ){
                MatrizY.set( j, 0, GlobalValoresInacc.get( j ) );
            }
        }

        MatrizAI = MatrizA.inverse( );

        MatrixResultado = MatrizAI.times( MatrizY );

        CoefAngular = MatrixResultado.get( 0, 0 );
        CoefLinear = MatrixResultado.get( 1, 0 );

    }


    public void Sort(  ){

        double Buffer = 0;

        int position = 0;

        if( GlobalValoresAcc.size() > 1 ) {

            for (int i = 0; i < (GlobalValoresAcc.size() - 1); i++) {

                for (int j = i; j < (GlobalValoresAcc.size() ); j++) {

                    if( j == (i) ){
                        Buffer = GlobalValoresAcc.get( j );
                        position = j;
                    }
                    else{
                        if( GlobalValoresAcc.get( j ) < Buffer ){
                            Buffer =  GlobalValoresAcc.get( j );
                            position = j;
                        }
                    }

                }

                if( position != i ){

                    Collections.swap( GlobalValoresAcc, position, i );
                    Collections.swap( GlobalValoresInacc, position, i );
                }
            }
        }
    }


}
