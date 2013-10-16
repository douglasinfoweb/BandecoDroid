//
//  Cardapio.h
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 15/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Cardapio : NSObject
@property (strong, nonatomic) NSDate* data;
@property (strong, nonatomic) NSString* pratoPrincipal;
@property (strong, nonatomic) NSString* pts;
@property (strong, nonatomic) NSString* salada;
@property (strong, nonatomic) NSString* sobremesa;
@property (strong, nonatomic) NSString* suco;
@end
