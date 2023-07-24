//
//  PrismaCampaignsView.m
//  PrismaCampaigns
//
//  Created by Nahuel Alderete on 21/07/2023.
//  Copyright © 2023 Facebook. All rights reserved.
//
#import <React/RCTViewManager.h>
#import <Prisma/Prisma-Swift.h>

@interface PrismaCampaignsView : RCTViewManager
@end

@implementation PrismaCampaignsView

RCT_EXPORT_MODULE()

RCT_CUSTOM_VIEW_PROPERTY(placeholderName, NSString, UIStackView)
{
  //Obtengo la view placeholder de dentro del UIStack
  Placeholder *childPlaceHolder = view.arrangedSubviews.firstObject;
    
    void (^onErrorView)(NSInteger) = ^(NSInteger code){
      NSLog(@"RNPrismaNativeLog: ERROR - syncViewWithViewName return code is: %ld", (long)code);
    };

  //Convierto el paramentro que llego del lado JS a tipo NSString
  NSString *convertedPlaceholderName = [RCTConvert NSString:json];
  NSLog(@"RNPrismaNativeLog: PlaceholderName: %@", convertedPlaceholderName);
  
  //Seteo el nombre del placeholder al placeholder nativo
  [childPlaceHolder setValue:convertedPlaceholderName forKeyPath:@"name"];
  
  //Sincronizo el placeholder para obtener las campañas
  [
    [Client shared] syncViewWithViewName:convertedPlaceholderName
                           placeholders:@[childPlaceHolder]
                               onSynced:nil
                                onError:onErrorView
                        redirectHandler:nil
                         dismissHandler:nil
  ];
}


- (UIView *)view
{
    NSLog(@"RNPrismaNativeLog: Creating Placeholder instance");
    
    Placeholder *placeholderInstance = [[Placeholder alloc] init];
    
    UIStackView *viewInstance = [[UIStackView alloc] init];
  
    [viewInstance addArrangedSubview:placeholderInstance];

    return viewInstance;
}


@end
