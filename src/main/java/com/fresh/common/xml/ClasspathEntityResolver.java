package com.fresh.common.xml;

import com.fresh.common.utils.ClazzUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**实现EntityResolver，对dtd,xsd使用classpath加载
 */
public class ClasspathEntityResolver implements EntityResolver {

    public static final String DTD_SUFFIX = ".dtd";
    public static final String XSD_SUFFIX = ".xsd";
    private final ClassLoader classLoader;
    private String location;
    private String locationPath;

    public void setLocation(String location) {
        this.location = location;
    }

    public ClasspathEntityResolver(String location) {
        this(location, ClazzUtils.getDefaultClassLoader());
    }

    public ClasspathEntityResolver(String location, ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.location = location;
        int sep = location.lastIndexOf("/");
        if(sep != -1) {
            locationPath = location.substring(0, sep) + "/";
        } else {
            locationPath = "";
        }
    }

    /**
     * dtd
     * {@code <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
     *           publicId="-//mybatis.org//DTD Mapper 3.0//EN" ; systemId="http://mybatis.org/dtd/mybatis-3-mapper.dtd" }
     * <br>
     * xsd
     * {@code <root ... xsi:schemaLocation="
     *                  http://www.fresh.com/namespace/roots
     *                  file:/D:\ideaProject\NormalProject\src\main\resources\validate_schema_test_2.xml1.xsd">}
     * publicId=null ; systemId="file:/D:/ideaProject/NormalProject/src/main/resources/validate_schema_test_2.xml1.xsd"
     *
     * @param publicId publicId
     * @param systemId systemId
     * @return InputSource
     * @throws IOException IOException
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException {
        if(systemId != null && (systemId.endsWith(DTD_SUFFIX) || systemId.endsWith(XSD_SUFFIX))) {
            int lastPathSeparator = systemId.lastIndexOf('/');
            String fileName = systemId.substring(lastPathSeparator + 1);
            String filePath = locationPath + fileName;
            InputStream is = classLoader.getResourceAsStream(filePath);
            if (is == null) {
                throw new IOException("Could not find resource " + filePath);
            }
            InputSource source = new InputSource(is);
            source.setPublicId(publicId);
            source.setSystemId(systemId);
            return source;
        }
        return null;
    }

}
