package org.setareh.wadl.codegen.module;

import org.setareh.wadl.codegen.module.android.AndroidClientModule;
import org.setareh.wadl.codegen.module.coffeescript.CoffeeScriptClientModule;
import org.setareh.wadl.codegen.module.objectivec.ObjectiveCClientModule;
import org.setareh.wadl.codegen.module.swift.SwiftClientModule;
import org.setareh.wadl.codegen.module.typescript.TypeScriptModule;

public enum ModuleName {

    ANDROID(new AndroidClientModule()),  // android java
    COFFEESCRIPT(new CoffeeScriptClientModule()),
    OBJECTIVEC(new ObjectiveCClientModule()), // ios objective-c
    SWIFT(new SwiftClientModule()), // ios swift
    TYPESCRIPT(new TypeScriptModule()); // TypeScript
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
