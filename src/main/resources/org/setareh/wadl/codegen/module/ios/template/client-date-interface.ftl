[#ftl]
#import <Foundation/Foundation.h>
#import "NSDate+${projectPrefix}.h"

@interface ${projectPrefix}DateFormatterUtils : NSObject

+(NSString*) formatWithDate:(NSDate*) date;
+(NSDate*) convertToDate:(NSObject*)input;

@end