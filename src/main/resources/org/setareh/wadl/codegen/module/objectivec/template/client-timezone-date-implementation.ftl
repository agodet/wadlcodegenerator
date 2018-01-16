#import "${projectPrefix}TimeZoneDate.h"
#import "${projectPrefix}DateFormatterUtils.h"

@interface ${projectPrefix}TimeZoneDate()

// Internal properties of NSTimeZone will be persisted
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSData *data;

@end

@implementation ${projectPrefix}TimeZoneDate

- (id)initWithDate:(NSDate *)date timeZone:(NSTimeZone *)timeZone {
    self = [super init];
    if(self){
        _date = [date copy];
        _name = [timeZone.name copy];
        _data = [timeZone.data copy];
    }

    return self;
}

- (NSString *)asString {
    return [${projectPrefix}DateFormatterUtils stringFromDate:self.date withTimeZone:self.timeZone];
}

- (BOOL)isEqualToTimeZoneDate:(${projectPrefix}TimeZoneDate *)tz {
    return [self.date isEqualToDate:tz.date];
}

- (NSTimeZone *)timeZone {
    if (!_name) return nil;

    return [[NSTimeZone alloc] initWithName:_name data:_data];
}

#pragma mark - Static

+ (${projectPrefix}TimeZoneDate *)timeZoneDateFromDate:(NSDate *)date {
    if(!date) return nil;
    return [[${projectPrefix}TimeZoneDate alloc] initWithDate:date timeZone:nil];
}

+ (${projectPrefix}TimeZoneDate *)timeZoneDateFromDate:(NSDate *)date timeZone:(NSTimeZone *)timeZone {
    if(!date) return nil;
    return [[${projectPrefix}TimeZoneDate alloc] initWithDate:date timeZone:timeZone];
}

+ (${projectPrefix}TimeZoneDate *)timeZoneDateFromString:(NSString *)inputString {
    NSDate *date = [${projectPrefix}DateFormatterUtils dateFromString:inputString];
    if(!date) return nil;
    NSTimeZone *timeZone = [${projectPrefix}DateFormatterUtils timeZoneFromString:inputString];
    return [[${projectPrefix}TimeZoneDate alloc] initWithDate:date timeZone:timeZone];
}

#pragma mark - Realm

+ (NSArray *)ignoredProperties {
    return @[@"timeZone"];
}

@end