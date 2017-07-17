[#ftl]
#import "${projectPrefix}ApiClient.h"
#import "${projectPrefix}File.h"

@interface ${projectPrefix}ApiClient ()

- (NSString *)descriptionForRequest:(NSURLRequest *)request;
- (void)logRequest:(NSURLRequest *)request;
- (void)logResponse:(id)data forRequest:(NSURLRequest *)request error:(NSError *)error;

@end

@implementation ${projectPrefix}ApiClient

static long requestId = 0;
static BOOL offlineState = NO;
static BOOL cacheEnabled = NO;
static BOOL loggingEnabled = NO;
static NSMutableSet *queuedRequests = nil;
static AFNetworkReachabilityStatus reachabilityStatus = AFNetworkReachabilityStatusNotReachable;
static NSOperationQueue *sharedQueue;
static void (^reachabilityChangeBlock)(int);


+ (void)setLoggingEnabled:(BOOL)loggingEnabled {
    loggingEnabled = loggingEnabled;
}

+ (void)clearCache {
    [[NSURLCache sharedURLCache] removeAllCachedResponses];
}

+ (void)setCacheEnabled:(BOOL)enabled {
cacheEnabled = enabled;
}

+ (void)configureCacheWithMemoryAndDiskCapacity:(unsigned long) memorySize
                                       diskSize:(unsigned long) diskSize {
    NSAssert(memorySize > 0, @"invalid in-memory cache size");
    NSAssert(diskSize >= 0, @"invalid disk cache size");

    NSURLCache *cache = [[NSURLCache alloc] initWithMemoryCapacity:memorySize diskCapacity:diskSize diskPath:@"codegen_url_cache"];
    [NSURLCache setSharedURLCache:cache];
}

+ (NSOperationQueue *)sharedQueue {
    return sharedQueue;
}


+ (unsigned long)requestQueueSize {
    return [queuedRequests count];
}

+ (NSNumber *)nextRequestId {
    long nextId = ++requestId;
    if(loggingEnabled) {
        DDLogDebug(@"got id %ld", nextId);
    }
    return [NSNumber numberWithLong:nextId];
}

+ (NSNumber *)queueRequest {
    NSNumber* requestId = [${projectPrefix}ApiClient nextRequestId];
    if(loggingEnabled) {
        DDLogDebug(@"added %@ to request queue", requestId);
    }
    [queuedRequests addObject:requestId];
    return requestId;
}

+ (void)cancelRequest:(NSNumber*)requestId {
    [queuedRequests removeObject:requestId];
}

+ (NSString *)escape:(id)unescaped {
    if([unescaped isKindOfClass:[NSString class]]){
        return (NSString *)CFBridgingRelease(CFURLCreateStringByAddingPercentEscapes(
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

#pragma mark - Reachability / Offline mode

+(AFNetworkReachabilityStatus) getReachabilityStatus {
    return reachabilityStatus;
}

+ (void)setReachabilityChangeBlock:(void(^)(int))changeBlock {
    reachabilityChangeBlock = changeBlock;
}

+ (void)setOfflineState:(BOOL) state {
    offlineState = state;
}

+ (void)configureCacheReachibilityForHost:(NSString *)host withGroup:(NSString *)group {
    [[AFNetworkReachabilityManager sharedManager] setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status) {
        reachabilityStatus = status;
        switch (status) {
            case AFNetworkReachabilityStatusUnknown:
                if(loggingEnabled) {
                    DDLogDebug(@"reachability changed to AFNetworkReachabilityStatusUnknown");
                }
                [${projectPrefix}ApiClient setOfflineState:true];
                break;

            case AFNetworkReachabilityStatusNotReachable:
                if(loggingEnabled){
                    DDLogDebug(@"reachability changed to AFNetworkReachabilityStatusNotReachable");
                }
                [${projectPrefix}ApiClient setOfflineState:true];
                break;

            case AFNetworkReachabilityStatusReachableViaWWAN:
                if(loggingEnabled){
                    DDLogDebug(@"reachability changed to AFNetworkReachabilityStatusReachableViaWWAN");
                }
                [${projectPrefix}ApiClient setOfflineState:false];
                break;

            case AFNetworkReachabilityStatusReachableViaWiFi:
                if(loggingEnabled){
                    DDLogDebug(@"reachability changed to AFNetworkReachabilityStatusReachableViaWiFi");
                }
                [${projectPrefix}ApiClient setOfflineState:false];
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

#pragma mark - Constructors

+ (${projectPrefix}ApiClient *)sharedClientFromPool:(NSString *)baseUrl {
    return [${projectPrefix}ApiClient sharedClientFromPool:baseUrl withGroup:nil];
}

+ (${projectPrefix}ApiClient *)sharedClientFromPool:(NSString *)baseUrl withGroup:(NSString *)group {

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
        [${projectPrefix}ApiClient configureCacheWithMemoryAndDiskCapacity:4*1024*1024 diskSize:32*1024*1024];

        // configure reachability
        [${projectPrefix}ApiClient configureCacheReachibilityForHost:baseUrl withGroup: group];
    }

    @synchronized(self) {
        NSString *shareKey = group ? [NSString stringWithFormat: @"%@_%@", group, baseUrl] : baseUrl;
        ${projectPrefix}ApiClient *client = [_pool objectForKey:shareKey];
        if (client == nil) {
            client = [[${projectPrefix}ApiClient alloc] initWithBaseURL:[NSURL URLWithString:baseUrl]];
            [_pool setValue:client forKey:shareKey ];
            if(loggingEnabled) {
                DDLogDebug(@"[API POOL] New client for path %@", shareKey);
            }
        }
        if(loggingEnabled) {
            DDLogDebug(@"[API POOL] Returning client for path %@", shareKey);
        }
        return client;
    }
}

-(id)initWithBaseURL:(NSURL *)url {
    self = [super initWithBaseURL:url];
    if (!self) {
        return nil;
    }
    return self;
}


#pragma mark - Authorization

- (void)setAuthenticationLogin:(NSString *)login andPassword:(NSString *)password {
    self.authenticationLogin = login;
    self.authenticationPassword = password;
}

- (void)setAuthorizationWithBlock:(NSString *(^)(NSURL *, NSString * method, NSData *body))authorizationBlock {
    self.authorizationBlock = authorizationBlock;
}

#pragma mark - Utils

- (BOOL)executeRequestWithId:(NSNumber*) requestId {
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
            DDLogDebug(@"removing request id %@", requestId);
        }
        [queuedRequests removeObject:requestId];
        return true;
    }
    else {
        return false;
    }
}

- (NSString *)pathWithQueryParamsToString:(NSString *)path queryParams:(NSDictionary *)queryParams {
    NSString * separator = nil;
    int counter = 0;

    NSMutableString * requestUrl = [NSMutableString stringWithFormat:@"%@", path];
    if(queryParams != nil){
        for(NSString * key in [queryParams keyEnumerator]){
            if(counter == 0) separator = @"?";
            else separator = @"&";
            NSString * value;
            if([[queryParams valueForKey:key] isKindOfClass:[NSString class]]){
                value = [${projectPrefix}ApiClient escape:[queryParams valueForKey:key]];
            }
            else {
                value = [NSString stringWithFormat:@"%@", [queryParams valueForKey:key]];
            }
            [requestUrl appendString:[NSString stringWithFormat:@"%@%@=%@", separator, [${projectPrefix}ApiClient escape:key], value]];
            counter += 1;
        }
    }
    return requestUrl;
}

#pragma mark - Call Service

- (NSNumber *)dictionary:(NSString*)path
                  method:(NSString*) method
             queryParams:(NSDictionary*) queryParams
                    body:(id) body
            headerParams:(NSDictionary*) headerParams
      requestContentType:(NSString*) requestContentType
     responseContentType:(NSString*) responseContentType
         completionBlock:(void (^)(NSInteger, NSDictionary *, NSError *))completionBlock {

    @synchronized(self){

    NSString *completePath = [self pathWithQueryParamsToString:path queryParams:queryParams];

    //HEADERS
    self.requestSerializer = [self requestSerializerForUrlPath:completePath
                                                        method:method
                                            requestContentType:requestContentType
                                                   requestBody:body
                                        additionalHeaderParams:headerParams
                                           responseContentType:responseContentType];
    [self.requestSerializer setValue:@"gzip" forHTTPHeaderField:@"Accept-Encoding"];

    //CACHE POLICY
    BOOL hasHeaderParams = NO;
    if(headerParams != nil && [headerParams count] > 0){
        hasHeaderParams = YES;
    }

    if(offlineState) {
        DDLogDebug(@"%@ cache forced", completePath);
        [self.requestSerializer setCachePolicy:NSURLRequestReturnCacheDataDontLoad];
    }
    else if(!hasHeaderParams && [method isEqualToString:@"GET"] && cacheEnabled) {
        DDLogDebug(@"%@ cache enabled", completePath);
        [self.requestSerializer setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    }
    else {
        DDLogDebug(@"%@ cache disabled", completePath);
        [self.requestSerializer setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    }

    // Always disable cookies!
    [self.requestSerializer setHTTPShouldHandleCookies:NO];

    NSNumber* requestId = [${projectPrefix}ApiClient queueRequest];

    if ([body isKindOfClass:[${projectPrefix}File class]]){
        ${projectPrefix}File *file = (${projectPrefix}File *)body;

        [self POST:completePath
        parameters:nil
        constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
            [formData appendPartWithFileData:[file data]
            name:@"image"
            fileName:[file name]
            mimeType:[file mimeType]];
        }
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
            if (self.logRequests) {
                [self logRequest:operation.request];
            }

            if([self executeRequestWithId:requestId]) {
                if(self.logServerResponses){
                    [self logResponse:responseObject forRequest:operation.request error:nil];
                }
                completionBlock(operation.response.statusCode, responseObject, nil);
            }
        }
        failure:^(AFHTTPRequestOperation *operation, NSError *error) {

            if (self.logRequests) {
                [self logRequest:operation.request];
            }

            if([self executeRequestWithId:requestId]) {
                if(self.logServerResponses){
                    [self logResponse:nil forRequest:operation.request error:error];
                }
                completionBlock(operation.response.statusCode, operation.responseObject, error);
            }
        }];
    }
    else {
        [self simpleRequestWithPath:completePath
                             method:method
                               body:body
             successCompletionBlock:^(NSURLRequest *request, NSHTTPURLResponse *response, id JSON) {
                if (self.logRequests) {
                    [self logRequest:request];
                }
                if([self executeRequestWithId:requestId]) {
                    if(self.logServerResponses){
                    [self logResponse:JSON forRequest:request error:nil];
                    }
                    completionBlock(response.statusCode, JSON, nil);
                }
             }
             failureCompletionBlock:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error, id errorObject) {
                if (self.logRequests) {
                    [self logRequest:request];
                }
                if([self executeRequestWithId:requestId]) {
                    if(self.logServerResponses){
                        [self logResponse:nil forRequest:request error:error];
                    }
                    completionBlock(response.statusCode, errorObject, error);
                }
             }];
    }
    return requestId;

    }
}


-(void)simpleRequestWithPath:(NSString *)path
                      method:(NSString *)method
                        body:(id)body
      successCompletionBlock:(void (^)(NSURLRequest *request, NSHTTPURLResponse *response, id responseObject))successCompletionBlock
      failureCompletionBlock:(void (^)(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error, id errorObject))failureCompletionBlock
{
    if([ @"GET" isEqualToString:method]){
        [self GET:path
       parameters:body
          success:^(AFHTTPRequestOperation *operation, id responseObject) {
            successCompletionBlock(operation.request, operation.response, responseObject);
          }
          failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            failureCompletionBlock(operation.request, operation.response, error, operation.responseObject);
          }];
    }
    else if([ @"HEAD" isEqualToString:method]){
        [self HEAD:path
        parameters:body
           success:^(AFHTTPRequestOperation *operation) {
            successCompletionBlock(operation.request, operation.response, nil);
           }
           failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            failureCompletionBlock(operation.request, operation.response, error, operation.responseObject);
           }];
    }
    else if([ @"POST" isEqualToString:method]){
        [self POST:path
        parameters:body
           success:^(AFHTTPRequestOperation *operation, id responseObject) {
                successCompletionBlock(operation.request, operation.response, responseObject);
           }
           failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                failureCompletionBlock(operation.request, operation.response, error, operation.responseObject);
        }];
    }
    else if([ @"PUT" isEqualToString:method]){
        [self PUT:path
       parameters:body
          success:^(AFHTTPRequestOperation *operation, id responseObject) {
                successCompletionBlock(operation.request, operation.response, responseObject);
          }
          failure:^(AFHTTPRequestOperation *operation, NSError *error) {
                failureCompletionBlock(operation.request, operation.response, error, operation.responseObject);
          }];
    }
    else if([ @"PATCH" isEqualToString:method]){
        [self PATCH:path
        parameters:body
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
        successCompletionBlock(operation.request, operation.response, responseObject);
        }
        failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        failureCompletionBlock(operation.request, operation.response, error, operation.responseObject);
        }];
    }
    else if([ @"DELETE" isEqualToString:method]){
        [self DELETE:path
        parameters:body
        success:^(AFHTTPRequestOperation *operation, id responseObject) {
        successCompletionBlock(operation.request, operation.response, responseObject);
        }
        failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        failureCompletionBlock(operation.request, operation.response, error, operation.responseObject);
        }];
    }
    else {
        DDLogDebug(@"HTTP Method not recognized : %@", method);
        failureCompletionBlock(nil, nil, nil, nil);
    }
}

#pragma mark - Headers

- (AFJSONRequestSerializer *)requestSerializerForUrlPath:(NSString *)urlPath
                                                  method:(NSString *)method
                                      requestContentType:(NSString *)requestContentType
                                             requestBody:(id)body
                                  additionalHeaderParams:(NSDictionary *)additionalHeaderParams
                                     responseContentType:(NSString *)responseContentType
{

    AFJSONRequestSerializer *reqSerializer = [AFJSONRequestSerializer serializer];

    //REQUEST BODY CONTENT-TYPE
    if(body != nil) {
        if([body isKindOfClass:[NSDictionary class]]){
            [reqSerializer setValue:requestContentType forHTTPHeaderField:@"Content-Type"];
        }
        else if ([body isKindOfClass:[${projectPrefix}File class]]) {
            [reqSerializer setValue:((${projectPrefix}File *)body).mimeType forHTTPHeaderField:@"Content-Type"];
        }
        else {
            NSAssert(false, @"unsupported post type!");
            return nil;
        }
    }

    //ADDITIONAL PARAMS
    if(additionalHeaderParams != nil){
        for(NSString * key in [additionalHeaderParams keyEnumerator]){
            [reqSerializer setValue:[additionalHeaderParams valueForKey:key] forHTTPHeaderField:key];
        }
    }

    //RESPONSE CONTENT TYPE
    [reqSerializer setValue:[additionalHeaderParams valueForKey:responseContentType] forHTTPHeaderField:@"Accept"];

    /*************************** AUTHORIZATION **************************/

    //Basic Authorization :  UserName/Password
    if(self.authenticationLogin && self.authenticationPassword){
        [reqSerializer setAuthorizationHeaderFieldWithUsername:self.authenticationLogin password:self.authenticationPassword];
    }
    //Authorization Block : priority > Basic Authorization
    if (self.authorizationBlock) {
        NSURLRequest *request = [reqSerializer requestWithMethod:method URLString:urlPath parameters:body error:nil];
        [reqSerializer setValue:self.authorizationBlock(request.URL, request.HTTPMethod, request.HTTPBody) forHTTPHeaderField:@"Authorization"];
    }

    /*************************** AUTHORIZATION **************************/

    return reqSerializer;
}

#pragma mark - Logs

- (NSString *)descriptionForRequest:(NSURLRequest *)request {
    return [[request URL] absoluteString];
}

- (void)logRequest:(NSURLRequest *)request {
    DDLogDebug(@"Request: %@", [self descriptionForRequest:request]);
}

- (void)logResponse:(id)data forRequest:(NSURLRequest *)request error:(NSError *)error {
    DDLogDebug(@"Request: %@  Response: %@ ",  [self descriptionForRequest:request], data );
}

@end
