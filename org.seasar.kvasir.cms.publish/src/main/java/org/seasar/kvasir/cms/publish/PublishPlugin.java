package org.seasar.kvasir.cms.publish;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.publish.setting.PublishPluginSettings;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.io.Resource;


/**
 * ページの公開用HTMLを生成するためのプラグインです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface PublishPlugin
    extends Plugin<PublishPluginSettings>
{
    String ID = "org.seasar.kvasir.cms.publish";

    String ID_PATH = ID.replace('.', '/');


    /**
     * 指定されたページにアクセスした際に表示されるHTMLを出力します。
     * <p><code>recursive</code>がtrueの場合はページの子孫ページも出力されます。
     * </p>
     * 
     * @param dir 生成したHTMLの出力先を表すディレクトリリソース。このリソースの下に子リソースとしてHTMLが出力されます。
     * @param page 生成元となるページ。
     * @param recursive 再帰的に出力するかどうか。
     */
    void publish(Resource dir, Page page, boolean recursive);
}
