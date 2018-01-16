#import <Foundation/Foundation.h>
#import <Realm/Realm.h>

@interface ${projectPrefix}TimeZoneDate : RLMObject

@property (nonatomic, strong) NSDate *date;

@property (nonatomic, readonly, strong) NSTimeZone *timeZone;

- (NSString *)asString;

- (BOOL)isEqualToTimeZoneDate:(${projectPrefix}TimeZoneDate *)tz;

+ (${projectPrefix}TimeZoneDate *)timeZoneDateFromDate:(NSDate *)date;

+ (${projectPrefix}TimeZoneDate *)timeZoneDateFromDate:(NSDate *)date timeZone:(NSTimeZone *)timeZone;

+ (${projectPrefix}TimeZoneDate *)timeZoneDateFromString:(NSString *)inputString;

@end