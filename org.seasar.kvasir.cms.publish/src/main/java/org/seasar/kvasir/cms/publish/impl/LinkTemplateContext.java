package org.seasar.kvasir.cms.publish.impl;

import net.skirnir.freyja.impl.TemplateContextImpl;


public class LinkTemplateContext extends TemplateContextImpl
{
    private String basePathname_;


    public String getBasePathname()
    {
        return basePathname_;
    }


    public void setBasePathname(String basePathname)
    {
        basePathname_ = basePathname;
    }
}
