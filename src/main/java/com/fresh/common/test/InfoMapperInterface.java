package com.fresh.common.test;

public interface InfoMapperInterface {
    @HereSelect("select info_name from info")
    String getInfoName(String param);
}
