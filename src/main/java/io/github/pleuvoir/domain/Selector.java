package io.github.pleuvoir.domain;

public enum Selector {
   
    /**
     * 新消息
     */
    NEW_MESSAGE(2);

    private final int code;

    public int getCode() {
        return code;
    }

    Selector(int code) {
        this.code = code;
    }
}
