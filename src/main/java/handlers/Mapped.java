package handlers;

/**
 * Created by dshelygin on 06.09.2017.
 * аннотация для указания uri для обработчиков запросов HTTP
 */

import java.lang.annotation.*;

@Target(value= ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface Mapped {
    String uri();
}

