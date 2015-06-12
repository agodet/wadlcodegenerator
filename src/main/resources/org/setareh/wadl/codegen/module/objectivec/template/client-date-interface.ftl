[#ftl]
#import <Foundation/Foundation.h>

static NSString *timeZoneRegistryIdentifier = @"DateTimeZone";

@interface ${projectPrefix}DateFormatterUtils : NSObject

+(NSString *)stringFromDate:(NSDate *)date withTimeZone:(NSTimeZone *)timeZone;
+(NSDate *)dateFromString:(NSString *)inputString;
+(NSTimeZone *)timeZoneFromString:(NSString *)inputString;

@end