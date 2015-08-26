package com.comcast.zucchini;

public class ZucchiniUtils
{
    /**
     * This is a shortcut to extract the name from the TestContext.
     */
    public static String tcname() {
        TestContext tc = TestContext.getCurrent();
        if(tc != null)
            return tc.name();
        else
            return "<NULL>";
    }
}
