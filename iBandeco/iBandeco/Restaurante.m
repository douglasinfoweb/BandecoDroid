//
//  Restaurante.m
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 15/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import "Restaurante.h"

@implementation Restaurante


- (void)encodeWithCoder:(NSCoder *)encoder
{
    [encoder encodeObject:_nome forKey:@"nome"];
    [encoder encodeObject:_codigo forKey:@"codigo"];
    [encoder encodeObject:_tinyUrl forKey:@"tinyUrl"];
    [encoder encodeObject:_site forKey:@"site"];
    [encoder encodeObject:_cardapios forKey:@"cardapios"];
    
}
//Designated
- (id)initWithCodigo:(NSString *)codigo
{
    self = [super init];
    if (self) {
        self.codigo = codigo;
    }
    return self;
}


- (id)initWithCoder:(NSCoder *)decoder
{
    self = [self initWithCodigo:[decoder decodeObjectForKey:@"codigo"]];
    if (self) {
        self.nome = [decoder decodeObjectForKey:@"nome"];
        self.tinyUrl = [decoder decodeObjectForKey:@"tinyUrl"];
        self.site = [decoder decodeObjectForKey:@"site"];
        self.cardapios = [decoder decodeObjectForKey:@"cardapios"];
    }
    return self;
}

- (NSMutableArray *)cardapios {
    if (!_cardapios) {
        _cardapios = [[NSMutableArray alloc] init];
    }
    return _cardapios;
}

- (NSString *)description {
    return [NSString stringWithFormat:@"%@: %@", self.codigo, self.cardapios];
}
@end
