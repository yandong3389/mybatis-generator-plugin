package d.mybatis_generator_plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import com.alibaba.fastjson.JSONObject;

import d.util.StringUtil;
import d.util.VMUtil;
import d.util.VelocityUtil;

/**
 * 生成service类
 */
public class GeneratorJSPandControllerPlugin extends PluginAdapter {

	private FullyQualifiedJavaType slf4jLogger;
	private FullyQualifiedJavaType slf4jLoggerFactory;
	private FullyQualifiedJavaType serviceType;
	private FullyQualifiedJavaType daoType;
	private FullyQualifiedJavaType interfaceType;
	private FullyQualifiedJavaType pojoType;
	private FullyQualifiedJavaType pojoCriteriaType;
	private FullyQualifiedJavaType listType;
	private FullyQualifiedJavaType autowired;
	private FullyQualifiedJavaType service;
	private FullyQualifiedJavaType returnType;
	private String servicePack;
	private String serviceImplPack;
	private String project;
	private String pojoUrl;

	private List<MyMethod> methods;

	public GeneratorJSPandControllerPlugin() {
		super();
		// 默认是slf4j
		slf4jLogger = new FullyQualifiedJavaType("org.slf4j.Logger");
		slf4jLoggerFactory = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
		methods = new ArrayList<MyMethod>();
	}

	public boolean validate(List<String> warnings) {

		servicePack = properties.getProperty("targetPackage");
		serviceImplPack = properties.getProperty("implementationPackage");
		project = properties.getProperty("targetProject");

		pojoUrl = context.getJavaModelGeneratorConfiguration().getTargetPackage();

		System.out.println("*****************************");
		System.out.println("servicePack = " + servicePack);
		System.out.println("serviceImplPack = " + serviceImplPack);
		System.out.println("project = " + project);
		System.out.println("pojoUrl = " + pojoUrl);
		System.out.println("*****************************");
		
		return true;
	}

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
		List<GeneratedJavaFile> files = new ArrayList<GeneratedJavaFile>();
		String table = introspectedTable.getBaseRecordType();
		String tableName = table.replaceAll(this.pojoUrl + ".", "");
		interfaceType = new FullyQualifiedJavaType(servicePack + "." + tableName + "Service");

		// 表名
		
		// 字段类型
		
		// 字段名称(字段中文名称)
		
		List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
		
		for (IntrospectedColumn introspectedColumn : columns) {
			
			System.out.print("columns = " + introspectedColumn.getJdbcTypeName());
			System.out.print("    " + introspectedColumn.getJavaProperty());
			System.out.print("    " + introspectedColumn.getFullyQualifiedJavaType().getShortName());
			System.out.print("    " + introspectedColumn.getFullyQualifiedJavaType().getPackageName());
			System.out.print("    " + introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName());
			System.out.print("    " + introspectedColumn.getFullyQualifiedJavaType().getImportList());
			System.out.println();
		}
		
		// mybatis
		daoType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());

		// logger.info(toLowerCase(daoType.getShortName()));
		serviceType = new FullyQualifiedJavaType(serviceImplPack + "." + tableName + "ServiceImpl");

		pojoType = new FullyQualifiedJavaType(pojoUrl + "." + tableName);

		// TODO 1.3.2 与1.3.1有区别
//		pojoCriteriaType = new FullyQualifiedJavaType(pojoUrl + "." + "Criteria");
		
		// 1.3.2方式
		pojoCriteriaType = new FullyQualifiedJavaType(pojoUrl + "." + tableName + "Example");
		
		listType = new FullyQualifiedJavaType("java.util.List");
		Interface interface1 = new Interface(interfaceType);
		TopLevelClass topLevelClass = new TopLevelClass(serviceType);
//		// 导入必要的类
//		addImport(interface1, topLevelClass);
//
//		// 接口
//		addService(interface1,introspectedTable, tableName, files);
//		// 实现类
//		addServiceImpl(topLevelClass,introspectedTable, tableName, files);
//		addLogger(topLevelClass);

		genFiles(introspectedTable);
		
		return files;
	}
	
	/**
	// 获得模块引擎
	// 获得模板路径
	// 获得指定模板
	// 抽取表结构数据
	
	// 填充表结构数据到JSP模板（list、add、update），生成内容
	// 将生成的JSP内容输出到文件，JSP文件后缀以list、add、update为命名（add和update考虑使用同一个文件）
	// 填充表结构数据到Controller模板（package、import、class、anno、method），生成内容
	// 真生成的Controller内容输出到文件，以.java为后缀，名称方式为 tableName + Colltroller.java
	 * 
	 */
	public void genFiles(IntrospectedTable introspectedTable){
		   
		genController(introspectedTable);
		genJSP(introspectedTable);
		genJS(introspectedTable);
		genCSS(introspectedTable);
	}
	
	public void genController(IntrospectedTable introspectedTable){
		
		String fullPojoJavaName = introspectedTable.getBaseRecordType();
		String pojoJavaName = fullPojoJavaName.replaceAll(this.pojoUrl + ".", "");
		String pojoObjName = StringUtil.toFirstLowerClass(pojoJavaName);
		
		String fullPojoExampleJavaName = fullPojoJavaName + "Example";
		String pojoExampleJavaName = fullPojoExampleJavaName.replaceAll(this.pojoUrl + ".", "");
		String pojoExampleObjName = StringUtil.toFirstLowerClass(pojoExampleJavaName);
		
		String pojoObjListName = pojoObjName + "List";
		
		String serviceJavaName = pojoJavaName + "Service";
		String serviceObjName = StringUtil.toFirstLowerClass(serviceJavaName);
		String fullServiceJavaName = "d.money.service.base." + serviceJavaName;
		
		String controllerName = "Back" + pojoJavaName + "Controller"; 
		
		String table_name = introspectedTable.getTableConfiguration().getTableName();
		
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		paramMap.put("fullPojoJavaName", fullPojoJavaName);
		paramMap.put("pojoJavaName", pojoJavaName);
		paramMap.put("pojoObjName", pojoObjName);
		paramMap.put("fullPojoExampleJavaName", fullPojoExampleJavaName);
		paramMap.put("pojoExampleJavaName", pojoExampleJavaName);
		paramMap.put("pojoExampleObjName", pojoExampleObjName);
		paramMap.put("pojoObjListName", pojoObjListName);
		paramMap.put("serviceJavaName", serviceJavaName);
		paramMap.put("serviceObjName", serviceObjName);
		paramMap.put("fullServiceJavaName", fullServiceJavaName);
		paramMap.put("controllerName", controllerName);
		paramMap.put("table_name", table_name);

		paramMap.put("util", new VMUtil());
		
		List<String> pojoFieldList = new ArrayList<String>();
		
		List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
		
		for (IntrospectedColumn column : columns) {
			
			
			System.out.println(JSONObject.toJSONString(column));
			
			pojoFieldList.add(column.getJavaProperty());
			
		}
		paramMap.put("pojoFieldList", pojoFieldList);
		paramMap.put("orderField", columns.get(1).getActualColumnName());
	
		// column.getRemarks 备注
		
//		System.out.println();
//		System.out.println();
//		System.out.println();
//		System.out.println(JSONObject.toJSONString(paramMap));
//		System.out.println();
//		System.out.println();
//		System.out.println();
		
		String content = null;
		try {
			content = new VelocityUtil().createMailContent("controller", paramMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("========================================================================================");
		
		System.out.println(content);
		
		System.out.println("========================================================================================");
		
	}
	
	
	
	public void genJSP(IntrospectedTable introspectedTable){
		// TODO 
		System.out.println("1111");
	}
	
	public void genJS(IntrospectedTable introspectedTable){
		// TODO 
	}
	
	public void genCSS(IntrospectedTable introspectedTable){
		// TODO 
	}
	
	// Actual Column Name: qu_type_id, JDBC Type: 1, Nullable: false, Length: 32, Scale: 0, Identity: false, 
	// Actual Column Name: qu_type_name, JDBC Type: 1, Nullable: true, Length: 32, Scale: 0, Identity: false, 
	// Actual Column Name: create_date, JDBC Type: 93, Nullable: true, Length: 19, Scale: 0, Identity: false
	
//	table = d.money.pojo.base.BaseQuTag
//	tableName = BaseQuTag
//	columns = CHAR    quTagId    String    java.lang    java.lang.String    []
//	columns = VARCHAR    quTagName    String    java.lang    java.lang.String    []
//	columns = VARCHAR    quTagFlag    String    java.lang    java.lang.String    []
//	columns = CHAR    quTypeId    String    java.lang    java.lang.String    []
//	columns = DECIMAL    useCount    Long    java.lang    java.lang.Long    []
//	columns = VARCHAR    createFlag    String    java.lang    java.lang.String    []
//	columns = TIMESTAMP    createDate    Date    java.util    java.util.Date    [java.util.Date]
//	columns = CHAR    userId    String    java.lang    java.lang.String    []
//	controllerName = BackBaseQuTagController
//	controllerFileName = BackBaseQuTagController.java
//	jspName = BaseQuTag_list.jsp
//	objListName = BaseQuTagList
//	objName = BaseQuTagObj

//	/**
//	 * 添加接口
//	 * 
//	 * @param tableName
//	 * @param files
//	 */
//	protected void addService(Interface interface1,IntrospectedTable introspectedTable, String tableName, List<GeneratedJavaFile> files) {
//
//		interface1.setVisibility(JavaVisibility.PUBLIC);
//
//		// 添加方法
//		MyMethod method = countByExample(introspectedTable, tableName);
//		
//		method.removeAllBodyLines();
//		interface1.addMethod(method);
//
//		method = selectByPrimaryKey(introspectedTable, tableName);
//		method.removeAllBodyLines();
//		interface1.addMethod(method);
//
//		method = selectByExample(introspectedTable, tableName);
//		method.removeAllBodyLines();
//		interface1.addMethod(method);
//
//		if (enableDeleteByPrimaryKey) {
//			method = getOtherInteger("deleteByPrimaryKey", introspectedTable, tableName, 2);
//			method.removeAllBodyLines();
//			interface1.addMethod(method);
//		}
//		if (enableUpdateByPrimaryKeySelective) {
//			method = getOtherInteger("updateByPrimaryKeySelective", introspectedTable, tableName, 1);
//			method.removeAllBodyLines();
//			interface1.addMethod(method);
//		}
//		if (enableUpdateByPrimaryKey) {
//			method = getOtherInteger("updateByPrimaryKey", introspectedTable, tableName, 1);
//			method.removeAllBodyLines();
//			interface1.addMethod(method);
//		}
//		if (enableDeleteByExample) {
//			method = getOtherInteger("deleteByExample", introspectedTable, tableName, 3);
//			method.removeAllBodyLines();
//			interface1.addMethod(method);
//		}
//		if (enableUpdateByExampleSelective) {
//			method = getOtherInteger("updateByExampleSelective", introspectedTable, tableName, 4);
//			method.removeAllBodyLines();
//			interface1.addMethod(method);
//		}
//		if (enableUpdateByExample) {
//			method = getOtherInteger("updateByExample", introspectedTable, tableName, 4);
//			method.removeAllBodyLines();
//			interface1.addMethod(method);
//		}
//		if (enableInsert) {
//			method = getOtherInsertboolean("insert", introspectedTable, tableName);
//			method.removeAllBodyLines();
//			interface1.addMethod(method);
//		}
//		if (enableInsertSelective) {
//			method = getOtherInsertboolean("insertSelective", introspectedTable, tableName);
//			method.removeAllBodyLines();
//			interface1.addMethod(method);
//		}
//// TODO 
//		GeneratedJavaFile file = new GeneratedJavaFile(interface1, project, context.getJavaFormatter());
//		files.add(file);
//	}
//
//	/**
//	 * 添加实现类
//	 * 
//	 * @param introspectedTable
//	 * @param tableName
//	 * @param files
//	 */
//	protected void addServiceImpl(TopLevelClass topLevelClass,IntrospectedTable introspectedTable, String tableName, List<GeneratedJavaFile> files) {
//		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
//		// 设置实现的接口
//		topLevelClass.addSuperInterface(interfaceType);
//
//		if (enableAnnotation) {
//			topLevelClass.addAnnotation("@Service");
//			topLevelClass.addImportedType(service);
//		}
//		// 添加引用dao
//		addField(topLevelClass, tableName);
//		// 添加方法
//		topLevelClass.addMethod(countByExample(introspectedTable, tableName));
//		topLevelClass.addMethod(selectByPrimaryKey(introspectedTable, tableName));
//		topLevelClass.addMethod(selectByExample(introspectedTable, tableName));
//
//		/**
//		 * type 的意义 pojo 1 ;key 2 ;example 3 ;pojo+example 4
//		 */
//		if (enableDeleteByPrimaryKey) {
//			topLevelClass.addMethod(getOtherInteger("deleteByPrimaryKey", introspectedTable, tableName, 2));
//		}
//		if (enableUpdateByPrimaryKeySelective) {
//			topLevelClass.addMethod(getOtherInteger("updateByPrimaryKeySelective", introspectedTable, tableName, 1));
//
//		}
//		if (enableUpdateByPrimaryKey) {
//			topLevelClass.addMethod(getOtherInteger("updateByPrimaryKey", introspectedTable, tableName, 1));
//		}
//		if (enableDeleteByExample) {
//			topLevelClass.addMethod(getOtherInteger("deleteByExample", introspectedTable, tableName, 3));
//		}
//		if (enableUpdateByExampleSelective) {
//			topLevelClass.addMethod(getOtherInteger("updateByExampleSelective", introspectedTable, tableName, 4));
//		}
//		if (enableUpdateByExample) {
//			topLevelClass.addMethod(getOtherInteger("updateByExample", introspectedTable, tableName, 4));
//		}
//		if (enableInsert) {
//			topLevelClass.addMethod(getOtherInsertboolean("insert", introspectedTable, tableName));
//		}
//		if (enableInsertSelective) {
//			topLevelClass.addMethod(getOtherInsertboolean("insertSelective", introspectedTable, tableName));
//		}
//		// TODO 生成文件
//		GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, project, context.getJavaFormatter());
//		files.add(file);
//	}
//
//	/**
//	 * 添加字段
//	 * 
//	 * @param topLevelClass
//	 */
//	protected void addField(TopLevelClass topLevelClass, String tableName) {
//		// 添加 dao
//		Field field = new Field();
//		field.setName(toLowerCase(daoType.getShortName())); // 设置变量名
//		topLevelClass.addImportedType(daoType);
//		field.setType(daoType); // 类型
//		field.setVisibility(JavaVisibility.PRIVATE);
//		if (enableAnnotation) {
//			field.addAnnotation("@Autowired");
//		}
//		topLevelClass.addField(field);
//	}
//
//	/**
//	 * 添加方法
//	 * 
//	 */
//	protected MyMethod selectByPrimaryKey(IntrospectedTable introspectedTable, String tableName) {
//		MyMethod method = new MyMethod();
//		method.setName("selectByPrimaryKey");
//		method.setReturnType(pojoType);
//		if (introspectedTable.getRules().generatePrimaryKeyClass()) {
//			FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
//			method.addParameter(new Parameter(type, "key"));
//		} else {
//			for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
//				FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
//				method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
//			}
//		}
//		method.setVisibility(JavaVisibility.PUBLIC);
//		StringBuilder sb = new StringBuilder();
//		// method.addBodyLine("try {");
//		sb.append("return this.");
//		sb.append(getDaoShort());
//		sb.append("selectByPrimaryKey");
//		sb.append("(");
//		for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
//			sb.append(introspectedColumn.getJavaProperty());
//			sb.append(",");
//		}
//		sb.setLength(sb.length() - 1);
//		sb.append(");");
//		method.addBodyLine(sb.toString());
//		// method.addBodyLine("} catch (Exception e) {");
//		// method.addBodyLine("logger.error(\"Exception: \", e);");
//		// method.addBodyLine("return null;");
//		// method.addBodyLine("}");
//		return method;
//	}
//
//	/**
//	 * 添加方法
//	 * 
//	 */
//	protected MyMethod countByExample(IntrospectedTable introspectedTable, String tableName) {
//		MyMethod method = new MyMethod();
//		method.setName("countByExample");
//		method.setReturnType(FullyQualifiedJavaType.getIntInstance());
//		method.addParameter(new Parameter(pojoCriteriaType, "example"));
//		method.setVisibility(JavaVisibility.PUBLIC);
//		StringBuilder sb = new StringBuilder();
//		sb.append("int count = this.");
//		sb.append(getDaoShort());
//		sb.append("countByExample");
//		sb.append("(");
//		sb.append("example");
//		sb.append(");");
//		method.addBodyLine(sb.toString());
//		method.addBodyLine("logger.debug(\"count: {}\", count);");
//		method.addBodyLine("return count;");
//		return method;
//	}
//
//	/**
//	 * 添加方法
//	 * 
//	 */
//	protected MyMethod selectByExample(IntrospectedTable introspectedTable, String tableName) {
//		MyMethod method = new MyMethod();
//		method.setName("selectByExample");
//		method.setReturnType(new FullyQualifiedJavaType("List<" + tableName + ">"));
//		method.addParameter(new Parameter(pojoCriteriaType, "example"));
//		method.setVisibility(JavaVisibility.PUBLIC);
//		StringBuilder sb = new StringBuilder();
//		sb.append("return this.");
//		sb.append(getDaoShort());
//		if (introspectedTable.hasBLOBColumns()) {
//			sb.append("selectByExampleWithoutBLOBs");
//		} else {
//			sb.append("selectByExample");
//		}
//		sb.append("(");
//		sb.append("example");
//		sb.append(");");
//		method.addBodyLine(sb.toString());
//		return method;
//	}
//
//	/**
//	 * 添加方法
//	 * 
//	 */
//	protected MyMethod getOtherInteger(String methodName, IntrospectedTable introspectedTable, String tableName, int type) {
//		MyMethod method = new MyMethod();
//		method.setName(methodName);
//		method.setReturnType(FullyQualifiedJavaType.getIntInstance());
//		String params = addParams(introspectedTable, method, type);
//		method.setVisibility(JavaVisibility.PUBLIC);
//		StringBuilder sb = new StringBuilder();
//		// method.addBodyLine("try {");
//		sb.append("return this.");
//		sb.append(getDaoShort());
//		if (introspectedTable.hasBLOBColumns()
//				&& (!"updateByPrimaryKeySelective".equals(methodName) && !"deleteByPrimaryKey".equals(methodName)
//						&& !"deleteByExample".equals(methodName) && !"updateByExampleSelective".equals(methodName))) {
//			sb.append(methodName + "WithoutBLOBs");
//		} else {
//			sb.append(methodName);
//		}
//		sb.append("(");
//		sb.append(params);
//		sb.append(");");
//		method.addBodyLine(sb.toString());
//		return method;
//	}
//
//	/**
//	 * 添加方法
//	 * 
//	 */
//	protected MyMethod getOtherInsertboolean(String methodName, IntrospectedTable introspectedTable, String tableName) {
//		MyMethod method = new MyMethod();
//		method.setName(methodName);
//		method.setReturnType(returnType);
//		method.addParameter(new Parameter(pojoType, "record"));
//		method.setVisibility(JavaVisibility.PUBLIC);
//		StringBuilder sb = new StringBuilder();
//		if (returnType==null) {
//			sb.append("this.");
//		} else {
//			sb.append("return this.");
//		}
//		sb.append(getDaoShort());
//		sb.append(methodName);
//		sb.append("(");
//		sb.append("record");
//		sb.append(");");
//		method.addBodyLine(sb.toString());
//		return method;
//	}
//
//	/**
//	 * type 的意义 pojo 1 key 2 example 3 pojo+example 4
//	 */
//	protected String addParams(IntrospectedTable introspectedTable, Method method, int type1) {
//		switch (type1) {
//		case 1:
//			method.addParameter(new Parameter(pojoType, "record"));
//			return "record";
//		case 2:
//			if (introspectedTable.getRules().generatePrimaryKeyClass()) {
//				FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
//				method.addParameter(new Parameter(type, "key"));
//			} else {
//				for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
//					FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
//					method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
//				}
//			}
//			StringBuffer sb = new StringBuffer();
//			for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
//				sb.append(introspectedColumn.getJavaProperty());
//				sb.append(",");
//			}
//			sb.setLength(sb.length() - 1);
//			return sb.toString();
//		case 3:
//			method.addParameter(new Parameter(pojoCriteriaType, "example"));
//			return "example";
//		case 4:
//
//			method.addParameter(0, new Parameter(pojoType, "record"));
//			method.addParameter(1, new Parameter(pojoCriteriaType, "example"));
//			return "record, example";
//		default:
//			break;
//		}
//		return null;
//	}
//
//	protected void addComment(JavaElement field, String comment) {
//		StringBuilder sb = new StringBuilder();
//		field.addJavaDocLine("/**");
//		sb.append(" * ");
//		comment = comment.replaceAll("\n", "<br>\n\t * ");
//		sb.append(comment);
//		field.addJavaDocLine(sb.toString());
//		field.addJavaDocLine(" */");
//	}
//
//	/**
//	 * 添加字段
//	 * 
//	 * @param topLevelClass
//	 */
//	protected void addField(TopLevelClass topLevelClass) {
//		// 添加 success
//		Field field = new Field();
//		field.setName("success"); // 设置变量名
//		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance()); // 类型
//		field.setVisibility(JavaVisibility.PRIVATE);
//		addComment(field, "执行结果");
//		topLevelClass.addField(field);
//		// 设置结果
//		field = new Field();
//		field.setName("message"); // 设置变量名
//		field.setType(FullyQualifiedJavaType.getStringInstance()); // 类型
//		field.setVisibility(JavaVisibility.PRIVATE);
//		addComment(field, "消息结果");
//		topLevelClass.addField(field);
//	}
//
//	/**
//	 * 添加方法
//	 * 
//	 */
//	protected void addMethod(TopLevelClass topLevelClass) {
//		MyMethod method = new MyMethod();
//		method.setVisibility(JavaVisibility.PUBLIC);
//		method.setName("setSuccess");
//		method.addParameter(new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance(), "success"));
//		method.addBodyLine("this.success = success;");
//		topLevelClass.addMethod(method);
//
//		method = new MyMethod();
//		method.setVisibility(JavaVisibility.PUBLIC);
//		method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
//		method.setName("isSuccess");
//		method.addBodyLine("return success;");
//		topLevelClass.addMethod(method);
//
//		method = new MyMethod();
//		method.setVisibility(JavaVisibility.PUBLIC);
//		method.setName("setMessage");
//		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "message"));
//		method.addBodyLine("this.message = message;");
//		topLevelClass.addMethod(method);
//
//		method = new MyMethod();
//		method.setVisibility(JavaVisibility.PUBLIC);
//		method.setReturnType(FullyQualifiedJavaType.getStringInstance());
//		method.setName("getMessage");
//		method.addBodyLine("return message;");
//		topLevelClass.addMethod(method);
//	}
//
//	/**
//	 * 添加方法
//	 * 
//	 */
//	protected void addMethod(TopLevelClass topLevelClass, String tableName) {
//		MyMethod method2 = new MyMethod();
//		for (int i = 0; i < methods.size(); i++) {
//			MyMethod method = new MyMethod();
//			method2 = methods.get(i);
//			method = method2;
//			// TODO 
////			method.removeAllBodyLines();
////			method.removeAnnotation();
//			StringBuilder sb = new StringBuilder();
//			sb.append("return this.");
//			sb.append(getDaoShort());
//			sb.append(method.getName());
//			sb.append("(");
//			List<Parameter> list = method.getParameters();
//			for (int j = 0; j < list.size(); j++) {
//				sb.append(list.get(j).getName());
//				sb.append(",");
//			}
//			sb.setLength(sb.length() - 1);
//			sb.append(");");
//			method.addBodyLine(sb.toString());
//			topLevelClass.addMethod(method);
//		}
//		methods.clear();
//	}
//
//	/**
//	 * BaseUsers to baseUsers
//	 * 
//	 * @param tableName
//	 * @return
//	 */
//	protected String toLowerCase(String tableName) {
//		StringBuilder sb = new StringBuilder(tableName);
//		sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
//		return sb.toString();
//	}
//
//	/**
//	 * BaseUsers to baseUsers
//	 * 
//	 * @param tableName
//	 * @return
//	 */
//	protected String toUpperCase(String tableName) {
//		StringBuilder sb = new StringBuilder(tableName);
//		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
//		return sb.toString();
//	}
//
//	/**
//	 * 导入需要的类
//	 */
//	private void addImport(Interface interfaces, TopLevelClass topLevelClass) {
//		interfaces.addImportedType(pojoType);
//		interfaces.addImportedType(pojoCriteriaType);
//		interfaces.addImportedType(listType);
//		topLevelClass.addImportedType(daoType);
//		topLevelClass.addImportedType(interfaceType);
//		topLevelClass.addImportedType(pojoType);
//		topLevelClass.addImportedType(pojoCriteriaType);
//		topLevelClass.addImportedType(listType);
//		topLevelClass.addImportedType(slf4jLogger);
//		topLevelClass.addImportedType(slf4jLoggerFactory);
//		if (enableAnnotation) {
//			topLevelClass.addImportedType(service);
//			topLevelClass.addImportedType(autowired);
//		}
//	}
//
//	/**
//	 * 导入logger
//	 */
//	private void addLogger(TopLevelClass topLevelClass) {
//		Field field = new Field();
//		field.setFinal(true);
//		field.setInitializationString("LoggerFactory.getLogger(" + topLevelClass.getType().getShortName() + ".class)"); // 设置值
//		field.setName("logger"); // 设置变量名
//		field.setStatic(true);
//		field.setType(new FullyQualifiedJavaType("Logger")); // 类型
//		field.setVisibility(JavaVisibility.PRIVATE);
//		topLevelClass.addField(field);
//	}
//
//	private String getDaoShort() {
//		return toLowerCase(daoType.getShortName()) + ".";
//	}
//	public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
//		returnType = method.getReturnType();
//		return true;
//	}
}
