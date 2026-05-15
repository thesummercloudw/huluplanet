#!/bin/bash
# 修复下载失败的猫粮产品图片
cd /Users/zhaomu/IdeaProjects/catplanet/catplanet-server/uploads

echo "=== 修复下载失败的图片 ==="

# 1. GO! Solutions Carnivore 九种肉 - Petcurean Contentful CDN
echo "[1/14] GO!九种肉..."
curl -sL -o food-go-9meat.png "https://images.ctfassets.net/sa0sroutfts9/4OoklC2DlRenx7iHzF19QI/026fa9ffbe29e713e108288f7ffa3ad9/go-carnivore-grain-free-chicken-turkey-duck-recipe-for-cats.jpg?w=800&h=800&fit=fill"

# 2. Instinct Original Chicken - PetSmart Scene7 CDN
echo "[2/14] 百利经典无谷鸡肉..."
curl -sL -o food-instinct-chicken.png "https://s7d2.scene7.com/is/image/PetSmart/5265751?wid=800&hei=800&fmt=png-alpha"

# 3. Royal Canin Kitten K36 - Mars Petcare CDN
echo "[3/14] 皇家幼猫K36..."
curl -sL -o food-royal-k36.png "https://marspetcareaprimocdn.petcare.global/fa1241ac-7528-4e00-b045-b2490009a695/fa1241ac-7528-4e00-b045-b2490009a695_DownloadAsJpg.jpg"

# 4. Ziwi Peak Mackerel & Lamb - BigCommerce CDN
echo "[4/14] 巅峰马鲛鱼羊肉罐..."
curl -sL -o food-ziwi-mackerel.png "https://cdn11.bigcommerce.com/s-d20sba7s0/products/1136/images/21312/Plain__ZIWI-Peak-Wet-Canned_Mackerel-Lamb_Cat_Front-of-Pack_185g-6.5oz__95939.1667182511.1280.1280.png?c=1"

# 5. Ziwi Peak Chicken - Shopify CDN (PetPost AU)
echo "[5/14] 巅峰鸡肉罐..."
curl -sL -o food-ziwi-chicken.png "https://cdn.shopify.com/s/files/1/0093/8059/3743/files/110000010121-ziwi-peak-chicken-recipe-wet-cat-food-1.jpg?v=1772660938"

# 6. K9 Natural (Feline Natural) Chicken Can - Shopify CDN
echo "[6/14] K9 Natural鸡肉罐..."
curl -sL -o food-k9-chicken.png "https://cdn.shopify.com/s/files/1/0578/6956/6116/files/1980-canned-chicken-feast-85g_1_2048x2048.png?v=1720386061"

# 7. Leonardo Pure Poultry (小李子) - CloudFront CDN
echo "[7/14] 小李子猫罐..."
curl -sL -o food-xiaolizi.png "https://d23dsm0lnesl7r.cloudfront.net/media/b6/f6/35/1653894283/bl-dosen-gefluegel-200g.png?ts=1653900505"

# 8. RANOVA 朗诺 - Shopify CDN (Maokids Pet)
echo "[8/14] 朗诺冻干..."
curl -sL -o food-langnuo-chicken.png "https://cdn.shopify.com/s/files/1/0705/4966/7997/files/60g_-1.jpg?v=1760479850&width=800"

# === 中国品牌：使用Referer头绕过防盗链 ===

# 9. 高爷家 - 淘宝CDN (带Referer)
echo "[9/14] 高爷家..."
curl -sL -o food-catgrandpa.png "https://img.alicdn.com/imgextra/i3/2216004269830/O1CN01qKHVqZ1h6BKEK7QWG_!!2216004269830-0-cib.jpg" -H "Referer: https://www.taobao.com/" -H "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

# 10. 诚实一口 - 淘宝CDN (带Referer)
echo "[10/14] 诚实一口..."
curl -sL -o food-onebyone.png "https://img.alicdn.com/imgextra/i2/2212362879580/O1CN01gJMPj11hLn5xJg2cw_!!2212362879580-0-cib.jpg" -H "Referer: https://www.taobao.com/" -H "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

# 11. 网易严选 - 淘宝CDN (带Referer)
echo "[11/14] 网易严选..."
curl -sL -o food-netease.png "https://img.alicdn.com/imgextra/i1/2207378882152/O1CN016x3F1U1OuZTG4fYHW_!!2207378882152-0-cib.jpg" -H "Referer: https://www.taobao.com/" -H "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

# 12. 阿飞和巴弟 干粮 - 淘宝CDN (带Referer)
echo "[12/14] 阿飞和巴弟干粮..."
curl -sL -o food-afbd-dry.png "https://img.alicdn.com/imgextra/i2/2214281520312/O1CN01BSYCZ01ij7pZYxD8i_!!2214281520312-0-cib.jpg" -H "Referer: https://www.taobao.com/" -H "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

# 13. 有鱼 - 淘宝CDN (带Referer)
echo "[13/14] 有鱼..."
curl -sL -o food-yuyu.png "https://img.alicdn.com/imgextra/i4/2208591616498/O1CN010HQXPN1SdVhakU7Wy_!!2208591616498-0-cib.jpg" -H "Referer: https://www.taobao.com/" -H "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

# 14. 阿飞和巴弟 冻干 - 淘宝CDN (带Referer)
echo "[14/14] 阿飞和巴弟冻干..."
curl -sL -o food-afbd-fd.png "https://img.alicdn.com/imgextra/i1/2214281520312/O1CN01U7hs331ij7pbwLkWr_!!2214281520312-0-cib.jpg" -H "Referer: https://www.taobao.com/" -H "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

echo ""
echo "=== 检查修复结果 ==="
for f in food-go-9meat.png food-instinct-chicken.png food-royal-k36.png food-ziwi-mackerel.png food-ziwi-chicken.png food-k9-chicken.png food-xiaolizi.png food-langnuo-chicken.png food-catgrandpa.png food-onebyone.png food-netease.png food-afbd-dry.png food-yuyu.png food-afbd-fd.png; do
    size=$(stat -f%z "$f" 2>/dev/null || stat -c%s "$f" 2>/dev/null)
    ftype=$(file -b "$f" | head -c 30)
    if [ "$size" -lt 1000 ]; then
        echo "⚠️  $f - ${size}B ($ftype) - 可能下载失败"
    else
        echo "✅ $f - $(($size/1024))KB ($ftype)"
    fi
done

echo ""
echo "=== 重新复制PGC测评首图 ==="
cp food-orijen-6fish.png pgc-orijen-6fish.png
cp food-orijen-chicken.png pgc-orijen-chicken.png
cp food-acana-farm.png pgc-acana-farm.png
cp food-instinct-chicken.png pgc-instinct-chicken.png
cp food-nutrience-blackdiamond.png pgc-nutrience-blackdiamond.png
cp food-catgrandpa.png pgc-catgrandpa.png
cp food-onebyone.png pgc-onebyone.png
cp food-netease.png pgc-netease.png
cp food-royal-i27.png pgc-royal-i27.png
cp food-ziwi-mackerel.png pgc-ziwi-mackerel.png
cp food-k9-chicken.png pgc-k9-chicken.png
cp food-langnuo-chicken.png pgc-langnuo-chicken.png
cp food-ciao-churu.png pgc-ciao-churu.png
echo "PGC首图复制完成"
