//
//  PrincipalViewController.m
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 17/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import "PrincipalViewController.h"

@interface PrincipalViewController ()

@property (strong, nonatomic) NSMutableArray* restaurantes;

@end

@implementation PrincipalViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}
- (IBAction)clickIconeConfig:(UIBarButtonItem *)sender {
    [ self performSegueWithIdentifier:@"abreConfig" sender:self];
}


-(void) updatedFinished:(NSNotification *)notification
{
    NSLog(@"UPDATED FINISHED");
    [SVProgressHUD dismiss];
    NSString *string = [[notification userInfo]
                        objectForKey:@"Resultado"];
    if ([string isEqualToString:@"OK"]) {
        //TODO: Reload view, etc
        NSLog(@"%@", self.restaurantes);
    } else {
        NSLog(@"Erro!!! NOT COOL");
    }
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    //Carrega restaurantes serializados, se possivel
    self.restaurantes = [ RestauranteStorage loadRestaurantes ];
    
    //Se tiver restaurantes carregados mas nao tiver selecionados
    if (self.restaurantes && !self.restaurantesSelecionados) {
        //Popula restaurantes selecionados se nao tiver nenhum selecionado
        if (!self.restaurantesSelecionados) {
            NSMutableArray* selecionadosTemp = [[NSMutableArray alloc] init];
            
            for (Restaurante* r in self.restaurantes) {
                [selecionadosTemp addObject:r];
            }
            
            self.restaurantesSelecionados = [selecionadosTemp mutableCopy];
        }
    }
    
	//Se a gente já sabe os restaurantes
    if (self.restaurantesSelecionados) {
        //Monta array com os restaurantes
        NSMutableArray* novosRestaurantes = [[NSMutableArray alloc] init];
        for (NSString* restCodigo in self.restaurantesSelecionados) {
            Restaurante *r =[self restauranteComCodigo:restCodigo];
            if (r) {
                //Se a gente ja tiver em restaurantes, copia ponteiro
                [novosRestaurantes addObject:r];
            } else {
                //Se nao, cria um objeto em branco
                [novosRestaurantes addObject:[[Restaurante alloc] initWithCodigo:restCodigo]];
            }
        }
        self.restaurantes = novosRestaurantes;
    
    }
}


- (Restaurante*)restauranteComCodigo: (NSString*)codigo
{
    for (Restaurante* r in self.restaurantes) {
        if ([r.nome isEqualToString:codigo])
             return r;
    }
    return nil;
}



- (void)viewDidAppear:(BOOL)animated
{
    if ([self.restaurantesSelecionados count] == 0) {
        [ self performSegueWithIdentifier:@"abreConfig" sender:self];
    } else {
        //Verifica se tem que atualizar algum restaurante
        // ... e atualiza ...
        //*Restaurantes "em branco" serao atualizados*
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(updatedFinished:)
                                                     name:@"UpdatedFinished"
                                                   object:nil];
        
        [SVProgressHUD showWithStatus:@"Carregando"];
        for (Restaurante* r in self.restaurantes) {
            [ RestauranteStorage atualizaRestaurante:r];
            
            NSLog(@"Atualiza %@", r);
        }
    }
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([segue.identifier isEqualToString:@"abreConfig"]) {
        //SEGUE para tela de Configuracoes
        NSLog(@"Abre config SEGUE!");
        ConfiguracoesViewController* configVC = segue.destinationViewController;
        
        configVC.restaurantesSelecionados = [self.restaurantesSelecionados mutableCopy];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
