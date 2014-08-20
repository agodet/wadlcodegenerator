[#ftl]
#import <Foundation/Foundation.h>
#import "${projectPrefix}Object.h"

@interface ${projectPrefix}DateFormatterUtils : NSObject {
@private
}

+(NSString*) formatWithDate:(NSDate*) date;
+(NSDate*) convertToDate:(NSObject*)input;
@end