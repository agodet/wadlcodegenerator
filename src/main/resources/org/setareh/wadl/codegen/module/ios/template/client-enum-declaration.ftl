[#ftl]
[#--template for the client-side enum declaration.--]
// Generated by xsd compiler for ios/objective-c
// DO NOT CHANGE!

#import <Foundation/Foundation.h>
#import "${generatedPrefix}Enum.h"

[#if enum.docComment??]
/**
 @file
 ${enum.docComment?chop_linebreak?replace("\n", "\n ")?replace("\t", "")}

*/
[/#if]
  [#list enum.enumConstants as constant]

  [#if constant.docComment??]
/**
 ${constant.docComment?replace("\n", "\n ")?replace("\t", "")}
*/
  [/#if]
extern ${generatedPrefix}${enum.name} * ${enum.name?upper_case}_${constant.name?upper_case};
  [/#list]

typedef enum {
[#list enum.enumConstants as constant]
${enum.name}_${constant.name?capitalize},
[/#list]
} ${enum.name}Type;

@interface ${generatedPrefix}${enum.name} : ${generatedPrefix}Enum

@end