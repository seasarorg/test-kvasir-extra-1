package org.seasar.kvasir.cms.publish.impl;

import org.seasar.kvasir.page.PageUtils;

import net.skirnir.freyja.Attribute;
import net.skirnir.freyja.Element;
import net.skirnir.freyja.Macro;
import net.skirnir.freyja.TagEvaluator;
import net.skirnir.freyja.TagEvaluatorUtils;
import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.TemplateEvaluator;
import net.skirnir.freyja.VariableResolver;


public class LinkTagEvaluator
    implements TagEvaluator
{
    private static final String[] SPECIALATTRIBUTEPATTERNSTRINGS = new String[] {
        "href", "src" };

    private static final String[] SPECIALTAGPATTERNSTRINGS = new String[0];

    private static final String ATTR_HREF = "href";

    private static final String ATTR_SRC = "src";


    public String evaluate(TemplateContext context, String name,
        Attribute[] attributes, Element[] body)
    {
        String[] basePathTokens = PageUtils
            .tokenizePathname(((LinkTemplateContext)context).getBasePathname());

        for (int i = 0; i < attributes.length; i++) {
            Attribute attribute = attributes[i];
            if (ATTR_HREF.equals(attribute.getName())
                || ATTR_SRC.equals(attribute.getName())) {
                attributes[i] = new Attribute(attribute.getName(),
                    TagEvaluatorUtils.filter(PageUtils.toRelativePathname(
                        TagEvaluatorUtils.defilter(attribute.getValue()),
                        basePathTokens)), attribute.getQuote());
            }
        }
        return TagEvaluatorUtils.evaluate(context, name, attributes, body);
    }


    public Element expandMacroVariables(TemplateContext context,
        VariableResolver macroVarResolver, String name, Attribute[] attributes,
        Element[] body)
    {
        return null;
    }


    public void gatherMacroVariables(TemplateContext context,
        VariableResolver macroVarResolver, String name, Attribute[] attributes,
        Element[] body)
    {
    }


    public Macro getMacro(TemplateEvaluator evaluator, String name,
        Attribute[] attributes, Element[] body, String macroName,
        Element previousElement)
    {
        return null;
    }


    public String[] getSpecialAttributePatternStrings()
    {
        return SPECIALATTRIBUTEPATTERNSTRINGS;
    }


    public String[] getSpecialTagPatternStrings()
    {
        return SPECIALTAGPATTERNSTRINGS;
    }


    public TemplateContext newContext()
    {
        return new LinkTemplateContext();
    }
}
