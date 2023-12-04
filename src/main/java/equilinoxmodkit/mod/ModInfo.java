package equilinoxmodkit.mod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModInfo {
	String id();
	String name();
	String version();
	String author();
	String description();
	String thumbnail();
}