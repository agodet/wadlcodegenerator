#import <Foundation/Foundation.h>

@interface ${projectPrefix}TimeZoneDate : NSObject

@property (nonatomic, strong) NSDate *date;

@property (nonatomic, strong) NSTimeZone *timeZone;

-(NSString *)asString;

-(BOOL)isEqualToTimeZoneDate:(${projectPrefix}TimeZoneDate *)tz;

+(${projectPrefix}TimeZoneDate *)timeZoneDateFromDate:(NSDate *)date;

+(${projectPrefix}TimeZoneDate *)timeZoneDateFromDate:(NSDate *)date timeZone:(NSTimeZone *)timeZone;

+(${projectPrefix}TimeZoneDate *)timeZoneDateFromString:(NSString *)inputString;

@end