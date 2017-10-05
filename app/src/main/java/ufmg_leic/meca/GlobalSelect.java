package ufmg_leic.meca;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arthur on 17/07/2017.
 */

public class GlobalSelect {

    int Position;

    String NomeTurma,
           NomeTurmaToDelete,
           NomeTurmaSelecionada,
           NomeExperimento,
           NumGrupos,
           NomeExperimentoToDelete,
           NomeGrupoAntigo,
           NomeGrupoNovo,
           NomeGrupoAtual,
           Email;

    ArrayList<String> ListaTurmas,
                      ListaExperimentos,
                      ListaGrupos,
                      ListaAluno;

    String SelectedSheet;

    private static GlobalSelect instance;

    // *** Para Instanceamento da Classe *** //
    public static synchronized GlobalSelect getInstance(){

        if(instance==null){
            instance = new GlobalSelect();
        }
        return(instance);
    }

}