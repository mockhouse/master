package com.quick.base;

import java.util.HashMap;

/**
 * Created by eropate on 8/16/2015.
 */
public class MockScoopCache extends HashMap<String,Object> {

    public static class CacheKeys {
        private CacheKeys(){}
        public static final String ALL_CATEGORIES = "ALL_CATEGORIES";
        public static final String QUESTIONS = "QUESTIONS";
    }
    private static class CacheHolder {
        public static final MockScoopCache instance = new MockScoopCache();
    }

    public static MockScoopCache getInstance() {
        return CacheHolder.instance;
    }
    private MockScoopCache(){}

}
