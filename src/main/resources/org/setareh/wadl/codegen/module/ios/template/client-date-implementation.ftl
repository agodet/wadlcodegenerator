[#ftl]
#import "${projectPrefix}DateFormatterUtils.h"

@implementation ${projectPrefix}DateFormatterUtils

+(NSString *) formatWithDate:(NSDate *)date {
    NSDateFormatter* df = [NSDateFormatter new];
    [df setDateFormat:@"yyyy-MM-dd'T'HH:mm:ssZZZZZ"];
    if(date.originalTimeZone){
        [df setTimeZone:date.originalTimeZone];
    }
    return [df stringFromDate:date];
}

+(NSDate *) convertToDate:(NSObject*)input {
    NSDate* date;
    if([input isKindOfClass:[NSString class]]){
        NSString* inputString = (NSString*) input;
        NSDateFormatter* df = [NSDateFormatter new];
        [df setDateFormat:@"yyyy-MM-dd'T'HH:mm:ssZZZZZ"];
        date = [df dateFromString:inputString];
        date.originalTimeZone = [${projectPrefix}DateFormatterUtils timeZoneFromDateFormat:inputString];
    }
    else if([input isKindOfClass:[NSNumber class]]) {
        NSNumber* inputNumber = (NSNumber*) input;
        NSTimeInterval interval = [inputNumber doubleValue];
        date = [[NSDate alloc] initWithTimeIntervalSince1970:interval];
    }
    return date;
}

+ (NSTimeZone *)timeZoneFromDateFormat:(NSString *)inputString {
    NSTimeZone *result = nil;
    NSString *pattern = @"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}([Z+-]{1})(.*)";
    NSError *error;
    NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:pattern
                                                        options:0
                                                        error:&error];

    NSTextCheckingResult *match = [regex firstMatchInString:inputString options:0 range:NSMakeRange(0, [inputString length])];
    if (match != nil) {
        NSInteger secondsFromGMT = 0;
        //Handling Z for GMT
        NSString *parityString = [inputString substringWithRange:[match rangeAtIndex:1]];
        if([ @"Z" isEqualToString:parityString]){
            return [NSTimeZone timeZoneForSecondsFromGMT:secondsFromGMT];
        }
        //Parity
        NSInteger parity;
        if([ @"+" isEqualToString:parityString]){
            parity = 1;
        }
        else {
            parity = -1;
        }
        //Seconds from GMT
        NSString *timezoneString = [inputString substringWithRange:[match rangeAtIndex:2]];
        NSArray *components = [timezoneString componentsSeparatedByString:@":"];
        NSInteger hours = 0;
        NSInteger minutes = 0;
        if([components count] > 1){
            hours = [[components objectAtIndex:0] integerValue];
            minutes = [[components objectAtIndex:1] integerValue];
        }
        else if([timezoneString length] == 4){
            hours = [[[components firstObject] substringToIndex:2] integerValue];
            minutes = [[[components firstObject] substringFromIndex:2] integerValue];
        }
        else {
            NSLog(@"Timezone String format problem");
        }
        secondsFromGMT = (hours * 3600 + minutes * 60) * parity;
        result = [NSTimeZone timeZoneForSecondsFromGMT:secondsFromGMT];
    }
    return result;
}

@end