package net.odinmc.core.common.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtil {

  private static final MethodHandle MODIFIERS_SETTER_METHOD_HANDLE;
  private static final MethodHandle FIELD_ACCESSOR_SETTER_METHOD_HANDLE;
  private static final MethodHandle OVERRIDE_FIELD_ACCESSOR_SETTER_METHOD_HANDLE;
  private static final MethodHandle ROOT_GETTER_METHOD_HANDLE;

  static {
    try {
      var lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
      lookupField.setAccessible(true);

      var lookup = ((MethodHandles.Lookup) lookupField.get(null));
      MODIFIERS_SETTER_METHOD_HANDLE = lookup.findSetter(Field.class, "modifiers", int.class);
      var fieldAccessorClass = Class.forName("jdk.internal.reflect.FieldAccessor");
      FIELD_ACCESSOR_SETTER_METHOD_HANDLE = lookup.findSetter(Field.class, "fieldAccessor", fieldAccessorClass);
      OVERRIDE_FIELD_ACCESSOR_SETTER_METHOD_HANDLE = lookup.findSetter(Field.class, "overrideFieldAccessor", fieldAccessorClass);
      ROOT_GETTER_METHOD_HANDLE = lookup.findGetter(Field.class, "root", Field.class);
    } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException exception) {
      throw new RuntimeException(exception);
    }
  }

  public static void set(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
    field(object, fieldName).set(object, value);
  }

  public static void set(Class<?> objectClass, Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
    field(objectClass, fieldName).set(object, value);
  }

  public static Object get(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
    return ReflectionUtil.get(object.getClass(), object, fieldName);
  }

  public static Object get(Class<?> objectClass, Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
    var field = objectClass.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(object);
  }

  @SuppressWarnings("unchecked")
  public static <T> T get(Object object, Class<T> fieldClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
    return (T) ReflectionUtil.get(object.getClass(), object, fieldName);
  }

  @SuppressWarnings("unchecked")
  public static <T> T get(Class<?> objectClass, Object object, Class<T> fieldClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
    return (T) ReflectionUtil.get(objectClass, object, fieldName);
  }

  public static Field field(Object object, String fieldName) throws NoSuchFieldException {
    var field = object.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    ensureNotFinal(field);
    return field;
  }

  public static Field field(Class<?> objectClass, String fieldName) throws NoSuchFieldException {
    var field = objectClass.getDeclaredField(fieldName);
    field.setAccessible(true);
    ensureNotFinal(field);
    return field;
  }

  public static void ensureNotFinal(Field field) {
    if (Modifier.isFinal(field.getModifiers())) {
      try {
        MODIFIERS_SETTER_METHOD_HANDLE.invokeExact(field, field.getModifiers() & ~Modifier.FINAL);

        var currentField = field;
        do {
          FIELD_ACCESSOR_SETTER_METHOD_HANDLE.invoke(currentField, null);
          OVERRIDE_FIELD_ACCESSOR_SETTER_METHOD_HANDLE.invoke(currentField, null);
        } while ((currentField = (Field) ROOT_GETTER_METHOD_HANDLE.invoke(currentField)) != null);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }
}
