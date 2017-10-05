package ufmg_leic.meca;

import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arthur on 04/08/2017.
 */

public class GlobalBean {


    private static GlobalBean instance;

    Bean bean;

    BeanListener beanListener;


    public void InicializaBean ( Bean beanbuffer ){

        bean = beanbuffer;
    }


    // *** Para Instanceamento da Classe *** //
    public static synchronized GlobalBean getInstance(){

        if(instance==null){
            instance = new GlobalBean();
        }
        return(instance);
    }

    // *** Para Instanceamento da Classe *** //
    public static synchronized GlobalBean setInstance(){

        instance = new GlobalBean();

        return(instance);
    }
}
