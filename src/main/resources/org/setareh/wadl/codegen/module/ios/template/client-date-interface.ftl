[#ftl]
#import <Foundation/Foundation.h>
#import "NSDate+TimeZone.h"

@interface ${projectPrefix}DateFormatterUtils : NSObject

+(NSString*) formatWithDate:(NSDate*) date;
+(NSDate*) convertToDate:(NSObject*)input;

@end