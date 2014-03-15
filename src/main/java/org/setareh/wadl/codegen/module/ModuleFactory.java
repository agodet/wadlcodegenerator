package org.setareh.wadl.codegen.module;

import org.setareh.wadl.codegen.module.android.AndroidClientModule;
import org.setareh.wadl.codegen.module.ios.IOSClientModule;

public class ModuleFactory {
	
	public static ClientModule getModule(ModuleName name) {
		ClientModule module = new AndroidClientModule();
		if (name == ModuleName.ANDROID) {
			module = new AndroidClientModule();
		} else if (name == ModuleName.IOS) {
			module = new IOSClientModule();
		}
		return module;
	}

}
