[#ftl]
#import "${generatedPrefix}DateFormatterUtils.h"

@implementation ${generatedPrefix}DateFormatterUtils

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
    }
    else if([input isKindOfClass:[NSNumber class]]) {
        NSNumber* inputNumber = (NSNumber*) input;
        NSTimeInterval interval = [inputNumber doubleValue];
        date = [[NSDate alloc] initWithTimeIntervalSince1970:interval];
    }
    return date;
}

@end
