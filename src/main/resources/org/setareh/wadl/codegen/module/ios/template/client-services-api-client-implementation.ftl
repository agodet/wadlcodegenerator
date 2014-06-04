[#ftl]
#import "${generatedPrefix}ApiClient.h"
#import "${generatedPrefix}File.h"

#import "AFJSONRequestOperation.h"

@implementation ${generatedPrefix}ApiClient

static long requestId = 0;
static bool offlineState = true;
static NSMutableSet * queuedRequests = nil;
static bool cacheEnabled = false;
static AFNetworkReachabilityStatus reachabilityStatus = AFNetworkReachabilityStatusNotReachable;
static NSOperationQueue* sharedQueue;
static void (^reachabilityChangeBlock)(int);
static bool loggingEnabled = false;

+(void)setLoggingEnabled:(bool) state {
    loggingEnabled = state;
}

+(void)clearCache {
    [[NSURLCache sharedURLCache] removeAllCachedResponses];
}

+(void)setCacheEnabled:(BOOL)enabled {
    cacheEnabled = enabled;
}

+(void)configureCacheWithMemoryAndDiskCapacity:(unsigned long) memorySize
diskSize:(unsigned long) diskSize {
    NSAssert(memorySize > 0, @"invalid in-memory cache size");
    NSAssert(diskSize >= 0, @"invalid disk cache size");

    NSURLCache *cache =
    [[NSURLCache alloc]
    initWithMemoryCapacity:memorySize
    diskCapacity:diskSize
    diskPath:@"swagger_url_cache"];

    [NSURLCache setSharedURLCache:cache];
}

+(NSOperationQueue*) sharedQueue {
    return sharedQueue;
}

+(${generatedPrefix}ApiClient *)sharedClientFromPool:(NSString *)baseUrl {
    static NSMutableDictionary *_pool = nil;
    if (queuedRequests == nil) {
        queuedRequests = [[NSMutableSet alloc]init];
    }
    if(_pool == nil) {
        // setup static vars
        // create queue
        sharedQueue = [[NSOperationQueue alloc] init];

        // create pool
        _pool = [[NSMutableDictionary alloc] init];

        // initialize URL cache
        [${generatedPrefix}ApiClient configureCacheWithMemoryAndDiskCapacity:4*1024*1024 diskSize:32*1024*1024];

        // configure reachability
        [${generatedPrefix}ApiClient configureCacheReachibilityForHost:baseUrl];
    }

    @synchronized(self) {
        ${generatedPrefix}ApiClient * client = [_pool objectForKey:baseUrl];
        if (client == nil) {
            client = [[${generatedPrefix}ApiClient alloc] initWithBaseURL:[NSURL URLWithString:baseUrl]];
            [client registerHTTPOperationClass:[AFJSONRequestOperation class]];
            client.parameterEncoding = AFJSONParameterEncoding;
            [_pool setValue:client forKey:baseUrl ];
            if(loggingEnabled) {
                NSLog(@"new client for path %@", baseUrl);
            }
        }
        if(loggingEnabled) {
            NSLog(@"returning client for path %@", baseUrl);
        }
        return client;
    }
}

-(void)setHeaderValue:(NSString*) value
forKey:(NSString*) forKey {
    [self setDefaultHeader:forKey value:value];
}

+(unsigned long)requestQueueSize {
    return [queuedRequests count];
}

+(NSNumber*) nextRequestId {
    long nextId = ++requestId;
    if(loggingEnabled) {
        NSLog(@"got id %ld", nextId);
    }
    return [NSNumber numberWithLong:nextId];
}

+(NSNumber*) queueRequest {
    NSNumber* requestId = [${generatedPrefix}ApiClient nextRequestId];
    if(loggingEnabled) {
        NSLog(@"added %@ to request queue", requestId);
    }
    [queuedRequests addObject:requestId];
    return requestId;
}

+(void) cancelRequest:(NSNumber*)requestId {
    [queuedRequests removeObject:requestId];
}

+(NSString*) escape:(id)unescaped {
    if([unescaped isKindOfClass:[NSString class]]){
        return (NSString *)CFBridgingRelease
        (CFURLCreateStringByAddingPercentEscapes(
        NULL,
        (__bridge CFStringRef) unescaped,
        NULL,
        (CFStringRef)@"!*'();:@&=+$,/?%#[]",
        kCFStringEncodingUTF8));
    }
    else {
        return [NSString stringWithFormat:@"%@", unescaped];
    }
}

-(Boolean) executeRequestWithId:(NSNumber*) requestId {
    NSSet* matchingItems = [queuedRequests objectsPassingTest:^BOOL(id obj, BOOL *stop) {
        if([obj intValue]  == [requestId intValue]) {
            return TRUE;
        }
        else {
            return FALSE;
        }
    }];

    if(matchingItems.count == 1) {
        if(loggingEnabled) {
            NSLog(@"removing request id %@", requestId);
        }
        [queuedRequests removeObject:requestId];
        return true;
    }
    else {
        return false;
    }
}

-(id)initWithBaseURL:(NSURL *)url {
    self = [super initWithBaseURL:url];
    if (!self) {
        return nil;
    }
    return self;
}

+(AFNetworkReachabilityStatus) getReachabilityStatus {
    return reachabilityStatus;
}

+(void) setReachabilityChangeBlock:(void(^)(int))changeBlock {
    reachabilityChangeBlock = changeBlock;
}

+(void) setOfflineState:(BOOL) state {
    offlineState = state;
}

+(void) configureCacheReachibilityForHost:(NSString*)host {
    [[${generatedPrefix}ApiClient sharedClientFromPool:host ] setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status) {
        reachabilityStatus = status;
        switch (status) {
            case AFNetworkReachabilityStatusUnknown:
                if(loggingEnabled) {
                    NSLog(@"reachability changed to AFNetworkReachabilityStatusUnknown");
                }
                [${generatedPrefix}ApiClient setOfflineState:true];
                break;

            case AFNetworkReachabilityStatusNotReachable:
                if(loggingEnabled)
                NSLog(@"reachability changed to AFNetworkReachabilityStatusNotReachable");
                [${generatedPrefix}ApiClient setOfflineState:true];
                break;

            case AFNetworkReachabilityStatusReachableViaWWAN:
                if(loggingEnabled)
                NSLog(@"reachability changed to AFNetworkReachabilityStatusReachableViaWWAN");
                [${generatedPrefix}ApiClient setOfflineState:false];
                break;

            case AFNetworkReachabilityStatusReachableViaWiFi:
                if(loggingEnabled)
                NSLog(@"reachability changed to AFNetworkReachabilityStatusReachableViaWiFi");
                [${generatedPrefix}ApiClient setOfflineState:false];
                break;
            default:
                break;
        }

        // call the reachability block, if configured
        if(reachabilityChangeBlock != nil) {
            reachabilityChangeBlock(status);
        }
    }];
}

-(NSString*) pathWithQueryParamsToString:(NSString*) path queryParams:(NSDictionary*) queryParams {
    NSString * separator = nil;
    int counter = 0;

    NSMutableString * requestUrl = [NSMutableString stringWithFormat:@"%@", path];
    if(queryParams != nil){
        for(NSString * key in [queryParams keyEnumerator]){
            if(counter == 0) separator = @"?";
            else separator = @"&";
            NSString * value;
            if([[queryParams valueForKey:key] isKindOfClass:[NSString class]]){
                value = [${generatedPrefix}ApiClient escape:[queryParams valueForKey:key]];
            }
            else {
                value = [NSString stringWithFormat:@"%@", [queryParams valueForKey:key]];
            }

            [requestUrl appendString:[NSString stringWithFormat:@"%@%@=%@", separator,
            [${generatedPrefix}ApiClient escape:key], value]];
            counter += 1;
        }
    }
    return requestUrl;
}

- (NSString*)descriptionForRequest:(NSURLRequest*)request {
    return [[request URL] absoluteString];
}

- (void)logRequest:(NSURLRequest*)request {
    NSLog(@"request: %@", [self descriptionForRequest:request]);
}

- (void)logResponse:(id)data forRequest:(NSURLRequest*)request error:(NSError*)error {
    NSLog(@"request: %@  response: %@ ",  [self descriptionForRequest:request], data );
}


-(NSNumber*)  dictionary:(NSString*) path
method:(NSString*) method
queryParams:(NSDictionary*) queryParams
body:(id) body
headerParams:(NSDictionary*) headerParams
requestContentType:(NSString*) requestContentType
responseContentType:(NSString*) responseContentType
completionBlock:(void (^)(NSDictionary*, NSError *))completionBlock {

    NSMutableURLRequest * request = nil;

    if ([body isKindOfClass:[${generatedPrefix}File class]]){
        GeneratedFile * file = (${generatedPrefix}File*) body;

        request = [self multipartFormRequestWithMethod:@"POST"
        path:path
        parameters:nil
        constructingBodyWithBlock: ^(id <AFMultipartFormData> formData) {
            [formData appendPartWithFileData:[file data]
            name:@"image"
            fileName:[file name]
            mimeType:[file mimeType]];
        }];
    }
    else {
        request = [self requestWithMethod:method
        path:[self pathWithQueryParamsToString:path queryParams:queryParams]
        parameters:body];
    }

    BOOL hasHeaderParams = false;
    if(headerParams != nil && [headerParams count] > 0)
        hasHeaderParams = true;

    if(offlineState) {
        NSLog(@"%@ cache forced", path);
        [request setCachePolicy:NSURLCacheStorageAllowedInMemoryOnly];
    }
    else if(!hasHeaderParams && [method isEqualToString:@"GET"] && cacheEnabled) {
        NSLog(@"%@ cache enabled", path);
        [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    }
    else {
        NSLog(@"%@ cache disabled", path);
        [request setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    }

    if(body != nil) {
        if([body isKindOfClass:[NSDictionary class]]){
            [request setValue:requestContentType forHTTPHeaderField:@"Content-Type"];
        }
        else if ([body isKindOfClass:[${generatedPrefix}File class]]) {}
        else {
            NSAssert(false, @"unsupported post type!");
        }
    }
    if(headerParams != nil){
        for(NSString * key in [headerParams keyEnumerator]){
            [request setValue:[headerParams valueForKey:key] forHTTPHeaderField:key];
        }
    }
    [request setValue:[headerParams valueForKey:responseContentType] forHTTPHeaderField:@"Accept"];

    // Always disable cookies!
    [request setHTTPShouldHandleCookies:NO];


    if (self.logRequests) {
        [self logRequest:request];
    }

    NSNumber* requestId = [${generatedPrefix}ApiClient queueRequest];
    AFJSONRequestOperation *op =
    [self
    JSONRequestOperationWithRequest:request
    success:^(NSURLRequest *request, NSHTTPURLResponse *response, id JSON) {
        if([self executeRequestWithId:requestId]) {
            if(self.logServerResponses)
                [self logResponse:JSON forRequest:request error:nil];
            completionBlock(JSON, nil);
        }
    } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error, id data) {
        if([self executeRequestWithId:requestId]) {
            if(self.logServerResponses)
                [self logResponse:nil forRequest:request error:error];
            completionBlock(nil, error);
        }
    }
    ];

    [self enqueueHTTPRequestOperation:op];
    return requestId;
}

-(NSNumber*)  stringWithCompletionBlock:(NSString*) path
method:(NSString*) method
queryParams:(NSDictionary*) queryParams
body:(id) body
headerParams:(NSDictionary*) headerParams
requestContentType:(NSString*) requestContentType
responseContentType:(NSString*) responseContentType
completionBlock:(void (^)(NSString*, NSError *))completionBlock {

    AFHTTPClient *client = self;
    client.parameterEncoding = AFJSONParameterEncoding;

    NSMutableURLRequest * request = nil;

    if ([body isKindOfClass:[${generatedPrefix}File class]]){
        GeneratedFile * file = (${generatedPrefix}File*) body;

        request = [self multipartFormRequestWithMethod:@"POST"
        path:path
        parameters:nil
        constructingBodyWithBlock: ^(id <AFMultipartFormData> formData) {
            [formData appendPartWithFileData:[file data]
            name:@"image"
            fileName:[file name]
            mimeType:[file mimeType]];
        }];
    }
    else {
        request = [self requestWithMethod:method
        path:[self pathWithQueryParamsToString:path queryParams:queryParams]
        parameters:body];
    }

    BOOL hasHeaderParams = false;
    if(headerParams != nil && [headerParams count] > 0)
        hasHeaderParams = true;
    if(offlineState) {
        NSLog(@"%@ cache forced", path);
        [request setCachePolicy:NSURLRequestReturnCacheDataDontLoad];
    }
    else if(!hasHeaderParams && [method isEqualToString:@"GET"] && cacheEnabled) {
        NSLog(@"%@ cache enabled", path);
        [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    }
    else {
        NSLog(@"%@ cache disabled", path);
        [request setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    }

    if(body != nil) {
        if([body isKindOfClass:[NSDictionary class]]){
            [request setValue:requestContentType forHTTPHeaderField:@"Content-Type"];
        }
        else if ([body isKindOfClass:[${generatedPrefix}File class]]){}
        else {
            NSAssert(false, @"unsupported post type!");
        }
    }
    if(headerParams != nil){
        for(NSString * key in [headerParams keyEnumerator]){
            [request setValue:[headerParams valueForKey:key] forHTTPHeaderField:key];
        }
    }

    [request setValue:[headerParams valueForKey:responseContentType] forHTTPHeaderField:@"Accept"];

    // Always disable cookies!
    [request setHTTPShouldHandleCookies:NO];

    NSNumber* requestId = [${generatedPrefix}ApiClient queueRequest];
    AFHTTPRequestOperation *op = [[AFHTTPRequestOperation alloc] initWithRequest:request];
    [op setCompletionBlockWithSuccess:
    ^(AFHTTPRequestOperation *resp,
    id responseObject) {
        NSString *response = [resp responseString];
        if([self executeRequestWithId:requestId]) {
            if(self.logServerResponses)
                [self logResponse:responseObject forRequest:request error:nil];
        completionBlock(response, nil);
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        if([self executeRequestWithId:requestId]) {
            if(self.logServerResponses)
                [self logResponse:nil forRequest:request error:error];
            completionBlock(nil, error);
        }
    }];

    [self enqueueHTTPRequestOperation:op];
    return requestId;
}

#pragma mark - AFRequestOperation

- (AFJSONRequestOperation *)JSONRequestOperationWithRequest:(NSURLRequest *)urlRequest
success:(void (^)(NSURLRequest *request, NSHTTPURLResponse *response, id JSON))success
failure:(void (^)(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error, id JSON))failure
{
    AFJSONRequestOperation *requestOperation = [[AFJSONRequestOperation alloc] initWithRequest:urlRequest];
    NSURLCredential *credential = [NSURLCredential credentialWithUser:self.authenticationLogin password:self.authenticationPassword persistence:NSURLCredentialPersistenceForSession];
    [requestOperation setCredential:credential];

    [requestOperation setCompletionBlockWithSuccess:^(AFHTTPRequestOperation *operation, id responseObject) {
        if (success) {
            success(operation.request, operation.response, responseObject);
        }
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        if (failure) {
            failure(operation.request, operation.response, error, [(AFJSONRequestOperation *)operation responseJSON]);
        }
    }];
    return requestOperation;
}

- (void)setAuthenticationLogin:(NSString *)login andPassword:(NSString *)password {
    self.authenticationLogin = login;
    self.authenticationPassword = password;
}

@end
