package org.cdc.framework.interfaces.annotation;

import java.lang.annotation.*;

/**
 * When method is decorated by the annotation. It means that it can be used for category.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface ProcedureCategoryLikeMethod {}
