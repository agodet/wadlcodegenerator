[#ftl]
#import <Foundation/Foundation.h>
@import AFNetworking;
#import <CocoaLumberjack/CocoaLumberjack.h>

@interface ${projectPrefix}ApiClient : AFHTTPRequestOperationManager

@property(nonatomic, assign) NSURLRequestCachePolicy cachePolicy;
@property(nonatomic, assign) NSTimeInterval timeoutInterval;
@property(nonatomic, assign) BOOL logRequests;
@property(nonatomic, assign) BOOL logCacheHits;
@property(nonatomic, assign) BOOL logServerResponses;
@property(nonatomic, assign) BOOL logJSON;
@property(nonatomic, assign) BOOL logHTTP;
@property(nonatomic, readonly) NSOperationQueue* queue;
@property(nonatomic, copy) NSString * authenticationLogin;
@property(nonatomic, copy) NSString * authenticationPassword;
@property (nonatomic, copy) NSString * (^authorizationBlock)(NSURL *url, NSString * method, NSData *body);

+ (${projectPrefix}ApiClient *)sharedClientFromPool:(NSString *)baseUrl;
+ (${projectPrefix}ApiClient *)sharedClientFromPool:(NSString *)baseUrl withGroup:(NSString *)group;
+ (NSOperationQueue *)sharedQueue;
+ (void)setLoggingEnabled:(BOOL)loggingEnabled;
+ (void)clearCache;
+ (void)setCacheEnabled:(BOOL)enabled;
+ (unsigned long)requestQueueSize;
+ (void)setOfflineState:(BOOL)state;
+ (AFNetworkReachabilityStatus)getReachabilityStatus;
+ (NSNumber *)nextRequestId;
+ (NSNumber *)queueRequest;
+ (void)cancelRequest:(NSNumber*)requestId;
+ (NSString *)escape:(id)unescaped;
+ (void)setReachabilityChangeBlock:(void(^)(int))changeBlock;
+ (void)configureCacheReachibilityForHost:(NSString *)host withGroup:(NSString *)group;

- (NSNumber *)dictionary:(NSString *)path
method:(NSString *)method
queryParams:(NSDictionary *)queryParams
body:(id)body
headerParams:(NSDictionary *)headerParams
requestContentType:(NSString *)requestContentType
responseContentType:(NSString *)responseContentType
completionBlock:(void (^)(NSInteger, NSDictionary *, NSError *))completionBlock;

- (void)setAuthenticationLogin:(NSString *)login andPassword:(NSString *)password;
- (void)setAuthorizationWithBlock:(NSString *(^)(NSURL *, NSString *method, NSData *body))authorizationBlock;

@end


