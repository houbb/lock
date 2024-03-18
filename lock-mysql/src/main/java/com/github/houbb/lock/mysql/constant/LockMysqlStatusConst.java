package com.github.houbb.lock.mysql.constant;

public enum LockMysqlStatusConst {
    I("I", "初始化"),
    P("P", "处理中"),
    ;

    private final String code;
    private final String desc;

    LockMysqlStatusConst(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
