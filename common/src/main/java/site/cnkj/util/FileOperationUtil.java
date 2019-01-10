package site.cnkj.util;

import org.apache.commons.io.FileUtils;

import java.io.File;


/**
 * 文件操作工具类
 */
public class FileOperationUtil {

    /**
     * 写入日志内容到指定文件中，带当前时间
     *
     * @param filePath 文件路径
     * @param info     日志内容
     */
    public static void writeLog(String filePath, String info) {
        String time = DateUtil.translateTimeToDate(DateUtil.getCurrentTime(), DateUtil.DATEFORMATE.FULLTIMEBY_yMdHmsS);
        String fullInfo = time.concat(" ").concat(info);
        File file = new File(filePath);
        try {
            //如果路径不存在则自动创建
            if (!file.exists()) {
                file.createNewFile();
            }
            FileUtils.write(file, fullInfo.concat("\n"), "utf-8", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
