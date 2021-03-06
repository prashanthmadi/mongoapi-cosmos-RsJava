package com.ms.consoleapp;

import com.mongodb.MongoTimeoutException;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public  class ObservableSubscriber<T> implements Subscriber<T> {
    private final List<T> received;
    private final List<Throwable> errors;
    private final CountDownLatch latch;
    private volatile Subscription subscription;
    private volatile boolean completed;

    ObservableSubscriber() {
        this.received = new ArrayList<T>();
        this.errors = new ArrayList<Throwable>();
        this.latch = new CountDownLatch(1);
    }

    public void onSubscribe(final Subscription s) {
        s.request(Integer.MAX_VALUE);
        subscription = s;
    }

    public void onNext(final T t) {
        received.add(t);
    }

    public void onError(final Throwable t) {
        errors.add(t);
        onComplete();
    }

    public void onComplete() {
        completed = true;
        latch.countDown();
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public List<T> getReceived() {
        return received;
    }

    public Throwable getError() {
        if (errors.size() > 0) {
            return errors.get(0);
        }
        return null;
    }

    public boolean isCompleted() {
        return completed;
    }

    public List<T> get(final long timeout, final TimeUnit unit) throws Throwable {
        return await(timeout, unit).getReceived();
    }

    public ObservableSubscriber<T> await() throws Throwable {
        return await(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public ObservableSubscriber<T> await(final long timeout, final TimeUnit unit) throws Throwable {
        subscription.request(Integer.MAX_VALUE);
        if (!latch.await(timeout, unit)) {
            throw new MongoTimeoutException("Publisher onComplete timed out");
        }
        if (!errors.isEmpty()) {
            throw errors.get(0);
        }
        return this;
    }
}