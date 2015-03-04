[#ftl]
#import "${projectPrefix}Object.h"

@implementation ${projectPrefix}Object

- (id) initWithDictionnary:(NSDictionary*)dict {
    return self;
}

- (NSDictionary*) asDictionary{
    return [[NSDictionary alloc] init];
}

- (NSString*)description {
    return [NSString stringWithFormat:@"%@ %@", [super description], [self asDictionary]];
}

@end