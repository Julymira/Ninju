package com.ninju.startup;

import com.ninju.dao.FoodDao;
import com.ninju.model.Food;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FoodCatalogInitializer {

    @Inject
    FoodDao foodDao;

    @Transactional
    public void onStart(@Observes StartupEvent ev) {
        if (!foodDao.findAll().isEmpty()) return;

        // Fontes de Proteína
        insert("Frango Grelhado (Peito)",              165, 31.0, 0.0,  3.6);
        insert("Ovo Inteiro Cozido",                   155, 13.0, 1.1,  11.0);
        insert("Salmão Assado",                        208, 20.0, 0.0,  13.0);
        insert("Patinho Moído Grelhado",               219, 35.9, 0.0,  7.3);
        insert("Filet Mignon Grelhado",                220, 32.8, 0.0,  8.8);
        insert("Alcatra Grelhada",                     241, 31.9, 0.0,  11.6);
        insert("Atum Sólido em Enlatado (Água)",       116, 26.0, 0.0,  0.8);
        insert("Sardinha Assada",                      210, 25.0, 0.0,  11.5);
        insert("Clara de Ovo",                         52,  11.0, 0.7,  0.2);
        insert("Coxa de Frango Assada (sem pele)",     167, 25.0, 0.0,  6.8);
        insert("Sobrecoxa de Frango Assada (sem pele)",233, 24.0, 0.0,  14.5);
        insert("Lombo de Porco Assado",                210, 31.1, 0.0,  8.6);
        insert("Músculo Bovino Cozido",                194, 31.2, 0.0,  6.7);
        insert("Tilápia Grelhada (Filet)",             128, 26.0, 0.0,  2.3);
        insert("Camarão Cozido",                       99,  24.0, 0.2,  0.3);
        insert("Peru (Peito Defumado)",                104, 17.0, 2.0,  3.1);
        insert("Corte de Carne de Sol Grelhado",       250, 32.0, 0.0,  12.5);
        insert("Cupim Assado",                         330, 22.0, 0.0,  26.0);
        insert("Costela de Porco Assada",              290, 24.0, 0.0,  21.0);
        insert("Merluza Grelhada",                     122, 19.2, 0.0,  4.5);

        // Carboidratos e Grãos
        insert("Arroz Branco Cozido",                  130, 2.7,  28.2, 0.3);
        insert("Feijão Carioca Cozido",                77,  4.8,  13.6, 0.5);
        insert("Batata Doce Cozida",                   86,  1.6,  20.1, 0.1);
        insert("Aveia em Flocos",                      389, 17.0, 66.0, 7.0);
        insert("Arroz Integral Cozido",                124, 2.6,  25.8, 1.0);
        insert("Feijão Preto Cozido",                  77,  4.5,  14.0, 0.5);
        insert("Batata Inglesa Cozida",                86,  2.0,  20.1, 0.1);
        insert("Mandioca (Aipim) Cozida",              125, 0.6,  30.1, 0.3);
        insert("Macarrão de Sêmola Cozido",            143, 5.5,  28.5, 0.6);
        insert("Macarrão Integral Cozido",             124, 5.3,  25.0, 0.5);
        insert("Quinoa Cozida",                        120, 4.4,  21.3, 1.9);
        insert("Cuscuz Paulista / Nordestino",         112, 2.2,  25.4, 0.2);
        insert("Tapioca (Goma Pronta)",                240, 0.0,  60.0, 0.0);
        insert("Pão de Forma Tradicional",             62,  2.2,  12.3, 0.7);
        insert("Pão de Forma Integral",                58,  2.5,  10.5, 0.6);
        insert("Pão Francês",                          150, 4.5,  29.3, 1.5);
        insert("Milho Verde Cozido",                   98,  3.2,  19.0, 2.4);
        insert("Grão de Bico Cozido",                  139, 8.8,  20.0, 2.5);
        insert("Lentilha Cozida",                      93,  6.3,  15.0, 0.5);
        insert("Inhame Cozido",                        116, 1.5,  27.6, 0.1);
        insert("Mandioquinha (Batata Baroa) Cozida",   105, 1.0,  24.5, 0.2);
        insert("Granola Tradicional",                  420, 10.0, 68.0, 12.0);
        insert("Polenta Cozida",                       64,  1.4,  14.0, 0.2);
        insert("Farofa de Mandioca Pronta",            410, 1.3,  80.0, 9.5);
        insert("Biscoito de Arroz Integral",           30,  0.7,  6.5,  0.2);

        // Laticínios e Derivados
        insert("Leite Desnatado",                      35,  3.3,  5.0,  0.1);
        insert("Leite Integral",                       61,  3.2,  4.8,  3.3);
        insert("Iogurte Natural Integral",             63,  4.1,  5.0,  3.5);
        insert("Iogurte Natural Desnatado",            41,  4.5,  5.5,  0.2);
        insert("Queijo Cottage",                       98,  11.0, 3.4,  4.3);
        insert("Queijo Minas Frescal",                 240, 16.0, 3.2,  18.0);
        insert("Queijo Muçarela",                      320, 24.0, 3.0,  24.0);
        insert("Queijo Prato",                         350, 23.0, 2.0,  28.0);
        insert("Queijo Parmesão",                      431, 38.0, 4.1,  29.0);
        insert("Requeijão Cremoso Tradicional",        257, 9.5,  3.5,  23.0);
        insert("Requeijão Light",                      170, 11.0, 2.5,  13.5);
        insert("Whey Protein Concentrado (pó)",        400, 80.0, 6.0,  6.0);
        insert("Creme de Leite",                       220, 2.0,  4.0,  22.0);
        insert("Manteiga com Sal",                     717, 0.8,  0.1,  81.0);
        insert("Ricota",                               140, 11.0, 4.0,  9.0);

        // Frutas
        insert("Banana Prata",                         89,  1.1,  22.8, 0.3);
        insert("Maçã com casca",                       52,  0.3,  13.8, 0.2);
        insert("Mamão Papaia",                         43,  0.5,  11.0, 0.1);
        insert("Abacaxi",                              50,  0.5,  13.0, 0.1);
        insert("Morango",                              32,  0.7,  7.7,  0.3);
        insert("Uva Italiana",                         69,  0.7,  18.1, 0.2);
        insert("Laranja (Suco/Polpa)",                 47,  0.9,  11.7, 0.1);
        insert("Melancia",                             30,  0.6,  7.5,  0.2);
        insert("Melão Amarelo",                        34,  0.8,  8.2,  0.2);
        insert("Abacate",                              160, 2.0,  8.5,  14.7);
        insert("Manga Palmer",                         60,  0.8,  15.0, 0.4);
        insert("Pêra com casca",                       57,  0.4,  15.2, 0.1);
        insert("Limão Taquari",                        29,  1.1,  9.3,  0.3);
        insert("Goiaba Vermelha",                      68,  2.6,  14.3, 1.0);
        insert("Kiwi",                                 61,  1.1,  14.7, 0.5);
        insert("Maracujá",                             97,  2.2,  23.4, 0.7);
        insert("Tangerina Ponkan",                     53,  0.8,  13.3, 0.3);
        insert("Açaí (Polpa pura sem xarope)",         60,  1.2,  6.0,  4.0);
        insert("Coco (Polpa seca)",                    354, 3.3,  15.2, 33.5);
        insert("Ameixa Fresca",                        46,  0.7,  11.4, 0.3);

        // Verduras, Legumes e Oleaginosas
        insert("Brócolis Cozido",                      35,  2.4,  7.0,  0.4);
        insert("Cenoura Crua",                         41,  0.9,  9.6,  0.2);
        insert("Tomate Italiano",                      18,  0.9,  3.9,  0.2);
        insert("Alface Crespa",                        15,  1.4,  2.9,  0.2);
        insert("Espinafre Refogado",                   23,  3.0,  3.8,  0.3);
        insert("Chuchu Cozido",                        19,  0.4,  4.5,  0.1);
        insert("Abobrinha Italiana Grelhada",          17,  1.2,  3.1,  0.1);
        insert("Pepino Japonês",                       15,  0.7,  3.6,  0.1);
        insert("Beterraba Cozida",                     43,  1.6,  10.0, 0.2);
        insert("Couve Manteiga Refogada",              35,  2.0,  6.0,  0.8);
        insert("Cebola Crua",                          40,  1.1,  9.3,  0.1);
        insert("Alho",                                 149, 6.4,  33.1, 0.5);
        insert("Vagem Cozida",                         31,  1.8,  7.0,  0.2);
        insert("Azeite de Oliva Extra Virgem",         884, 0.0,  0.0,  100.0);
        insert("Castanha do Pará",                     656, 14.0, 12.0, 66.0);
        insert("Castanha de Caju Torrada",             553, 18.0, 30.0, 44.0);
        insert("Amendoim Torrado",                     567, 26.0, 16.0, 49.0);
        insert("Pasta de Amendoim Integral",           588, 25.0, 20.0, 50.0);
        insert("Cogumelo Paris Fresco",                22,  3.1,  3.3,  0.3);
        insert("Rúcula",                               25,  2.6,  3.7,  0.7);
    }

    private void insert(String name, int calories, double protein, double carbohydrates, double fat) {
        Food f = new Food();
        f.setName(name);
        f.setCalories(calories);
        f.setProtein(protein);
        f.setCarbohydrates(carbohydrates);
        f.setFat(fat);
        f.setOwner(null);
        foodDao.save(f);
    }
}
