package org.seasar.kvasir.cms.publish;

import junit.framework.Test;
import org.seasar.kvasir.test.KvasirPluginTestCase;


public class PublishPluginIT extends KvasirPluginTestCase<PublishPlugin>
{
    protected String getTargetPluginId()
    {
        return PublishPlugin.ID;
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(PublishPluginIT.class);
    }
}
