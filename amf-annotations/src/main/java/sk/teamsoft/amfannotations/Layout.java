package sk.teamsoft.amfannotations;

import android.support.annotation.LayoutRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Dusan Bartos
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Layout {
    @LayoutRes int value() default -1;
}
