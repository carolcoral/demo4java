package site.cnkj.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


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

    /**
     * 自动获取windows系统下当前应用的properties文件
     * @return File
     */
    public static File getPropertiesFileForWin(){
        String propertiesPath = new String();
        String propertiesPathPrefix = new String();
        try {
            StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
            Class localClass = stackTraceElement.getClassName().getClass();
            String filePathName = localClass.getResource("/").getPath();
            File file = new File(filePathName);
            for (File file1 : file.listFiles()) {
                String[] names = file1.getPath().split("\\\\");
                if ("application.properties".equals(names[names.length-1])){
                    for (String line : FileUtils.readLines(file1, "UTF-8")) {
                        if ("spring.profiles.active".equals(line.split("=")[0].trim())){
                            propertiesPathPrefix = "-"+line.split("=")[1].trim();
                            break;
                        }
                    }
                    break;
                }else if (names[names.length-1].contains(".properties") && names[names.length-1] != "application.properties"){
                    propertiesPath = names[names.length-1];
                }
            }
            if (propertiesPathPrefix == null && "".equals(propertiesPathPrefix)){
                return new File(localClass.getResource("/"+propertiesPath).getPath());
            }else {
                return new File(localClass.getResource("/application"+propertiesPathPrefix+".properties").getPath());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取linux系统下项目路径下相对指定路径的配置文件
     * @param pathName 指定项目下配置文件的路径，可选，默认指定 ./config/application.properties
     *                 扫描优先级
     *                  指定的pathName地址
     *                  指定的pathName地址下的符合*.properties的文件
     *                   如果存在多个则根据application.properties中指定的文件
     *                   否则按照 pro > dev > test 原则获取
     *                   如果上述三种类型的properties均不存在，则返回空
     * @return
     */
    public static File getPropertiesFileForLinux(String... pathName){
        File realFile = null;
        try {
            String userPath = System.getProperty("user.dir");
            if (pathName.length > 0){
                File userPathFile = new File(Paths.get(userPath, pathName).toString());
                if (userPathFile.isFile()){
                    if (FileUtils.readLines(userPathFile, "UTF-8").contains("spring.profiles.active")){
                        for (String userFileLine : FileUtils.readLines(userPathFile, "UTF-8")) {
                            if ("spring.profiles.active".equals(userFileLine.split("=")[0].trim())){
                                String namePath = Paths.get(userPathFile.getParentFile().toString(), "application-"+userFileLine.split("=")[1].trim()+".properties").toString();
                                realFile = new File(namePath);
                                break;
                            }
                        }
                    }else {
                        realFile = userPathFile;
                    }
                }
            }else if (pathName.length == 0 || pathName == null){
                pathName[0] = "config";
                pathName[1] = "application.properties";
                File pathFile = new File(Paths.get(userPath, pathName).toString());
                if (pathFile.exists()){
                    if (FileUtils.readLines(pathFile, "UTF-8").contains("spring.profiles.active")) {
                        for (String lin : FileUtils.readLines(pathFile, "UTF-8")) {
                            if ("spring.profiles.active".equals(lin.split("=")[0].trim())){
                                String propertiesPrefix = lin.split("=")[1].trim();
                                pathName[1] = "application-"+propertiesPrefix+".properties";
                                realFile =  new File(Paths.get(userPath, pathName).toString());
                                break;
                            }
                        }
                    }else {
                        realFile = pathFile;
                    }
                }else if (!pathFile.exists()){
                    pathName[1] = "application-pro.properties";
                    File proPathFile = new File(Paths.get(userPath, pathName).toString());
                    if (proPathFile.exists()){
                        realFile = new File(Paths.get(userPath, pathName).toString());
                    }else {
                        pathName[1] = "application-dev.properties";
                        File devPathFile = new File(Paths.get(userPath, pathName).toString());
                        if (devPathFile.exists()){
                            realFile = new File(Paths.get(userPath, pathName).toString());
                        }else {
                            pathName[1] = "application-test.properties";
                            File testPathFile = new File(Paths.get(userPath, pathName).toString());
                            if (testPathFile.exists()){
                                realFile = new File(Paths.get(userPath, pathName).toString());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return realFile;
        }
    }

    /**
     * 获取在不同系统下指定配置文件的路径
     * @param pathName 如果是非windows系统则需要指定配置文件在项目中所在的完整路径
     *                 需要注意的是该参数的最后一位必须为配置文件的文件名
     *                 例如 test/config/test.properties
     *                 则配置 "config" "test.properties"
     * @return File
     */
    public static File getPropertiesByName(String... pathName){
        try {
            StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
            Class currentClass = stackTraceElement.getClassName().getClass();
            String osVersion = System.getProperty("os.name").toUpperCase();
            System.out.println(pathName);
            if (osVersion.contains("WINDOWS")) {
                return new File(currentClass.getResource("/"+pathName[pathName.length-1]).getPath());
            }else {
                return new File(Paths.get(System.getProperty("user.dir"), pathName).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //public static void main(String[] args){
    //    //System.out.println(getPropertiesByName("application.properties"));
    //    //H:\github\demo4java\common\target\classes\application.properties
    //    //File file = new File("H:\\github\\demo4java\\common\\target\\classes\\application.properties");
    //    //System.out.println(file.getParentFile());
    //    System.out.println(System.nanoTime());
    //}

}
