[#ftl]
#import "${projectPrefix}DateFormatterUtils.h"
#import "${projectPrefix}RegistryManager.h"

@implementation ${projectPrefix}DateFormatterUtils

+(NSDate *)dateFromString:(NSString *)inputString {
    NSObject<${projectPrefix}RegistryProtocol> *registry = [[${projectPrefix}RegistryManager sharedManager] registryForIdentifier:timeZoneRegistryIdentifier];
    if(!registry || ![[registry formatter] isKindOfClass:[NSDateFormatter class]]){
        [NSException raise:NSInternalInconsistencyException format:@"You must implement a NSDate formatter for the type '%@'", timeZoneRegistryIdentifier];
    }
    NSDateFormatter *dateFormatter = (NSDateFormatter *)[registry formatter];
    return [dateFormatter dateFromString:inputString];
}

+(NSTimeZone *)timeZoneFromString:(NSString *)inputString {
    if(!inputString) return nil;
    NSString *pattern = @"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}([Z+-]{1})(.*)";
    NSError *error;
    NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:pattern
                                                options:0
                                                error:&error];

    NSTextCheckingResult *match = [regex firstMatchInString:inputString options:0 range:NSMakeRange(0, [inputString length])];
    if (match != nil) {
        //Handling Z for GMT
        NSString *parityString = [inputString substringWithRange:[match rangeAtIndex:1]];
        if([ @"Z" isEqualToString:parityString]){
            return [NSTimeZone timeZoneWithName:@"GMT"];
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
        NSInteger secondsFromGMT = 0;
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
            return nil;
        }
        secondsFromGMT = (hours * 3600 + minutes * 60) * parity;
        return [NSTimeZone timeZoneForSecondsFromGMT:secondsFromGMT];
    }
    return nil;
}


+(NSString *)stringFromDate:(NSDate *)date withTimeZone:(NSTimeZone *)timeZone {
    NSObject<${projectPrefix}RegistryProtocol> *registry = [[${projectPrefix}RegistryManager sharedManager] registryForIdentifier:timeZoneRegistryIdentifier];
    if(!registry || ![[registry formatter] isKindOfClass:[NSDateFormatter class]]){
        [NSException raise:NSInternalInconsistencyException format:@"You must implement a NSDate formatter for the type '%@'", timeZoneRegistryIdentifier];
    }
    NSDateFormatter *dateFormatter = (NSDateFormatter *)[registry formatter];
    if(timeZone){
        [dateFormatter setTimeZone:timeZone];
    }
    return [dateFormatter stringFromDate:date];
}

@end