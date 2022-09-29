package nextstep.study.di.stage4.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.study.di.stage3.context.NoSuchBeanDefinitionException;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContainer {

    private final Set<Object> beans;

    public DIContainer(final Set<Class<?>> classes) {
        Set<Object> beans = classes.stream()
                .map(this::create)
                .collect(Collectors.toSet());
        beans.forEach(bean -> injectDependency(bean, beans));
        this.beans = beans;
    }

    public static DIContainer createContainerForPackage(final String rootPackageName) {
        Set<Class<?>> classes = ClassPathScanner.getAllClassesInPackage(rootPackageName);
        return new DIContainer(classes);
    }

    private Object create(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    private void injectDependency(Object bean, Set<Object> beans) {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                inject(bean, beans, field);
            }
        }
    }

    private void inject(Object bean, Set<Object> beans, Field field) {
        for (Object injectedBean : beans) {
            if (field.getType().isInstance(injectedBean)) {
                field.setAccessible(true);
                try {
                    field.set(bean, injectedBean);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                field.setAccessible(false);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        return (T) beans.stream()
                .filter(bean -> bean.getClass().equals(aClass))
                .findAny()
                .orElseThrow(NoSuchBeanDefinitionException::new);
    }
}
