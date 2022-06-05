package com.fresh.common.placeholder;

import java.util.HashMap;

public class SimplePlaceHolderResolverTest {

    public static void main(String argv[]) {

        SimplePlaceHolderResolver resolver = new SimplePlaceHolderResolver();

        //System.out.println(resolver.findRelativeSuffix(new StringBuilder("012${holder"), 3));
        //System.out.println(resolver.findRelativeSuffix(new StringBuilder("012${holder}111"), 3));
        //System.out.println(resolver.findRelativeSuffix(new StringBuilder("012${a${b${c}}}lll${o${p}}"), 3));
        //System.out.println(resolver.findRelativeSuffix(new StringBuilder("012${a}lll${b}"), 3));
        //System.out.println(resolver.findRelativeSuffix(new StringBuilder("012${a${b${c}}}lll${o${p}}"), 6));
        //System.out.println(resolver.findRelativeSuffix(new StringBuilder("a${b${c}}"), 1));

        MapDataSource source = new MapDataSource();
        source.put("username", "root");
        source.put("password", "123456");
        source.put("sep", ";");
        source.put("username-password", "${username}${sep}${password}");
        source.put("username;123456", "nextedValue");


        //resolver.resolve(null, source::get);
        //System.out.println(resolver.resolve("username: ${username}", source::get));
        //System.out.println(resolver.resolve("password: ${password}", source::get));
        //System.out.println(resolver.resolve("username: ${username}${sep}password: ${password}", source::get));
        //System.out.println(resolver.resolve("username-password: ${username-password}", source::get));
        System.out.println(resolver.resolve("nested: ${username${sep:-}${password:123}:defaultNested}...u:${username}", source::get));


    }

    private static class MapDataSource extends HashMap<String, String> {

        public String get(String key) {
            return super.get(key);
        }

    }

}
