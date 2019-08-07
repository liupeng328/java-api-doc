package com.apidoc.service;


import cn.hutool.core.util.ReflectUtil;
import com.apidoc.annotation.Api;
import com.apidoc.common.Const;
import com.apidoc.dao.ApidocActionDao;
import com.apidoc.dao.ApidocInfoDao;
import com.apidoc.dao.ApidocModuleDao;
import com.apidoc.dao.ApidocParamDao;
import com.apidoc.entity.ApidocAction;
import com.apidoc.entity.ApidocInfo;
import com.apidoc.entity.ApidocModule;
import com.apidoc.entity.ApidocParam;
import com.apidoc.entity.bean.Detail;
import com.apidoc.entity.bean.Params;
import com.apidoc.utis.ClassScanUtil;
import com.apidoc.utis.JsonUtil;
import com.apidoc.utis.SpringUtil;
import com.apidoc.utis.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 生成API文档工具类
 *
 * @author : admin
 */
@Service
public class ApiDocService {

    /**
     * 封装基本类型和参数类型的对应关心
     */
    private static final Map<Class, String> typeMap = new HashMap<>();

    //初始化
    static {
        typeMap.put(byte.class, Const.NUMBER);
        typeMap.put(short.class, Const.NUMBER);
        typeMap.put(int.class, Const.NUMBER);
        typeMap.put(long.class, Const.NUMBER);
        typeMap.put(float.class, Const.NUMBER);
        typeMap.put(double.class, Const.NUMBER);
        typeMap.put(char.class, Const.STRING);
        typeMap.put(boolean.class, Const.BOOLEANN);
    }

    @Autowired
    private ApidocInfoDao apidocInfoDao;
    @Autowired
    private ApidocModuleDao apidocModuleDao;
    @Autowired
    private ApidocActionDao apidocActionDao;
    @Autowired
    private ApidocParamDao apidocParamDao;

    private DataSource dataSource;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        createTable();
    }

    /**
     * 自动创建数据库的表
     * apidoc-info   文档基本信息表
     * apidoc-module 模块表
     * apidoc-action 接口信息表
     * apidoc-param  接口参数表
     */
    private void createTable() {
        //apidoc-info   文档基本信息表
        //apidoc-module 模块表
        //apidoc-action 接口信息表
        //apidoc-param  接口参数表
        String createInfo = "CREATE TABLE `apidoc_info` (\n" +
                "\t`id` INT(11) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "\t`title` VARCHAR(200) NOT NULL COMMENT '文档标题' COLLATE 'utf8_bin',\n" +
                "\t`description` TEXT NULL COMMENT '文档描述' COLLATE 'utf8_bin',\n" +
                "\t`version` VARCHAR(20) NOT NULL DEFAULT '1.0.0' COMMENT '版本信息 如1.0.0' COLLATE 'utf8_bin',\n" +
                "\t`packageName` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '包名，用于区别一个项目中的多个文档' COLLATE 'utf8_bin',\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tUNIQUE INDEX `title_packageName` (`title`, `packageName`)\n" +
                ")\n" +
                "COMMENT='文件基本信息'\n" +
                "COLLATE='utf8_bin'\n" +
                "ENGINE=InnoDB\n" +
                ";\n";
        String createModule = "CREATE TABLE `apidoc_module` (\n" +
                "\t`id` INT(11) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "\t`name` VARCHAR(200) NOT NULL COMMENT '模块名称' COLLATE 'utf8_bin',\n" +
                "\t`order` INT(11) NOT NULL COMMENT '排序',\n" +
                "\t`packageName` VARCHAR(200) NOT NULL COMMENT '包名，区分不用的文档' COLLATE 'utf8_bin',\n" +
                "\t`classList` VARCHAR(1000) NOT NULL COMMENT '类全名，多个之间用英文逗号隔开' COLLATE 'utf8_bin',\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tUNIQUE INDEX `name_packageName` (`name`, `packageName`)\n" +
                ")\n" +
                "COMMENT='文档模块信息'\n" +
                "COLLATE='utf8_bin'\n" +
                "ENGINE=InnoDB\n" +
                ";";
        String createAction = "CREATE TABLE `apidoc_action` (\n" +
                "\t`id` INT(11) NOT NULL AUTO_INCREMENT COMMENT 'id',\n" +
                "\t`name` VARCHAR(200) NOT NULL COMMENT '名称' COLLATE 'utf8_bin',\n" +
                "\t`moduleId` INT(11) NOT NULL COMMENT '模块id',\n" +
                "\t`order` INT(11) NOT NULL COMMENT '排序',\n" +
                "\t`methodUUID` VARCHAR(200) NOT NULL COMMENT '方法的唯一标示符，方法名-形参类型,形参类型   \\\\n为了区别java方法的重载' COLLATE 'utf8_bin',\n" +
                "\t`description` TEXT NULL COMMENT '接口描述' COLLATE 'utf8_bin',\n" +
                "\t`requestDescription` TEXT NULL COMMENT '请求参数描述' COLLATE 'utf8_bin',\n" +
                "\t`responseDescription` TEXT NULL COMMENT '响应参数描述' COLLATE 'utf8_bin',\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tUNIQUE INDEX `moduleId_methodUUID` (`moduleId`, `methodUUID`)\n" +
                ")\n" +
                "COMMENT='文档接口信息'\n" +
                "COLLATE='utf8_bin'\n" +
                "ENGINE=InnoDB\n" +
                ";\n";
        String createParam = "CREATE TABLE `apidoc_param` (\n" +
                "\t`id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "\t`name` VARCHAR(100) NULL DEFAULT NULL COMMENT '名称' COLLATE 'utf8_bin',\n" +
                "\t`dataType` VARCHAR(50) NULL DEFAULT NULL COMMENT '数据类型' COLLATE 'utf8_bin',\n" +
                "\t`description` VARCHAR(500) NULL DEFAULT NULL COMMENT '描述' COLLATE 'utf8_bin',\n" +
                "\t`defaultValue` VARCHAR(500) NULL DEFAULT NULL COMMENT '默认值' COLLATE 'utf8_bin',\n" +
                "\t`required` TINYINT(4) NOT NULL DEFAULT '1' COMMENT '是否必须',\n" +
                "\t`actionId` INT(11) NOT NULL COMMENT '接口id',\n" +
                "\t`returnd` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否是返回值',\n" +
                "\t`pid` INT(11) NOT NULL DEFAULT '0' COMMENT '父参数id',\n" +
                "\t`pclassName` VARCHAR(200) NOT NULL DEFAULT '0' COMMENT '父类名  所属类名' COLLATE 'utf8_bin',\n" +
                "\tPRIMARY KEY (`id`),\n" +
                "\tINDEX `FK_apidoc_param_apidoc_action` (`actionId`)\n" +
                ")\n" +
                "COMMENT='文档参数信息'\n" +
                "COLLATE='utf8_bin'\n" +
                "ENGINE=InnoDB\n" +
                ";\n";

        if (!existTable("apidoc_info")) {
            createNewTable(createInfo);
        }
        if (!existTable("apidoc_module")) {
            createNewTable(createModule);
        }
        if (!existTable("apidoc_action")) {
            createNewTable(createAction);
        }
        if (!existTable("apidoc_param")) {
            createNewTable(createParam);
        }
    }

    /**
     * 执行sql
     */
    private boolean existTable(String param) {
        String sql = "SELECT count(*) FROM information_schema.TABLES where table_name = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int execute = -1;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, param);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                execute = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, preparedStatement);
        }
        return execute > 0;
    }

    /**
     * 关闭数据库连接
     *
     * @param connection
     * @param preparedStatement
     */
    private void close(Connection connection, PreparedStatement preparedStatement) {
        if (null != preparedStatement) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (null != connection) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 创建表格
     */
    private void createNewTable(String sql) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, preparedStatement);
        }
    }

    /**
     * 获取文档基本信息
     * <p>
     * 读取数据库，数据存在返回，不存在返回默认值
     * </p>
     *
     * @return String 基本信息
     */
    public ApidocInfo getInfo(String packageName) {
        ApidocInfo apidocInfo = apidocInfoDao.selectByPackageName(packageName);
        if (apidocInfo != null) {
            return apidocInfo;
        } else {
            ApidocInfo info = new ApidocInfo();
            info.setPackageName(packageName);
            apidocInfoDao.insert(info);
            return info;
        }
    }


    /**
     * 修改文档信息
     *
     * @param apidocInfo 文档信息
     * @return boolean
     */
    public boolean updateInfo(ApidocInfo apidocInfo) {
        return apidocInfo.updateById();
    }

    /**
     * 获取模块信息
     * <p>
     * 查询数据库的模块信息列表，
     * 扫描package下的所有class得到模块，
     * 如果该模块数据库已存在则组合成list返回前端，
     * 否则则解析代码并保存信息到数据库然后组合成list返回前端
     * </p>
     */
    @Transactional
    public List<ApidocModule> getModules(String packageName) {
        //查询数据库所有该package下的模块信息
        Set<ApidocModule> modules4db = apidocModuleDao.selectByPackageName(packageName);
        //把list转为HashMap，方便快速查询
        final Map<String, ApidocModule> moduleMap = new HashMap<>();
        modules4db.forEach(m -> moduleMap.put(m.getName(), m));
        //返回前端的集合
        Set<ApidocModule> modules4front = new HashSet<>();

        //扫描包得到类
        Set<Class> classSet = ClassScanUtil.getClass4Annotation(packageName, Api.class);
        if (classSet.isEmpty()) {
            throw new RuntimeException("该包下没有class文件： " + packageName);
        } else {
            for (Class claszz : classSet) {
                //获取元数据
                Api api = (Api) claszz.getAnnotation(Api.class);
                //没有写明模块名称时 默认取类名全称 com.xxx.xxx
                String name = StringUtil.isNotEmpty(api.value()) ? api.value() : claszz.getName();
                //默认排序int最大值
                Integer order = Integer.MAX_VALUE;
                String className = claszz.getName();
                //判断数据库是否已经存在
                ApidocModule apidocModule = moduleMap.get(name);
                //模块已存在时，判断class是否已经存在模块信息中
                if (apidocModule != null) {
                    String classListStr = apidocModule.getClassList();
                    if (!classListStr.contains(className)) {
                        apidocModule.setClassList(apidocModule.getClassList() + "," + className);
                        apidocModule.updateById();
                    }
                } else {//模块不存在时，新增
                    apidocModule = new ApidocModule();
                    apidocModule.setName(name);
                    apidocModule.setOrder(order);
                    apidocModule.setPackageName(packageName);
                    apidocModule.setClassList(className);
                    //插入数据库
                    apidocModule.insert();
                }
                //添加到返回集合
                modules4front.add(apidocModule);
                moduleMap.put(apidocModule.getName(), apidocModule);
            }
            //删除没有用到的模块
            modules4db.forEach(m -> {
                if (!modules4front.contains(m)) {
                    //删除模块信息
                    apidocModuleDao.deleteById(m.getId());
                }
            });
        }
        //排序 并返回前端 默认按名称排序 如果存在order则按order排序
        List<ApidocModule> moduleList = new ArrayList<>(modules4front);
        moduleList.sort(Comparator.comparing(ApidocModule::getName));
        moduleList.sort(Comparator.comparing(ApidocModule::getOrder));
        return moduleList;
    }

    /**
     * 获取接口列表信息
     * 获取模块下 所有类 的所有public方法 组成的信息
     *
     * @param moduleId 模块id
     * @return List<ApidocAction>
     */
    @Transactional
    public List<ApidocAction> getActions(Integer moduleId) {
        //查询模块有几个class组成
        String classListStr = apidocModuleDao.findClassListById(moduleId);
        if (StringUtil.isEmpty(classListStr)) {
            return null;
        }
        String[] classList = classListStr.split(",");
        if (classList.length > 0) {
            //获得该模块下数据库中所有的接口
            List<ApidocAction> actions4db = apidocActionDao.selectByModuleId(moduleId);
            //为了方便快速查询，list转map
            Map<String, ApidocAction> actionMap = new HashMap<>();
            actions4db.forEach(m -> actionMap.put(m.getMethodUUID(), m));
            //返回前台的list
            List<ApidocAction> actions4front = new ArrayList<>();

            for (int i = 0; i < classList.length; i++) {
                String className = classList[i];
                //获得该类文件的所有public方法的注释
                Map<String, String> methodNotes = StringUtil.getMethodNotes(className);
                Class claszz = getClassByName(className);
                //获得本类的所有public方法
                if (claszz != null) {
                    Method[] methods = claszz.getMethods();
                    Class superclass = claszz.getSuperclass();
                    Method[] superclassMethods = superclass.getMethods();
                    List<Method> methodList = new LinkedList<>(Arrays.asList(methods));
                    List<Method> superclassMethodsList = new LinkedList<>(Arrays.asList(superclassMethods));
                    methodList.removeAll(superclassMethodsList);

                    //获得该类的public方法信息
                    if (methodList.size() > 0) {
                        List<ApidocAction> actions = new ArrayList<>(methods.length);
                        for (Method method : methodList) {
                            //todo 暂时没处理方法重载
                            //方法名 格式：全类名-方法名
                            String methodUUID = claszz.getName() + "-" + method.getName();
                            //返回前端的数据
                            ApidocAction action = actionMap.get(methodUUID);
                            //判断数据库中是否已经存在该接口信息,不存在时添加
                            if (action == null) {
                                action = new ApidocAction();
                                action.setMethodUUID(methodUUID);
                                //得到方法的注释信息
                                String name = methodNotes.get(methodUUID);
                                //有注释时用注释 没有时默认方法的名称
                                action.setName(StringUtil.isEmpty(name) ? method.getName() : name);
                                action.setOrder(Integer.MAX_VALUE);
                                action.setModuleId(moduleId);
                                //写入数据库
                                action.insert();
                            }
                            //添加到list
                            actions.add(action);
                        }
                        //添加到总actions
                        actions4front.addAll(actions);
                        actions.forEach(m -> actionMap.put(m.getMethodUUID(), m));
                    }
                }
            }

            //删除不用的action
            actions4db.forEach(m -> {
                if (!actions4front.contains(m)) {
                    //删掉action
                    apidocActionDao.deleteById(m.getId());
                    //删除接口参数
                    apidocParamDao.deleteByActionId(m.getId());
                }
            });

            //排序 默认按名称排序 存在order时,order优先排序
            actions4front.sort(Comparator.comparing(ApidocAction::getName));
            actions4front.sort(Comparator.comparing(ApidocAction::getOrder));
            return actions4front;
        }
        return null;
    }

    /**
     * 修改action
     */
    public boolean updateAction(ApidocAction apidocAction) {
        return apidocActionDao.updateById(apidocAction) > 0;
    }

    /**
     * 修改模块的排序
     */
    @Transactional
    public boolean updateModulesSort(List<ApidocModule> apidocModuleList) {
        apidocModuleList.forEach(m -> apidocModuleDao.updateById(m));
        return true;
    }

    /**
     * 修改action的排序
     */
    @Transactional
    public boolean updateActionsSort(List<ApidocAction> apidocActionList) {
        apidocActionList.forEach(m -> apidocActionDao.updateById(m));
        return true;
    }

    /**
     * 根据方法名唯一标识 获取 该方法的文档信息
     *
     * @param id
     * @param methodUUID 方法名的唯一标识
     * @return List<Details>
     */
    @Transactional
    public Detail getDetail(Integer id, String methodUUID) {
        //拆分类名和方法名
        String[] split = methodUUID.split("-");
        if (split.length == 2) {
            String className = split[0];
            Class claszz = getClassByName(className);
            if (claszz != null) {
                String methodName = split[1];
                //todo 方法可能存在重载，后期处理
                // 即：重名相同但是参数列表不同（参数的类型 数量 顺序不同）
                Method method = getMethod(claszz, methodName);
                if (method != null) {
                    //url 映射 mapping 为类上的mapping+方法上的mapping
                    String mapping = SpringUtil.getMapping(claszz) + SpringUtil.getMapping(method);
                    //请求方式
                    String requestMethod = SpringUtil.getRequestMethod(method);
                    String description = apidocActionDao.selectDescriptionById(id);
                    //请求参数和响应参数
                    Params requestParams = getParams(id, method);
                    Params responseParams = getReturn(id, method);

                    //返回前端数据
                    Detail detail = new Detail();
                    detail.setMapping(mapping);
                    detail.setRequestMethod(requestMethod);
                    detail.setDescription(description);
                    detail.setRequestParam(requestParams);
                    detail.setResponseParam(responseParams);
                    return detail;
                }
            }
        }
        return null;
    }

    /**
     * 根据类全名获得类对象
     *
     * @param className 类全名
     * @return Class
     */
    private Class getClassByName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得方法的参数列表
     *
     * @param id     actionId
     * @param method 方法对象
     * @return Params
     */
    private Params getParams(Integer id, Method method) {
        String type = getType(method);
        String description = apidocActionDao.selectRequestDescriptionById(id);
        List<ApidocParam> apidocParams = getParams(method, id);
        System.err.println("转化前的请求参数： " + JsonUtil.toJsonString(apidocParams));
        List<ApidocParam> apidocParamList = list2Tree(apidocParams);
        System.err.println("转化完成的请求参数： " + JsonUtil.toJsonString(apidocParamList));

        Params params = new Params();
        params.setType(type);
        params.setDescription(description);
        params.setParams(apidocParamList);
        return params;
    }

    /**
     * 将list数据转换为tree结构数据
     */
    private List<ApidocParam> list2Tree(List<ApidocParam> params) {
        if (null == params || params.size() == 0) {
            return null;
        }
        List<ApidocParam> trees = new ArrayList<>();
        for (ApidocParam treeNode : params) {
            if (0 == treeNode.getPid()) {
                trees.add(treeNode);
            }
            for (ApidocParam it : params) {
                if (it.getPid().equals(treeNode.getId())) {
                    if (treeNode.getList() == null) {
                        treeNode.setList(new ArrayList<>());
                    }
                    treeNode.getList().add(it);
                }
            }
        }
        //如果参数个数为一个 且是对象类型且拥有子参数 去掉第一个参数 =》符合spring的参数规范
        if (trees.size() == 1 && trees.get(0).getList() != null && trees.get(0).getDataType().contains("object")) {
            return trees.get(0).getList();
        }
        return trees;
    }

    /**
     * 获取参数列表
     *
     * @param method   方法
     * @param actionId actionId
     * @return List<ParamItem>
     */
    private List<ApidocParam> getParams(Method method, Integer actionId) {
        //查询数据库，参数如果存在则直接返回 否则解析代码生成
        List<ApidocParam> list = apidocParamDao.selectListByActionId(actionId, false);
        if (list.isEmpty()) {
            return generateParams(method, actionId);
        }
        return list;
    }

    /**
     * 解析java代码 构建请求参数
     */
    private List<ApidocParam> generateParams(Method method, Integer actionId) {
        List<ApidocParam> list = new ArrayList<>();
        //1.得到参数名
        String[] paramNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(method);
        //2.得到参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        //3.得到参数的通用类型
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        //遍历所有参数，解析每个参数
        for (int i = 0; i < parameterTypes.length; i++) {
            //请求参数的类型 只能是 字符串string 数字number 自定义对象 数组（普通数组，对象数组） list map 文件
            isType(list, actionId, paramNames[i], parameterTypes[i], genericParameterTypes[i], null, 0, true, false);
        }
        System.err.println(JsonUtil.toJsonString(list));
        return list;
    }

    /**
     * 判断参数的类型
     * 请求参数的类型 只能是 字符串string 数字number 自定义对象 数组（普通数组，对象数组） 泛型（list map） 文件 boolean 日期Data
     * <p>
     *
     * @param list      参数项列表
     * @param actionId  接口详情id
     * @param paramName 参数名
     * @param tclass    参数的Class
     * @param genType   参数的通用类型
     * @param pclass    父class
     * @param pid       父id 父对象名称
     * @param isSelf    是否是对象的自嵌套，用于判断对象的自嵌套
     * @param isReturn  是否是返回值，用于标注参数属于返回值
     */
    private Class class4data = null;

    private synchronized void isType(List<ApidocParam> list, Integer actionId, String paramName, Class tclass, Type genType,
                                     Class pclass, Integer pid, boolean isSelf, boolean isReturn) {
        ApidocParam item = new ApidocParam();
        item.setPid(pid);
        //基本类型可能取不到class的名称 这里toString一下
        if (StringUtil.isEmpty(paramName)) {
            paramName = tclass.toString();
        }
        item.setName(paramName);
        item.setDefaultValue(getObjectDefaultValue(tclass) + "");
        //默认必须
        item.setRequired(true);
        item.setActionId(actionId);
        //如果是返回值的参数需要标注
        if (isReturn) {
            item.setReturnd(true);
        }
        //设置所属类名
        if (pclass != null) {
            String pclassName = pclass.getName();
            item.setPclassName(pclassName);
            Map<String, String> fieldsNotes = StringUtil.getFieldsNotes(pclassName);
            if (fieldsNotes != null) {
                String description = fieldsNotes.get(paramName);
                if (StringUtil.isNotEmpty(description)) {
                    item.setDescription(description);
                    item.setPclassName(pclassName);
                } else {
                    item.setDescription(paramName);
                    item.setPclassName("0");
                }
            }
        } else {
            item.setDescription(paramName);
            item.setPclassName("0");
        }
        item.insert();

        //设置参数类型
        //数组 或者多维数组
        if (tclass.isArray()) {
            //获得数组类型
            Class typeClass = tclass.getComponentType();
            String shortName = typeClass.getSimpleName();
            item.setDataType(Const.ARRAY + shortName);
            item.updateById();
            //添加到list
            list.add(item);
            //处理多维数组
            isType(list, actionId, typeClass.getSimpleName().toLowerCase(),
                    typeClass, null, tclass, item.getId(), isSelf, isReturn);
        }

        //泛型 或泛型中嵌套泛型
        if (genType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genType;
            Type[] parameterArgTypes = aType.getActualTypeArguments();
            //todo 先支持collection和map类型 后期有需要再加
            if (Collection.class.isAssignableFrom(tclass)) {
                // 是Collection
                Type type = parameterArgTypes[0];
                String[] split = type.getTypeName().split("<");
                if (split.length > 0) {
                    Class typeClass = null;
                    try {
                        typeClass = Class.forName(split[0]);
                        item.setDataType(Const.ARRAY + typeClass.getSimpleName());
                        item.updateById();
                        list.add(item);
                        isType(list, actionId, typeClass.getSimpleName().toLowerCase(), typeClass, type, tclass, item.getId(), isSelf, isReturn);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else if (Map.class.isAssignableFrom(tclass)) {
                // 是 Map map比较特殊，只能运行时得到值，用户只能页面手动修改了
                item.setDataType(Const.OBJECT + "Map");
                item.updateById();
                list.add(item);
            } else if (parameterArgTypes.length == 1 && tclass.getName().contains("Result")) {
                // 针对自定义类型 Result<T> 特殊处理
                //保存对象中属性名为data的类型
                class4data = (Class) parameterArgTypes[0];
            }
        }

        //基本类型 ：字符串，数字，文件，时间日期类型
        //数字
        if (Number.class.isAssignableFrom(tclass) || Const.NUMBER.equals(typeMap.get(tclass))) {
            item.setDataType(Const.NUMBER);
            item.updateById();
            list.add(item);
        }
        //字符串
        if (CharSequence.class.isAssignableFrom(tclass) || Character.class.isAssignableFrom(tclass) || Const.STRING.equals(typeMap.get(tclass))) {
            item.setDataType(Const.STRING);
            item.updateById();
            list.add(item);
        }
        //boolean
        if (Boolean.class.isAssignableFrom(tclass) || Const.BOOLEANN.equals(typeMap.get(tclass))) {
            item.setDataType(Const.BOOLEANN);
            item.updateById();
            list.add(item);
        }
        //文件 MultipartFile
        if (InputStreamSource.class.isAssignableFrom(tclass)) {
            item.setDataType(Const.FILE);
            item.updateById();
            list.add(item);
        }
        //文件 MultipartFile
        if (Date.class.isAssignableFrom(tclass)) {
            item.setDataType(Const.DATE);
            item.updateById();
            list.add(item);
        }
        //自定义对象类型
        if (isMyClass(tclass)) {
            //自定义对象类型为对象的名称
            item.setDataType(Const.OBJECT + tclass.getSimpleName());
            item.updateById();
            list.add(item);
            //获得对象的所有字段 包括继承的所有父类的属性
            Field[] fields = ReflectUtil.getFieldsDirectly(tclass, true);
            //排除static和final修饰的属性
            List<Field> fieldList = removeStaticAndFinal(fields);
            if (fieldList.size() > 0) {
                for (Field field : fieldList) {
                    Class<?> typeClass = field.getType();
                    String fieldName = field.getName();
                    //针对自定义类型 Result<T> 特殊处理
                    if (class4data != null) {
                        typeClass = class4data;
                        class4data = null;
                    }
                    //考虑对象的字段可能是对象  可能存在 自嵌套 互相嵌套的类
                    //自嵌套  只走一次
                    if (typeClass == tclass && isSelf) {
                        isType(list, actionId, fieldName, typeClass, null, tclass, item.getId(), false, isReturn);
                    }
                    if (typeClass != tclass) {
                        isType(list, actionId, fieldName, typeClass, null, tclass, item.getId(), isSelf, isReturn);
                    }
                }
            }
        }//isObject end

    }


    /**
     * 获得对象的默认值
     *
     * @param tclass class对象
     * @return Object
     */
    private Object getObjectDefaultValue(Class tclass) {
        Object defaultValue = null;
        //数字类型 默认值 0
        if (Number.class.isAssignableFrom(tclass)) {
            defaultValue = 0;
        }
        //字符串类型 默认 ""
        else if (CharSequence.class.isAssignableFrom(tclass)) {
            defaultValue = "";
        }
//        //文件类型 默认"file"
//        else if (MultipartFile.class.isAssignableFrom(tclass)) {
//            defaultValue = "file";
//        }
        return defaultValue;
    }

    /**
     * 去掉static的final修饰的字段
     *
     * @param fields 字段列表
     * @return Field[]
     */
    private List<Field> removeStaticAndFinal(Field[] fields) {
        List<Field> fieldList = new ArrayList<>();
        if (fields.length > 0) {
            for (Field field : fields) {
                String modifier = Modifier.toString(field.getModifiers());
                if (modifier.contains("static") || modifier.contains("final")) {
                    //舍弃
                } else {
                    fieldList.add(field);
                }
            }
        }
        return fieldList;
    }

    /**
     * 是否是自定义类型
     *
     * @param clz class对象
     * @return boolean
     */
    private boolean isMyClass(Class<?> clz) {
        if (clz == null) {
            return false;
        }
        //排除 spring的文件类型
        if (MultipartFile.class.isAssignableFrom(clz)) {
            return false;
        }
        //排除数组
        if (clz.isArray()) {
            return false;
        }
        //Object 类型特殊处理
        if (clz == Object.class) {
            return true;
        }
        //只能是jdk的根加载器
        return clz.getClassLoader() != null;
    }

    /**
     * 获得指定method的请求方式
     *
     * @param method 方法
     * @return String
     */
    private String getType(Method method) {
        //获得参数类型
        //get: url ,path
        //post: from,json
        //put: json
        //delete: path
        String type = Const.JSON;
        String requestMethod = SpringUtil.getRequestMethod(method);
        switch (requestMethod) {
            case Const.GET:
            case Const.DELETE:
                if (containsPathVariableAnnotation(method.getParameterAnnotations())) {
                    type = Const.URI;
                } else {
                    type = Const.URL;
                }
                break;
            case Const.PUT:
            case Const.POST:
                if (containsRequestBodyAnnotation(method.getParameterAnnotations())) {
                    type = Const.JSON;
                } else {
                    type = Const.FROM;
                }
                break;
        }
        return type;
    }

    private boolean containsRequestBodyAnnotation(Annotation[][] parameterAnnotations) {
        for (Annotation[] annotations : parameterAnnotations) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof RequestBody) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsPathVariableAnnotation(Annotation[][] parameterAnnotations) {
        for (Annotation[] annotations : parameterAnnotations) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof PathVariable) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 根据名称获得方法对象
     *
     * @param claszz 类对象
     * @return Method
     */
    private Method getMethod(Class<?> claszz, String name) {
        Method[] methods = claszz.getMethods();
        for (Method m : methods) {
            if (name.equals(m.getName())) {
                return m;
            }
        }
        return null;
    }


    /**
     * 得到返回参数类型 组成的参数列表
     *
     * @param actionId
     * @param method   方法
     * @return Params
     */
    private Params getReturn(Integer actionId, Method method) {
        // 1.封装响应数据
        Params params = new Params();
        //获得方法的返回值
        Class<?> rclass = method.getReturnType();
        // 2.设置请求或响应类型
        if (rclass.getTypeName().equals(void.class.getTypeName())) {
            params.setType(Const.BLOB);
        } else {
            params.setType(Const.JSON);
        }
        // 3.设置描述
        params.setDescription(apidocActionDao.selectResponseDescriptionById(actionId));
        //查询数据库，存在返回参数之间返回，否则解析代码生成写入数据库并返回
        List<ApidocParam> list = apidocParamDao.selectListByActionId(actionId, true);
        if (null == list || list.isEmpty()) {
            list = new ArrayList<>();
            // 得到通用类型
            Type genericParameterTypes = method.getGenericReturnType();
            isType(list, actionId, rclass.getSimpleName().toLowerCase(), rclass, genericParameterTypes, null, 0, true, true);
        }
        // 转tree结构
        List<ApidocParam> paramItemList = list2Tree(list);
        System.err.println("转换后的  " + JsonUtil.toJsonString(paramItemList));
        // 4.设置参数列表
        params.setParams(paramItemList);
        return params;
    }

    /**
     * 修改接口描述
     *
     * @param apidocAction
     * @return
     */
    public boolean updateActionDescription(ApidocAction apidocAction) {
        return apidocAction.updateById();
    }

    /**
     * 修改接口参数
     *
     * @param apidocParam
     * @return
     */
    public boolean updateParam(ApidocParam apidocParam) {
        return apidocParam.updateById();
    }

    /**
     * 添加接口参数
     *
     * @param apidocParam
     * @return
     */
    public Integer addParam(ApidocParam apidocParam) {
        apidocParam.insert();
        return apidocParam.getId();
    }

    /**
     * 删除接口参数
     *
     * @param id
     * @return
     */
    public boolean deleteParam(Integer id) {
        return apidocParamDao.deleteById(id) > 0;
    }

}
