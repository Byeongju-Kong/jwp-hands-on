package concurrency.stage0;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 스레드 풀은 무엇이고 어떻게 동작할까?
 * 테스트를 통과시키고 왜 해당 결과가 나왔는지 생각해보자.
 *
 * Thread Pools
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/pools.html
 *
 * Introduction to Thread Pools in Java
 * https://www.baeldung.com/thread-pool-java-and-guava
 */
class ThreadPoolsTest {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolsTest.class);

    @Test
    void testNewFixedThreadPool() {
        final var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 2;
        final int expectedQueueSize = 1;

        assertThat(executor.getPoolSize()).isEqualTo(expectedPoolSize);
        assertThat(executor.getQueue().size()).isEqualTo(expectedQueueSize);

        /**
         * Thread의 개수를 2로 설정해두었음. 따라서 CorePoolSize는 2
         * executor가 submit하는 task의 개수는 3개임
         * 따라서, 1개의 일은 당장 처리하지 못함
         * 여기서, 새로운 Thread를 생성하는 것이 아니라, workQueue에 Runnable 객체를 실행 대기 시켜둠
         * 그럼, 언제 새로운 Thread를 생성하나? Queue가 가득찼을 떄임.
         * 그런데, Queue의 size를 설정해주지 않으면 size는 Integer.MAX_VALUE가 지정됨
         * 따라서, 거의 새로운 Thread가 생성될 일이 없음.
         * 새로운 Thread를 생성하게 함으로써 Task를 진행하도록 하려면 workQueue의 size를 지정해줘야함.
         */
    }

    @Test
    void testNewCachedThreadPool() {
        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 3;
        final int expectedQueueSize = 0;

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());
    }

    private Runnable logWithSleep(final String message) {
        return () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info(message);
        };
    }
}
