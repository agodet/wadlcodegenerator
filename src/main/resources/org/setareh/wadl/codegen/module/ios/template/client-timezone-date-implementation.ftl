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

-(id)initWithString:(NSString *)dateString {
    self = [super init];
    if (self) {
        self.date = [SUDateFormatterUtils dateFromString:dateString];
        self.timeZone = [SUDateFormatterUtils timeZoneFromString:dateString];
    }
    return self;
}

-(NSString *)asString {
    return [SUDateFormatterUtils stringFromDate:self.date withTimeZone:self.timeZone];
}

-(BOOL)isEqualToTimeZoneDate:(${projectPrefix}TimeZoneDate *)tz {
    return [self.date isEqualToDate:tz.date];
}

@end