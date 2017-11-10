package com.prophet.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class HiveDataSourceConfig {
	@Bean(name="hiveServer2DS")
	@ConfigurationProperties(prefix="spring.ds_hive_server2")
	public DataSource prophetHiveServer2DataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name="hiveServer2JdbcTemplate")
	public JdbcTemplate getHiveServer2JdbcTemplate(@Qualifier("hiveServer2DS") DataSource dsHiveServer2) {
		return new JdbcTemplate(dsHiveServer2);
	}
	
}
