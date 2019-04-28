package ka.follow.app.Interface;

public interface PurchaseInterface
{
    void buyItem(String sdk, int requestCode);
    void specialBanner(String sku,int requestCode,int followCoin,int LikeCoin);
}
