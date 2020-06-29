/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.osgi.transform.velocity;

import java.io.File;
import java.io.IOException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.event.IncludeEventHandler;
import org.apache.velocity.context.Context;

import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;

/**
 * Event handler that looks for included files relative to the path of the current template. The
 * handler assumes that paths are separated by a forward slash "/" or backwards slash "\".
 *
 * @author <a href="mailto:wglass@forio.com">Will Glass-Husain </a>
 * @version $Id: IncludeRelativePath.java 685685 2008-08-13 21:43:27Z nbubna $
 * @since 1.5
 */
public class IncludeFullPath extends MLog implements IncludeEventHandler {

    //static ThreadLocal<VelocityContext> contexts = new ThreadLocal<>();
    static ThreadLocal<String> projectPathes = new ThreadLocal<>();

    /**
     * Return path relative to the current template's path.
     *
     * @param includeResourcePath the path as given in the include directive.
     * @param currentResourcePath the path of the currently rendering template that includes the
     *     include directive.
     * @param directiveName name of the directive used to include the resource. (With the standard
     *     directives this is either "parse" or "include").
     * @return new path relative to the current template's path
     */
    @Override
    public String includeEvent(Context context, 
            String includeResourcePath, String currentResourcePath, String directiveName) {
        // if the resource name starts with a slash, it's not a relative path
        if (includeResourcePath.startsWith("/") || includeResourcePath.startsWith("\\")) {
            return includeResourcePath;
        }

        // VelocityContext context2 = contexts.get();
        String path = (String) context.get("path");
        try {
            String projectPath = projectPathes.get();
            String out = MFile.getFile(new File(path), includeResourcePath).getCanonicalPath();
            return out.substring(projectPath.length());
        } catch (IOException e) {
            log().e(e);
        }
        return null;
    }

    public static void setProjectPath(String path) {
        if (path == null) projectPathes.remove();
        else projectPathes.set(path);
    }

//    public static void setContext(VelocityContext context) {
//        if (context == null) contexts.remove();
//        else contexts.set(context);
//    }
}
