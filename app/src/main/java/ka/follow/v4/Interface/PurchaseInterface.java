package ka.follow.v4.Interface;

public interface PurchaseInterface
{
    void buyItem(String sdk, int requestCode);
    void specialBanner(String sku,int requestCode,int followCoin,int LikeCoin);
}
