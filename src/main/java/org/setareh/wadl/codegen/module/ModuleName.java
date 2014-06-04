package org.setareh.wadl.codegen.module;

import org.setareh.wadl.codegen.module.android.AndroidClientModule;
import org.setareh.wadl.codegen.module.ios.IOSClientModule;

public enum ModuleName {

    ANDROID(new AndroidClientModule()),  // android java
	IOS(new IOSClientModule()); // ios objective-c
    //PHP, // php
    //JAVA; // pure java;

    private ClientModule clientModule;

    ModuleName(ClientModule clientModule) {
        this.clientModule = clientModule;
    }


    public ClientModule getClientModule() {
        return clientModule;
    }
}
