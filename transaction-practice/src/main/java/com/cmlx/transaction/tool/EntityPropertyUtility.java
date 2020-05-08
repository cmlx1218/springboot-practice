package com.cmlx.transaction.tool;

import com.cmlx.transaction.tool.model.EntityPropertyInfo;
import lombok.experimental.UtilityClass;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对象属性工具类
 *
 * @author Gnoll
 * @create 2017-06-16 17:00
 **/
@UtilityClass
public class EntityPropertyUtility extends BeanUtils {

    private final Map<Class<?>, PropertyDescriptor[]> cacheBeanPropertyDes = new ConcurrentHashMap<>();
    private final Map<Class<?>, PropertyDescriptor[]> cacheEntityPropertyDes = new ConcurrentHashMap<>();
    private final List<String> excludes = new ArrayList<>();

    private final List<Class<?>> notNullProperty = new ArrayList<>();
    private final List<Class<?>> allProperty = new ArrayList<>();
    private final Map<Class<?>, EntityPropertyInfo> cacheNotNullPropertyInfo = new ConcurrentHashMap<>();
    private final Map<Class<?>, EntityPropertyInfo> cacheAllPropertyInfo = new ConcurrentHashMap<>();
    private static Map<Class<?>, PropertyDescriptor[]> cacheBeanProDes = new ConcurrentHashMap<>();

//    static {
//        excludes.add("class");
//
//        notNullProperty.add(Integer.class);
//        notNullProperty.add(Short.class);
//        notNullProperty.add(Byte.class);
//        notNullProperty.add(Float.class);
//        notNullProperty.add(Double.class);
//        notNullProperty.add(Long.class);
//        notNullProperty.add(Boolean.class);
//        notNullProperty.add(Character.class);
//        notNullProperty.add(String.class);
//        notNullProperty.add(Timestamp.class);
//        notNullProperty.add(java.sql.Date.class);
//        notNullProperty.add(Time.class);
//        notNullProperty.add(BigDecimal.class);
//
//        allProperty.add(int.class);
//        allProperty.add(Integer.class);
//        allProperty.add(short.class);
//        allProperty.add(Short.class);
//        allProperty.add(byte.class);
//        allProperty.add(Byte.class);
//        allProperty.add(float.class);
//        allProperty.add(Float.class);
//        allProperty.add(double.class);
//        allProperty.add(Double.class);
//        allProperty.add(long.class);
//        allProperty.add(Long.class);
//        allProperty.add(boolean.class);
//        allProperty.add(Boolean.class);
//        allProperty.add(char.class);
//        allProperty.add(Character.class);
//        allProperty.add(String.class);
//        allProperty.add(Timestamp.class);
//        allProperty.add(java.sql.Date.class);
//        allProperty.add(Time.class);
//        allProperty.add(BigDecimal.class);
//    }

    /**
     * 获取对象属性信息
     *
     * @param clazz
     * @param all   所有字段或者非空字段
     * @return
     */
    public EntityPropertyInfo getProperty(Class<?> clazz, boolean all) {
        /**
         * Assert 断言，断定某一个实际的值是自己想要的
         */
        Assert.notNull(clazz, "Class required");

        EntityPropertyInfo entityPropertyInfo = all ? cacheAllPropertyInfo.get(clazz) : cacheNotNullPropertyInfo.get(clazz);
        if (null != entityPropertyInfo) {
            return entityPropertyInfo;
        }
        entityPropertyInfo = getEntityPropertyInfo(clazz, all ? allProperty : notNullProperty);
        if (all) {
            cacheAllPropertyInfo.put(clazz, entityPropertyInfo);
        } else {
            cacheNotNullPropertyInfo.put(clazz, entityPropertyInfo);
        }
        return entityPropertyInfo;
    }

    /**
     * 获取对象指定属性字段信息
     *
     * @param clazz
     * @param include
     * @return
     */
    public EntityPropertyInfo getEntityPropertyInfo(Class<?> clazz, List<Class<?>> include) {
        Assert.notNull(clazz, "Class required");
        if (null == include) {
            include = new ArrayList<>();
        }
        PropertyDescriptor primaryKey = null;
        List<PropertyDescriptor> otherKeys = new ArrayList<>();
        PropertyDescriptor[] propertyDescriptor = getEntityPropertyDescriptor(clazz);
        for (PropertyDescriptor descriptor : propertyDescriptor) {
            /*if (checkAnnotation(clazz, descriptor, Id.class) || checkAnnotation(clazz, descriptor, EmbeddedId.class)) {
                primaryKey = descriptor;
                continue;
            }*/
            if (include.contains(descriptor.getPropertyType())) {
                otherKeys.add(descriptor);
            }
        }
        Assert.notNull(primaryKey, clazz.getName() + " cant find the primary key annotation");
//        Assert.notEmpty(otherKeys, clazz.getName() + " cant find the other property");
        return new EntityPropertyInfo(primaryKey, otherKeys.toArray(new PropertyDescriptor[otherKeys.size()]));
    }

    /**
     * 获取对象的所有属性，根据excludes列表过滤
     *
     * @param clazz
     * @return
     */
    public PropertyDescriptor[] getBeanPropertyDescriptor(Class<?> clazz) {
        PropertyDescriptor[] propertyDescriptors = cacheBeanPropertyDes.get(clazz);
        if (null == propertyDescriptors) {
            PropertyDescriptor[] propertyDesList = BeanUtils.getPropertyDescriptors(clazz);
            List<PropertyDescriptor> list = new ArrayList<>();
            for (PropertyDescriptor propertyDes : propertyDesList) {
                if (excludes.contains(propertyDes.getName())) {
                    continue;
                }
                list.add(propertyDes);
            }
            propertyDescriptors = new PropertyDescriptor[list.size()];
            cacheBeanPropertyDes.put(clazz, list.toArray(propertyDescriptors));
        }
        return propertyDescriptors;
    }

    /**
     * 获取Entity对象所有属性，过滤class属性和拥有Transient注解的属性
     *
     * @param clazz
     * @return
     */
    public PropertyDescriptor[] getEntityPropertyDescriptor(Class<?> clazz) {
        PropertyDescriptor[] propertyDescriptors = cacheEntityPropertyDes.get(clazz);
        if (null == propertyDescriptors) {
            PropertyDescriptor[] propertyDesList = BeanUtils.getPropertyDescriptors(clazz);
            List<PropertyDescriptor> list = new ArrayList<>();
            for (PropertyDescriptor propertyDes : propertyDesList) {
                if ("class".equals(propertyDes.getName())) {
                    continue;
                }
                //FIXME
                //if (checkAnnotation(clazz, propertyDes, Transient.class)) continue;
                list.add(propertyDes);
            }
            propertyDescriptors = new PropertyDescriptor[list.size()];
            cacheEntityPropertyDes.put(clazz, list.toArray(propertyDescriptors));
        }
        return propertyDescriptors;
    }

    /**
     * 检查Entity对象某个属性是否有某个注解
     *
     * @param clazz           Entity对象
     * @param proDes          属性
     * @param annotationClass 注解
     * @return
     */
    public boolean checkAnnotation(Class<?> clazz, PropertyDescriptor proDes, Class<? extends Annotation> annotationClass) {
        Assert.notNull(clazz, " Class required");
        Assert.notNull(proDes, "PropertyDescriptor required");
        Assert.notNull(annotationClass, "Annotation class required");
        Method readMethod = proDes.getReadMethod();
        Method method;
        try {
            method = clazz.getMethod(readMethod.getName(), readMethod.getParameterTypes());
            if (null != method && null != method.getAnnotation(annotationClass)) {
                return true;
            }
        } catch (NoSuchMethodException e) {
        }
        try {
            method = clazz.getDeclaredMethod(readMethod.getName(), readMethod.getParameterTypes());
            if (null != method && null != method.getAnnotation(annotationClass)) {
                return true;
            }
        } catch (NoSuchMethodException e) {
        }

        Field field;
        try {
            field = clazz.getField(proDes.getName());
            if (null != field && null != field.getAnnotation(annotationClass)) {
                return true;
            }
        } catch (NoSuchFieldException e) {
        }
        try {
            field = clazz.getDeclaredField(proDes.getName());
            if (null != field && null != field.getAnnotation(annotationClass)) {
                return true;
            }
        } catch (NoSuchFieldException e) {
        }
        Class<?> superclass = clazz.getSuperclass();
        if (null == superclass) {
            return false;
        }
        return checkAnnotation(superclass, proDes, annotationClass);
    }

    /**
     * 获取注解为@Id的属性值
     *
     * @param entity
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object getValueByAnnotationId(Object entity) throws InvocationTargetException, IllegalAccessException {
        Assert.notNull(entity, "entity must be not null");
        EntityPropertyInfo allProperty = getProperty(entity.getClass(), true);
        Method readMethod = allProperty.getPrimaryKey().getReadMethod();
        return readMethod.invoke(entity);
    }

    /**
     * 获取对象包含某个注解的所有属性名称
     *
     * @param annotationClass
     * @return
     */
    public Collection<String> hasAnnotationPropertys(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Assert.notNull(clazz, "Class required");
        Assert.notNull(annotationClass, "Annotation class required");
        Set<String> filedNames = new HashSet<>();
        PropertyDescriptor[] beanPropertyDescriptor = getBeanPropertyDescriptor(clazz);
        for (PropertyDescriptor propertyDes : beanPropertyDescriptor) {
            if (checkAnnotation(clazz, propertyDes, annotationClass)) {
                filedNames.add(propertyDes.getName());
            }
        }
        Class<?> superclass = clazz.getSuperclass();
        if (null != superclass) {
            filedNames.addAll(hasAnnotationPropertys(superclass, annotationClass));
        }
        return filedNames;
    }

    /**
     * 复制非空属性
     *
     * @param origin
     * @param target
     */
    public static void copyNotNull(Object origin, Object target) {
        Assert.notNull(origin, "Origin required");
        Assert.notNull(target, "Target required");
        if (!origin.getClass().equals(target.getClass()) && !origin.getClass().isInstance(target)) {
            BeanUtils.copyProperties(origin, target, getNullPropertyNames(origin));
        } else {
            PropertyDescriptor[] beanPropertyDescriptor = getBeanPropertyDescriptor(origin.getClass());
            for (PropertyDescriptor descriptor : beanPropertyDescriptor) {
                Method readMethod = descriptor.getReadMethod();
                Method writeMethod = descriptor.getWriteMethod();
                Class<?> returnType = readMethod.getReturnType();
                if (returnType.isPrimitive()) {
                    continue;
                }
                try {
                    Object invoke = readMethod.invoke(origin);
                    if (null != invoke) {
                        writeMethod.invoke(target, invoke);
                    }
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }
    }

    public Map entityToMap(Object object) {
        Assert.notNull(object, "Object required");
        PropertyDescriptor[] beanPropertyDescriptor = getBeanPropertyDescriptor(object.getClass());
        Map<String, Object> map = new HashMap<>();
        for (PropertyDescriptor descriptor : beanPropertyDescriptor) {
            Method readMethod = descriptor.getReadMethod();
            Class<?> returnType = readMethod.getReturnType();
            if (returnType.isPrimitive()) {
                continue;
            }
            try {
                map.put(descriptor.getDisplayName(), readMethod.invoke(object));
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }
        return map;
    }

    /**
     * 实现键值对集合中将对应的键值,赋值给目标对象的属性
     * 前提:键的值必须和目标对象的属性名一致,基本数据类型必须是字符串格式!!!
     * 可以实现任意类型的赋值
     * @param map 键值对集合
     * @param o   要赋值的目标对象
     */
    public void mapToEntity(Map map,Object o){
        Class c=o.getClass();  //创建目标对象的class对象
        Field[] fields=c.getDeclaredFields(); //得到目标对象的所有属性
        for (Field f : fields) { //遍历属性集合
            if(map.get(f.getName())!=null){//如果属性名在集合中,有对应的键值对  进入赋值处理
                String methodName="set"+f.getName().substring(0,1).toUpperCase()+f.getName().substring(1);
                //拼接set方法的名字
                try {
                    Method m=c.getDeclaredMethod(methodName, f.getType());//创建set方法的method对象
                    Object par=map.get(f.getName()); //通过键得到值的对象  执行set方法赋值时 作为参数传递
                    Class typeClass=f.getType(); //得到属性的类型的class对象
                    if(typeClass.isPrimitive()){ //判断是否为基础数据类型 基础数据类型需要特殊处理
                        String p=(String)par; //所有的基本类型都可以转换为string

                        if("int".equals(typeClass.getName())){ //int的包装类名字特殊 需要单独处理
                            par=Integer.parseInt(p);
                        } else if("char".equals(typeClass.getName())){//char的包装类名字特殊 需要单独处理
                            par=p.charAt(0); //char类型只能存储一个字符  所以直接截取
                        }else{
                            //除了int和char外 其他包装类都是基本类型名首字母大写 可以利用反射调用对应的parse方法 实现转换为基本数据类型
                            String className=typeClass.getName().substring(0,1).toUpperCase()+typeClass.getName().substring(1);
                            //将基本类型名首字母大写 拼接成包装类名
                            Class packageClass=Class.forName("java.lang."+className);//通过类名得到对应的包装类class对象
                            Method parseMethod=packageClass.getDeclaredMethod("parse"+className, String.class);//得到对应的parse方法
                            par= parseMethod.invoke(par, p);//调用parse方法 将字符串转换为对应的基本数据类型
                        }

                    }else{        //如果不是基本数据类型  直接调用属性类型的class对象的cast方法 将参数直接强转为,属性类型的对象
                        par=typeClass.cast(par);
                    }

                    m.invoke(o, par); //执行set方法赋值 par已经是处理完 和属性类型,对应的类型的值

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }

    }

    public <K,V> Map<K,V> translationToMap(String key,List<V> objects) throws Exception {
        if (CollectionUtils.isEmpty(objects)) {
            return new HashMap<>();
        }
        Map<K, V> map = new HashMap<>(objects.size());
        for (V object : objects) {
            PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(object.getClass(), key);
            Method readMethod = propertyDescriptor.getReadMethod();
            Object keyValue = readMethod.invoke(object);
            map.put((K)keyValue, object);
        }
        return map;
    }

    public Map<String, Object> entityToMapNotNullProperty(Object object) {
        Map<String, Object> map = entityToMap(object);
        Map<String, Object> nullAbleMap = new HashMap<>();
        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            if (stringObjectEntry.getValue() != null) {
                nullAbleMap.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
            }
        }
        return nullAbleMap;
    }

    /**
     * 复制所有选定属性
     *
     * @param origin
     * @param target
     * @param include
     * @param property
     */
    public void copy(Object origin, Object target, boolean include, String... property) {
        Assert.notNull(origin, "Origin required");
        Assert.notNull(target, "Target required");
        if (!origin.getClass().equals(target.getClass()) && !origin.getClass().isInstance(target)) {
            if (!include) {
                BeanUtils.copyProperties(origin, target, property);
            } else if (null == property) {
                BeanUtils.copyProperties(origin, target);
            } else {
                PropertyDescriptor[] descriptors = getBeanPropertyDescriptor(origin.getClass());
                List<String> ignoreProper = new ArrayList<>();
                List<String> includeProper = Arrays.asList(property);
                for (PropertyDescriptor descriptor : descriptors) {
                    String propertyName = descriptor.getName();
                    if (includeProper.contains(propertyName)) {
                        continue;
                    }
                    ignoreProper.add(propertyName);
                }
                BeanUtils.copyProperties(origin, target, ignoreProper.toArray(new String[ignoreProper.size()]));
            }
        } else {
            PropertyDescriptor[] descriptors = getBeanPropertyDescriptor(origin.getClass());
            List<String> proper = new ArrayList<>();
            proper.addAll(Arrays.asList(property));
            for (PropertyDescriptor descriptor : descriptors) {
                String propertyName = descriptor.getName();
                if (proper.size() > 0 && include ? !proper.contains(propertyName) : proper.contains(propertyName)) {
                    continue;
                }
                Method readMethod = descriptor.getReadMethod();
                Method writeMethod = descriptor.getWriteMethod();
                try {
                    Object value = readMethod.invoke(origin);
                    writeMethod.invoke(target, value);
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }
    }

    public String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public PropertyDescriptor[] getSclarProDes(Class<?> clazz) throws IntrospectionException {
        PropertyDescriptor[] proDescriptors = cacheBeanProDes.get(clazz);
        if (null == proDescriptors) {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            List<PropertyDescriptor> list = new ArrayList<>();
            proDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : proDescriptors) {
                if ("class".equals(descriptor.getName())) {
                    continue;
                }
                //FIXME
                //if (checkAnnotation(clazz, descriptor, Transient.class)) continue;
                list.add(descriptor);
            }
            proDescriptors = new PropertyDescriptor[list.size()];
            cacheBeanProDes.put(clazz, list.toArray(proDescriptors));
        }
        return proDescriptors;
    }
}
