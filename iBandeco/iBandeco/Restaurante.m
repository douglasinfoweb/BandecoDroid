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

- (BOOL)temQueAtualizar
{
    NSDate* now = [NSDate date];
    
    
    NSCalendar *gregorian = [[NSCalendar alloc]
                             initWithCalendarIdentifier:NSGregorianCalendar];
    
    
    NSDateComponents* nowComponents = [gregorian components:(NSCalendarUnitWeekOfYear|NSCalendarUnitYear) fromDate:now];
    
    if (self.cardapios.count >= 1) {
        Cardapio* ultimoCardapio = self.cardapios.lastObject;
        NSDateComponents* ultimoCardapioComponents = [gregorian components:(NSCalendarUnitWeekOfYear|NSCalendarUnitYear) fromDate:ultimoCardapio.data];
        
        if (ultimoCardapioComponents.weekOfYear >= nowComponents.weekOfYear &&
            ultimoCardapioComponents.year >= nowComponents.year) {
            return NO;
        }
    }
    
    return YES;
}

-(void)removeCardapiosAntigos
{
    NSDate* now = [NSDate date];
    
    NSCalendar *gregorian = [[NSCalendar alloc]
                             initWithCalendarIdentifier:NSGregorianCalendar];
    
    for (Cardapio* c in [self.cardapios copy]) {
        NSDateComponents* componente= [[NSDateComponents alloc] init];
        
        switch (c.refeicao) {
            case ALMOCO:
                componente.hour = 15;
                break;
            case JANTA:
                componente.hour = 22;
                break;
        }
        
        NSDate* dataCardapio = [ gregorian dateByAddingComponents:componente toDate: c.data options:0];
        //Se now eh depois do dataCardapio
        if ([now compare: dataCardapio] == NSOrderedDescending) {
            //Remove cardapio
            [ self.cardapios removeObject:c];
        }
    }
}

@end
