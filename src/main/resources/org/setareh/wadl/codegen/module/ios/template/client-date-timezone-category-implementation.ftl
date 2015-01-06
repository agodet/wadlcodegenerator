#import "NSDate+TimeZone.h"

@implementation NSDate (TimeZone)

@dynamic originalTimeZone;

- (BOOL)originalTimeZoneEqualToTimeZone:(NSTimeZone *)timeZone {
    if(self.originalTimeZone){
        return self.originalTimeZone.secondsFromGMT == timeZone.secondsFromGMT;
    }
    return NO;
}

@end
