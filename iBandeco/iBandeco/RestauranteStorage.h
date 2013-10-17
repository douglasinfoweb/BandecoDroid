//
//  RestauranteStorage.h
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 17/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Restaurante.h"
#import "Cardapio.h"
#import "BandecoConstantes.h"
#import <AFNetworking.h>

@interface RestauranteStorage : NSObject

+ (void)saveRestaurantes:(NSMutableArray*) restaurantes;

+ (NSMutableArray*)loadRestaurantes;

+ (void) atualizaRestaurante:(Restaurante*) restaurante;

@end
