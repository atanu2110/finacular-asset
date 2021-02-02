package com.finadv.assets.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author atanu
 *
 */
@Configuration
public class AssetUtil {

	@Autowired
	private Environment env;

	public String getProperty(String pPropertyKey) {
		String str = env.getProperty(pPropertyKey);
		return str;
	}
}
