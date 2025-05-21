package space.mori.dalbodeule.snapadmin.external;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import space.mori.dalbodeule.snapadmin.external.controller.DataExportController;
import space.mori.dalbodeule.snapadmin.external.controller.FileDownloadController;
import space.mori.dalbodeule.snapadmin.external.controller.GlobalController;
import space.mori.dalbodeule.snapadmin.external.controller.SnapAdminController;
import space.mori.dalbodeule.snapadmin.external.controller.rest.AutocompleteController;
import space.mori.dalbodeule.snapadmin.external.dbmapping.CustomJpaRepository;
import space.mori.dalbodeule.snapadmin.external.dbmapping.DbObjectSchema;
import space.mori.dalbodeule.snapadmin.external.dbmapping.SnapAdminRepository;
import space.mori.dalbodeule.snapadmin.internal.InternalSnapAdminConfiguration;
import space.mori.dalbodeule.snapadmin.internal.UserConfiguration;
import space.mori.dalbodeule.snapadmin.internal.service.ConsoleQueryService;
import space.mori.dalbodeule.snapadmin.internal.service.UserActionService;
import space.mori.dalbodeule.snapadmin.internal.service.UserSettingsService;

/**
 * SnapAdmin 자동 설정 클래스. 메인 애플리케이션의 JPA 설정을 재사용합니다.
 */
@Configuration
@ConditionalOnProperty(name = "snapadmin.enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(SnapAdminProperties.class)
@EnableJpaRepositories(basePackages = "space.mori.dalbodeule.snapadmin.internal.repository")
@EntityScan(basePackages = "space.mori.dalbodeule.snapadmin.internal.model")
@Import({
        SnapAdmin.class,
        SnapAdminMvcConfig.class,
        StartupAuthCheckRunner.class,
        ThymeleafUtils.class,

        // controllers
        SnapAdminController.class,
        DataExportController.class,
        FileDownloadController.class,
        GlobalController.class,
        AutocompleteController.class,

        // dbmapping
        SnapAdminRepository.class,

        // internals
        ConsoleQueryService.class,
        UserActionService.class,
        UserSettingsService.class,
        InternalSnapAdminConfiguration.class,
        UserConfiguration.class
})
public class SnapAdminAutoConfiguration {
}