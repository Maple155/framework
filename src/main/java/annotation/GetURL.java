import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GetURL {
    String url() default "/default";
}
