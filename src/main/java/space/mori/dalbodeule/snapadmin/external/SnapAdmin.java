/* 
 * SnapAdmin - An automatically generated CRUD admin UI for Spring Boot apps
 * Copyright (C) 2023 Ailef (http://ailef.tech)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package space.mori.dalbodeule.snapadmin.external;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import space.mori.dalbodeule.snapadmin.external.annotations.Disable;
import space.mori.dalbodeule.snapadmin.external.annotations.DisableEditField;
import space.mori.dalbodeule.snapadmin.external.annotations.DisplayFormat;
import space.mori.dalbodeule.snapadmin.external.dbmapping.CustomJpaRepository;
import space.mori.dalbodeule.snapadmin.external.dbmapping.DbObjectSchema;
import space.mori.dalbodeule.snapadmin.external.dbmapping.fields.DbField;
import space.mori.dalbodeule.snapadmin.external.dbmapping.fields.DbFieldType;
import space.mori.dalbodeule.snapadmin.external.dbmapping.fields.EnumFieldType;
import space.mori.dalbodeule.snapadmin.external.dbmapping.fields.StringFieldType;
import space.mori.dalbodeule.snapadmin.external.dbmapping.fields.TextFieldType;
import space.mori.dalbodeule.snapadmin.external.dto.MappingError;
import space.mori.dalbodeule.snapadmin.external.exceptions.SnapAdminException;
import space.mori.dalbodeule.snapadmin.external.exceptions.SnapAdminNotFoundException;
import space.mori.dalbodeule.snapadmin.external.exceptions.UnsupportedFieldTypeException;
import space.mori.dalbodeule.snapadmin.external.misc.Utils;

/**
 * The main SnapAdmin class is responsible for the initialization phase. This class scans
 * the user provided package containing the {@code Entity} definitions and tries to map each
 * entity to a {@link DbObjectSchema} instance.
 * 
 * This process involves determining the correct type for each class field and its 
 * configuration at the database level. An exception will be thrown if it's not possible
 * to determine the field type.
 */
@Component
public class SnapAdmin {
	private static final Logger logger = LoggerFactory.getLogger(SnapAdmin.class.getName());
	
	private EntityManager entityManager;
	
	private List<DbObjectSchema> schemas = new ArrayList<>();
	
	private List<String> modelsPackage;
	
	private SnapAdminProperties properties;
	
	private boolean authenticated;
	
	private static final String VERSION = "0.4.1";
    
    /**
	 * Builds the SnapAdmin instance by scanning the `@Entity` beans and loading
	 * the schemas.
	 * @param entityManager	the entity manager
	 * @param properties	the configuration properties
	 */
	public SnapAdmin(@Autowired EntityManager entityManager, @Autowired SnapAdminProperties properties) {
		this.modelsPackage = Arrays.stream(properties.getModelsPackage().split(",")).map(String::trim).toList();
		this.entityManager = entityManager;
		this.properties = properties;
	}
	
	@PostConstruct
	private void init() {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
		
		logger.debug("Initializing SnapAdmin...");
		
		for (String currentPackage : modelsPackage) {
			logger.debug("Scanning package " + currentPackage);
			
			Set<BeanDefinition> beanDefs = provider.findCandidateComponents(currentPackage);
			logger.debug("Found " + beanDefs.size() + " candidate @Entity classes");
			
			for (BeanDefinition bd : beanDefs) {
				// This can return null if the Entity has the @Disable annotation
				DbObjectSchema schema = processBeanDefinition(bd);
				if (schema != null)
					schemas.add(schema);
			}
			
			logger.info("Scanned package '" + currentPackage + "'. Loaded " + beanDefs.size() + " schemas.");
		}

		boolean hasErrors = schemas.stream().flatMap(s -> s.getErrors().stream()).count() > 0;
		
		logger.info("SnapAdmin initialized. Loaded " + schemas.size() 
				+ " schemas from " + modelsPackage.size() + " packages"	+ (hasErrors ? " (with errors)" : ""));
		logger.info("SnapAdmin web interface at: http://YOUR_HOST:YOUR_PORT/" + properties.getBaseUrl());
		
	}

	/**
	 * Returns the current version 
	 * @return
	 */
	public String getVersion() {
		return VERSION;
	}
	
	/**
	 * Returns all the loaded schemas (i.e. entity classes)
	 * @return the list of loaded schemas from the `@Entity` classes
	 */
	public List<DbObjectSchema> getSchemas() {
		return Collections.unmodifiableList(schemas);
	}
	
	/**
	 * Finds a schema by its full class name
	 * @param className	qualified class name
	 * @return the schema with this class name
	 * @throws SnapAdminException if corresponding schema not found
	 */
	public DbObjectSchema findSchemaByClassName(String className) {
		return schemas.stream().filter(s -> s.getClassName().equals(className)).findFirst().orElseThrow(() -> {
			return new SnapAdminNotFoundException("Schema " + className + " not found.");
		});
	}
	
	/**
	 * Finds a schema by its table name
	 * @param tableName the table name on the database
	 * @return the schema with this table name
	 * @throws SnapAdminException if corresponding schema not found
	 */
	public DbObjectSchema findSchemaByTableName(String tableName) {
		return schemas.stream().filter(s -> s.getTableName().equals(tableName)).findFirst().orElseThrow(() -> {
			return new SnapAdminException("Schema " + tableName + " not found.");
		});
	}
	
	/**
	 * Finds a schema by its class object
	 * @param klass the `@Entity` class you want to find the schema for
	 * @return the schema for the `@Entity` class
	 * @throws SnapAdminException if corresponding schema not found
	 */
	public DbObjectSchema findSchemaByClass(Class<?> klass) {
		return findSchemaByClassName(klass.getName());
	}

	/**
	 * Returns whether this class is managed by SnapAdmin
	 */
	public boolean isManagedClass(Class<?> klass) {
		Optional<DbObjectSchema> hasSchema = 
			schemas.stream().filter(s -> s.getClassName().equals(klass.getName())).findFirst();
		return hasSchema.isPresent();
	}
	
	/**
	 * This method processes a BeanDefinition into a DbObjectSchema object,
	 * where all fields have been correctly mapped to DbField objects.
	 * 
	 * If any field is not mappable, the method will throw an exception.
	 * @param bd
	 * @return a schema derived from the `@Entity` class
	 */
	private DbObjectSchema processBeanDefinition(BeanDefinition bd) {
		String fullClassName = bd.getBeanClassName();
		
		try {
			Class<?> klass = Class.forName(fullClassName);
			
			Disable disabled = klass.getAnnotation(Disable.class);
			if (disabled != null)
				return null;
			
			DbObjectSchema schema = new DbObjectSchema(klass, this);
			CustomJpaRepository simpleJpaRepository = new CustomJpaRepository(schema, entityManager);
			schema.setJpaRepository(simpleJpaRepository);
			
			logger.debug("Processing class: "  + klass + " - Table: " + schema.getTableName());
			
			Field[] fields = klass.getDeclaredFields();
			for (Field f : fields) {
				try {
					if(f.getName().contains("hibernate")) continue;
					DbField field = mapField(f, schema);
					field.setSchema(schema);
					schema.addField(field);
				} catch (UnsupportedFieldTypeException e) {
					if(klass.getSimpleName().startsWith("$$_hibernate")) continue;
					logger.warn("The class " + klass.getSimpleName()  + " contains the field `" 
								+ f.getName() + "` of type `" + f.getType().getSimpleName() + "`, which is not supported");
					schema.addError(
						new MappingError(
							"The class contains the field `" + f.getName() + "` of type `" + f.getType().getSimpleName() + "`, which is not supported"
						)
					);
				}
			}
			
			logger.debug("Processed " + klass + ", extracted " + schema.getSortedFields().size() + " fields");
			
			return schema;
		} catch (ClassNotFoundException |
				IllegalArgumentException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Determines the name for the given field, by transforming it to snake_case
	 * and checking if the `@Column` annotation is present.
	 * @param f
	 * @return
	 */
	private String determineFieldName(Field f) {
		Column[] columnAnnotations = f.getAnnotationsByType(Column.class);
		String fieldName = Utils.camelToSnake(f.getName());
		
		if (columnAnnotations.length != 0) {
			Column col = columnAnnotations[0];
			if (col.name() != null && !col.name().isBlank())
				fieldName = col.name();
		}
		
		return fieldName;
	}
	
	/**
	 * Determines if a field is nullable from the `@Column` annotation
	 * @param f
	 * @return
	 */
	private boolean determineNullable(Field f) {
		Column[] columnAnnotations = f.getAnnotationsByType(Column.class);

		boolean nullable = true;
		if (columnAnnotations.length != 0) {
			Column col = columnAnnotations[0];
			nullable = col.nullable();
		}
		
		return nullable;
	}
	
	/**
	 * Builds a DbField object from a primitive Java field. This process involves
	 * determining the correct field name on the database, its type and additional
	 * attributes (e.g. nullable). 
	 * This method returns null if a field cannot be mapped to a supported type.
	 * @param f primitive Java field to construct a DbField from
	 * @param schema the schema this field belongs to
	 * @return
	 */
	private DbField mapField(Field f, DbObjectSchema schema) {
		logger.debug("Processing field " + f.getName());
		OneToMany oneToMany = f.getAnnotation(OneToMany.class);
		ManyToMany manyToMany = f.getAnnotation(ManyToMany.class);
		ManyToOne manyToOne = f.getAnnotation(ManyToOne.class);
		OneToOne oneToOne = f.getAnnotation(OneToOne.class);
		Lob lob = f.getAnnotation(Lob.class);
		
		String fieldName = determineFieldName(f);
		
		// This will contain the type of the entity linked by the
		// foreign key, if any
		Class<?> connectedType = null;
		
		// Try to assign default field type determining it by the raw field type and its annotations
		DbFieldType fieldType = null;
		try {
			Class<? extends DbFieldType> fieldTypeClass = DbFieldType.fromClass(f.getType());
			
			if (fieldTypeClass == StringFieldType.class && lob != null) {
				fieldTypeClass = TextFieldType.class;
			}
			
			// Enums are instantiated later because they call a different constructor
			if (fieldTypeClass != EnumFieldType.class) {
				try {
					fieldType = fieldTypeClass.getConstructor().newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// If failure, we try to map a relationship on this field later
				}
			}
		} catch (SnapAdminException e) {
			// If failure, we try to map a relationship on this field later
		}

		if (manyToOne != null || oneToOne != null) {
			fieldName = mapRelationshipJoinColumn(f);
			fieldType = mapForeignKeyType(f.getType());
			connectedType = f.getType();
		}
		
		if (manyToMany != null || oneToMany != null) {
			ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
	        Class<?> targetEntityClass = (Class<?>) stringListType.getActualTypeArguments()[0];
	        fieldType = mapForeignKeyType(targetEntityClass);
	        connectedType = targetEntityClass;
		}
		
		// Check if field has @Enumerated annotation and process accordingly
		if (fieldType == null) {
			Enumerated enumerated = f.getAnnotation(Enumerated.class);
			if (enumerated != null) {
				EnumType type = enumerated.value();
				
				fieldType = new EnumFieldType(f.getType(), type);
			}
		}
		
		if (fieldType == null) {
			throw new UnsupportedFieldTypeException("Unable to determine fieldType for " + f.getType());
		}
		
		DisplayFormat displayFormat = f.getAnnotation(DisplayFormat.class);
		DisableEditField disableEdit = f.getAnnotation(DisableEditField.class);

		DbField field = new DbField(f.getName(), fieldName, f, fieldType, schema, displayFormat != null ? displayFormat.format() : null);
		field.setConnectedType(connectedType);
		
		Id[] idAnnotations = f.getAnnotationsByType(Id.class);
		field.setPrimaryKey(idAnnotations.length != 0);
		
		field.setNullable(determineNullable(f));
		
		if (field.isPrimaryKey())
			field.setNullable(false);

        field.setDisableEditField(disableEdit != null);
		
		return field;
	}
	
	/**
	 * Returns the join column name for the relationship defined on 
	 * the input Field object.
	 * @param f
	 * @return
	 */
	private String mapRelationshipJoinColumn(Field f) {
		String joinColumnName = Utils.camelToSnake(f.getName()) + "_id"; 
		JoinColumn[] joinColumn = f.getAnnotationsByType(JoinColumn.class);
		if (joinColumn.length != 0) {
			joinColumnName = joinColumn[0].name();
		}
		return joinColumnName;

	}
	
	/**
	 * Returns the type of a foreign key field, by looking at the type
	 * of the primary key (defined as `@Id`) in the referenced table.
	 * 
	 * @param entityClass
	 * @return
	 */
	private DbFieldType mapForeignKeyType(Class<?> entityClass) {
		try {
			Object linkedEntity = entityClass.getConstructor().newInstance();
			Class<?> linkType = null;
			
			for (Field ef : linkedEntity.getClass().getDeclaredFields()) {
				if (ef.getAnnotationsByType(Id.class).length != 0) {
					linkType = ef.getType();
				}
			}
			
			if (linkType == null)
				throw new SnapAdminException("Unable to find @Id field in Entity class " + entityClass);
			
			return DbFieldType.fromClass(linkType).getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new SnapAdminException(e);
		}
	}

	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

}