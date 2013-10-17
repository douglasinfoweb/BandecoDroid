//
//  Cardapio.h
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 15/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum {
    ALMOCO,
    JANTA
} Refeicao;

@interface Cardapio : NSObject <NSCoding>
@property (nonatomic) Refeicao refeicao;
@property (strong, nonatomic) NSDate* data;
@property (strong, nonatomic) NSString* pratoPrincipal;
@property (strong, nonatomic) NSString* pts;
@property (strong, nonatomic) NSString* salada;
@property (strong, nonatomic) NSString* sobremesa;
@property (strong, nonatomic) NSString* suco;

//Designated
- initComPratoPrincipal:(NSString*)pratoPrincipal
                ComData:(NSDate*)data
            ComRefeicao:(Refeicao) refeicao;
@end
