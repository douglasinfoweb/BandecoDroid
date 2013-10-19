//
//  PrincipalViewController.m
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 17/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import "PrincipalViewController.h"

@interface PrincipalViewController ()
@property (weak, nonatomic) IBOutlet UINavigationItem *tituloBar;

@property (strong, nonatomic) NSMutableArray* restaurantes;

@property (weak, nonatomic) IBOutlet UITableView *cardapiosTableView;
@property (atomic) NSUInteger restaurantesToUpdate;

@property (nonatomic) NSUInteger restauranteNaTelaIndex;

@end

@implementation PrincipalViewController


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CardapioTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"cardapioTableViewCell" forIndexPath:indexPath];
    
    if (!cell) {
        cell = [[CardapioTableViewCell alloc] init];
    }
    
    
    Restaurante* restauranteNaTela = self.restaurantes[self.restauranteNaTelaIndex];
    Cardapio* cardapio = restauranteNaTela.cardapios[indexPath.section];
    
    NSInteger rowEquiv = indexPath.row;
    //Como nem toda row aparece, temos que adicionar as que nao aparecem
    if (rowEquiv >= 1 && cardapio.suco == nil)
        rowEquiv++;
    
    if (rowEquiv >= 2 && cardapio.sobremesa == nil)
        rowEquiv++;
    
    if (rowEquiv >= 3 && cardapio.salada == nil)
        rowEquiv++;
    
    if (rowEquiv >= 4 && cardapio.pts == nil)
        rowEquiv++;
    
    
    cell.textView.scrollEnabled = (rowEquiv==0);
    
    switch (rowEquiv) {
        case 0: //Prato principal
            cell.iconeLabel.text = @"🍗";
            cell.textView.text = cardapio.pratoPrincipal;
            break;
        case 1: //Suco
            cell.iconeLabel.text = @"🍹";
            cell.textView.text = cardapio.suco;
            break;
        case 2: //Sobremesa
            cell.iconeLabel.text = @"🍮";
            cell.textView.text = cardapio.sobremesa;
            break;
        case 3: //Salada
            cell.iconeLabel.text = @"🌿";
            cell.textView.text = cardapio.salada;
            break;
        case 4: //PTS
            cell.iconeLabel.text = @"🌾";
            cell.textView.text = cardapio.pts;
            break;
    }
    
    
    
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    
    Restaurante* restauranteNaTela = self.restaurantes[self.restauranteNaTelaIndex];
    Cardapio* cardapio = restauranteNaTela.cardapios[section];
    NSInteger subsectionCount = 1;
    if (cardapio.suco != nil)
        subsectionCount++;
    if (cardapio.sobremesa != nil)
        subsectionCount++;
    if (cardapio.salada != nil)
        subsectionCount++;
    if (cardapio.pts != nil)
        subsectionCount++;
        
    return subsectionCount;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    Restaurante* restauranteNaTela = self.restaurantes[self.restauranteNaTelaIndex];
    Cardapio* cardapio = restauranteNaTela.cardapios[section];
    //Pegando refeicao
    NSString* refeicao;
    if (cardapio.refeicao == ALMOCO) {
        refeicao = @"Almoço";
    } else {
        refeicao = @"Jantar";
    }
    //Pegando dia da semana
    NSCalendar *gregorian = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];

    NSDateComponents *weekdayComponents =[gregorian components:NSWeekdayCalendarUnit fromDate:cardapio.data];

    NSString* diaSemana;
    
    switch ([weekdayComponents weekday]) {
        case 1:
            diaSemana=@"Domingo";
            break;
        case 2:
            diaSemana=@"Segunda";
            break;
        case 3:
            diaSemana=@"Terça";
            break;
        case 4:
            diaSemana=@"Quarta";
            break;
        case 5:
            diaSemana=@"Quinta";
            break;
        case 6:
            diaSemana=@"Sexta";
            break;
        case 7:
            diaSemana=@"Sábado";
            break;
        default:
            diaSemana=@"Erro data";
            break;
    }
    
    NSString* titulo = [NSString stringWithFormat:@"%@ - %@", diaSemana, refeicao];
    return titulo;
}

- (void) update
{
    Restaurante* restauranteNaTela = self.restaurantes[self.restauranteNaTelaIndex];
    
    self.tituloBar.leftBarButtonItem.enabled = !(self.restauranteNaTelaIndex == 0);
    
    self.tituloBar.rightBarButtonItem.enabled = !(self.restauranteNaTelaIndex == [self.restaurantes count] - 1);
    
    [self.cardapiosTableView reloadData];
    
    self.tituloBar.title = restauranteNaTela.nome;
}
- (IBAction)nextBtnClick:(UIBarButtonItem *)sender {
    self.restauranteNaTelaIndex++;
    [self update];
}
- (IBAction)backBtnClick:(UIBarButtonItem *)sender {
    self.restauranteNaTelaIndex--;
    [self update];
}



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


-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    Restaurante* restauranteSelecionado = self.restaurantes[self.restauranteNaTelaIndex];
    return [ restauranteSelecionado.cardapios count ];
}

-(void) updatedFinished:(NSNotification *)notification
{
    self.restaurantesToUpdate--;
    
    //Se faltar restaurante para atualizar, nao faz nada
    if (self.restaurantesToUpdate > 0)
        return;
    
    NSString *string = [[notification userInfo]
                        objectForKey:@"Resultado"];
    if ([string isEqualToString:@"OK"]) {
        //Salve os cardapios
        [RestauranteStorage saveRestaurantes:self.restaurantes];
        
    } else {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Erro" message:@"Não foi possível atualizar os cardápios." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
    }
    [self update];
    [SVProgressHUD dismiss];
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    //Carrega restaurantes serializados, se possivel
    self.restaurantes = [ RestauranteStorage loadRestaurantes ];
    self.restauranteNaTelaIndex=0;
    
    //Se tiver restaurantes carregados mas nao tiver selecionados
    if (self.restaurantes && !self.restaurantesSelecionados) {
        //Popula restaurantes selecionados se nao tiver nenhum selecionado
        if (!self.restaurantesSelecionados) {
            NSMutableArray* selecionadosTemp = [[NSMutableArray alloc] init];
            
            for (Restaurante* r in self.restaurantes) {
                [selecionadosTemp addObject:r.codigo];
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
        
        [self update];
    
    }
}


- (Restaurante*)restauranteComCodigo: (NSString*)codigo
{
    for (Restaurante* r in self.restaurantes) {
        if ([r.codigo isEqualToString:codigo])
             return r;
    }
    return nil;
}
- (IBAction)atualizarBtnClick:(UIBarButtonItem *)sender {
    
    [SVProgressHUD showWithStatus:@"Carregando"];
    self.restaurantesToUpdate = 0;
    for (Restaurante* r in self.restaurantes) {
        self.restaurantesToUpdate++;
        [ RestauranteStorage atualizaRestaurante:r];
    }
}

- (IBAction)siteBtnClick:(UIBarButtonItem *)sender {
    Restaurante* restauranteSelecionado = self.restaurantes[self.restauranteNaTelaIndex];
    NSURL *url = [NSURL URLWithString:restauranteSelecionado.site];
    [[UIApplication sharedApplication] openURL:url];
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
        
        
        self.restaurantesToUpdate = 0;
        bool temQueAtualizar = NO;
        for (Restaurante* r in self.restaurantes) {
            [ r removeCardapiosAntigos ];
            if ([ r temQueAtualizar]) {
                temQueAtualizar = YES;
                self.restaurantesToUpdate++;
                [ RestauranteStorage atualizaRestaurante:r];
            }
        }
        if (temQueAtualizar) {
            [SVProgressHUD showWithStatus:@"Carregando"];
        } else {
            //Salve os cardapios
            [RestauranteStorage saveRestaurantes:self.restaurantes];
        }
    }
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([segue.identifier isEqualToString:@"abreConfig"]) {
        //SEGUE para tela de Configuracoes
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
