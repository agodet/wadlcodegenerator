[#ftl]
#import <Foundation/Foundation.h>

@protocol ${projectPrefix}RegistryProtocol <NSObject>

@required
-(NSFormatter *)formatter;

@end

@interface ${projectPrefix}RegistryManager : NSObject

+(instancetype)sharedManager;

-(void)addRegistry:(NSObject<${projectPrefix}RegistryProtocol> *)registry forIdentifier:(NSString *)identifier;
-(NSObject<${projectPrefix}RegistryProtocol> *)registryForIdentifier:(NSString *)identifier;

@end