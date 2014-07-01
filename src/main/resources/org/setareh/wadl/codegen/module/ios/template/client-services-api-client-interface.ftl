[#ftl]
#import <Foundation/Foundation.h>
#import "AFHTTPClient.h"



@interface ${generatedPrefix}ApiClient : AFHTTPClient

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

+(${generatedPrefix}ApiClient *)sharedClientFromPool:(NSString *)baseUrl;

+ (${generatedPrefix}ApiClient *)sharedClientFromPool:(NSString *)baseUrl withGroup:(NSString *)group;

+(NSOperationQueue*) sharedQueue;

+(void)setLoggingEnabled:(bool) state;

+(void)clearCache;

+(void)setCacheEnabled:(BOOL) enabled;

+(unsigned long)requestQueueSize;

+(void) setOfflineState:(BOOL) state;

+(AFNetworkReachabilityStatus) getReachabilityStatus;

+(NSNumber*) nextRequestId;

+(NSNumber*) queueRequest;

+(void) cancelRequest:(NSNumber*)requestId;

+(NSString*) escape:(id)unescaped;

+(void) setReachabilityChangeBlock:(void(^)(int))changeBlock;

+(void) configureCacheReachibilityForHost:(NSString*)host withGroup:(NSString *)group;

-(void)setHeaderValue:(NSString*) value
forKey:(NSString*) forKey;

-(NSNumber*)  dictionary:(NSString*) path
method:(NSString*) method
queryParams:(NSDictionary*) queryParams
body:(id) body
headerParams:(NSDictionary*) headerParams
requestContentType:(NSString*) requestContentType
responseContentType:(NSString*) responseContentType
completionBlock:(void (^)(NSDictionary*, NSError *))completionBlock;

-(NSNumber*)  stringWithCompletionBlock:(NSString*) path
method:(NSString*) method
queryParams:(NSDictionary*) queryParams
body:(id) body
headerParams:(NSDictionary*) headerParams
requestContentType:(NSString*) requestContentType
responseContentType:(NSString*) responseContentType
completionBlock:(void (^)(NSString*, NSError *))completionBlock;

- (void)setAuthenticationLogin:(NSString *)login andPassword:(NSString *)password;
- (void)setAuthorizationWithBlock:(NSString *(^)(NSURL *, NSString * method, NSData *body))authorizationBlock;
@end

