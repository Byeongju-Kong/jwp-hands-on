package reflection;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class Junit4TestRunner {

    @Test
    void run() throws Exception {
        Class<Junit4Test> clazz = Junit4Test.class;
        Junit4Test object = new Junit4Test();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(MyTest.class)) {
                method.invoke(object);
            }
        }
        // TODO Junit4Test에서 @MyTest 애노테이션이 있는 메소드 실행
    }
}
