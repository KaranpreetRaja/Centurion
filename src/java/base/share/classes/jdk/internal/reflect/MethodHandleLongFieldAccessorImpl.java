/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class MethodHandleLongFieldAccessorImpl extends MethodHandleFieldAccessorImpl {
    static FieldAccessorImpl fieldAccessor(Field field, MethodHandle getter, MethodHandle setter, boolean isReadOnly) {
        boolean isStatic = Modifier.isStatic(field.getModifiers());
        if (isStatic) {
            getter = getter.asType(MethodType.methodType(long.class));
            if (setter != null) {
                setter = setter.asType(MethodType.methodType(void.class, long.class));
            }
        } else {
            getter = getter.asType(MethodType.methodType(long.class, Object.class));
            if (setter != null) {
                setter = setter.asType(MethodType.methodType(void.class, Object.class, long.class));
            }
        }
        return new MethodHandleLongFieldAccessorImpl(field, getter, setter, isReadOnly, isStatic);
    }

    MethodHandleLongFieldAccessorImpl(Field field, MethodHandle getter, MethodHandle setter, boolean isReadOnly, boolean isStatic) {
        super(field, getter, setter, isReadOnly, isStatic);
    }

    public Object get(Object obj) throws IllegalArgumentException {
        return Long.valueOf(getLong(obj));
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
        try {
            if (isStatic()) {
                return (long) getter.invokeExact();
            } else {
                return (long) getter.invokeExact(obj);
            }
        } catch (IllegalArgumentException|NullPointerException e) {
            throw e;
        } catch (ClassCastException e) {
            throw newGetIllegalArgumentException(obj);
        } catch (Throwable e) {
            throw new InternalError(e);
        }
    }

    public float getFloat(Object obj) throws IllegalArgumentException {
        return getLong(obj);
    }

    public double getDouble(Object obj) throws IllegalArgumentException {
        return getLong(obj);
    }

    public void set(Object obj, Object value)
            throws IllegalArgumentException, IllegalAccessException
    {
        ensureObj(obj);
        if (isReadOnly()) {
            throwFinalFieldIllegalAccessException(value);
        }

        if (value == null) {
            throwSetIllegalArgumentException(value);
        }

        if (value instanceof Byte b) {
            setLong(obj, b.byteValue());
        }
        else if (value instanceof Short s) {
            setLong(obj, s.shortValue());
        }
        else if (value instanceof Character c) {
            setLong(obj, c.charValue());
        }
        else if (value instanceof Integer i) {
            setLong(obj, i.intValue());
        }
        else if (value instanceof Long l) {
            setLong(obj, l.longValue());
        }
        else {
            throwSetIllegalArgumentException(value);
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
        setLong(obj, b);
    }

    public void setChar(Object obj, char c)
        throws IllegalArgumentException, IllegalAccessException
    {
        setLong(obj, c);
    }

    public void setShort(Object obj, short s)
        throws IllegalArgumentException, IllegalAccessException
    {
        setLong(obj, s);
    }

    public void setInt(Object obj, int i)
        throws IllegalArgumentException, IllegalAccessException
    {
        setLong(obj, i);
    }

    public void setLong(Object obj, long l)
        throws IllegalArgumentException, IllegalAccessException
    {
        if (isReadOnly()) {
            ensureObj(obj);     // throw NPE if obj is null on instance field
            throwFinalFieldIllegalAccessException(l);
        }
        try {
            if (isStatic()) {
                setter.invokeExact(l);
            } else {
                setter.invokeExact(obj, l);
            }
        } catch (IllegalArgumentException|NullPointerException e) {
            throw e;
        } catch (ClassCastException e) {
            // receiver is of invalid type
            throw newSetIllegalArgumentException(obj);
        } catch (Throwable e) {
            throw new InternalError(e);
        }
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
