package space.mori.dalbodeule.snapadmin.aot;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import space.mori.dalbodeule.snapadmin.external.SnapAdmin;
import space.mori.dalbodeule.snapadmin.external.SnapAdminProperties;
import space.mori.dalbodeule.snapadmin.external.annotations.Disable;
import space.mori.dalbodeule.snapadmin.external.annotations.DisableEditField;
import space.mori.dalbodeule.snapadmin.external.annotations.DisplayFormat;
import space.mori.dalbodeule.snapadmin.external.annotations.DisplayImage; // Assuming this is used
import space.mori.dalbodeule.snapadmin.external.annotations.HiddenEditForm; // Assuming this is used
import space.mori.dalbodeule.snapadmin.external.dbmapping.CustomJpaRepository;
import space.mori.dalbodeule.snapadmin.external.dbmapping.DbObjectSchema;
import space.mori.dalbodeule.snapadmin.external.dbmapping.fields.*;
import space.mori.dalbodeule.snapadmin.external.dto.MappingError;
import space.mori.dalbodeule.snapadmin.external.misc.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.aot.hint.MemberCategory.*;

public class SnapAdminRuntimeHints implements RuntimeHintsRegistrar {

    private static final Set<Class<?>> dbFieldTypes = new HashSet<>(Arrays.asList(
            BooleanFieldType.class, LongFieldType.class, IntegerFieldType.class,
            BigIntegerFieldType.class, ShortFieldType.class, StringFieldType.class,
            LocalDateFieldType.class, DateFieldType.class, LocalDateTimeFieldType.class,
            InstantFieldType.class, FloatFieldType.class, DoubleFieldType.class,
            BigDecimalFieldType.class, ByteArrayFieldType.class, OffsetDateTimeFieldType.class,
            ByteFieldType.class, UUIDFieldType.class, CharFieldType.class,
            EnumFieldType.class, TextFieldType.class
            // Add any other concrete DbFieldType implementations here
    ));

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Register SnapAdmin's own classes
        hints.reflection().registerType(SnapAdmin.class, INTROSPECT_DECLARED_METHODS, INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(SnapAdminProperties.class, INVOKE_DECLARED_CONSTRUCTORS, INVOKE_PUBLIC_METHODS); // For Spring binding

        hints.reflection().registerType(DbObjectSchema.class, INVOKE_DECLARED_CONSTRUCTORS, INTROSPECT_DECLARED_METHODS, INVOKE_PUBLIC_METHODS);
        // CustomJpaRepository 인터페이스 자체는 생성자 호출 힌트가 불필요할 수 있음
        hints.reflection().registerType(DbField.class, INVOKE_DECLARED_CONSTRUCTORS, INTROSPECT_DECLARED_METHODS, INVOKE_PUBLIC_METHODS);
        hints.reflection().registerType(MappingError.class, INVOKE_DECLARED_CONSTRUCTORS);
        // hints.reflection().registerType(Utils.class); // 사용 패턴 확인 후 필요하면 활성화

        // Register DbFieldType and its subclasses for default constructor invocation
        hints.reflection().registerType(DbFieldType.class);
        for (Class<?> dbFieldTypeClass : dbFieldTypes) {
            hints.reflection().registerType(dbFieldTypeClass, INVOKE_DECLARED_CONSTRUCTORS);
        }
        // EnumFieldType has a special constructor too
        hints.reflection().registerType(EnumFieldType.class, INVOKE_DECLARED_CONSTRUCTORS);


        // Register SnapAdmin's custom annotations (and assume their attributes might be read)
        registerAnnotation(hints, Disable.class);
        registerAnnotation(hints, DisableEditField.class);
        registerAnnotation(hints, DisplayFormat.class);
        registerAnnotation(hints, DisplayImage.class);
        registerAnnotation(hints, HiddenEditForm.class);

        // Register Jakarta Persistence annotations (and assume their attributes might be read)
        registerAnnotation(hints, jakarta.persistence.Entity.class);
        registerAnnotation(hints, jakarta.persistence.Id.class);
        registerAnnotation(hints, jakarta.persistence.Column.class);
        registerAnnotation(hints, jakarta.persistence.Lob.class);
        registerAnnotation(hints, jakarta.persistence.Enumerated.class);
        registerAnnotation(hints, jakarta.persistence.EnumType.class); // TYPE_VISIBLE 제거
        registerAnnotation(hints, jakarta.persistence.OneToMany.class);
        registerAnnotation(hints, jakarta.persistence.ManyToMany.class);
        registerAnnotation(hints, jakarta.persistence.ManyToOne.class);
        registerAnnotation(hints, jakarta.persistence.OneToOne.class);
        registerAnnotation(hints, jakarta.persistence.JoinColumn.class);
        // Add other JPA annotations if used, e.g. @Table, @Transient

        // Hints for operations on arbitrary (user-defined) @Entity classes
        // 가능하면 스캔 범위를 제한하거나, 필요한 메서드만 등록
        // 예시: 특정 패키지 내의 @Entity 클래스 스캔
        // ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        // scanner.addIncludeFilter(new AnnotationTypeFilter(jakarta.persistence.Entity.class));
        // for (BeanDefinition bd : scanner.findCandidateComponents("com.example.entities")) {
        //     try {
        //         Class<?> entityClass = Class.forName(bd.getBeanClassName());
        //         hints.reflection().registerType(entityClass, INTROSPECT_DECLARED_FIELDS, INVOKE_DECLARED_METHODS);
        //     } catch (ClassNotFoundException e) {
        //         // Handle exception
        //     }
        // }
        // Register SnapAdmin's own classes
        hints.reflection().registerType(SnapAdmin.class, INTROSPECT_DECLARED_METHODS, INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(SnapAdminProperties.class, INVOKE_DECLARED_CONSTRUCTORS, INVOKE_PUBLIC_METHODS); // For Spring binding

        hints.reflection().registerType(DbObjectSchema.class, INVOKE_DECLARED_CONSTRUCTORS, INTROSPECT_DECLARED_METHODS, INVOKE_PUBLIC_METHODS);
        hints.reflection().registerType(CustomJpaRepository.class, INVOKE_DECLARED_CONSTRUCTORS);
        hints.reflection().registerType(DbField.class, INVOKE_DECLARED_CONSTRUCTORS, INTROSPECT_DECLARED_METHODS, INVOKE_PUBLIC_METHODS);
        hints.reflection().registerType(MappingError.class, INVOKE_DECLARED_CONSTRUCTORS);
        hints.reflection().registerType(Utils.class); // If it contains static methods called, or if instantiated


        // For Class.forName(className) on unknown classes (typically user entities)
        // and subsequent operations like getDeclaredFields(), getAnnotation(), newInstance()
        // This is a general hint. Users should still ensure their entities are hinted.
        // Consider making this more specific if possible, e.g., by scanning packages if configured.
        hints.reflection().registerType(Object.class,
                INTROSPECT_DECLARED_CONSTRUCTORS, INVOKE_DECLARED_CONSTRUCTORS,
                INTROSPECT_DECLARED_METHODS, INVOKE_DECLARED_METHODS, // For getters/setters if library invokes them
                DECLARED_FIELDS // For field access
        );

        // For ClassPathScanningCandidateComponentProvider
        hints.reflection().registerType(org.springframework.beans.factory.config.BeanDefinition.class, INVOKE_PUBLIC_METHODS); // For getBeanClassName()
        hints.reflection().registerType(org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider.class, INVOKE_DECLARED_CONSTRUCTORS);
        hints.reflection().registerType(org.springframework.core.type.filter.AnnotationTypeFilter.class, INVOKE_DECLARED_CONSTRUCTORS);


        // Resource hints if any .properties or .xml files are loaded from classpath by the library
        // hints.resources().registerPattern("my-library-config.xml");

        // Proxy hints if JDK proxies are created for library interfaces
        // hints.proxies().registerJdkProxy(MyLibraryInterface.class);

        // Serialization hints if objects are serialized by the library
        // hints.serialization().registerType(MySerializableObject.class);
    }

    private void registerAnnotation(RuntimeHints hints, Class<?> annotationType) {
        hints.reflection().registerType(annotationType, INVOKE_DECLARED_METHODS);
    }
    private void registerAnnotation(RuntimeHints hints, Class<?> annotationType, org.springframework.aot.hint.MemberCategory... categories) {
        hints.reflection().registerType(annotationType, categories);
    }
}