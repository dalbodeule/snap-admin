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


package space.mori.dalbodeule.snapadmin.external.dbmapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import space.mori.dalbodeule.snapadmin.external.annotations.DisplayName;
import space.mori.dalbodeule.snapadmin.external.dbmapping.fields.BooleanFieldType;
import space.mori.dalbodeule.snapadmin.external.dbmapping.fields.DbField;
import space.mori.dalbodeule.snapadmin.external.exceptions.SnapAdminException;

/**
 * Wrapper for all objects retrieved from the database.
 *
 */
public class DbObject {
	/**
	 * The instance of the object, i.e. an instance of the `@Entity` class
	 */
	private Object instance;
	
	/**
	 * The schema this object belongs to
	 */
	private DbObjectSchema schema;
	
	public DbObject(Object instance, DbObjectSchema schema) {
		if (instance == null)
			throw new SnapAdminException("Trying to build object with instance == null");
		
		this.instance = instance;
		this.schema = schema;
	}

	public boolean has(DbField field) {
		return findGetter(field.getJavaName()) != null;
	}
	
	public Object getUnderlyingInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public List<DbObject> getValues(DbField field) {
		Collection<Object> values = (Collection<Object>)get(field.getJavaName()).getValue();
		return values.stream().map(o -> new DbObject(o, field.getConnectedSchema()))
				.collect(Collectors.toList());
	}
	
	public DbFieldValue get(DbField field) {
		return get(field.getJavaName());
	}
	
	public DbObject traverse(String fieldName) {
		DbField field = schema.getFieldByName(fieldName);
		return traverse(field);
	}
	
	public DbObject traverse(DbField field) {
		ManyToOne manyToOne = field.getPrimitiveField().getAnnotation(ManyToOne.class);
		OneToOne oneToOne = field.getPrimitiveField().getAnnotation(OneToOne.class);
		if (oneToOne != null || manyToOne != null) {
			Object linkedObject = get(field.getJavaName()).getValue();
			if (linkedObject == null) return null;
			
			DbObject linkedDbObject = new DbObject(linkedObject, field.getConnectedSchema());
			return linkedDbObject;
		} else {
			throw new SnapAdminException("Cannot traverse field " + field.getName() + " in class " + schema.getClassName());
		}
	}
	
	public List<DbObject> traverseMany(String fieldName) {
		DbField field = schema.getFieldByName(fieldName);
		return traverseMany(field);
	}
	
	@SuppressWarnings("unchecked")
	public List<DbObject> traverseMany(DbField field) {
		ManyToMany manyToMany = field.getPrimitiveField().getAnnotation(ManyToMany.class);
		OneToMany oneToMany = field.getPrimitiveField().getAnnotation(OneToMany.class);
		if (manyToMany != null || oneToMany != null) {
			Collection<Object> linkedObjects = (Collection<Object>)get(field.getJavaName()).getValue();
			return linkedObjects.stream().map(o -> new DbObject(o, field.getConnectedSchema()))
				.collect(Collectors.toList());
		} else {
			throw new SnapAdminException("Cannot traverse field " + field.getName() + " in class " + schema.getClassName());
		}
	}
	
	public DbFieldValue get(String name) {
		Method getter = findGetter(name);
		
		if (getter == null)
			throw new SnapAdminException("Unable to find getter method for field `"
				+ name + "` in class " + instance.getClass());

		try {
			Object result = getter.invoke(instance);
			return new DbFieldValue(result, schema.getFieldByJavaName(name));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new SnapAdminException(e);
		}
	}
	
	public Object getPrimaryKeyValue() {
		DbField primaryKeyField = schema.getPrimaryKey();
		Method getter = findGetter(primaryKeyField.getJavaName());
		
		if (getter == null)
			throw new SnapAdminException("Unable to find getter method for field `"
				+ primaryKeyField.getJavaName() + "` in class " + instance.getClass());
		
		try {
			Object result = getter.invoke(instance);
			return result;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new SnapAdminException(e);
		}
	}
	
	public String getDisplayName() {
		Method[] methods = instance.getClass().getMethods();
		
		Optional<Method> displayNameMethod = 
			Arrays.stream(methods)
			      .filter(m -> m.getAnnotation(DisplayName.class) != null)
			      .findFirst();
		
		if (displayNameMethod.isPresent()) {
			try {
				Object displayName = displayNameMethod.get().invoke(instance);
				if (displayName == null) return null;
				else return displayName.toString();
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new SnapAdminException(e);
			}
		} else {
			return getPrimaryKeyValue().toString();
		}
	}
	
	public List<String> getComputedColumns() {
		return schema.getComputedColumnNames();
	}
	
	public DbObjectSchema getSchema() {
		return schema;
	}
	
	public Object compute(String column) {
		Method method = schema.getComputedColumn(column);
		
		if (method == null)
			throw new SnapAdminException("Unable to find mapped method for @ComputedColumn " + column);
		
		try {
			return method.invoke(instance);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new SnapAdminException("Error while calling @ComputedColumn " + column
					+ " on class " + schema.getClassName());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setRelationship(String fieldName, Object primaryKeyValue) {
		DbField field = schema.getFieldByName(fieldName);
		DbObjectSchema linkedSchema = field.getConnectedSchema();
		Optional<?> obj = linkedSchema.getJpaRepository().findById(primaryKeyValue);
		
		if (!obj.isPresent()) {
			throw new SnapAdminException("Invalid value " + primaryKeyValue + " for " + fieldName
					+ ": item does not exist.");
		}
		
		Method setter = findSetter(field.getJavaName());
		
		if (setter == null) {
			throw new SnapAdminException("Unable to find setter method for " + fieldName + " in " + schema.getClassName());
		}
		
		try {
			setter.invoke(instance, obj.get());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		}
	}
	
	public void set(String fieldName, Object value) {
		Method setter = findSetter(fieldName);
		
		if (setter == null) {
			throw new SnapAdminException("Unable to find setter method for " + fieldName + " in " + schema.getClassName());
		}
		
		Class<?>[] types = setter.getParameterTypes();
		if (types.length != 1) 
			throw new SnapAdminException("Setter " + setter + " has " + types.length + " parameters (expected 1)");
		
		Class<?> expectedSetterType = types[0];
		if (!expectedSetterType.isAssignableFrom(value.getClass())) {
			// If the value is not assignable we check if it's a collection 
			// mismatch, e.g. the setter expects a Set but we are passing a List
			// or viceversa
			if (expectedSetterType == Set.class) {
				// Convert value to Set
				value = ((Collection<?>)value).stream().collect(Collectors.toSet());
			} else if (expectedSetterType == List.class) {
				// Convert value to List
				value = ((Collection<?>)value).stream().collect(Collectors.toList());
			}
		}
		
		
		
		
		
		try {
			setter.invoke(instance, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			if (e instanceof IllegalArgumentException) {
				throw new RuntimeException("setter: " + setter + ", passed: " + value.getClass(), e);
			}
			throw new RuntimeException(e);
		}
	}
	
	
	protected Method findSetter(String fieldName) {
		String capitalize = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		Method[] methods = instance.getClass().getDeclaredMethods();
		
		for (Method m : methods) {
			if (m.getName().equals("set" + capitalize))
				return m;
		}
		
		return null;
	}
	
	protected Method findGetter(String fieldName) {
		String capitalize = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		Method[] methods = instance.getClass().getDeclaredMethods();
		
		DbField dbField = schema.getFieldByJavaName(fieldName);
		if (dbField == null) return null;
		
		String prefix = "get";
		if (dbField.getType() instanceof BooleanFieldType) {
			prefix = "is";
		}
		
		for (Method m : methods) {
			if (m.getName().equals(prefix + capitalize))
				return m;
		}
		
		return null;
	}
	
	/**
	 * Converts this object to map where each key is a field name,
	 * including only the specified fields.
	 * If raw, values are not processed and are included as they are
	 * in the database table.
	 * 
	 * @return
	 */
	public Map<String, Object> toMap(List<String> fields, boolean raw) {
		Map<String, Object> result = new HashMap<>();
		
		for (String field : fields) {
			DbField dbField = schema.getFieldByName(field);
			
			if (dbField == null) {
				// The field is a computed column
				Object computedValue = compute(field);
				result.put(field, computedValue);
			} else {
				if (dbField.isForeignKey()) {
					DbObject linkedItem = traverse(dbField);
					
					if (linkedItem == null) result.put(field, null);
					else {
						if (raw) {
							result.put(field, linkedItem.getPrimaryKeyValue().toString());
						} else {
							result.put(field, linkedItem.getPrimaryKeyValue() + " (" + linkedItem.getDisplayName() + ")");
						}
					}
				} else {
					if (raw) {
						DbFieldValue fieldValue = get(dbField);
						if (fieldValue.getValue() == null) result.put(field, null);
						else result.put(field, fieldValue.getValue().toString());
					} else {
						
						result.put(field, get(dbField).getFormattedValue());
					}
					
				}
			}
		}
		
		
		return result;
	}

	@Override
	public String toString() {
		return "DbObject [instance=" + instance + ", schema=" + schema + "]";
	}
	
	
}
