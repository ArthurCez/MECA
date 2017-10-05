package ufmg_leic.meca;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Arthur on 18/08/2017.
 */

public class GlobalMapeamento {

    private static GlobalMapeamento instance;

    double Largura,
           Profundidade,
           MediaSala,
           DensidadeSala,
           Potencia;

    String Instituicao,
           Unidade;

    JSONArray JSONarray = new JSONArray();

    double [][] MatrizMedias;

    int X,
        Y,
        Sala,
        NumLum,
        NumLamp;

    int[] Quad;

    double UltimaMedida;

    int [][] Mapa;

    // *** Para Instanceamento da Classe *** //
    public static synchronized GlobalMapeamento getInstance(){

        if(instance==null){
            instance = new GlobalMapeamento();
        }
        return(instance);
    }

    public void CalculaDensidade(){
        DensidadeSala = MediaSala / ( Largura * Profundidade );
    }
}
