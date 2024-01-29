package com.yuzi;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mybatisplus自动根据数据库的表生成部分代码，包括，entity、mapper(接口和mapper.xml)、service、controller
 * author yuzi
 */
public class MybatisPlusGenerator {

    //////////******************以下配置根据项目自己手动配置*******************************////////

    //生成文件所在项目路径
    private static String baseProjectPath;
    static{
        baseProjectPath = System.getProperty("user.dir") + "/client1";
    }

    //基本包名
    private static String basePackage = "com.yuzi";
    //作者
    private static String authorName = "yuzi";
    //要生成的表名
    private static String[] tables = {"t_sharding_record"};
    //table前缀
    private static String prefix = "t_";

    //数据库配置四要素
    //数据库类型默认配置的是： DbType.MYSQL ，如有改变修改gen.setDataSource
    private static String driverName = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3306/client?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static String username = "root";
    private static String password = "admin@1234";

    //模块相关名称
    //controller包名
    private static String controllerName = "controller";
    //实体类包名
    private static String entity = "entity";
    //mapper接口包名
    private static String mapper = "mapper";
    //service接口包名
    private static String service = "service";
    //service实现类包名
    private static String serviceImpl = "service.impl";

    //////////*************************************************////////


    public static void main(String[] args) {


        AutoGenerator gen = new AutoGenerator();

        /**
         * 数据库配置
         */
        gen.setDataSource(new DataSourceConfig()
                .setDbType(DbType.MYSQL)
                .setDriverName(driverName)
                .setUrl(url)
                .setUsername(username)
                .setPassword(password)
                .setTypeConvert(new MySqlTypeConvert() {
                    @Override
                    public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                        System.out.println("转换类型：" + fieldType);
                        return super.processTypeConvert(globalConfig, fieldType);
                    }
                }));

        /**
         * 全局配置
         */
        gen.setGlobalConfig(new GlobalConfig()
                .setOutputDir(baseProjectPath + "/src/main/java")//输出目V1.001__init_task_tables.sql录
                .setFileOverride(false)// 是否覆盖文件
                .setActiveRecord(true)// 开启 activeRecord 模式
                .setEnableCache(false)// XML 二级缓存
                .setBaseResultMap(true)// XML ResultMap
                .setBaseColumnList(true)// XML columList
                .setDateType(DateType.ONLY_DATE)//使用 java.util.date ，否则默认是使用jkd8的 java.time 包下的
                .setOpen(false)//生成后打开文件夹
                .setAuthor(authorName)
                // 自定义文件命名，注意 %s 会自动填充表实体属性！
                .setMapperName("%sMapper")
                .setXmlName("%sMapper")
        );

        /**
         * 策略配置
         */
        gen.setStrategy(new StrategyConfig()
                        // .setCapitalMode(true)// 全局大写命名
                        //.setDbColumnUnderline(true)//全局下划线命名
                        .setTablePrefix(new String[]{prefix})// 此处可以修改为您的表前缀
                        .setNaming(NamingStrategy.underline_to_camel)// 表名生成策略
                        .setInclude(tables) // 需要生成的表
                        .setRestControllerStyle(true)
                        .setEntityLombokModel(true)
        );

        /**
         * 包配置
         */
        gen.setPackageInfo(new PackageConfig()
                .setParent(basePackage)// 自定义包路径
                .setEntity(entity)
                .setMapper(mapper)
                .setXml("mybatis/mapper")
        );

        /**
         * 注入自定义配置
         */
        // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
        InjectionConfig abc = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        };
        //自定义文件输出位置（非必须）
        List<FileOutConfig> fileOutList = new ArrayList<>();
        fileOutList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return baseProjectPath + "/src/main/resources/mybatis/mapper/" + tableInfo.getEntityName() + ".xml";
            }
        });
        abc.setFileOutConfigList(fileOutList);
        gen.setCfg(abc);

        /**
         * 指定模板引擎 默认是VelocityTemplateEngine ，需要引入相关引擎依赖
         */
        gen.setTemplateEngine(new FreemarkerTemplateEngine());

        /**
         * 模板配置
         */
        gen.setTemplate(
                // 关闭默认 xml 生成，调整生成 至 根目录
                new TemplateConfig().setXml(null)
        );

        // 执行生成
        gen.execute();
    }
}
