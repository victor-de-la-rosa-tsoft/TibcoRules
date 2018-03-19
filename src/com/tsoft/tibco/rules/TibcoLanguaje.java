package com.tsoft.tibco.rules;

import java.io.File;
import java.util.regex.Pattern;

public class TibcoLanguaje {

    private static Pattern atomico = Pattern.compile("^(ws|int|intws)CL1(SP|MD|WS|WL|WY|DP).*");

    public static boolean isAtomicoService(String serviceName) {


        if(atomico.matcher(serviceName).matches()) {
            return true;
        }

        return false;
    }

    public static String getServiceName(File file) {

        //TODO identificar servicio SOAP o REST
        return null;
    }
}
