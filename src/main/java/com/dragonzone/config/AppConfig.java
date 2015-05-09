package com.dragonzone.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.encryption.pbe.config.PBEConfig;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.jasypt.spring31.properties.EncryptablePropertiesPropertySource;
import org.jasypt.spring31.properties.EncryptablePropertyPlaceholderConfigurer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@EnableWebMvc
@Configuration
@ComponentScan({ "com.dragonzone.spring.*" })
@Import({ SecurityConfig.class })
public class AppConfig {
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(AppConfig.class);

	@Autowired
	private ApplicationContext ctx;
	
	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/pages/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

	public static void main(String[] args) throws IOException {
		AppConfig cfe = new AppConfig();
		StringEncryptor enc = cfe.getStandardPBEStringEncryptor(cfe
				.getEnvironmentStringPBEConfig());
		String encryptedValue = PropertyValueEncryptionUtils.encrypt(
				"userpwd", enc);
		System.out.println(encryptedValue);
	}

	@Bean
	@Qualifier("environmentVariablesConfiguration")
	@Autowired
	public PBEConfig getEnvironmentStringPBEConfig() {
		EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
		config.setAlgorithm("PBEWithMD5AndDES");
		config.setPassword("jasypt");
		return config;
	}

	@Bean
	@Qualifier("configurationEncryptor")
	@Autowired
	public StringEncryptor getStandardPBEStringEncryptor(
			@Qualifier("environmentVariablesConfiguration") PBEConfig config) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setConfig(config);

		return encryptor;
	}
	
	private void resourceExists(List<Resource> resourceList) {
		if (resourceList != null) {
			try {
				for (int i = (resourceList.size() - 1); i >= 0; i--) {
					File resourceFile = resourceList.get(i).getFile();
					if (!resourceFile.exists()) {
						logger.warn("Resource file does not exists, omitting: " + resourceFile);
						resourceList.remove(i);
					}
				}
			} catch (Exception e) {
				logger.error("Error verifying resources.");
			}
		}
	}
	
	private List<Resource> getResourceList() {
		List<Resource> resourceList = new ArrayList<>();
		resourceList.add(new ClassPathResource("resources.properties"));
		resourceList.add(new ClassPathResource("webexplorer.properties"));
		resourceList.add(new FileSystemResource("/opt/config/webexplorer.properties"));
		
		resourceExists(resourceList);
		
		return resourceList;
	}

	/**
	 * This is call before the class is instantiated (notice the static),
	 * which will load all the properties file and allow to use the @Value
	 * to inject properties.
	 * @return PropertyPlaceholderConfigurer
	 */
	@Bean
	public static PropertyPlaceholderConfigurer loadPropertyPlaceholderConfigurer() {
		AppConfig appConfig = new AppConfig();
		EncryptablePropertyPlaceholderConfigurer ppc = new EncryptablePropertyPlaceholderConfigurer(
				appConfig.getStandardPBEStringEncryptor(appConfig
						.getEnvironmentStringPBEConfig()));
		ppc.setLocations(appConfig.getResourceList().toArray(new Resource[appConfig.getResourceList().size()]));
		ppc.setIgnoreUnresolvablePlaceholders(true);

		return ppc;
	}
	
	/**
	 * This is called after the class is instantiated,
	 * which will load all the properties files along with the encryptor
	 * so it will be available for the Spring Environment object
	 */
	@PostConstruct
	public void loadEncryptedProperties() {
		Properties properties = new Properties();
		List<Resource> resourceList = getResourceList();
		for (Resource resource : resourceList) {
			try {
				properties.load(resource.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		EncryptablePropertiesPropertySource encPropSource = new EncryptablePropertiesPropertySource(
				"encProps", properties, getStandardPBEStringEncryptor(getEnvironmentStringPBEConfig()));
		ConfigurableApplicationContext configCtx = (ConfigurableApplicationContext) ctx;
		MutablePropertySources sources = configCtx.getEnvironment().getPropertySources();
		sources.addFirst(encPropSource);
	}
}