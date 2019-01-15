package site.cnkj.util;

/**
 * 公共类
 */
public interface CommonConstant {

    public static class API {
        //查询当前服务的commonInfo信息的固定接口
        public static final String COMMON_INFO = "/actuator/info";
    }

    public static class REDIS {
        //es查询的分页id
        public static final String ESScrollId = "scrollId";
    }

}
