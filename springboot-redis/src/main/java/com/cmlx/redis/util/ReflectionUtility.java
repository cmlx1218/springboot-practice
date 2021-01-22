package com.cmlx.redis.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author cmlx
 * @Date -> 2021/1/22 16:40
 * @Desc ->
 **/
@Slf4j
@UtilityClass
public class ReflectionUtility {

    public Set<Class<?>> loadClassesByAnnotationClass(Class<? extends Annotation> annotationClass, String... packageNames) throws IOException, ClassNotFoundException {
        String annotationClassName = annotationClass.getName();
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(patternResolver);

        Set<Class<?>> classes = new HashSet<>();

        for (String packageName : packageNames) {
            String locationPattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName)) +
                    "/" + "**/*.class";
            Resource[] resources = patternResolver.getResources(locationPattern);
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }

                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

                if (!annotationMetadata.hasAnnotation(annotationClassName)) {
                    continue;
                }

                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> loadedClass = ClassUtils.forName(className, null);
                classes.add(loadedClass);
            }
        }

        return classes;
    }

    public Set<Class<?>> loadClassesByAnnotationClass(Class<? extends Annotation>[] annotationClass, String... packageNames) throws IOException, ClassNotFoundException {
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(patternResolver);

        Set<Class<?>> classes = new HashSet<>();

        for (String packageName : packageNames) {
            String locationPattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName)) +
                    "/" + "**/*.class";
            Resource[] resources = patternResolver.getResources(locationPattern);
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }

                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

                boolean flag = false;
                for (Class<? extends Annotation> aClass : annotationClass) {
                    String name = aClass.getName();
                    if (annotationMetadata.hasAnnotation(name)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) continue;

                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> aClass = ClassUtils.forName(className, null);
                classes.add(aClass);
            }
        }
        return classes;
    }

}
