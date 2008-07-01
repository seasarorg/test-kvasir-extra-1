package org.seasar.kvasir.cms.publish.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.cms.publish.PublishPlugin;
import org.seasar.kvasir.cms.publish.setting.PublishPluginSettings;
import org.seasar.kvasir.cms.util.ServletUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.MimeUtils;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;
import org.seasar.kvasir.webapp.util.Response;

import net.skirnir.freyja.TemplateEvaluator;


public class PublishPluginImpl extends AbstractPlugin<PublishPluginSettings>
    implements PublishPlugin
{
    private static final String NAME_ROOT = "ROOT";

    private static final String NAME_NODE = "index.html";

    private static final String MEDIATYPE_CSS = "text/css";

    private static final Pattern CSS_URL_PATTERN = Pattern
        .compile("url\\('([^']*)'\\)|url\\(\"([^\"]*)\"\\)|url\\(([^\\)]*)\\)");

    private PageAlfr pageAlfr_;

    private TemplateEvaluator templateEvaluator_ = new TemplateEvaluator(
        new LinkTagEvaluator(), null);


    @Override
    public Class<PublishPluginSettings> getSettingsClass()
    {
        return PublishPluginSettings.class;
    }


    @Binding(bindingType = BindingType.MUST)
    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    public void publish(final Resource dir, final Page page, boolean recursive)
    {
        final String name;
        if (page.isRoot()) {
            name = NAME_ROOT;
        } else {
            name = page.getName();
        }

        Resource resource;
        if (page.isNode()) {
            Resource theDir = dir.getChildResource(name);
            theDir.mkdirs();
            resource = theDir.getChildResource(NAME_NODE);
        } else {
            resource = dir.getChildResource(name);
        }

        Response response = ServletUtils.getResponse(ServletUtils.getURL(page));
        String basePath = ServletUtils.getURI(page);
        if (MimeUtils.isHTML(response.getMediaType())) {
            convertURLsInHTML(response, basePath);
        } else if (isCSS(response.getMediaType())) {
            convertURLsInCSS(response, basePath);
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            is = response.getInputStream();
            os = resource.getOutputStream();
            IOUtils.pipe(is, os);
            is = null;
            os = null;
        } catch (ResourceNotFoundException ex) {
            throw new IORuntimeException(ex);
        } catch (IOException ex) {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }

        if (recursive) {
            Page[] pages = page.getChildren(new PageCondition()
                .setIncludeConcealed(false).setUser(
                    pageAlfr_.getPage(User.class, User.ID_ANONYMOUS_USER))
                .setPrivilege(Privilege.ACCESS_VIEW));
            for (int i = 0; i < pages.length; i++) {
                publish(dir.getChildResource(name), pages[i], recursive);
            }
        }
    }


    boolean isCSS(String mediaType)
    {
        return MEDIATYPE_CSS.equals(mediaType);
    }


    void convertURLsInCSS(Response response, String basePathname)
    {
        response.setString(toRelativePathnamesInCSS(response.getString(), basePathname));
    }


    String toRelativePathnamesInCSS(String css, String basePathname)
    {
        String[] basePathnameTokens = PageUtils.tokenizePathname(basePathname);

        StringBuilder sb = new StringBuilder();
        Matcher matcher = CSS_URL_PATTERN.matcher(css);
        int pre = 0;
        while (matcher.find()) {
            // 3つある括弧のうちマッチしているものを取得する。
            int start = matcher.start(1);
            int end = matcher.end(1);
            if (start == -1) {
                start = matcher.start(2);
                end = matcher.end(2);
                if (start == -1) {
                    start = matcher.start(3);
                    end = matcher.end(3);
                }
            }

            sb.append(css.substring(pre, start)).append(
                PageUtils.toRelativePathname(css.substring(start, end),
                    basePathnameTokens)).append(
                css.substring(end, matcher.end()));

            pre = matcher.end();
        }
        sb.append(css.substring(pre, css.length()));

        return sb.toString();
    }


    void convertURLsInHTML(Response response, String basePathname)
    {
        response.setString(toRelativePathnamesInHTML(response.getString(),
            basePathname));

    }


    String toRelativePathnamesInHTML(String html, String basePathname)
    {
        LinkTemplateContext context = (LinkTemplateContext)templateEvaluator_
            .newContext();
        context.setBasePathname(basePathname);
        return templateEvaluator_.evaluate(context, new StringReader(html));
    }


    protected boolean doStart()
    {
        return true;
    }


    protected void doStop()
    {
    }
}
