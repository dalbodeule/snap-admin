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


package space.mori.dalbodeule.snapadmin.external.dbmapping.fields;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import space.mori.dalbodeule.snapadmin.external.annotations.DisplayImage;
import space.mori.dalbodeule.snapadmin.external.annotations.Filterable;
import space.mori.dalbodeule.snapadmin.external.annotations.FilterableType;
import space.mori.dalbodeule.snapadmin.external.annotations.ReadOnly;
import space.mori.dalbodeule.snapadmin.external.dbmapping.DbFieldValue;
import space.mori.dalbodeule.snapadmin.external.dbmapping.DbObject;
import space.mori.dalbodeule.snapadmin.external.dbmapping.DbObjectSchema;

/**
 * Represent a field on the database, generated from an Entity class instance variable.
 */
public class DbField {
	/**
	 * The inferred name of this field on the database
	 */
	protected String dbName;

	/**
	 * The name of this field in the Java code (instance variable)
	 */
	protected String javaName;
	
	/**
	 * The type of this field
	 */
	protected DbFieldType type;
	
	@JsonIgnore
	/**
	 * The primitive Field object from the Class
	 */
	protected Field field;
	
	/**
	 * If this field is a foreign key, the class of the
	 * entity that is connected to it
	 */
	@JsonIgnore
	private Class<?> connectedType;
	
	/**
	 * Whether this field is a primary key
	 */
	private boolean primaryKey;
	
	/**
	 * Whether this field is nullable
	 */
	private boolean nullable;
	
	/**
	 * The optional format to apply to this field, if the `@DisplayFormat` 
	 * annotation has been applied.
	 */
	private String format;

	private boolean disableEditField = false;
	
	/**
	 * The schema this field belongs to
	 */
	@JsonIgnore
	private DbObjectSchema schema;
	
	public DbField(String javaName, String name, Field field, DbFieldType type, DbObjectSchema schema, String format) {
		this.javaName = javaName;
		this.dbName = name;
		this.schema = schema;
		this.field = field;
		this.type = type;
		this.format = format;
	}
	
	public String getJavaName() {
		return javaName;
	}
	
	public DbObjectSchema getSchema() {
		return schema;
	}
	
	public DbObjectSchema getConnectedSchema() {
		if (connectedType == null) return null;
		return schema.getSnapAdmin().findSchemaByClass(connectedType);
	}
	
	public void setSchema(DbObjectSchema schema) {
		this.schema = schema;
	}
	
	@JsonIgnore
	public Field getPrimitiveField() {
		return field;
	}
	
	public void setField(Field field) {
		this.field = field;
	}

	public String getName() {
		return dbName;
	}

	public void setName(String name) {
		this.dbName = name;
	}
	
	public DbFieldType getType() {
		return type;
	}
	
	public void setType(DbFieldType type) {
		this.type = type;
	}
	
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	
	public Class<?> getConnectedType() {
		return connectedType;
	}
	
	public void setConnectedType(Class<?> connectedType) {
		this.connectedType = connectedType;
	}

	public boolean isForeignKey() {
		return connectedType != null;
	}
	
	public boolean isNullable() {
		return nullable;
	}
	
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isBinary() {
		return type instanceof ByteArrayFieldType;
	}
	
	public boolean isImage() {
		return field.getAnnotation(DisplayImage.class) != null;
	}
	
	public String getFormat() {
		return format;
	}
	
	public boolean isText() {
		return type instanceof TextFieldType;
	}

	public boolean isDisableEditField() {
		return disableEditField;
	}

	public void setDisableEditField(boolean managed) {
		this.disableEditField = managed;
	}
	
	/**
	 * Returns the value to use in the "step" HTML attribute
	 * for numeric data fields. For fields that are not numeric,
	 * we just return the String "any", as it is not included
	 * in the HTML template anyway.
	 * @return
	 */
	public String getStep() {
		if (getType() instanceof IntegerFieldType
			|| getType() instanceof BigIntegerFieldType
			|| getType() instanceof ByteFieldType) {
			return "0";
		}
		return "any";
	}
	
	/**
	 * Returns the name of the Thymeleaf fragment used to render
	 * the input for this field.
	 * @return
	 */
	public String getFragmentName() {
		return type.getFragmentName();
	}
	
	public boolean isFilterable() {
		return getPrimitiveField().getAnnotation(Filterable.class) != null;
	}
	
	public boolean isFilterableCategorical() {
		Filterable filterable = getPrimitiveField().getAnnotation(Filterable.class);
		return filterable != null && filterable.type() == FilterableType.CATEGORICAL;
	}
	
	public boolean isReadOnly() {
		return getPrimitiveField().getAnnotation(ReadOnly.class) != null;
	}
	
	public boolean isToOne() {
		return  (getPrimitiveField().getAnnotation(OneToOne.class) != null &&
				getPrimitiveField().getAnnotation(OneToOne.class).mappedBy().isBlank())
				|| getPrimitiveField().getAnnotation(ManyToOne.class) != null;
	}
	
	/**
	 * Returns if this field is settable with a raw value, i.e.
	 * a field that is not a relationship to another entity;
	 * @return
	 */
	public boolean isSettable() {
		return getPrimitiveField().getAnnotation(ManyToOne.class) == null
			&& getPrimitiveField().getAnnotation(OneToMany.class) == null
			&& getPrimitiveField().getAnnotation(OneToOne.class) == null
			&& getPrimitiveField().getAnnotation(ManyToMany.class) == null;
	}
	
	/**
	 * Returns whether this field is exportable into a CSV file.
	 * 
	 * @return
	 */
	public boolean isExportable() {
		return !isBinary();
	}
	
	
	public boolean isGeneratedValue() {
		return getPrimitiveField().getAnnotation(GeneratedValue.class) != null;
	}
	
	public Set<DbFieldValue> getAllValues() {
		if (type instanceof EnumFieldType) {
			List<?> values = type.getValues();
			return values.stream().map(v -> {
				return new DbFieldValue(v, this);
			}).collect(Collectors.toSet());
		} else {
			List<?> findAll = schema.getJpaRepository().findAll();
			return findAll.stream()
						.map(o -> new DbObject(o, schema).get(this))
						.collect(Collectors.toSet());
		}
	}
	
	@Override
	public String toString() {
		return "DbField [name=" + dbName + ", javaName=" + javaName + ", type=" + type + ", field=" + field
				+ ", connectedType=" + connectedType + ", primaryKey=" + primaryKey + ", nullable=" + nullable
				+ ", schema=" + schema.getClassName() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(dbName, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DbField other = (DbField) obj;
		return Objects.equals(dbName, other.dbName) && type == other.type;
	}
}

