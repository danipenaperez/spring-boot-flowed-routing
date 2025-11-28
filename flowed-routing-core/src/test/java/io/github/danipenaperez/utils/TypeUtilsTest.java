package io.github.danipenaperez.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.danipenaperez.lib.flowedrouting.utils.TypeUtils;

public class TypeUtilsTest {
    
	@Test
	void isProxyTest() throws Exception {
		TypeUtilsTest object = new TypeUtilsTest();
        Assertions.assertEquals(false,TypeUtils.isProxy(object));

    
    }
}
