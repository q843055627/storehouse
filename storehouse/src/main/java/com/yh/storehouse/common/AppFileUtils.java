package com.yh.storehouse.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppFileUtils {

    //文件上传的保存路径
    public static  String UPLOAD_PATH="E:/upload/";//默认值

    static {
        //获取到资源文件
        InputStream stream = AppFileUtils.class.getClassLoader().getResourceAsStream("file.properties");
        Properties properties = new Properties();
        try{
            properties.load(stream);
        }catch (IOException e){
            e.printStackTrace();
        }
        String property = properties.getProperty("filepath");
        if(null != property){
            UPLOAD_PATH = property;
        }
    }

    public static ResponseEntity<Object> createResponseEntity(String path){
        File file = new File(UPLOAD_PATH + path);
        if(file.exists()){
            byte[] bytes = null;
            try{
                bytes = FileUtil.readBytes(file);
            }catch (Exception e){
                e.printStackTrace();
            }

            //创建封装响应头信息的对象
            HttpHeaders headers  = new HttpHeaders();
            //封装响应内容类型(APPLICATION_OCTET_STREAM 响应的内容不限定)
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            //请求体、请求头和状态码
            ResponseEntity<Object> entity =
                    new ResponseEntity<>(bytes,headers, HttpStatus.CREATED);
            return entity;
        }
        return null;
    }

    /**
     * 根据文件老名字得到新名字
     * @param oldName
     * @return
     */
    public static String createNewFileName(String oldName) {
       String stuff = oldName.substring(oldName.lastIndexOf("."),oldName.length());
       return IdUtil.simpleUUID().toUpperCase() + stuff;
    }

    /**
     * 根据路径改名字 去掉_temp
     * @param goodsimg
     * @return
     */
    public static String renameFile(String goodsimg) {
       File file = new File(UPLOAD_PATH,goodsimg);
       String replace = goodsimg.replace("_temp","");
       if(file.exists()){
           file.renameTo(new File(UPLOAD_PATH,replace));
       }
       return replace;
    }

    /**
     * 根据老路径删除图片
     * @param oldPath
     */
    public static void removeFileByPath(String oldPath) {
        if(!oldPath.equals(Constant.IMAGES_DEFAULTGOODSIMG_PNG)){
            File file = new File(UPLOAD_PATH,oldPath);
            if(file.exists()){
                file.delete();
            }
        }
    }
}
