/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class MethodHandleObjectFieldAccessorImpl extends MethodHandleFieldAccessorImpl {
    static FieldAccessorImpl fieldAccessor(Field field, MethodHandle getter, MethodHandle setter, boolean isReadOnly) {
        boolean isStatic = Modifier.isStatic(field.getModifiers());
        if (isStatic) {
            getter = getter.asType(MethodType.methodType(Object.class));
            if (setter != null) {
                setter = setter.asType(MethodType.methodType(void.class, Object.class));
            }
        } else {
            getter = getter.asType(MethodType.methodType(Object.class, Object.class));
            if (setter != null) {
                setter = setter.asType(MethodType.methodType(void.class, Object.class, Object.class));
            }
        }
        return new MethodHandleObjectFieldAccessorImpl(field, getter, setter, isReadOnly, isStatic);
    }

    MethodHandleObjectFieldAccessorImpl(Field field, MethodHandle getter, MethodHandle setter, boolean isReadOnly, boolean isStatic) {
        super(field, getter, setter, isReadOnly, isStatic);
    }

    @Override
    public Object get(Object obj) throws IllegalArgumentException {
        try {
            return isStatic() ? getter.invokeExact() : getter.invokeExact(obj);
        } catch (IllegalArgumentException|NullPointerException e) {
            throw e;
        } catch (ClassCastException e) {
            throw newGetIllegalArgumentException(obj);
        } catch (Throwable e) {
            throw new InternalError(e);
        }
    }

    public boolean getBoolean(Object obj) throws IllegalArgumentException {
        throw newGetBooleanIllegalArgumentException();
    }

    public byte getByte(Object obj) throws IllegalArgumentException {
        throw newGetByteIllegalArgumentException();
    }

    public char getChar(Object obj) throws IllegalArgumentException {
        throw newGetCharIllegalArgumentException();
    }

    public short getShort(Object obj) throws IllegalArgumentException {
        throw newGetShortIllegalArgumentException();
    }

    public int getInt(Object obj) throws IllegalArgumentException {
        throw newGetIntIllegalArgumentException();
    }

    public long getLong(Object obj) throws IllegalArgumentException {
        throw newGetLongIllegalArgumentException();
    }

    public float getFloat(Object obj) throws IllegalArgumentException {
        throw newGetFloatIllegalArgumentException();
    }

    public double getDouble(Object obj) throws IllegalArgumentException {
        throw newGetDoubleIllegalArgumentException();
    }

    @Override
    public void set(Object obj, Object value) throws IllegalAccessException {
        ensureObj(obj);
        if (isReadOnly()) {
            throwFinalFieldIllegalAccessException(value);
        }
        try {
            if (isStatic()) {
                setter.invokeExact(value);
            } else {
                setter.invokeExact(obj, value);
            }
        } catch (IllegalArgumentException|NullPointerException e) {
            throw e;
        } catch (ClassCastException e) {
            // already ensure the receiver type.  So this CCE is due to the value.
            throwSetIllegalArgumentException(value);
        } catch (Throwable e) {
            throw new InternalError(e);
        }
    }

    public void setBoolean(Object obj, boolean z)
        throws IllegalArgumentException, IllegalAccessException
    {
        throwSetIllegalArgumentException(z);
    }

    public void setByte(Object obj, byte b)
        throws IllegalArgumentException, IllegalAccessException
    {
        throwSetIllegalArgumentException(b);
    }

    public void setChar(Object obj, char c)
        throws IllegalArgumentException, IllegalAccessException
    {
        throwSetIllegalArgumentException(c);
    }

    public void setShort(Object obj, short s)
        throws IllegalArgumentException, IllegalAccessException
    {
        throwSetIllegalArgumentException(s);
    }

    public void setInt(Object obj, int i)
        throws IllegalArgumentException, IllegalAccessException
    {
        throwSetIllegalArgumentException(i);
    }

    public void setLong(Object obj, long l)
        throws IllegalArgumentException, IllegalAccessException
    {
        throwSetIllegalArgumentException(l);
    }

    public void setFloat(Object obj, float f)
        throws IllegalArgumentException, IllegalAccessException
    {
        throwSetIllegalArgumentException(f);
    }

    public void setDouble(Object obj, double d)
        throws IllegalArgumentException, IllegalAccessException
    {
        throwSetIllegalArgumentException(d);
    }
}
