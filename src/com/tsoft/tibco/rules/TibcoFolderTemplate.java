package com.tsoft.tibco.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Permite construir una estructura de carpetas y tipos de archivo permitidos y validar un path contra la estructura.
 *
 */
class TibcoFolderTemplate {

    /** lista de subcarpetas */
    private HashMap<String, TibcoFolderTemplate> subfolders = new HashMap<String, TibcoFolderTemplate>();

    /** lista de tipos archivo aceptados */
    private ArrayList<Pattern> acceptedTypes = new ArrayList<Pattern>();

    /** carpeta actual */
    private String folderName;

    /**
     * Constructor
     * @param foldername
     */
    public TibcoFolderTemplate(String foldername) {
        this.folderName = foldername;
    }

    public void addSubFolder(TibcoFolderTemplate folder) {
        this.subfolders.put(folder.getName(), folder);
    }

    private String getName() {
        return this.folderName;
    }

    public TibcoFolderTemplate accepterTypes(String... types) {
        for(String type: types) {
            this.acceptedTypes.add(Pattern.compile(type));
        }

        return this;
    }

    /**
     * Valida un path contra esta plantilla y sub plantillas
     *
     * @param path
     * @return
     */
    public ValidResult validPath(String path) {

        ValidResult validationResult = new ValidResult(false, "");

        String[] pathElements = path.split("/" );

        //retira separador inicial
        if(path.indexOf("/") == 0) {
            path = path.substring(1);
        }

        String pathElement = path;
        String pendingPath = "";

        if(path.indexOf("/") > -1) {
            pathElement = path.substring(0, path.indexOf("/"));
            pendingPath = path.substring(path.indexOf("/"));
        }

        //valida archivo
        if(pendingPath.equals("")) {

            //valida archivo
            for(Pattern filePattern : this.acceptedTypes) {
                if(!filePattern.matcher(pathElement).matches()) {
                    return new ValidResult(false, String.format("Archivo %s no esperado en ruta %s", pathElement, path));
                }
            }
        }
        //valida subcarpeta
        else if(subfolders.keySet().contains(pathElement)) {
            subfolders.get(pathElement).validPath(pendingPath);
        }

        return null;

    }

    private boolean validaSubFolderIgnoreCase(String path) {
        for(String subfolder : this.subfolders.keySet()) {
            if(!subfolder.equalsIgnoreCase(path)) {
                return false;
            }
        }
        return true;
    }

    class ValidResult {
        private boolean result;
        private String message;

        public ValidResult(boolean result, String message) {
            this.result = result;
            this.message = message;
        }


        public boolean getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }
    }
}
