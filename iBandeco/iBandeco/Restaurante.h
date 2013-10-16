//
//  Restaurante.h
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 15/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Restaurante : NSObject

@property (strong, nonatomic) NSString* nome;

@property (strong, nonatomic) NSString* codigo;

@property (strong, nonatomic) NSString* tinyUrl;

@property (strong, nonatomic) NSString* site;

@property (strong, nonatomic) NSMutableArray* cardapios;


@end
