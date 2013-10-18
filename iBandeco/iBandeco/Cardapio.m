//
//  Cardapio.m
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 15/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import "Cardapio.h"

@implementation Cardapio
//Designated
- (id)initComPratoPrincipal:(NSString *)pratoPrincipal ComData:(NSDate *)data
                ComRefeicao:(Refeicao)refeicao
{
    self = [super init];
    if (self) {
        if (pratoPrincipal == nil || data == nil) {
            NSLog(@"Erro ao tentar criar cardapio (Prato: %@ Data: %@)",pratoPrincipal, data);
            return nil;
        }
        self.pratoPrincipal = pratoPrincipal;
        self.data = data;
        self.refeicao = refeicao;
    }
    return self;
}

-(void)encodeWithCoder:(NSCoder *)encoder
{
    [encoder encodeObject:_data forKey:@"data"];
    [encoder encodeObject:_pratoPrincipal forKey:@"pratoPrincipal"];
    [encoder encodeObject:_pts forKey:@"pts"];
    [encoder encodeObject:_salada forKey:@"salada"];
    [encoder encodeObject:_sobremesa forKey:@"sobremesa"];
    [encoder encodeObject:_suco forKey:@"suco"];
    
    [encoder encodeInteger:_refeicao forKey:@"refeicao"];
}

-(id)initWithCoder:(NSCoder *)decoder
{
    self = [self initComPratoPrincipal:[decoder decodeObjectForKey:@"pratoPrincipal"] ComData: [decoder decodeObjectForKey:@"data"]
            ComRefeicao:[decoder decodeIntegerForKey:@"refeicao"]];
    if (self) {
        self.pts = [decoder decodeObjectForKey:@"pts"];
        self.salada = [decoder decodeObjectForKey:@"salada"];
        self.sobremesa = [decoder decodeObjectForKey:@"sobremesa"];
        self.suco = [decoder decodeObjectForKey:@"suco"];
    }
    return self;
}

-(NSString *)description
{
    return [NSString stringWithFormat:@"%@ - %@", self.pratoPrincipal, self.data];
}

@end
