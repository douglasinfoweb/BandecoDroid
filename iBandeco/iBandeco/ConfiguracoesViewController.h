//
//  BandecoViewController.h
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 15/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LogoUniversidadeViewCell.h"
#import <AFNetworking.h>
#import <SVProgressHUD.h>
#import <AFNetworking/AFHTTPRequestOperation.h>
#import "PrincipalViewController.h"
#import "BandecoConstantes.h"

@interface ConfiguracoesViewController : UIViewController <UICollectionViewDataSource, UICollectionViewDelegate, UIAlertViewDelegate>

@property (strong, nonatomic) NSMutableArray*
restaurantesSelecionados;

@end
