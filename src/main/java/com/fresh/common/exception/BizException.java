package com.fresh.common.exception;

import com.fresh.common.enums.JsonResultEnum;
import com.fresh.common.utils.StringUtils;

import java.util.Optional;
import java.util.function.Supplier;

public class BizException extends RuntimeException {

    private Supplier<String> messageSupplier;
    private Supplier<String> exceptionCodeSupplier;
    private static final Supplier<String> NULL_STRING_SUPPLIER = () -> "";

    public BizException() {
    }

    public BizException(Supplier<String> messageSupplier) {
        super(Optional.ofNullable(messageSupplier).orElse(NULL_STRING_SUPPLIER).get());
        this.messageSupplier = Optional.ofNullable(messageSupplier).orElse(NULL_STRING_SUPPLIER);
    }

    public BizException(Supplier<String> messageSupplier, Supplier<String> exceptionCodeSupplier) {
        super(Optional.ofNullable(messageSupplier).orElse(NULL_STRING_SUPPLIER).get());
        this.messageSupplier = Optional.ofNullable(messageSupplier).orElse(NULL_STRING_SUPPLIER);
        this.exceptionCodeSupplier = Optional.ofNullable(exceptionCodeSupplier).orElse(NULL_STRING_SUPPLIER);
    }

    public BizException(Throwable e) {
        super(e);
    }

    public BizException(Throwable e, Supplier<String> messageSupplier) {
        super(Optional.ofNullable(messageSupplier).orElse(NULL_STRING_SUPPLIER).get(), e);
        this.messageSupplier = Optional.ofNullable(messageSupplier).orElse(NULL_STRING_SUPPLIER);
    }

    public BizException(Throwable e, Supplier<String> messageSupplier, Supplier<String> exceptionCodeSupplier) {
        super(Optional.ofNullable(messageSupplier).orElse(NULL_STRING_SUPPLIER).get(), e);
        this.messageSupplier = Optional.ofNullable(messageSupplier).orElse(NULL_STRING_SUPPLIER);
        this.exceptionCodeSupplier = Optional.ofNullable(exceptionCodeSupplier).orElse(NULL_STRING_SUPPLIER);
    }


    public String getExceptionCode() {
        return Optional.ofNullable(this.exceptionCodeSupplier).orElse(NULL_STRING_SUPPLIER).get();
    }

    public String getExceptionCodeWith(String code) {
        if(exceptionCodeSupplier==null || StringUtils.isEmpty(exceptionCodeSupplier.get())) {
            return Optional.ofNullable(code).orElse(JsonResultEnum.FAIL.getCode());
        }
        return exceptionCodeSupplier.get();
    }


    public static void main(String argv[]) {
        Supplier<String> s = null;
        BizException e = new BizException(s/*, () -> "403"*/);
        System.out.println(e.getMessage());
        System.out.println(e.getExceptionCode());
        System.out.println("with: " + e.getExceptionCodeWith("-33"));
    }

}
