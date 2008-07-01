package org.seasar.kvasir.cms.publish.impl;

import org.seasar.kvasir.util.io.IOUtils;

import junit.framework.TestCase;


public class PublishPluginImplTest extends TestCase
{
    private PublishPluginImpl target_ = new PublishPluginImpl();


    public void testToRelativePathnamesInCSS()
        throws Exception
    {
        assertEquals(IOUtils.readString(getClass().getResourceAsStream(
            "PublishPluginImplTest_toRelativePathnameInCSS_expected.txt"),
            "UTF-8", false), target_.toRelativePathnamesInCSS(IOUtils
            .readString(getClass().getResourceAsStream(
                "PublishPluginImplTest_toRelativePathnameInCSS.txt"), "UTF-8",
                false), "/a/b/c.html"));
    }


    public void testToRelativePathnamesInHTML()
        throws Exception
    {
        assertEquals(IOUtils.readString(getClass().getResourceAsStream(
            "PublishPluginImplTest_toRelativePathnameInHTML_expected.txt"),
            "UTF-8", false), target_.toRelativePathnamesInHTML(IOUtils
            .readString(getClass().getResourceAsStream(
                "PublishPluginImplTest_toRelativePathnameInHTML.txt"), "UTF-8",
                false), "/a/b/c.html"));
    }
}
