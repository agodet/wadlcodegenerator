#import <Foundation/Foundation.h>

@interface NSDate (TimeZone)

@property (nonatomic, strong) NSTimeZone *originalTimeZone;

- (BOOL)originalTimeZoneEqualToTimeZone:(NSTimeZone *)timeZone;

@end
