#import "PrismaCampaigns.h"
#import <Prisma/Prisma-Swift.h>

@implementation PrismaCampaigns
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(Load:(NSString *)Server Port:(NSString *)Port AppToken:(NSString*)AppToken CustomerId:(NSString *)CustomerId)
{
  NSLog(@"RNPrismaNativeLog: Calling Prisma.load | server: %@, token: %@, customerId: %@", Server, AppToken, CustomerId);
  
  [Client loadWithServer:Server port:Port appToken:AppToken customer:CustomerId proto:@"https"];
  
#ifdef DEBUG
  [Client shared].debug = true;
  NSLog(@"RNPrismaNativeLog: Setting debug to TRUE");
#endif
  
}

@end
