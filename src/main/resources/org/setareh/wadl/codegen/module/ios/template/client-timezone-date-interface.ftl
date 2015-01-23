#import <Foundation/Foundation.h>

@interface ${projectPrefix}TimeZoneDate : NSObject

@property (nonatomic, strong) NSDate *date;

@property (nonatomic, strong) NSTimeZone *timeZone;

-(id)initWithDate:(NSDate *)date timeZone:(NSTimeZone *)timeZone;

-(id)initWithString:(NSString *)dateString;

-(NSString *)asString;

-(BOOL)isEqualToTimeZoneDate:(${projectPrefix}TimeZoneDate *)tz;

@end