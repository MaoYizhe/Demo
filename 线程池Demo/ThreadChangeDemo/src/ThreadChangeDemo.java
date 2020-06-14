import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadChangeDemo {
    public static void main(String[] args) throws InterruptedException{
        dynamicModifyExecutor();
    }

    private static ThreadPoolExecutor buildThreadPoolExecutor(){
        return new ThreadPoolExecutor(2,
                5,
                60,
                TimeUnit.SECONDS,
                new MyLinkedBlockingQueue<>(10));
//                new LinkedBlockingDeque<>(10));
//                new NamedThreadFactory("why技术"));
    }

    private static void dynamicModifyExecutor() throws InterruptedException{
        ThreadPoolExecutor executor = buildThreadPoolExecutor();
        for(int i = 0; i<15; i++){
            executor.submit(()->{
                threadPoolStatus(executor,"创建任务");
                try{
                    TimeUnit.SECONDS.sleep(10);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            });
        }
        threadPoolStatus(executor,"改变之前");
        TimeUnit.SECONDS.sleep(1);
        executor.setCorePoolSize(10);
        executor.setMaximumPoolSize(10);
        executor.prestartAllCoreThreads();
        MyLinkedBlockingQueue queue = (MyLinkedBlockingQueue) executor.getQueue();
        queue.setCapacity(100);
        threadPoolStatus(executor,"改变之后");
        Thread.currentThread().join();
    }


    private static void threadPoolStatus(ThreadPoolExecutor executor, String name){
        MyLinkedBlockingQueue queue = (MyLinkedBlockingQueue) executor.getQueue();
        System.out.println(Thread.currentThread().getName() + "-" + name + "-:" +
                " 核心线程数：" + executor.getCorePoolSize() +
                " 活动线程数：" + executor.getActiveCount() +
                " 最大线程数：" + executor.getMaximumPoolSize() +
                " 线程池活跃度：" + divide(executor.getActiveCount(),executor.getMaximumPoolSize()) +
                " 任务完成数：" + executor.getCompletedTaskCount() +
                " 队列大小:" + (queue.size() + queue.remainingCapacity()) +
                " 当前队列线程数：" +queue.size() +
                " 队列剩余大小：" + queue.remainingCapacity() +
                " 队列使用度：" +divide(queue.size() , queue.size() + queue.remainingCapacity())
                );
    }

    private static String divide(int num1, int num2){
        return String.format("%1.2f%%",
                Double.parseDouble(num1 + "")/Double.parseDouble(num2 + "")*100);
    }
}
