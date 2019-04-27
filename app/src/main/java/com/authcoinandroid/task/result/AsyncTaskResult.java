package com.authcoinandroid.task.result;

public abstract class AsyncTaskResult<T> {
    private T result;
    private Exception error;

    public T getResult() {
        return result;
    }

    public Exception getError() {
        return error;
    }

    AsyncTaskResult(T result) {
        super();
        this.result = result;
    }

    AsyncTaskResult(Exception error) {
        super();
        this.error = error;
    }
}