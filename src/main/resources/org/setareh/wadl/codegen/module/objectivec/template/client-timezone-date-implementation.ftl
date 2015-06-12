#import "${projectPrefix}TimeZoneDate.h"
#import "${projectPrefix}DateFormatterUtils.h"

@implementation ${projectPrefix}TimeZoneDate

-(id)initWithDate:(NSDate *)date timeZone:(NSTimeZone *)timeZone {
    self = [super init];
    if(self){
        self.date = date;
        self.timeZone = timeZone;
    }
    return self;
}

-(NSString *)asString {
    return [${projectPrefix}DateFormatterUtils stringFromDate:self.date withTimeZone:self.timeZone];
}

-(BOOL)isEqualToTimeZoneDate:(${projectPrefix}TimeZoneDate *)tz {
    return [self.date isEqualToDate:tz.date];
}

#pragma mark - Static

+(${projectPrefix}TimeZoneDate *)timeZoneDateFromDate:(NSDate *)date {
    if(!date) return nil;
    return [[${projectPrefix}TimeZoneDate alloc] initWithDate:date timeZone:nil];
}

+(${projectPrefix}TimeZoneDate *)timeZoneDateFromDate:(NSDate *)date timeZone:(NSTimeZone *)timeZone {
    if(!date) return nil;
    return [[${projectPrefix}TimeZoneDate alloc] initWithDate:date timeZone:timeZone];
}

+(${projectPrefix}TimeZoneDate *)timeZoneDateFromString:(NSString *)inputString {
    NSDate *date = [${projectPrefix}DateFormatterUtils dateFromString:inputString];
    if(!date) return nil;
    NSTimeZone *timeZone = [${projectPrefix}DateFormatterUtils timeZoneFromString:inputString];
    return [[${projectPrefix}TimeZoneDate alloc] initWithDate:date timeZone:timeZone];
}

@end