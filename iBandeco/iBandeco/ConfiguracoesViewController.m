//
//  BandecoViewController.m
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 15/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import "ConfiguracoesViewController.h"

@interface ConfiguracoesViewController ()

@property (strong, nonatomic) NSMutableArray* restaurantesDisponiveis;

@property (weak, nonatomic) IBOutlet UICollectionView *listaRestaurantesView;

@end

@implementation ConfiguracoesViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    //Mostra janela de carregando
    [SVProgressHUD showWithStatus:@"Carregando"];
    //Pega todos os restaurantes
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObject:@"text/plain"];
    [manager GET:(BANDECO_SITE @"json/restaurantes") parameters:nil success:^(AFHTTPRequestOperation *operation, NSDictionary* responseObject) {
        
        
        for (NSString* rest_codigo in responseObject[@"restaurantes"])
        {
            [ self.restaurantesDisponiveis addObject:rest_codigo ];
        }
        
        [self.listaRestaurantesView reloadData];
        [SVProgressHUD dismiss];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [SVProgressHUD dismiss];
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Erro" message:@"Não foi possível atualizar universidades." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
    }];
}

-(void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex
{
    //Clicou no OK
    if (buttonIndex == 0) {
        [ self performSegueWithIdentifier:@"abrePrincipal" sender:self];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    LogoUniversidadeViewCell* cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"logoUniversidade" forIndexPath:indexPath];
    
    if (!cell) {
        cell = [[LogoUniversidadeViewCell alloc] init];
    }
    
    NSString* univCod = (NSString*)self.restaurantesDisponiveis[indexPath.row];
    
    NSString *docDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *pngFilePath = [NSString stringWithFormat:@"%@/logo_%@.png", docDir, univCod];
    
    UIImage* logoImage = [UIImage imageWithContentsOfFile:pngFilePath];
    if (!logoImage)
    {
        //Se nao tiver imagem, baixa e salva.
        [[NSData dataWithData:UIImagePNGRepresentation([UIImage imageWithData:[NSData dataWithContentsOfURL: [NSURL URLWithString:[NSString stringWithFormat:(BANDECO_SITE @"/images/logo_%@.png"), univCod]]]])]writeToFile:pngFilePath atomically:YES];
        logoImage = [UIImage imageWithContentsOfFile:pngFilePath];
    }
    
    cell.univLogo.image = logoImage;
    
    cell.univTick.hidden = ![self.restaurantesSelecionados containsObject:univCod];
  
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    LogoUniversidadeViewCell* cell = (LogoUniversidadeViewCell*)[ collectionView cellForItemAtIndexPath:indexPath ];
    
    NSString* codigoRestaurante = self.restaurantesDisponiveis[indexPath.row];
    
    if ([ self.restaurantesSelecionados containsObject:codigoRestaurante ]) {
        cell.univTick.hidden = YES;
        [ self.restaurantesSelecionados removeObject:codigoRestaurante];
    } else {
        cell.univTick.hidden = NO;
        [ self.restaurantesSelecionados addObject:codigoRestaurante];
    }
    
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return [self.restaurantesDisponiveis count];
}

- (IBAction)doDone:(UIButton *)sender {
    if ([self.restaurantesSelecionados count] > 0) {
        //Ordena por ordem alfabetica
        [self.restaurantesSelecionados sortUsingSelector:@selector(compare:)];
        
        //Chama SEGUE para view/controller principal
        [ self performSegueWithIdentifier:@"abrePrincipal" sender:self];
        //NSLog(@"%@", self.restaurantesSelecionados);
    } else {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Erro" message:@"Selecione ao menos uma universidade." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
    }
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([segue.identifier isEqualToString:@"abrePrincipal"]) {
        PrincipalViewController* pVC = segue.destinationViewController;
        pVC.restaurantesSelecionados = self.restaurantesSelecionados;
    }
}

-(NSMutableArray *)restaurantesDisponiveis
{
    if (!_restaurantesDisponiveis) {
        _restaurantesDisponiveis = [[NSMutableArray alloc] init];
    }
    return _restaurantesDisponiveis;
}

- (NSMutableArray *)restaurantesSelecionados
{
    if (!_restaurantesSelecionados) {
        _restaurantesSelecionados = [[NSMutableArray alloc] init];
    }
    return _restaurantesSelecionados;
}

@end
