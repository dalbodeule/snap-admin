package space.mori.dalbodeule.snapadmin.external.annotations;

import org.springframework.context.annotation.Import;
import space.mori.dalbodeule.snapadmin.external.SnapAdminAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SnapAdminAutoConfiguration.class) // SnapAdmin 설정 클래스를 Import
public @interface SnapAdminEnabled {
    // 필요한 속성이 있다면 정의
}