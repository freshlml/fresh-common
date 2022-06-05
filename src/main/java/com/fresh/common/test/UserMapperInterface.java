package com.fresh.common.test;

public interface UserMapperInterface {
    @HereSelect("select user_name from user")
    String getUserName(String param);
}
