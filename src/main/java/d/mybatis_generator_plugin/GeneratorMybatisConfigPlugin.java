package d.mybatis_generator_plugin;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

/**
 * 
 * 生成mybatis-config中的mapper配置,直接输出到控制台
 * 
 */
public class GeneratorMybatisConfigPlugin extends PluginAdapter {
    
    private static List<String> mybatisConfigXMLList = new ArrayList<String>();
    
    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        
        // <mapper resource="sqlmap/base/common_SqlMap.xml" />
        // <mapper resource="sqlmap/base/userInfoMapper.xml" />
        
        StringBuilder builder = new StringBuilder("<mapper resource=\"");
        builder.append(sqlMap.getTargetPackage().replaceAll("\\.", "/"));
        builder.append("/");
        builder.append(sqlMap.getFileName());
        builder.append("\" />");
        
        mybatisConfigXMLList.add(builder.toString());
        
        int tableSize = this.getContext().getTableConfigurations().size();
        
        if (tableSize == mybatisConfigXMLList.size()) {
            
            System.out.println("\r\n---------mybatis-config.xml(Begin)----------\r\n");
            
            for (String xml : mybatisConfigXMLList) {
                System.out.println(xml);
            }
            
            System.out.println("\r\n---------mybatis-config.xml(End)----------\r\n");
        }
        
        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }

    /**
     * This plugin is always valid - no properties are required
     */
    public boolean validate(List<String> warnings) {
        return true;
    }

}