package org.spider.junit;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.spider.AppHome;

public class InitExtension implements BeforeAllCallback {

	@Override
	public void beforeAll(ExtensionContext context) {
		AppHome.init();
	}
}