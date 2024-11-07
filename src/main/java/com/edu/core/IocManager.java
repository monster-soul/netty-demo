package com.edu.core;


import com.edu.web.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 依赖注入(Bean)管理
 *
 * @author DuHai
 * @since 2021/5/8 15:52
 **/
@Component
public class IocManager implements ListableBeanFactory, DisposableBean {

    private static IocManager instance;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ListableBeanFactory beanFactory;
    private final BeanDefinitionRegistry beanDefinitionRegistry;
    private final ConfigurableListableBeanFactory configurableListableBeanFactory;

    IocManager(ListableBeanFactory beanFactory) {
        if (instance != null) {
            throw new BaseException("IocManager不能重复构建");
        }
        this.beanFactory = beanFactory;
        this.beanDefinitionRegistry = (BeanDefinitionRegistry) this.beanFactory;
        instance = this;
        if (this.beanDefinitionRegistry == null) {
            this.logger.warn("IocManager无法使用BeanDefinitionRegistry相关的方法，因为beanFactory的类型为" + this.beanFactory.getClass().getSimpleName());
        }

        this.configurableListableBeanFactory = (ConfigurableListableBeanFactory) this.beanFactory;
        if (this.configurableListableBeanFactory == null) {
            this.logger.warn("IocManager无法使用ConfigurableListableBeanFactory相关的方法，因为beanFactory的类型为" + this.beanFactory.getClass().getSimpleName());
        }
    }

    /**
     * 当前实例
     *
     * @return
     */
    public static IocManager getInstance() {
        return instance;
    }

    /**
     * 获取指定类型所有bean的实例
     * 标注了{@link PriorityOrdered}会排在最前，其次按照{@link Ordered}排序，最后加载其他的
     *
     * @param type
     * @param <T>
     * @return
     * @throws BeansException
     */
    public <T> List<T> getBeanAll(Class<T> type) throws BeansException {
        String[] names = this.beanFactory.getBeanNamesForType(type, true, false);
        List<T> beans = new ArrayList<>(names.length);
        //已经加载了bean的名称
        Set<String> processedBeans = new HashSet<>();

        //region 标注了PriorityOrdered注解的Bean顺序靠前
        for (String name : names) {
            if (this.beanFactory.isTypeMatch(name, PriorityOrdered.class)) {
                T t = this.beanFactory.getBean(name, type);
                beans.add(t);
                processedBeans.add(name);
            }
        }
        //endregion

        //region 加载标注了Ordered注解的Bean
        for (String name : names) {
            if (!processedBeans.contains(name) && this.beanFactory.isTypeMatch(name, Ordered.class)) {
                T t = this.beanFactory.getBean(name, type);
                beans.add(t);
                processedBeans.add(name);
            }
        }
        //endregion

        for (String name : names) {
            if (!processedBeans.contains(name)) {
                T t = this.beanFactory.getBean(name, type);
                beans.add(t);
                processedBeans.add(name);
            }
        }

        return beans;
    }

    /**
     * 获取拥有指导构造函数参数的bean
     * @param requiredType
     * @param argMap
     * @param <T>
     * @return
     * @throws BeansException
     */
    public <T> T getBean(Class<T> requiredType, Map<String, Object> argMap) throws BeansException {
        String[] names = this.getBeanNames(requiredType);
        if (names.length > 1) {
            List<String> primaryNames = Arrays.stream(names).filter(row -> this.getBeanDefinition(row).isPrimary()).collect(Collectors.toList());
            names = primaryNames.toArray(new String[primaryNames.size()]);
        }
        Constructor<?> matchConstructor = null;//匹配到的构造函数
        int maxSimilarity = 0;//相似度
        for (String name : names) {
            BeanDefinition definition = this.getBeanDefinition(name);
            Constructor<?>[] constructors = null;
            try {
                constructors = Class.forName(definition.getBeanClassName()).getConstructors();
            } catch (ClassNotFoundException e) {
//                throw new BusinessException("getBean异常，无法获取实现类构造函数" + definition.getBeanClassName());
            }
            for (Constructor<?> constructor : constructors) {
                Parameter[] parameters = constructor.getParameters();
                Integer similarity = 0;
                for (Parameter parameter : parameters) {
                    if (argMap.containsKey(parameter.getName())) {
                        similarity++;
                    }
                }
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    matchConstructor = constructor;
                }
            }
        }
        if (matchConstructor == null) {
//            throw new BusinessException("请检查输入参数，入参没有匹配的构造函数");
        }
        Parameter[] parameters = matchConstructor.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (argMap.containsKey(parameter.getName())) {
                args[i] = argMap.get(parameter.getName());
            } else {
                if (this.containsBean((Class<?>) parameter.getParameterizedType())) {
                    args[i] = this.getBean((Class<?>) parameter.getParameterizedType());
                } else {
//                    throw new BusinessException("再构造" + requiredType.getName() + "时，" + parameter.getParameterizedType().getTypeName() + "不是Bean");
                }
            }
        }
        return (T) this.beanFactory.getBean(matchConstructor.getDeclaringClass(), args);
    }

    /**
     * 获取指定Bean的名称
     *
     * @param type
     * @return
     */
    public String[] getBeanNames(Class<?> type) {
        String[] names = this.beanFactory.getBeanNamesForType(type, true, false);
        return names;
    }

    /**
     * 检查指定bean是否存在
     *
     * @param type
     * @return
     */
    public boolean containsBean(Class<?> type) {
        String[] names = this.getBeanNames(type);
        return names != null && names.length > 0;
    }

    /**
     * 替换指定beanName的实现
     * 替换会导致依赖与当前bean的所有bean销毁（包括单例对象），替换后的新的实现类，不会马上实例化会在第一次使用时实例化
     * 建议替换应该在依赖当前bean都未实列化时进行
     *
     * @param beanName
     * @param clazz
     */
    public void replace(String beanName, Class clazz) {
        if (this.beanDefinitionRegistry == null) {
//            throw new BusinessException("IocManager无法使用BeanDefinitionRegistry相关的方法，因为beanFactory的类型为" + this.beanFactory.getClass().getSimpleName());
        }
        String[] dependentBeanNames = ((DefaultListableBeanFactory) this.beanDefinitionRegistry).getDependentBeans(beanName);
        BeanDefinition beanDefinition = this.beanDefinitionRegistry.getBeanDefinition(beanName);
        this.beanDefinitionRegistry.removeBeanDefinition(beanName);
        beanDefinition.setBeanClassName(clazz.getCanonicalName());
        this.beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
        //重新实例化依赖的Bean
        if (dependentBeanNames != null && dependentBeanNames.length > 0) {
            for (String dependentBeanName : dependentBeanNames) {
                if(this.containsBean(dependentBeanName)) {
                    this.getBean(dependentBeanName);
                }
            }
        }
    }

    /**
     * 替换指定类型Bean的实现
     * 注意：替换Bean会同时销毁依赖此Bean的所有Bean，并且这些Bean会在后续第一次使用时初始化
     *
     * @param beanType          bean的类型
     * @param implementBeanType bean新的实现
     */
    public void replace(Class<?> beanType, Class<?> implementBeanType) {
        String[] beanNames = this.beanFactory.getBeanNamesForType(beanType);
        for (String beanName : beanNames) {
            this.replace(beanName, implementBeanType);
        }
    }

    /**
     * 根据bean的注解进行注册
     *
     * @param beanName
     * @param beanClass
     * @param scope     {@link ConfigurableBeanFactory}
     */
    public void registerBeanDefinitionByAnnotated(String beanName, Class beanClass, String scope) {
        AnnotatedBeanDefinition annotatedBeanDefinition = new AnnotatedGenericBeanDefinition(beanClass);
        if (StringUtils.isEmpty(scope)) {
            annotatedBeanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
        } else {
            annotatedBeanDefinition.setScope(scope);
        }
        this.beanDefinitionRegistry.registerBeanDefinition(beanName, annotatedBeanDefinition);
    }

    /**
     * 根据bean的注解进行注册
     *
     * @param beanName
     * @param beanDefinition bean的定义信息
     */
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        this.beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
    }

    /**
     * 移除bean定义
     *
     * @param beanName
     */
    public void removeBeanDefinition(String beanName) {
        this.beanDefinitionRegistry.removeBeanDefinition(beanName);
    }

    /**
     * 销毁指定bean实例
     *
     * @param existingBean
     */
    public void destroyBean(Object existingBean) {
        if (this.configurableListableBeanFactory == null) {
            this.logger.warn("IocManager无法使用ConfigurableListableBeanFactory相关的方法，因为beanFactory的类型为" + this.beanFactory.getClass().getCanonicalName());
            return;
        }
        String[] names = this.beanFactory.getBeanNamesForType(existingBean.getClass(), true, false);
        for (String name : names) {
            BeanDefinition beanDefinition = this.configurableListableBeanFactory.getBeanDefinition(name);
            if (beanDefinition.isSingleton()) {
                //单例对象不进行释放
                if (this.getBean(name).equals(existingBean)) {
                    return;
                }
            }
        }
        this.configurableListableBeanFactory.destroyBean(existingBean);
    }

    /**
     * 获取bean的定义信息
     *
     * @param beanName
     * @return
     */
    public BeanDefinition getBeanDefinition(String beanName) {
        if (this.beanDefinitionRegistry == null) {
//            throw new BusinessException("IocManager无法使用BeanDefinitionRegistry相关的方法，因为beanFactory的类型为" + this.beanFactory.getClass().getCanonicalName());
        }
        BeanDefinition beanDefinition = this.beanDefinitionRegistry.getBeanDefinition(beanName);
        return beanDefinition;
    }

    public ListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override
    public void destroy() throws Exception {
        instance = null;
    }

    //region ListableBeanFactory方法

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanFactory.containsBeanDefinition(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanFactory.getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return this.beanFactory.getBeanDefinitionNames();
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
        return this.beanFactory.getBeanProvider(requiredType, allowEagerInit);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType, boolean allowEagerInit) {
        return this.beanFactory.getBeanProvider(requiredType, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type) {
        return this.beanFactory.getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        return this.beanFactory.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return this.beanFactory.getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return this.beanFactory.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return this.beanFactory.getBeansOfType(type);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        return this.beanFactory.getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        return this.beanFactory.getBeanNamesForAnnotation(annotationType);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        return this.beanFactory.getBeansWithAnnotation(annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        return this.beanFactory.findAnnotationOnBean(beanName, annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return this.beanFactory.findAnnotationOnBean(beanName, annotationType, allowFactoryBeanInit);
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return this.beanFactory.getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return this.beanFactory.getBean(name, requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return this.beanFactory.getBean(name, args);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return this.beanFactory.getBean(requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return this.beanFactory.getBean(requiredType, args);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        return this.beanFactory.getBeanProvider(requiredType);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return this.beanFactory.getBeanProvider(requiredType);
    }

    @Override
    public boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return this.beanFactory.isSingleton(name);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return this.beanFactory.isPrototype(name);
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return this.beanFactory.isTypeMatch(name, typeToMatch);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return this.beanFactory.isTypeMatch(name, typeToMatch);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getType(name);
    }

    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getType(name, allowFactoryBeanInit);
    }

    @Override
    public String[] getAliases(String name) {
        return this.beanFactory.getAliases(name);
    }

    //endregion
}
