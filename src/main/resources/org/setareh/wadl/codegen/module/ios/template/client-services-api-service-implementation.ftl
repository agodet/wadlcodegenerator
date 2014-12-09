[#ftl]
#import "${className}Api.h"
#import "${projectPrefix}File.h"
#import "${projectPrefix}ApiClient.h"
[#list imports as import]
#import "${import}"
[/#list]

@interface ${className}Api ()

@property(nonatomic, copy) NSString * basePath;
@property(nonatomic, copy) NSString * group;

@end

@implementation ${className}Api

+ (${className}Api*) sharedApiWithBasePath:(NSString *) basePath {
    return [${className}Api sharedApiWithBasePath:basePath withGroup:nil];
}

+ (${className}Api*) sharedApiWithBasePath:(NSString *) basePath  withGroup:(NSString *)group {
    static ${className}Api* singletonAPI = nil;

    if (singletonAPI == nil) {
        singletonAPI = [[${className}Api alloc] initWithBasePath:basePath group:group];
    }
    return singletonAPI;
}

-(${projectPrefix}ApiClient*) apiClient {
    return [${projectPrefix}ApiClient sharedClientFromPool:self.basePath withGroup:self.group];
}

-(id) initWithBasePath:basePath group:group {
    self = [super init];

    self.basePath = basePath;
    self.group = group;

    [self apiClient];

    return self;
}

-(unsigned long) requestQueueSize {
    return [${projectPrefix}ApiClient requestQueueSize];
}

-(void) setAuthenticationLogin:(NSString*) login andPassword:(NSString*)password {
    [[self apiClient] setAuthenticationLogin:login andPassword:password];
}

- (void)setAuthorizationWithBlock:(NSString *(^)(NSURL *, NSString * method, NSData *body))authorizationBlock {
    [[self apiClient] setAuthorizationWithBlock:authorizationBlock];
}

[#list methods as method]
-(NSNumber*) ${method.name}With[#if method.request??]CompletionBlock:(${projectPrefix}${method.request.name}*) body
[/#if][#if method.requestParams??][#list method.requestParams as param]param${param.name}: (NSString *) ${param.name} [/#list][/#if]completionHandler: (void (^)(${projectPrefix}${method.response.name} *output, [#if method.faultsMap?has_content]id specificErrorObject, [/#if]NSError *error))completionBlock{

    NSMutableString *requestUrl = [NSMutableString stringWithFormat:@"%@${method.path}", self.basePath];

    // remove format in URL if needed
    if ([requestUrl rangeOfString:@".{format}"].location != NSNotFound) {
        [requestUrl replaceCharactersInRange: [requestUrl rangeOfString:@".{format}"] withString:@".json"];
    }

    [#if method.request??]
    NSString* requestContentType = @"application/json";
    [#else]
    NSString* requestContentType = nil;
    [/#if]

    NSString* responseContentType = @"application/json";

    NSMutableDictionary* queryParams = [[NSMutableDictionary alloc] init];
    NSMutableDictionary* headerParams = [[NSMutableDictionary alloc] init];
    id bodyDictionary = nil;

    [#if method.type = "GET"]
    queryParams = [[NSMutableDictionary alloc] init];
    [#if method.requestParams??]
    [#list method.requestParams as param]
    if(${param.name}) queryParams${"[@"}"${param.name}"] = ${param.name};
    [/#list]
    [/#if]
    [/#if]
    [#if method.request??]
    if(body != nil && [body isKindOfClass:[NSArray class]]){
        NSMutableArray * objs = [[NSMutableArray alloc] init];
        for (id dict in (NSArray*)body) {
            if([dict respondsToSelector:@selector(asDictionary)]) {
                [objs addObject:[(${projectPrefix}Object*)dict asDictionary]];
            }
            else{
                [objs addObject:dict];
            }
        }
        bodyDictionary = objs;
    }
    else if([body respondsToSelector:@selector(asDictionary)]) {
        bodyDictionary = [(${projectPrefix}Object*)body asDictionary];
    }
    else if([body isKindOfClass:[NSString class]]) {
        // convert it to a dictionary
        NSError * error;
        NSString * str = (NSString*)body;
        NSDictionary *JSON =
        [NSJSONSerialization JSONObjectWithData:[str dataUsingEncoding:NSUTF8StringEncoding]
        options:NSJSONReadingMutableContainers
        error:&error];
        bodyDictionary = JSON;
    }
    else if([body isKindOfClass: [${projectPrefix}File class]]) {
        requestContentType = @"form-data";
        bodyDictionary = body;
    }
    else{
        NSLog(@"don't know what to do with %@", body);
    }

    if(body == nil) {
        // error
    }
    [/#if]
    ${projectPrefix}ApiClient* client = [${projectPrefix}ApiClient sharedClientFromPool:self.basePath withGroup: self.group];

    return [client dictionary:requestUrl
                       method:@"${method.type}"
                  queryParams:queryParams
                         body:bodyDictionary
                 headerParams:headerParams
           requestContentType:requestContentType
          responseContentType:responseContentType
              completionBlock:^(NSInteger responseStatusCode, NSDictionary *data, NSError *error) {

        ${projectPrefix}${method.response.name} *result = nil;
        [#if method.faultsMap?has_content]
        id specificErrorObject = nil;
        [/#if]
        if (data) {
            [#if method.faultsMap?has_content]
            switch (responseStatusCode) {
                [#list method.faultsMap?keys as key]
                case ${key}:
                    specificErrorObject = [[${projectPrefix}${method.faultsMap[key].name} alloc] initWithDictionnary:data];
                    break;
                [/#list]
                default:{
            [/#if]
                    if(!error){
                        [#if method.response.name == "InputStream"]
                        // FIXME Caution : won't work. Waiting for fix.
                        result = (${projectPrefix}InputStream*) data;
                        [#else]
                        result = [[${projectPrefix}${method.response.name} alloc] initWithDictionnary:data];
                        [/#if]
                    }
            [#if method.faultsMap?has_content]
                }
                break;
            }
            [/#if]
        }
        completionBlock(result, [#if method.faultsMap?has_content]specificErrorObject, [/#if]error);
    }];
}
[/#list]

@end