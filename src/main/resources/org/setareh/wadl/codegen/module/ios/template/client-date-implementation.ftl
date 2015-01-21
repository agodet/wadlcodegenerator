[#ftl]
#import "${projectPrefix}DateFormatterUtils.h"
#import "${projectPrefix}RegistryManager.h"

@implementation ${projectPrefix}DateFormatterUtils

+(NSString *) formatWithDate:(NSDate *)date {
    NSObject<${projectPrefix}RegistryProtocol> *registry = [[${projectPrefix}RegistryManager sharedManager] registryForIdentifier:timeZoneRegistryIdentifier];
    if(!registry){
        [NSException raise:NSInternalInconsistencyException format:@"You must implement a date formatter for the type '%@'", timeZoneRegistryIdentifier];
    }
    return [registry formatFromObject:date];
}

+(NSDate *) convertToDate:(NSObject*)input {
    NSObject<${projectPrefix}RegistryProtocol> *registry = [[${projectPrefix}RegistryManager sharedManager] registryForIdentifier:timeZoneRegistryIdentifier];
    if(!registry){
        [NSException raise:NSInternalInconsistencyException format:@"You must implement a date formatter for the type '%@'", timeZoneRegistryIdentifier];
    }
    return (NSDate *)[registry parseFromString:(NSString *)input];
}

@end