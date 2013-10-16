//
//  BandecoViewController.m
//  iBandeco
//
//  Created by Vinícius Daly Felizardo on 15/10/13.
//  Copyright (c) 2013 Vinícius Daly Felizardo. All rights reserved.
//

#import "BandecoViewController.h"

@interface BandecoViewController ()

@property (strong, nonatomic) NSMutableArray* restaurantesDisponiveis;
@property (strong, nonatomic) NSMutableArray*
    restaurantesSelecionados;
@property (weak, nonatomic) IBOutlet UICollectionView *listaRestaurantesView;

@end

@implementation BandecoViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    //Mostra janela de carregando
    [SVProgressHUD showWithStatus:@"Carregando"];
    //Pega todos os restaurantes
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObject:@"text/plain"];
    [manager GET:@"http://bandeco.felizardo.org/json/restaurantes" parameters:nil success:^(AFHTTPRequestOperation *operation, NSDictionary* responseObject) {
        
        
        for (NSString* rest_codigo in responseObject[@"restaurantes"])
        {
            [ self.restaurantesDisponiveis addObject:rest_codigo ];
        }
        
        [self.listaRestaurantesView reloadData];
        [SVProgressHUD dismiss];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [SVProgressHUD dismiss];
        NSLog(@"Error: %@", error);
    }];
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
	NSLog(@"%@", pngFilePath);
    
    if (![UIImage imageWithContentsOfFile:pngFilePath])
    {
        [[NSData dataWithData:UIImagePNGRepresentation([UIImage imageWithData:[NSData dataWithContentsOfURL: [NSURL URLWithString:[NSString stringWithFormat:@"http://bandeco.felizardo.org/images/logo_%@.png", univCod]]]])]writeToFile:pngFilePath atomically:YES];
    }
    
    cell.univLogo.image = [UIImage imageWithContentsOfFile:pngFilePath];
    
    cell.univTick.hidden = YES;
    
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    LogoUniversidadeViewCell* cell = (LogoUniversidadeViewCell*)[ collectionView cellForItemAtIndexPath:indexPath ];
    
    NSString* codigoRestaurante = self.restaurantesDisponiveis[indexPath.row];
    
    NSLog(@"%@", codigoRestaurante);
    cell.univTick.hidden = ! cell.univTick.hidden;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return [self.restaurantesDisponiveis count];
}

- (IBAction)doDone:(UIButton *)sender {

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
