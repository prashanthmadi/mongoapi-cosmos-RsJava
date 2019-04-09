package com.ms.consoleapp;

public class PrintStatus<T> extends ObservableSubscriber<T>{

    private String message;

    /**
     * A Subscriber that outputs a message onComplete.
     *
     * @param message the message to output onComplete
     */
    public PrintStatus(String message) {
        this.message = message;
    }

    @Override
    public void onComplete() {
        System.out.println(String.format(message, getReceived()));
        super.onComplete();
    }
}
