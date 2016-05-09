[#ftl]

#import <Foundation/Foundation.h>

@interface ${projectPrefix}InputStream : NSData
- (id) initWithDictionnary:(NSDictionary*)dict;
- (NSDictionary*) asDictionary;
@end