package site.cnkj.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.util.Map;

/**
 * The class JacksonUtil
 * <p>
 * json字符与对像转换
 *
 * @version: $Revision$ $Date$ $LastChangedBy$
 */
public final class JacksonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

    public static ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(MapperFeature.USE_ANNOTATIONS);

    /**
     * 使用泛型方法，把json字符串转换为相应的JavaBean对象。
     * (1)转换为普通JavaBean：readValue(json,Student.class)
     * (2)转换为List,如List<Student>,将第二个参数传递为Student
     * [].class.然后使用Arrays.asList();方法把得到的数组转换为特定类型的List
     *
     * @param jsonStr
     * @param valueType
     * @return
     */
    public static <T> T readValue(String jsonStr, Class<T> valueType) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }

        try {
            return objectMapper.readValue(jsonStr, valueType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * json数组转List
     *
     * @param jsonStr
     * @param valueTypeRef
     * @return
     */
    public static <T> T readValue(String jsonStr, TypeReference<T> valueTypeRef) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }

        try {
            return objectMapper.readValue(jsonStr, valueTypeRef);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 把JavaBean转换为json字符串
     *
     * @param object
     * @return
     */
    public static String toJSon(Object object) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T toObject(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (Exception e) {
            logger.error("ERROR:", e);
        }
        return null;
    }

    //public static void main(String[] args) throws JSONException {
    //    String jsonstr1 = "{\"code\":\"000000\",\"desc\":\"啊啊啊啊\",\"zxc\":\"asd\"}";
    //    String jsonstr = "{\"code\":\"000000\",\"zxc\":{\n" +
    //            "\t\"formatId\": \"999992\",\n" +
    //            "\t\"code\": \"000000\",\n" +
    //            "\t\"suffix\": \"mp3\",\n" +
    //            "\t\"url\": \"http://dlsdownfree.nf.migu.cn/wlansst?pars=CI=640463009561600913000000402342/F=999992/T=59167265698000/CH=d57dcf13-2c9f-45ad-9bcf-c15df69cc279/S=62ccd0dbe7/k=f45ad3fa9cbfc83c/t=1539740364/FN=filename.mp3\",\n" +
    //            "\t\"contentId\": \"600913000000402342\",\n" +
    //            "\t\"size\": \"776611\",\n" +
    //            "\t\"info\": \"success\"\n" +
    //            "}}";
    //    Map<String, Object> br = toObject(jsonstr, Map.class);
    //    System.out.println(getType(br.get("zxc")));
    //    // System.out.println(br.getDesc());
    //}

    public static String getType(Object object) {
        String typeName = object.getClass().getName();
        int length = typeName.lastIndexOf(".");
        String type = typeName.substring(length + 1);
        return type;
    }
}