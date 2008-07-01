package org.seasar.kvasir.cms.publish.publish.web;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.cms.manage.manage.web.MainPanePage;
import org.seasar.kvasir.cms.publish.PublishPlugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.ThrowableUtils;
import org.seasar.kvasir.util.io.impl.ZipWriterResource;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class PublishPagePage extends MainPanePage
{
    private static final String PAGETAB_PUBLISHPAGE = "publish";

    private static final String ROOT_NAME = "site";

    private PublishPlugin publishPlugin_;

    private HttpServletResponse response_;

    private boolean recursive_;


    @Binding(bindingType = BindingType.MUST)
    public void setPublishPlugin(PublishPlugin publishPlugin)
    {
        publishPlugin_ = publishPlugin;
    }


    @Binding(bindingType = BindingType.MUST)
    public void setResponse(HttpServletResponse response)
    {
        response_ = response;
    }


    public void setRecursive(boolean recursive)
    {
        recursive_ = recursive;
    }


    public Object do_execute()
    {
        enableTab(PAGETAB_PUBLISHPAGE);
        enableLocationBar(true);

        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            if (publishPage()) {
                return null;
            }
        }

        return "/publish-page.html";
    }


    boolean publishPage()
    {
        Page page = getPage();
        if (page == null) {
            return false;
        }

        String name;
        if (page.isRoot()) {
            name = ROOT_NAME;
        } else {
            name = page.getName();
        }

        response_.setContentType("application/zip");
        response_.setHeader("Content-Disposition", "attachment; filename="
            + name + ".zip");

        OutputStream os = null;
        ZipOutputStream zos = null;
        try {
            os = new BufferedOutputStream(response_.getOutputStream());
            zos = new ZipOutputStream(os);
            publishPlugin_.publish(new ZipWriterResource(zos, false), page,
                recursive_);
            zos.close();
            zos = null;
            os = null;
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            setNotes(new Notes().add(new Note("app.error.publishPage.failure",
                ThrowableUtils.getStackTraceString(ex))));
            return false;
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                    os = null;
                } catch (IOException ex) {
                    ;
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                    ;
                }
            }
        }
    }
}
