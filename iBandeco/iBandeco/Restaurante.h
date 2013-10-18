//
//  Restaurante.h
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 15/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Restaurante : NSObject <NSCoding>

@property (strong, nonatomic) NSString* nome;

@property (strong, nonatomic) NSString* codigo;

@property (strong, nonatomic) NSString* tinyUrl;

@property (strong, nonatomic) NSString* site;

@property (strong, nonatomic) NSMutableArray* cardapios;


//Designated
- (id)initWithCodigo:(NSString*)codigo;

- (BOOL) temQueAtualizar;

- (void) removeCardapiosAntigos;

@end
