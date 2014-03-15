[#ftl]
#import <Foundation/Foundation.h>

@interface ${generatedPrefix}Enum : NSObject
    @property (nonatomic, assign) NSInteger type;

    @property(nonatomic, copy) NSString *value;

    +(id)fromString:(NSString *)string;

    -(id)initWithString:(NSString *)stringValue withType:(NSInteger) type;
@end
