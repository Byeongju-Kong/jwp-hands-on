package reflection;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class Junit3TestRunner {

    @Test
    void run() throws Exception {
        Class<Junit3Test> clazz = Junit3Test.class;
        Junit3Test object = new Junit3Test();
        Method method = clazz.getDeclaredMethod("test1");
        method.invoke(object);

        // TODO Junit3Test에서 test로 시작하는 메소드 실행
    }
}
