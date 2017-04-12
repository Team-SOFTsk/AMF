package sk.teamsoft.amf.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import sk.teamsoft.amfannotations.Layout;
import timber.log.Timber;

/**
 * @author Dusan Bartos
 *         Created on 12.04.2017.
 */

public class LayoutUtil {

    private static final Map<String, Integer> PATH_LAYOUT_CACHE = new LinkedHashMap<>();

    private static int getAnnotationValue(Object o, final Map<String, Integer> cache, Class<?> clazz) {
        final Class pathType = o.getClass();
        Integer res = cache.get(pathType.getCanonicalName());
        if (res == null) {
            final Annotation annotation = pathType.getAnnotation(clazz);
            if (annotation != null) {
                try {
                    final Method m = annotation.getClass().getDeclaredMethod("value", (Class[]) null);
                    res = (Integer) m.invoke(annotation, (Object[]) null);
                    cache.put(pathType.getCanonicalName(), res);
                } catch (Exception e) {
                    Timber.e(e, "Error getting annotation value");
                }
            }
        }
        return res != null ? res : -1;
    }

    /**
     * Reads {@link Layout} annotation value of given object
     * @return layout resource if found, else -1
     */
    public static int getLayoutRes(Object o) {
        return getAnnotationValue(o, PATH_LAYOUT_CACHE, Layout.class);
    }

    public static void initCache(Map<String, Integer> cacheMap) {
//        PATH_LAYOUT_CACHE.clear();
        PATH_LAYOUT_CACHE.putAll(cacheMap);
    }
}
