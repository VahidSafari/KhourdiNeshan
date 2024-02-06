package com.safari.khourdineshan.core.base;

public abstract class Result<T> {
    public final static class Success<T> extends Result<T> {
        private T result;

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
