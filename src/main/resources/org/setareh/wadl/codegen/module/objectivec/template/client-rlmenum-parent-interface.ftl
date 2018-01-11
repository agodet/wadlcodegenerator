[#ftl]
#import <Foundation/Foundation.h>
#import <Realm/Realm.h>

@interface ${projectPrefix}RLMEnum : RLMObject

@property (nonatomic, assign) NSInteger type;

@property(nonatomic, copy) NSString *value;

+ (id)fromString:(NSString *)string;

- (id)initWithString:(NSString *)stringValue withType:(NSInteger)type;

@end
