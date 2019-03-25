package instahelper.ghonchegi.myfollower.Manager;

import java.util.ArrayList;
import java.util.Random;

import instahelper.ghonchegi.myfollower.Models.ShopItem;

public class Config {

    public static final int ReqeuestSpeciialBanner = 10009;
    public static final String SKUSpecialBanner = "SpecialBanner";

    public static ArrayList<ShopItem> shopItems = new ArrayList<>();

    public static void setupShopItems() {
        final int min = 10000;
        final int max = 99999;

        ShopItem shopItem = new ShopItem();


        shopItem.setId(1);
        shopItem.setAmount(100);
        shopItem.setPrice(1200);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(0);
        shopItems.add(shopItem);

        shopItem.setAmount(500);
        shopItem.setId(2);
        shopItem.setPrice(5000);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(0);
        shopItems.add(shopItem);

        shopItem.setAmount(1000);
        shopItem.setId(3);
        shopItem.setPrice(9000);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(0);
        shopItems.add(shopItem);


        shopItem.setAmount(2000);
        shopItem.setId(4);
        shopItem.setPrice(19000);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(0);
        shopItems.add(shopItem);

        shopItem.setAmount(3000);
        shopItem.setId(5);
        shopItem.setPrice(28000);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(0);
        shopItems.add(shopItem);
        /////End of Likes

        ////Start of Followers

        shopItem.setAmount(180);
        shopItem.setId(6);
        shopItem.setPrice(1500);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(1);
        shopItems.add(shopItem);


        shopItem.setAmount(500);
        shopItem.setId(7);
        shopItem.setPrice(4200);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(1);
        shopItems.add(shopItem);

        shopItem.setAmount(1200);
        shopItem.setId(8);
        shopItem.setPrice(10000);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(1);
        shopItems.add(shopItem);

        shopItem.setAmount(2600);
        shopItem.setId(9);
        shopItem.setPrice(20000);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(1);
        shopItems.add(shopItem);

        shopItem.setAmount(3800);
        shopItem.setId(10);
        shopItem.setPrice(40000);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(1);
        shopItems.add(shopItem);

        ////End of followers

        ////Start of Comments

        shopItem.setAmount(50);
        shopItem.setId(11);
        shopItem.setPrice(1800);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(2);
        shopItems.add(shopItem);


        shopItem.setAmount(100);
        shopItem.setId(12);
        shopItem.setPrice(2800);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(2);
        shopItems.add(shopItem);


        shopItem.setAmount(200);
        shopItem.setId(13);
        shopItem.setPrice(3500);
        shopItem.setReturnValue(new Random().nextInt((max - min) + 1) + min);
        shopItem.setSku("");
        shopItem.setType(2);
        shopItems.add(shopItem);


    }


}
