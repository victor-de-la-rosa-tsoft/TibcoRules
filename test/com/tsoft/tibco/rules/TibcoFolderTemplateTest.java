package com.tsoft.tibco.rules;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TibcoFolderTemplateTest {

    private TibcoFolderTemplate template;

    @Before
    public void setUp() throws Exception {
        this.template = new TibcoFolderTemplate("");

    }


    @Test
    public void firstLevelPathIsValid() {
        String path = "/AESchema";

        TibcoFolderTemplate.ValidResult result = this.template.validPath(path);

        assertEquals(true, result.getResult());
    }

    @Test
    public void secondLevelPathIsValid() {
        String path = "/Process/MAIN";

        TibcoFolderTemplate.ValidResult result = this.template.validPath(path);

        assertEquals(true, result.getResult());
    }

    @Test
    public void firstLevelPathNotValid() {
        String path = "/AnyPath";

        TibcoFolderTemplate.ValidResult result = this.template.validPath(path);

        assertEquals(false, result.getResult());
        assertEquals("carpeta no valida en path", result.getMessage());
    }

    @Test
    public void secondLevelPathBadCase() {
        String path = "/Process/main";

        TibcoFolderTemplate.ValidResult result = this.template.validPath(path);

        assertEquals(false, result.getResult());
        assertEquals("nombre carpeta no cumple con mayuculas y minusculas", result.getMessage());

    }

    @Test
    public void correctFileTypeShouldReturnTrue() {
        String path = "/Process/MAIN/a.process";

        TibcoFolderTemplate.ValidResult result = this.template.validPath(path);

        assertEquals(true, result.getResult());

    }


}