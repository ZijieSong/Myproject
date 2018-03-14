package com.nowcoder;


import java.sql.Time;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class MyThread extends Thread{
    private int tid;
    public MyThread(int tid){
        this.tid  =tid;
    }

    @Override
    public void run() {
        try{
            for(int i =0; i<10;i++){
                Thread.sleep(1000);
                System.out.println(tid+":"+i);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class consumer implements Runnable{
    private BlockingQueue<String> bq;
    public consumer(BlockingQueue<String> bq){
        this.bq = bq;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(Thread.currentThread().getName() + ":" + bq.take());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class producer implements Runnable{
    private BlockingQueue<String> bq;
    public producer(BlockingQueue<String> bq){
        this.bq = bq;
    }
    @Override
    public void run() {
        try{
            for(int i =0; i<100 ;i++){
                Thread.sleep(1000);
                bq.put(""+i);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

public class MultiThreadTests {

    private static Object object = new Object();

    public static void synchronizedTest1(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (object) {
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(1000);
                            System.out.println("T2:" + i);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static void testThread(){
        for(int i =0; i<10;i++){
            //方法中的内部类要用到方法中的变量，要加final才能用
            final int fi = i;
            //new MyThread(i).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < 10; j++) {
                            Thread.sleep(1000);
                            System.out.println(fi+"::"+j);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
    private static int userId;
    private static void threadlocaltest1(){
        for(int i =0;i<10;i++){
            final int fi = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        threadLocal.set(fi);
                        Thread.sleep(1000);
                        System.out.println("T1:"+threadLocal.get());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void threadlocaltest2(){
        for(int i =0; i<10; i++){
            final int i1 = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        userId =i1;
                        Thread.sleep(1000);
                        System.out.println("T2:"+userId);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    public static void BQtest(){
        BlockingQueue<String> bq = new ArrayBlockingQueue<String>(10);
        new Thread(new producer(bq)).start();
        new Thread(new consumer(bq),"consumer1").start();
        new Thread(new consumer(bq),"consumer2").start();
    }

    private static void ExecutorTest(){
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    for(int i =0; i<10;i++){
                        Thread.sleep(1000);
                        System.out.println("T1:"+i);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    for(int i =0; i<10;i++){
                        Thread.sleep(1000);
                        System.out.println("T2:"+i);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        executorService.shutdown();
        while(!executorService.isTerminated()){
            try{
                    Thread.sleep(1000);
                    System.out.println("wait");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static int count =0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void withoutaotomicInteger(){
        for(int i=0; i<10; ++i){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(1000);
                        count++;
                        System.out.println(count);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void withaotomicInteger(){
        for(int i=0; i<10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(1000);
                        atomicInteger.incrementAndGet();
                        System.out.println(atomicInteger);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static void futureTest(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(3000);
                return 1;
            }
        });
        executorService.shutdown();
        System.out.println("begin");
        try {
            System.out.println(future.get());
            //System.out.println(future.get(1000, TimeUnit.MILLISECONDS));
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("end");
    }


    public static void main(String[] args){
        //testThread();
        //for(int i=0; i<10;i++) {
         //   synchronizedTest1();
        //}
        //BQtest();
//        threadlocaltest1();
        //threadlocaltest2();
        //ExecutorTest();
        //withoutaotomicInteger();
        //withaotomicInteger();
        futureTest();
    }
}
