[#ftl]
#import "${projectPrefix}DateFormatterUtils.h"

@implementation ${projectPrefix}DateFormatterUtils

+(NSString*) formatWithDate:(NSDate*) date {
    NSDateFormatter* df = [NSDateFormatter new];
    NSLocale *locale = [[NSLocale new]
    initWithLocaleIdentifier:@"en_US_POSIX"];
    [df setLocale:locale];
    [df setDateFormat:@"yyyy-MM-dd'T'HH:mm:ssZZZ"];

    return [df stringFromDate:date];
}

+(NSDate*) convertToDate:(NSObject*)input {
    NSDate* date;
    if([input isKindOfClass:[NSString class]]){
        NSString* inputString = (NSString*) input;
        NSDateFormatter* df = [NSDateFormatter new];
        NSLocale *locale = [[NSLocale new]
        initWithLocaleIdentifier:@"en_US_POSIX"];
        [df setLocale:locale];
        [df setDateFormat:@"yyyy-MM-dd'T'HH:mm:ssZZZ"];
        date = [df dateFromString:inputString];
        date.secondsFromGMT = [${projectPrefix}DateFormatterUtils timeZoneSecondsFromGMT:inputString];
    }
    else if([input isKindOfClass:[NSNumber class]]) {
        NSNumber* inputNumber = (NSNumber*) input;
        NSTimeInterval interval = [inputNumber doubleValue];
        date = [[NSDate alloc] initWithTimeIntervalSince1970:interval];
    }
    return date;
}

+(NSInteger)timeZoneSecondsFromGMT:(NSString *)inputString {
    //Timezone
    NSString *tzSubstring = [inputString substringWithRange:NSMakeRange([inputString length] - 6, 6)];
    NSInteger parity = 1;
    if([tzSubstring rangeOfString:@"-"].location != NSNotFound){
        parity = -1;
    }
    tzSubstring = [tzSubstring stringByReplacingOccurrencesOfString:@"+" withString:@""];
    tzSubstring = [tzSubstring stringByReplacingOccurrencesOfString:@"-" withString:@""];
    NSArray *components = [tzSubstring componentsSeparatedByString:@":"];
    NSInteger secondsFromGMT = 0; //By Default
    if([components count] > 1){
        NSInteger hours = [[components firstObject] integerValue];
        NSInteger minutes = [[components objectAtIndex:1] integerValue];
        secondsFromGMT = (hours * 3600 + minutes * 60) * parity;
    }
    return secondsFromGMT;
}

@end
