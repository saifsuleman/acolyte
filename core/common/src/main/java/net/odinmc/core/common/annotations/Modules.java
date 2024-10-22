package net.odinmc.core.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.odinmc.core.common.terminable.module.TerminableModule;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Modules {
    Class<? extends TerminableModule>[] value();
}
