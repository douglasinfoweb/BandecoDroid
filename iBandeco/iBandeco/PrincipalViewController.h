//
//  PrincipalViewController.h
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 17/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ConfiguracoesViewController.h"
#import "Restaurante.h"
#import "RestauranteStorage.h"
#import "CardapioTableViewCell.h"
@interface PrincipalViewController : UIViewController < UITableViewDataSource, UITableViewDelegate>

@property (strong, nonatomic) NSArray*
restaurantesSelecionados;


@end
