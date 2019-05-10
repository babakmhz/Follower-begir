package ka.follow.app.Manager;

import java.util.ArrayList;
import java.util.Random;

import ka.follow.app.Models.ShopItem;

public class Config {

    public static final int ReqeuestSpeciialBanner = 10009;
    public static String SKUSpecialBanner = "SpecialBanner";


    //////
    ////Direct Purchase LIKE Config
    public static int reqeustLikeFirst = 30;
    public static String skuFirstLike = "FirstLikeItem";
    public static int reqeustLikeSecond = 31;
    public static String skuSecondLike = "SecondLikeItem";
    public static int reqeustLikeThird = 32;
    public static String skuThirdLike = "ThirdLikeItem";
    public static int reqeustLikeFourth = 33;
    public static String skuFourthLike = "PurchaseFourthLike";
    public static int reqeustLikeFifth = 34;
    public static String skuFifthLike = "FifthLikeItem";
    ////////
    ////Direct Purchase Comment Config
    public static int reqeustCommentFirst = 50;
    public static String skuFirstComment = "FirstCommentItem";
    public static int reqeustCommentSecond = 51;
    public static String skuSecondComment = "SecondCommentItem";
    public static int reqeustCommentThird = 52;
    public static String skuThirdComment = "ThirdCommentItem";
    ///////
    ////Direct Purchase Follow Config
    public static int reqeustFollowFirst = 16;
    public static String skuFirstFollow = "FirstFollowItem";
    public static int reqeustFollowSecond = 15;
    public static String skuSecondFollow = "SecondFollowItem";
    public static int reqeustFollowThird = 12;
    public static String skuThirdFollow = "ThirdFollowItem";
    public static int reqeustFollowFourth = 13;
    public static String skuFourthFollow = "PurchaseFourthFollow";
    public static int reqeustFollowFifth = 14;
    public static String skuFifthFollow = "FifthFollowItem";
    ////Direct Purchase View Config
    public static int reqeustViewtFirst = 87;
    public static String skuFirstVIew = "FirstViewItem";
    public static int reqeustViewSecond = 88;
    public static String skuSecondView = "SecondViewItem";
    public static int reqeustViewThird = 89;
    public static String skuThirdView = "ThirdViewItem";
    ///////
    /////ShopItem
    public static int requestShopItems = 888;


    public static ArrayList<ShopItem> shopItems = new ArrayList<>();
    public static int bannerLikeCoinCount=0;
    public static int bannerFollowCoin=0;
    public static String shopSku;
    public static int shopType;
    public static int shopCounts;


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
