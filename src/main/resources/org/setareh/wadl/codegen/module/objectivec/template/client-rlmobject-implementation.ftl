[#ftl]
#import "${projectPrefix}RLMObject.h"

@implementation ${projectPrefix}RLMObject

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