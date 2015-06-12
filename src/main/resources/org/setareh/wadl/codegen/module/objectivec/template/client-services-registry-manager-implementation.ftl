[#ftl]
#import "${projectPrefix}RegistryManager.h"

@implementation ${projectPrefix}RegistryManager

static dispatch_once_t managerToken;

static ${projectPrefix}RegistryManager *_sharedManager;
static NSMutableDictionary *_registries;

#pragma mark - Singleton

+(instancetype)sharedManager {
    dispatch_once (&managerToken, ^{
        _sharedManager = [[self alloc] init];
        _registries = [[NSMutableDictionary alloc] init];
    });
    return _sharedManager;
}

#pragma mark - Handling Registry

-(void)addRegistry:(NSObject<${projectPrefix}RegistryProtocol> *)registry forIdentifier:(NSString *)identifier {
    if(!identifier) return;
    _registries[identifier] = registry;
}

-(NSObject<${projectPrefix}RegistryProtocol> *)registryForIdentifier:(NSString *)identifier {
    return _registries[identifier];
}

@end