package com.dx1ngy.trading.api;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

public class Generator {
    public static void main(String[] args) {
        execMybatisPlus(
                "jdbc:mysql://127.0.0.1:3306/trading?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&failOverReadOnly=false",
                "root",
                "GewMxiNfOtgdaPd7",
                "/Users/dx1ngy/dev/trading/trading-api",
                "dx1ngy",
                "com.dx1ngy.trading.api",
                new String[]{"user", "message", "goods"},
                new String[]{}
        );
    }

    private static void execMybatisPlus(String url, String username, String password, String dir, String author,
                                        String parent, String[] include, String[] exclude) {
        FastAutoGenerator.create(new DataSourceConfig
                        .Builder(url, username, password)
                        .keyWordsHandler(new MySqlKeyWordsHandler()))
                .globalConfig(builder -> builder.outputDir(dir + "/src/main/java")
                        .author(author)
                        .disableOpenDir()
                        .dateType(DateType.TIME_PACK)
                        .commentDate("yyyy-MM-dd"))
                .packageConfig(builder -> builder.parent(parent)
                        .entity("entity")
                        .service("service")
                        .serviceImpl("service.impl")
                        .mapper("mapper")
                        .controller("controller")
                        .pathInfo(Map.of(OutputFile.xml, dir + "/src/main/resources/mappers")))
                .strategyConfig(builder -> builder.addInclude(include)
                        .addExclude(exclude)
                        .entityBuilder()
                        .enableChainModel()
                        .enableLombok()
                        .enableTableFieldAnnotation()
                        .enableColumnConstant()
                        .disableSerialVersionUID()
                        .enableFileOverride()
                        .versionColumnName("version")
                        .versionPropertyName("version")
                        .logicDeleteColumnName("deleted")
                        .logicDeletePropertyName("deleted")
                        .naming(NamingStrategy.underline_to_camel)
                        .columnNaming(NamingStrategy.underline_to_camel)
                        .formatFileName("%sEntity")
                        .controllerBuilder()
                        .template("/templates/controller.java")
                        .enableRestStyle()
                        .formatFileName("%sController")
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        .mapperBuilder()
                        .mapperAnnotation(Mapper.class)
                        .formatMapperFileName("%sMapper")
                        .formatXmlFileName("%sMapper"))
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
