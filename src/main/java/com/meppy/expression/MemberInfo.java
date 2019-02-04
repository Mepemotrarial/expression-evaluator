package com.meppy.expression;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Encapsulates the information required to identify a specific member of a particular object.
 */
final class MemberInfo {
    /**
     * The target object.
     */
    private final Object target;

    /**
     * The getter of the underlying object's property.
     */
    private Method propertyGetter;

    /**
     * A flag indicating whether the underlying object is of type MemberInfo instead of a direct object reference.
     * The actual object can be obtained by inspecting the value of the {@link MemberInfo#getValue} property.
     */
    private final boolean isMemberInfo;

    /**
     * For the same reason as above - if the underlying object is a {@link MemberInfo},
     * the actual property descriptor is obtained later, when the value is get or set.
     */
    private final String propertyName;

    /**
     * An object that can be used to resolve member references which fail to resolve through reflection.
     */
    private final EvaluationContext context;

    /**
     * Initializes a new instance of the {@link MemberInfo} class for the specified object and the property with the specified name.
     */
    MemberInfo(Object target, String propertyName, EvaluationContext context)
    {
        this.context = context;
        this.target = target;
        this.propertyName = propertyName;

        try {
            propertyGetter = getProperty(target.getClass(), propertyName);
        } catch (NoSuchMethodException ex) {
        }
        isMemberInfo = false;
    }

    /**
     * Initializes a new instance of the {@link MemberInfo} class with the object identified by the property
     * defined by the specified {@link MemberInfo} and the property with the specified name.
     */
    MemberInfo(MemberInfo info, String propertyName, EvaluationContext context) {
        this.context = context;
        this.target = info;
        this.propertyName = propertyName;

        isMemberInfo = true;
    }

    private static Method getProperty(Class<?> clazz, String name) throws NoSuchMethodException {
        try {
            return clazz.getMethod("get" + name);
        } catch (NoSuchMethodException ex) {
            return clazz.getDeclaredMethod("get" + name);
        }
    }

    private Object getTarget() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object resolvedTarget = target;
        if (isMemberInfo) {
            resolvedTarget = ((MemberInfo)this.target).getValue(this);
            if (propertyGetter == null) {
                try {
                    propertyGetter = getProperty(resolvedTarget.getClass(), propertyName);
                } catch (NoSuchMethodException ex) {
                }
            }
        }

        return resolvedTarget;
    }

    private Object getValue(MemberInfo parent) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object resolvedTarget = getTarget();
        if (propertyGetter != null) {
            if (!propertyGetter.isAccessible()) {
                propertyGetter.setAccessible(true);
            }
            return propertyGetter.invoke(resolvedTarget);
        }

        return context.evaluateMember(resolvedTarget, propertyName, parent == null);
    }

    /**
     * Gets the value of the represented property.
     */
    public Object getValue() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return getValue(null);
    }
}