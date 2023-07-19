
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNPrismaCampaignsSpec.h"

@interface PrismaCampaigns : NSObject <NativePrismaCampaignsSpec>
#else
#import <React/RCTBridgeModule.h>

@interface PrismaCampaigns : NSObject <RCTBridgeModule>
#endif

@end
