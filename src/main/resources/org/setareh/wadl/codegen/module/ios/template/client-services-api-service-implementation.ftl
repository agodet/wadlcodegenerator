[#ftl]
#import "${className}Api.h"
#import "${generatedPrefix}File.h"
#import "${generatedPrefix}ApiClient.h"
[#list imports as import]
#import "${import}"
[/#list]


@implementation ${className}Api
static NSString * basePath = @"";

+(${className}Api*) sharedApiWithBasePath:(NSString *) basePath {
    static ${className}Api* singletonAPI = nil;

    if (singletonAPI == nil) {
        singletonAPI = [[${className}Api alloc] init];
        [singletonAPI setBasePath:basePath];
    }
    return singletonAPI;
}

-(void) setBasePath:(NSString*)path {
    basePath = path;
}

-(${generatedPrefix}ApiClient*) apiClient {
    return [${generatedPrefix}ApiClient sharedClientFromPool:basePath];
}

-(void) addHeader:(NSString*)value forKey:(NSString*)key {
    [[self apiClient] setHeaderValue:value forKey:key];
}

-(id) init {
    self = [super init];
    [self apiClient];
    return self;
}

-(void) setHeaderValue:(NSString*) value forKey:(NSString*)key {
    [[self apiClient] setHeaderValue:value forKey:key];
}

-(unsigned long) requestQueueSize {
    return [${generatedPrefix}ApiClient requestQueueSize];
}

-(void) setAuthenticationLogin:(NSString*) login andPassword:(NSString*)password {
    [[self apiClient] setAuthenticationLogin:login andPassword:password];
}

[#list methods as method]
-(NSNumber*) ${method.name}With[#if method.request??]CompletionBlock:(${projectPrefix}${method.request.name}*) body
[/#if][#if method.requestParams??][#list method.requestParams as param]param${param.name}: (NSString *) ${param.name} [/#list][/#if]completionHandler: (void (^)(${projectPrefix}${method.response.name}* output, NSError* error))completionBlock{

    NSMutableString* requestUrl = [NSMutableString stringWithFormat:@"%@${method.path}", basePath];

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
    bodyDictionary = [[NSMutableDictionary alloc] init];
    [#if method.requestParams??]
    [#list method.requestParams as param]
    if(${param.name}) bodyDictionary${"[@"}"${param.name}"] = ${param.name};
    [/#list]
    [/#if]
    [/#if]
    [#if method.request??]
    if(body != nil && [body isKindOfClass:[NSArray class]]){
        NSMutableArray * objs = [[NSMutableArray alloc] init];
        for (id dict in (NSArray*)body) {
            if([dict respondsToSelector:@selector(asDictionary)]) {
                [objs addObject:[(${generatedPrefix}Object*)dict asDictionary]];
            }
            else{
                [objs addObject:dict];
            }
        }
        bodyDictionary = objs;
    }
    else if([body respondsToSelector:@selector(asDictionary)]) {
        bodyDictionary = [(${generatedPrefix}Object*)body asDictionary];
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
    else if([body isKindOfClass: [${generatedPrefix}File class]]) {
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
    ${generatedPrefix}ApiClient* client = [${generatedPrefix}ApiClient sharedClientFromPool:basePath];

    return [client dictionary:requestUrl
    method:@"${method.type}"
    queryParams:queryParams
    body:bodyDictionary
    headerParams:headerParams
    requestContentType:requestContentType
    responseContentType:responseContentType
    completionBlock:^(NSDictionary *data, NSError *error) {

    if (error) {
        completionBlock(nil, error);
        return;
    }

    ${projectPrefix}${method.response.name} *result = nil;

    if (data) {
        result = [[${projectPrefix}${method.response.name} alloc]initWithDictionnary: data];
    }

    completionBlock(result , nil);
    }
    ];
}
[/#list]

@end