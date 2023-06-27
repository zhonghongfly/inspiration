package com.zhonghong;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程交替执行
 * @author zhonghong
 */
public class AlternateExecution {

    private final int num = 10;

    public void condition() {
        Lock lock = new ReentrantLock();
        Condition c1 = lock.newCondition();
        Condition c2 = lock.newCondition();
        Condition c3 = lock.newCondition();

        Thread t1 = createThread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < num; i++) {
                lock.lock();
                try {
                    System.out.println("A");
                    c2.signalAll();
                    c1.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
        });
        Thread t2 = createThread(() -> {
            for (int i = 0; i < num; i++) {
                lock.lock();
                try {
                    c2.await();
                    System.out.println("B");
                    c3.signalAll();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
        });
        Thread t3 = createThread(() -> {
            for (int i = 0; i < num; i++) {
                lock.lock();
                try {
                    c3.await();
                    System.out.println("C");
                    c1.signalAll();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
        });
        t1.start();
        t2.start();
        t3.start();
    }
    public void lockSupport() {
        final Thread t1, t3;
        final AtomicReference<Thread> t2 = new AtomicReference<>();
        t1 = createThread(() -> {
            for (int i = 0; i < num; i++) {
                System.out.println("A");
                LockSupport.unpark(t2.get());
                LockSupport.park();
            }
        });
        t3 = createThread(() -> {
            for (int i = 0; i < num; i++) {
                LockSupport.park();
                System.out.println("C");
                LockSupport.unpark(t1);
            }
        });
        t2.set(createThread(() -> {
            for (int i = 0; i < num; i++) {
                LockSupport.park();
                System.out.println("B");
                LockSupport.unpark(t3);
            }
        }));
        t1.start();
        t2.get().start();
        t3.start();
    }

    public void waitNotify() {
        Object o1 = new Object();
        Object o2 = new Object();
        Thread t1 = createThread(() -> {
            for (int i = 0; i < num; i++) {
                synchronized (o1) {
                    System.out.println("A");
                    try {
                        o1.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        Thread t2 = createThread(() -> {
            for (int i = 0; i < num; i++) {
                synchronized (o2) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    synchronized (o1) {
                        System.out.println("B");
                        try {
                            o2.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        o1.notifyAll();
                    }
                    o2.notifyAll();
                }
            }
        });
        Thread t3 = createThread(() -> {
            for (int i = 0; i < num; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (o2) {
                    System.out.println("C");
                    o2.notifyAll();
                    try {
                        o2.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        t1.start();
        t2.start();
        t3.start();
    }

    private Thread createThread(Runnable runnable) {
        return new Thread(runnable);
    }

}
