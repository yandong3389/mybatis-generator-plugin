package d.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.omg.CORBA.SystemException;

/**
 * 类名称：模板处理类
 * 内容摘要：根据模板生成内容
 */
public class VelocityUtil {
    /** 扩展名【.vm】 */
    private static final String EXT_VM = ".vm";
    /** 字符集【UTF-8】 */
    private static final String ENCODE_UTF8 = "UTF-8";
    /** 模板引擎实例 */
    private static VelocityEngine velocityEngine;
    
    /**
     * 构造方法
     * @throws SystemException
     */
    public VelocityUtil() throws Exception {
        Properties p = new Properties();
        velocityEngine = new VelocityEngine();
        URL url = this.getClass().getClassLoader().getResource("velocity.properties");
        if (url != null) {
            try {
                InputStream is = url.openStream();
                p.load(is);
            } catch (IOException e) {
                throw new IOException("Velocity初始化失败");
            }
        }
        
        System.out.println("VMPath =======================" + getVMPath() + "template");
        p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, getVMPath() + "template");
        try {
            velocityEngine.init(p);
        } catch (Exception e) {
            System.out.println("初期化Velocity失败！");
            e.printStackTrace();
            throw new Exception("Velocity初始化失败");
        }
    }

    /**
     * 根据模板和Map合成内容
     * @param vmId 模板ID
     * @param paramMap 参数Map
     * @return 模板同字段内容合成后的String内容
     */
    public static String createMailContent(String vmId, Map<String, Object> paramMap) {
        // 实例化模板内容Context
        VelocityContext context = new VelocityContext();

        // 将参数Map转换为Context
        Set<String> set = paramMap.keySet();
        Iterator<String> ite = set.iterator();
        while (ite.hasNext()) {
            String key = String.valueOf(ite.next());
            context.put(key, paramMap.get(key));
        }
        // 取得模板实例
        Template t = velocityEngine.getTemplate(vmId + EXT_VM, ENCODE_UTF8);
        StringWriter strWriter = new StringWriter();
        // 合成内容
        t.merge(context, strWriter);
        // 返回合成后的内容
        return strWriter.toString();
    }

    /**
     * 根据操作系统不同返回不同的模板路径
     * @return String 模板路径
     */
    private String getVMPath() {
        // Windows操作系统时
        if (System.getProperty("file.separator").equals("\\")) {
//            return this.getClass().getClassLoader().getResource("../../").getPath().substring(1).replaceAll("\\/", "\\\\");
        	

//        	System.out.println("1");
//        	try {
//				System.out.println(this.getClass().getClassLoader().getResource("../../"));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        	
//        	System.out.println("2");
//        	try {
//        		System.out.println(VelocityUtil.class.getResource("").getPath());
//        	} catch (Exception e) {
//        		// TODO Auto-generated catch block
//        		e.printStackTrace();
//        	}
//        	System.out.println("22");
//        	try {
//				System.out.println(VelocityUtil.class.getResource("../../").getPath());
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        	
//        	System.out.println("3");
//        	try {
//				System.out.println(VelocityUtil.class.getResource("/"));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        	
//        	System.out.println("4");
//        	try {
//				System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        	
//        	System.out.println("5");
//        	try {
//				System.out.println(VelocityUtil.class.getClassLoader().getResource(""));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        	
//        	System.out.println("6");
//        	try {
//				System.out.println(ClassLoader.getSystemResource(""));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        	
        	
//        	return VelocityUtil.class.getResource("").getPath().replaceAll("/d/util", "");
            return "E:\\Users\\Lawliet\\git\\mybatis-generator-plugin\\src\\main\\resources\\";
        }
        // 其他操作系统时
        else {
//            return this.getClass().getClassLoader().getResource("../../").getPath();
            return this.getClass().getClassLoader().getResource("").getPath();
        }
    }
}
