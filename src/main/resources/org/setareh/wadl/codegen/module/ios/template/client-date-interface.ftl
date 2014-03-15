[#ftl]
#import <Foundation/Foundation.h>
#import "${generatedPrefix}Object.h"

@interface ${generatedPrefix}DateFormatterUtils : NSObject {
@private
}

+(NSString*) formatWithDate:(NSDate*) date;
+(NSDate*) convertToDate:(NSObject*)input;
@end