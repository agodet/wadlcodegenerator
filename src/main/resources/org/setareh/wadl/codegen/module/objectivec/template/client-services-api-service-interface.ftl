[#ftl]
#import <Foundation/Foundation.h>
[#list imports as import]
#import "${import}"
[/#list]


@interface ${className}Api: NSObject

@property(nonatomic, copy, readonly) NSString *basePath;
@property(nonatomic, copy, readonly) NSString *group;

+ (${className}Api*) sharedApiWithBasePath:(NSString *)basePath;
+ (${className}Api*) sharedApiWithBasePath:(NSString *)basePath  withGroup:(NSString *)group;

- (unsigned long)requestQueueSize;
- (void)setAuthenticationLogin:(NSString *)login andPassword:(NSString *)password;
- (void)setAuthorizationWithBlock:(NSString *(^)(NSURL *, NSString * method, NSData *body))authorizationBlock;
- (void)setBasePath:(NSString *)basePath;

[#list methods as method]
- (NSNumber *)${method.name}With[#if method.request??]CompletionBlock:(${projectPrefix}${method.request.name}*) body
[/#if]
[#compress]
    [#if method.templateParams??]
        [#list method.templateParams as param]
            param${param.name}: (NSString *) ${param.name}
        [/#list]
    [/#if]
    [#if method.requestParams??]
        [#list method.requestParams as param]
            param${param.name}: (NSString *) ${param.name}
        [/#list]
    [/#if]completionHandler: ([#if method.response??]void (^)(${projectPrefix}${method.response.name} *output,[/#if] [#if method.faultsMap?has_content]id specificErrorObject, [/#if] NSError *error))completionBlock;
[/#compress]
[/#list]

@end