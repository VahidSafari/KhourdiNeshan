package com.safari.khourdineshan.core.model.base;

public abstract class Result<T> {
    public final static class Success<T> extends Result<T> {
        private final T result;

        public Success(T result) {
            this.result = result;
        }

        public T getResult() {
            return result;
        }

    }

    public final static class Fail extends Result {
        private final Throwable throwable;

        public Fail(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }
    }
}
