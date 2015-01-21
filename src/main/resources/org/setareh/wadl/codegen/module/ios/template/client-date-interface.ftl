[#ftl]
#import <Foundation/Foundation.h>

static NSString *timeZoneRegistryIdentifier = @"DateTimeZone";

@interface ${projectPrefix}DateFormatterUtils : NSObject

+(NSString *) formatWithDate:(NSDate *)date;
+(NSDate *) convertToDate:(NSObject *)input;

@end