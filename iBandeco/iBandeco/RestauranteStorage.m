//
//  RestauranteStorage.m
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 17/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import "RestauranteStorage.h"

@implementation RestauranteStorage

+ (NSString*)pathRestaurantes
{
    return [NSString stringWithFormat: @"%@/restaurantes", [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0]];
}

+ (void)saveRestaurantes:(NSMutableArray*) restaurantes
{
    if (restaurantes) {
        [NSKeyedArchiver archiveRootObject:restaurantes toFile:[[self class] pathRestaurantes]];
    }
}

+ (NSMutableArray*)loadRestaurantes {
    //File exists?
    if ([[NSFileManager defaultManager] fileExistsAtPath:[[self class] pathRestaurantes]]) {
        
        NSMutableArray* restaurantes = [NSKeyedUnarchiver unarchiveObjectWithFile:[[self class] pathRestaurantes]];
        
        return restaurantes;
        
    } else {
        //Will set restaurantes as Null if it does not have the file
        return nil;
    }
}


+ (void) atualizaRestaurante:(Restaurante*) restaurante
{
    NSString* url = [NSString stringWithFormat:@"%@%@", (BANDECO_SITE @"json/"),restaurante.codigo];
    
    //Pega todos os restaurantes
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObject:@"text/plain"];
    [manager GET: url parameters:nil success:^(AFHTTPRequestOperation *operation, NSDictionary* response) {
        
        
        restaurante.cardapios = [[NSMutableArray alloc] init];
        
        NSDateFormatter *df = [[NSDateFormatter alloc] init];
        [df setDateFormat:@"dd/MM/yyyy"];
        
        for (NSDictionary* cardapioDict in response[@"cardapios"])
        {
            
            Refeicao refeicao;
            if ([ cardapioDict[@"refeicao"] isEqualToString:@"ALMOCO"]) {
                refeicao = ALMOCO;
            } else {
                refeicao = JANTA;
            }
            
            NSDate *data = [df dateFromString: cardapioDict[@"data"]];
            
                 Cardapio * cardapio = [[Cardapio alloc] initComPratoPrincipal:cardapioDict[@"pratoPrincipal"] ComData:data ComRefeicao:refeicao];
            
            if (!cardapio)
                continue;
            
            if ([ cardapioDict objectForKey:@"pts"]) {
                cardapio.pts = cardapioDict[@"pts"];
            }
            
            if ([ cardapioDict objectForKey:@"salada"]) {
                cardapio.salada = cardapioDict[@"salada"];
            }
            
            if ([ cardapioDict objectForKey:@"sobremesa"]) {
                cardapio.sobremesa = cardapioDict[@"sobremesa"];
            }
            
            if ([ cardapioDict objectForKey:@"suco"]) {
                cardapio.suco = cardapioDict[@"suco"];
            }
            
            [ restaurante.cardapios addObject: cardapio];
        }
        
        
        restaurante.nome = response[@"nome"];
        restaurante.tinyUrl = response[@"tinyUrl"];
        restaurante.site = response[@"site"];
        
        //Avisa a controller que acabou de atualizar
        [[NSNotificationCenter defaultCenter] postNotificationName:@"UpdatedFinished"
                          object:self
                        userInfo:[NSDictionary dictionaryWithObjectsAndKeys:@"OK", @"Resultado", nil]];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Error: %@", error);
        
        [[NSNotificationCenter defaultCenter] postNotificationName:@"UpdatedFinished"
                                                            object:self
                                                          userInfo:[NSDictionary dictionaryWithObjectsAndKeys:@"ERRO", @"Resultado", nil]];
    }];
}

@end
