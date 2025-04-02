package org.cdc.test;

import org.cdc.framework.utils.ColorUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest {
    @Test
    public void testHueColor(){
        Assertions.assertEquals(ColorUtils.colorHue("helloworld"),"{BKY_helloworld_HUE}");
    }
}
