[#ftl]
#import <Foundation/Foundation.h>
#import <Realm/Realm.h>

@interface ${projectPrefix}RLMObject : RLMObject
- (id) initWithDictionnary:(NSDictionary*)dict;
- (NSDictionary*) asDictionary;
@end
